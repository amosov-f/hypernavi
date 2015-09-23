package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


import org.apache.commons.io.IOUtils;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by Константин on 23.09.2015.
 */
@WebServlet(name = "apk download", value = "/hyperhavi.apk")
public class ApkDownoadServlet extends AbstractHttpService {
    @Override
    public void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        final URL myUrl = new URL("http://hypernavi.net:8111/repository/download/Hypernavi_ReleaseToDev/.lastSuccessful/app-1.0-SNAPSHOT.apk");
        final URLConnection urlConn = myUrl.openConnection();
        final String myCookie = "RememberMe=3506402^1#-7804521650216415278";
        urlConn.setRequestProperty("Cookie", myCookie);
        urlConn.connect();
        resp.getOutputStream().write(IOUtils.toByteArray(urlConn.getInputStream()));
    }
}
