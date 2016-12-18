package ru.hypernavi.server.servlet.telegram;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.io.IOException;

/**
 * Created by amosov-f on 17.12.16
 */
@WebServlet(name = "draw location", value = "/draw/location")
public final class DrawLocationService extends AbstractHttpService {
  private static final Log LOG = LogFactory.getLog(DrawLocationService.class);

  @Override
  public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
    resp.setStatus(HttpServletResponse.SC_OK);
    final Point location = session.demand(Property.MAP_POINT);
    final String imageLink = session.demand(Property.LINK);
    final BufferedImage image = ImageUtils.download(imageLink);
    final BufferedImage locatedImage = ImageEditor.INSTANCE.drawLocation(image, location);
    resp.setContentType("image/jpeg");
    final long start = System.currentTimeMillis();
    ImageIO.write(locatedImage, ImageUtils.format(locatedImage, imageLink), resp.getOutputStream());
    LOG.debug("Writing image to response finished in " + (System.currentTimeMillis() - start) + " ms");
  }
}
