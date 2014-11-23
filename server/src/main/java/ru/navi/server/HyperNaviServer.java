package ru.navi.server;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jetbrains.annotations.NotNull;
import ru.navi.hyper.algo.GoodsClassifier;
import ru.navi.server.servlet.HyperNaviServlet;

/**
 * User: amosov-f
 * Date: 24.11.14
 * Time: 0:19
 */
public class HyperNaviServer {
    public static void main(@NotNull final String[] args) throws Exception {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(GoodsClassifier.class).asEagerSingleton();
            }
        });
        final Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(injector.getInstance(HyperNaviServlet.class)), "/");
        server.setHandler(handler);
        System.out.println("!!!");
        server.start();
    }
}
