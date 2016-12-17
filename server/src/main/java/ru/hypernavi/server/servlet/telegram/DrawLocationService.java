package ru.hypernavi.server.servlet.telegram;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.webutil.ImageEditor;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.awt.ImageUtils;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by amosov-f on 17.12.16
 */
@WebServlet(name = "draw location", value = "/draw/location")
public final class DrawLocationService extends AbstractHttpService {
  @Override
  public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
    resp.setStatus(HttpStatus.SC_OK);
    final Point location = session.demand(Property.MAP_POINT);
    final String imageLink = session.demand(Property.LINK);
    final BufferedImage image = ImageUtils.download(imageLink);
    final BufferedImage locatedImage = ImageEditor.INSTANCE.drawLocation(image, location);
    resp.setContentType("image/jpg");
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ImageIO.write(locatedImage, ImageUtils.format(locatedImage, imageLink), bout);
    bout.flush();
    bout.close();
    resp.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
    resp.addDateHeader(HttpHeaders.LAST_MODIFIED, System.currentTimeMillis());
    resp.setContentLength(bout.toByteArray().length);
    ImageIO.write(locatedImage, ImageUtils.format(locatedImage, imageLink), resp.getOutputStream());
  }
}
