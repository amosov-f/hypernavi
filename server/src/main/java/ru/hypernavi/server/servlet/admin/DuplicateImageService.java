package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.FileUtils;
import ru.hypernavi.commons.Dimension;
import ru.hypernavi.commons.Image;
import ru.hypernavi.core.session.*;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;
import ru.hypernavi.core.webutil.ImageDimensioner;
import ru.hypernavi.core.webutil.ImageEditor;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.awt.ImageUtils;

/**
 * User: amosov-f
 * Date: 14.05.16
 * Time: 0:01
 */
@WebServlet(name = "image backup", value = "/admin/image/duplicate")
public final class DuplicateImageService extends AbstractHttpService {
    private static final Property<String> LINK = new Property<>("link");
    private static final Param<String> PARAM_LINK = new QueryParam.StringParam("link");

    private static final int THUMB_HEIGHT = 120;

    @Inject
    @Named("hypernavi.server.pathdata")
    private String dataPath;
    @Inject
    private ImageDimensioner dimensioner;
    @Inject
    @Named("hypernavi.server.host")
    private String host;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new ParamRequestReader(new RequestReader(req), LINK, PARAM_LINK);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String imageLink = session.demand(LINK);
        final BufferedImage image = ImageUtils.download(imageLink);
        final BufferedImage thumb = ImageEditor.INSTANCE.createThumb(image, THUMB_HEIGHT);
        final String path = new URL(imageLink).getPath();

        ImageIO.write(image, ImageUtils.format(image, imageLink), FileUtils.openOutputStream(Paths.get(dataPath, "img", path).toFile()));
        ImageIO.write(thumb, ImageUtils.format(thumb, imageLink), FileUtils.openOutputStream(Paths.get(dataPath, "thumb", path).toFile()));

        final String duplicateLink = "http://" + host + "/img" + path;
        final String thumbLink = "http://" + host + "/thumb" + path;
        final Dimension dimension = dimensioner.getDimension(image);
        final Image result = new Image(imageLink, thumbLink, dimension, new Image(duplicateLink, thumbLink, dimension));
        write(result, resp);
    }
}
