package ru.hypernavi.server;


import java.util.Objects;


import org.apache.log4j.xml.DOMConfigurator;
import ru.hypernavi.util.Config;
import ru.hypernavi.util.MoreIOUtils;

/**
 * Created by amosov-f on 22.08.15.
 */
public final class HyperNaviServerLauncher {
    public static final int PORT = 8081;
    private static final String[] CONFIG_PATHS = {"classpath:/common.properties", "classpath:/test.properties"};
    private static final String LOG_CFG = "classpath:/log4j-test.xml";

    private HyperNaviServer server;

    public void start() throws Exception {
        DOMConfigurator.configure(MoreIOUtils.toURL(LOG_CFG));
        server = new HyperNaviServer(PORT, Config.load(CONFIG_PATHS));
        server.start();
    }

    public void stop() {
        Objects.requireNonNull(server).stop();
    }
}
