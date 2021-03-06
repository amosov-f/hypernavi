package ru.hypernavi.server.servlet.admin;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.Dimension;
import ru.hypernavi.commons.Image;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.webutil.ImageDimensioner;
import ru.hypernavi.core.webutil.ImageEditor;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.MoreIOUtils;
import ru.hypernavi.util.awt.ImageUtils;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * User: amosov-f
 * Date: 14.05.16
 * Time: 0:01
 */
@WebServlet(name = "image backup", value = "/admin/image/duplicate")
public final class DuplicateImageService extends AbstractHttpService {
    private static final int THUMB_HEIGHT = 120;

    @Inject
    @Named("hypernavi.server.pathdata")
    private String dataPath;
    @Inject
    private ImageDimensioner dimensioner;
    @Inject
    @Named("hypernavi.server.host")
    private String host;

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String imageLink = session.demand(Property.LINK);
        final BufferedImage image = ImageUtils.download(imageLink);
        final BufferedImage thumb = ImageEditor.INSTANCE.createThumb(image, THUMB_HEIGHT);

        final String path = new URL(imageLink).getPath();
        final String imgPath = path.startsWith("/img") ? path : "/img" + path;
        final String thumbPath = path.startsWith("/thumb") ? path : "/thumb" + path;
        ImageIO.write(image, ImageUtils.format(image, imageLink), MoreIOUtils.openOutputStream(dataPath, imgPath));
        ImageIO.write(thumb, ImageUtils.format(thumb, imageLink), MoreIOUtils.openOutputStream(dataPath, thumbPath));

        final String duplicateLink = "http://" + host + imgPath;
        final String thumbLink = "http://" + host + thumbPath;
        final Dimension dimension = dimensioner.getDimension(image);
        final Image result = new Image(imageLink, thumbLink, dimension, new Image(duplicateLink, thumbLink, dimension));
        write(result, resp);
    }
}
