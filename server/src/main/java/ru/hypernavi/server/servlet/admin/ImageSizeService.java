package ru.hypernavi.server.servlet.admin;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.Dimension;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.webutil.ImageDimensioner;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.awt.ImageUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by amosov-f on 23.10.16
 */
@WebServlet(name = "image size", value = "/admin/image/size")
public final class ImageSizeService extends AbstractHttpService {
    @Inject
    private ImageDimensioner dimensioner;

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String imageLink = session.demand(Property.LINK);
        final BufferedImage image = ImageUtils.download(imageLink);
        final Dimension dimension = dimensioner.getDimension(image);
        final int fileSize = ImageUtils.toByteArray(image).length;
        write(new ImageSize(dimension, fileSize), resp);
    }

    public static final class ImageSize {
        @NotNull
        public final Dimension dimension;
        public final int fileSize;

        public ImageSize(@NotNull final Dimension dimension, final int fileSize) {
            this.dimension = dimension;
            this.fileSize = fileSize;
        }
    }
}
