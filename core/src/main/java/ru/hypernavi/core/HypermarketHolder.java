package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 12.08.2015.
 */
public class HypermarketHolder {
    private static final Log LOG = LogFactory.getLog(HypermarketHolder.class);
    private static HypermarketHolder instance = null;

    protected HypermarketHolder() {
        String[] pathHypermarket = null;
        try {
            final String initFile = IOUtils.toString(HypermarketHolder.class.getResourceAsStream("/hypermarketlist.txt"));
            pathHypermarket = initFile.split("[; \n]+");
        } catch (IOException e) {
            LOG.warn("Some problems with init file");
        }

        md5id = new TreeMap<>();
        final List<Hypermarket> listHyper = new ArrayList<>();
        for (int i = 0; i < pathHypermarket.length; ++i) {
            listHyper.add(new Hypermarket(pathHypermarket[i]));
            md5id.put("/" + ImageHash.generate(listHyper.get(i).getSchema()) + ".jpg", listHyper.get(i).getID());
        }
        final Hypermarket[] copyList = new Hypermarket[listHyper.size()];
        markets = new ArrayMapStructure<>(listHyper.toArray(copyList));
    }

    public static HypermarketHolder getInstance() {
        if (instance == null) {
            instance = new HypermarketHolder();
        }
        return instance;
    }


    @NotNull
    private final Map<String, Integer> md5id;
    @NotNull
    private MapStructure<Hypermarket> markets;

    public Hypermarket getMD5(final String ImadeHash) {
        return instance.markets.get(instance.md5id.get(ImadeHash));
    }

    public Hypermarket getClosest(final GeoPoint possition) {
        return (Hypermarket)markets.findClosest(possition);
    }
}
