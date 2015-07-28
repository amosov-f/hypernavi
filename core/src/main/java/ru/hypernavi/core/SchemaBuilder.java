package ru.hypernavi.core;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.IOUtils;
import org.omg.CORBA.portable.InputStream;
import ru.hypernavi.core.Hypernavi;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 28.07.2015.
 */
public class SchemaBuilder {
    public SchemaBuilder() {}
    public List<Hypernavi> read(final String path) {
        final List<Hypernavi> hypernavis = new ArrayList<>();
        final List<String> pathHypernavi = new ArrayList<>();
        final List<Double> longitude = new ArrayList<>();
        final List<Double> latitude = new ArrayList<>();

        try {
            final String initFile = IOUtils.toString(getClass().getResourceAsStream(path));
            final String[] tokens = initFile.split("[ \n]+");
            for (int i = 0; i < tokens.length; i += 3) {
                pathHypernavi.add(tokens[i]);
                latitude.add(Double.parseDouble(tokens[i + 1]));
                longitude.add(Double.parseDouble(tokens[i + 2]));
            }
        }
        catch (IOException e)
        {
        }

        try {
            for (int i = 0; i < pathHypernavi.size(); ++i)
            {
                hypernavis.add(new Hypernavi(new GeoPoint(latitude.get(i), longitude.get(i)),
                        ImageIO.read(getClass().getResourceAsStream("/" + pathHypernavi.get(i)))));
            }
        }
        catch (IOException e)
        {
        }
        return hypernavis;
    }
}
