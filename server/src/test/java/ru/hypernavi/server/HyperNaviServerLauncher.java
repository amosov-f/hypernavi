package ru.hypernavi.server;


import java.util.Objects;


import org.apache.log4j.xml.DOMConfigurator;
import ru.hypernavi.util.Config;

/**
 * Created by amosov-f on 22.08.15.
 */
public final class HyperNaviServerLauncher {
    public static final int PORT = 8081;
    private static final String[] CONFIG_PATHS = {"/common.properties", "/test.properties"};
    private static final String LOG_CFG = "/log4j-test.xml";

    private HyperNaviServer server;

    public void start() throws Exception {
        DOMConfigurator.configure(HyperNaviServer.class.getResource(LOG_CFG));
        server = new HyperNaviServer(PORT, Config.fromJar(CONFIG_PATHS));
        server.start();
    }

    public void stop() {
        Objects.requireNonNull(server).stop();
    }
}
