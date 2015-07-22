package ru.hypernavi.server;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.classify.GoodsClassifier;
import ru.hypernavi.core.classify.RandomGoodsClassifier;
import ru.hypernavi.core.classify.TomallGoodsClassifier;
import ru.hypernavi.server.handler.BeforeRequestHandler;
import ru.hypernavi.server.servlet.GoodsClassificationService;

import java.util.Properties;

/**
 * User: amosov-f
 * Date: 24.11.14
 * Time: 0:19
 */
public final class HyperNaviServer {
    private static final Log LOG = LogFactory.getLog(HyperNaviServer.class);

    private static final String OPT_PORT = "port";
    private static final String OPT_CONFIG = "cfg";

    private static final Options OPTIONS = new Options() {{
        addOption(Option.builder(OPT_PORT).hasArg().required().desc("server binding port").build());
        addOption(Option.builder(OPT_CONFIG).hasArgs().required().desc("config files").build());
    }};

    public static void main(@NotNull final String[] args) throws Exception {
        final CommandLine cmd = new DefaultParser().parse(OPTIONS, args);
        final int port = Integer.parseInt(cmd.getOptionValue(OPT_PORT));
        final Properties properties = new Properties();
        for (final String configPath : cmd.getOptionValues(OPT_CONFIG)) {
            // TODO: read config from file system
            properties.load(HyperNaviServer.class.getResourceAsStream(configPath));
        }

        // TODO: configure log4j files from command line
        DOMConfigurator.configure(HyperNaviServer.class.getResource("/log4j.xml"));

        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                final Class<? extends GoodsClassifier> goodsClassifierClass;
                if ("tomall".equals(properties.getProperty("hypernavi.category.model"))) {
                    goodsClassifierClass = TomallGoodsClassifier.class;
                } else {
                    goodsClassifierClass = RandomGoodsClassifier.class;
                }
                bind(GoodsClassifier.class).to(goodsClassifierClass);
            }
        });

        final ServletContextHandler context = new ServletContextHandler();
        context.addServlet(new ServletHolder(injector.getInstance(GoodsClassificationService.class)), "/category");
        final HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(new BeforeRequestHandler());
        handlers.addHandler(context);

        final Server server = new Server(port);
        server.setHandler(handlers);

        server.start();
        LOG.info("Server started on port " + port + "!");
        server.join();
    }
}
