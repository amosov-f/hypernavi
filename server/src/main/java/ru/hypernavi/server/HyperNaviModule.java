package ru.hypernavi.server;

import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.IOException;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import ru.hypernavi.commons.Platform;
import ru.hypernavi.core.classify.goods.GoodsClassifier;
import ru.hypernavi.core.classify.goods.RandomGoodsClassifier;
import ru.hypernavi.core.classify.goods.TomallGoodsClassifier;
import ru.hypernavi.core.database.DataLoader;
import ru.hypernavi.core.database.FileDataLoader;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.core.database.ImageDataBase;
import ru.hypernavi.util.Config;
import ru.hypernavi.util.MoreIOUtils;

/**
 * User: amosov-f
 * Date: 05.08.15
 * Time: 0:19
 */
public final class HyperNaviModule extends AbstractModule {
    @NotNull
    private final Config config;

    public HyperNaviModule(@NotNull final Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(Platform.class).toInstance(Platform.parse(config.getProperty("hypernavi.server.platform")));
        bindTemplates();

        bindString("hypernavi.server.pathdata");
        bindString("hypernavi.server.serviceimg");
        bindString("hypernavi.server.servicemarkets");


        bind(DataLoader.class).toInstance(new FileDataLoader(config.getProperty("hypernavi.server.pathdata")));
        bind(FileDataLoader.class).toInstance(new FileDataLoader(config.getProperty("hypernavi.server.pathdata")));
        bindGoodsClassifier();
    }

    private <T> void bindString(@NotNull final String key) {
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
}
