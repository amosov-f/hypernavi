package ru.hypernavi.server;

import org.jetbrains.annotations.NotNull;

import java.util.Properties;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.hypernavi.core.classify.GoodsClassifier;
import ru.hypernavi.core.classify.RandomGoodsClassifier;
import ru.hypernavi.core.classify.TomallGoodsClassifier;
import ru.hypernavi.server.handler.BeforeRequestHandler;
import ru.hypernavi.server.servlet.GoodsClassificationService;

/**
 * User: amosov-f
 * Date: 24.11.14
 * Time: 0:19
 */
public final class HyperNaviServer {
    private static final Log LOG = LogFactory.getLog(HyperNaviServer.class);

    private static final String OPT_PORT = "port";
    private static final String OPT_CONFIG = "cfg";
    private static final String OPT_LOG_CFG = "logcfg";
    private static final String OPT_LOG_DIR = "logdir";

    private static final Options OPTIONS = new Options() {{
        addOption(Option.builder(OPT_PORT).hasArg().required().desc("server binding port").build());
        addOption(Option.builder(OPT_CONFIG).hasArgs().required().desc("config files").build());
        addOption(Option.builder(OPT_LOG_CFG).hasArg().required().desc("log4j configuration file").build());
        addOption(Option.builder(OPT_LOG_DIR).hasArg().desc("directory for logs").build());
    }};

    private HyperNaviServer() {
    }

    public static void main(@NotNull final String[] args) throws Exception {
        // TODO: use DefaultParser
        final CommandLine cmd = new GnuParser().parse(OPTIONS, args);
        System.setProperty("PORT", cmd.getOptionValue(OPT_PORT));
        final int port = Integer.parseInt(cmd.getOptionValue(OPT_PORT));
        final Properties properties = new Properties();
        for (final String configPath : cmd.getOptionValues(OPT_CONFIG)) {
            // TODO: read config from file system
            properties.load(HyperNaviServer.class.getResourceAsStream(configPath));
        }

        if (cmd.hasOption(OPT_LOG_DIR)) {
            System.setProperty("LOGS_DIR", cmd.getOptionValue(OPT_LOG_DIR));
        }
        DOMConfigurator.configure(HyperNaviServer.class.getResource(cmd.getOptionValue(OPT_LOG_CFG)));

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
