package ru.hypernavi.server;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;


import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mysql.fabric.jdbc.FabricMySQLDriver;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import ru.hypernavi.core.server.Platform;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.auth.VkAuthValidator;
import ru.hypernavi.core.classify.goods.GoodsClassifier;
import ru.hypernavi.core.classify.goods.RandomGoodsClassifier;
import ru.hypernavi.core.classify.goods.TomallGoodsClassifier;
import ru.hypernavi.core.database.*;
import ru.hypernavi.core.database.provider.DatabaseProvider;
import ru.hypernavi.core.database.provider.mongo.SiteMongoProvider;
import ru.hypernavi.core.geoindex.GeoIndex;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.server.servlet.admin.site.SiteRequest;
import ru.hypernavi.server.servlet.search.SearchRequest;
import ru.hypernavi.util.Config;
import ru.hypernavi.util.MoreIOUtils;

/**
 * User: amosov-f
 * Date: 05.08.15
 * Time: 0:19
 */
public final class HyperNaviModule extends AbstractModule {
    private static final Log LOG = LogFactory.getLog(HyperNaviModule.class);

    @NotNull
    private final Config config;

    public HyperNaviModule(@NotNull final Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        install(RequestReader.module());
        install(SearchRequest.module());
        install(AdminRequestReader.module());
        install(SiteRequest.module());

        bind(Platform.class).toInstance(Platform.parse(config.getProperty("hypernavi.server.platform")));
        bindTemplates();
        bindHttpClient();

        bindProperty("hypernavi.server.pathdata");
        bindProperty("hypernavi.server.serviceimg");
        bindProperty("hypernavi.server.servicemarkets");

        bindDataSource();
        // TODO: remove
        bind(FileDataLoader.class).toInstance(new FileDataLoader(config.getProperty("hypernavi.server.pathdata")));

        bindTelegramBot();
        bindVkAuthValidator();

        bindGoodsClassifier();
    }

    private <T> void bindProperty(@NotNull final String key) {
        bind(String.class).annotatedWith(Names.named(key)).toInstance(config.getProperty(key));
    }

    private void bindTemplates() {
        final Configuration templatesConfig = new Configuration(Configuration.VERSION_2_3_23);
        final String pathToBundle = config.getProperty("hypernavi.web.bundle");
        if (MoreIOUtils.isClasspath(pathToBundle)) {
            templatesConfig.setTemplateLoader(new ClassTemplateLoader(getClass(), MoreIOUtils.toResourceName(pathToBundle)));
        } else {
            try {
                templatesConfig.setTemplateLoader(new FileTemplateLoader(new File(pathToBundle)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            templatesConfig.setTemplateUpdateDelayMilliseconds(0);
        }
        bind(Configuration.class).toInstance(templatesConfig);

        bind(HypermarketHolder.class).asEagerSingleton();
        bind(ImageDataBase.class).asEagerSingleton();
        requestStaticInjection(RegisterHypermarket.class);
    }

    private void bindHttpClient() {
        bind(HttpClient.class).toInstance(HttpClientBuilder.create().build());
    }

    private void bindGoodsClassifier() {
        final Class<? extends GoodsClassifier> goodsClassifierClass;
        if ("tomall".equals(config.getProperty("hypernavi.category.model"))) {
            goodsClassifierClass = TomallGoodsClassifier.class;
        } else {
            goodsClassifierClass = RandomGoodsClassifier.class;
        }
        bind(GoodsClassifier.class).to(goodsClassifierClass);
    }

    private void bindDataSource() {
        final String dataSource = Objects.requireNonNull(config.getProperty("hypernavi.data.source"), "No data source in config!");
        switch (dataSource) {
            case "file":
                final String dataPath = config.getProperty("hypernavi.server.pathdata");
                bind(DataLoader.class).toInstance(new FileDataLoader(dataPath));
                LOG.info("Data storage is file system: " + dataPath);
                return;
            case "mysql":
                final String url = config.getProperty("hypernavi.data.mysql.database");
                final Statement statement;
                try {
                    DriverManager.registerDriver(new FabricMySQLDriver());
                    // TODO: policy about reconnect
                    final Connection connection = DriverManager.getConnection(url, config.subConfig("hypernavi.data.mysql"));
                    try {
                        statement = connection.createStatement();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
//                bind(DataLoader.class).toInstance(new SQLDataLoader(statement));
                LOG.info("Data storage is MySQL database: " + url);
                return;
            case "mongo":
                final String[] hostWithPort = config.getProperty("hypernavi.data.mongo.client").split(":");
                final MongoClient client = new MongoClient(hostWithPort[0], Integer.parseInt(hostWithPort[1]));
                final MongoDatabase database = client.getDatabase(config.getProperty("hypernavi.data.mongo.database"));
                bind(MongoDatabase.class).toInstance(database);
                bind(new TypeLiteral<DatabaseProvider<Site>>() {}).to(SiteMongoProvider.class);
                bind(new TypeLiteral<GeoIndex<Site>>() {}).to(SiteMongoProvider.class);

                // TODO: remove
                bind(DataLoader.class).toInstance(new FileDataLoader(config.getProperty("hypernavi.server.pathdata")));
                return;
            default:
                throw new UnsupportedOperationException("Unknown data source: " + dataSource);
        }
    }

    private void bindTelegramBot() {
        bindProperty("hypernavi.telegram.bot.auth_token");
        bindProperty("hypernavi.telegram.bot.search_host");
    }

    private void bindVkAuthValidator() {
        final int appId = config.getInt("hypernavi.auth.vk.app_id");
        final String secretKey = config.getProperty("hypernavi.auth.vk.secret_key");
        bind(VkAuthValidator.class).toInstance(new VkAuthValidator(appId, secretKey));
    }
}
