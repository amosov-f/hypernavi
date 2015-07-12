package ru.hypernavi.server;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.classify.GoodsClassifier;
import ru.hypernavi.core.classify.RandomGoodsClassifier;
import ru.hypernavi.server.servlet.GoodsClassificationService;

import java.util.logging.Logger;

/**
 * User: amosov-f
 * Date: 24.11.14
 * Time: 0:19
 */
public final class HyperNaviServer {
    private static final Logger LOG = Logger.getLogger(HyperNaviServer.class.getName());

    public static void main(@NotNull final String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(GoodsClassifier.class).to(RandomGoodsClassifier.class);
            }
        });

        final int port = 80;
        final Server server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(injector.getInstance(GoodsClassificationService.class)), "/");
        server.setHandler(handler);

        server.start();
        LOG.info("Server started on port " + port + "!");
        server.join();
    }
}
