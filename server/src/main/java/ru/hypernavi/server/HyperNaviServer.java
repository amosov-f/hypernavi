package ru.hypernavi.server;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;


import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.reflections.Reflections;
import ru.hypernavi.server.handler.AfterRequestHandler;
import ru.hypernavi.server.handler.BeforeRequestHandler;

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

    @NotNull
    private static ServletContextHandler servlets(@NotNull final Properties properties) {
        final Injector injector = Guice.createInjector(new HyperNaviModule(properties));
        final ServletContextHandler context = new ServletContextHandler();
        for (final Class<? extends HttpServlet> servletClass : servletClasses()) {
            final HttpServlet service = injector.getInstance(servletClass);
            final WebServlet serviceConfig = servletClass.getAnnotation(WebServlet.class);
            for (final String path : serviceConfig.value()) {
                LOG.info("Bound http service '" + serviceConfig.name() + "' @'" + path + "' as " + servletClass.getSimpleName());
                context.addServlet(new ServletHolder(service), path);
            }
        }
        return context;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private static Set<Class<? extends HttpServlet>> servletClasses() {
        return new Reflections("ru.hypernavi").getTypesAnnotatedWith(WebServlet.class).stream()
                .map(annotatedClass -> (Class<? extends HttpServlet>) annotatedClass)
                .collect(Collectors.toSet());
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

        Optional.ofNullable(cmd.getOptionValue(OPT_LOG_DIR)).ifPresent(logsDir -> System.setProperty("LOGS_DIR", logsDir));
        DOMConfigurator.configure(HyperNaviServer.class.getResource(cmd.getOptionValue(OPT_LOG_CFG)));

        final HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(new BeforeRequestHandler());
        handlers.addHandler(servlets(properties));
        final RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(new AfterRequestHandler());
        handlers.addHandler(requestLogHandler);
        final Server server = new Server(port);
        server.setHandler(handlers);

        try {
            server.start();
        } catch (Exception e) {
            LOG.fatal("Server doesn't started on port " + port + "!", e);
            System.exit(0);
        }
        LOG.info("Server started on port " + port + "!");
        server.join();
    }
}
