package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    private final Map<String, Integer> md5id;
    @NotNull
    private MapStructure<Hypermarket> markets;

    protected HypermarketHolder() {
        final String[] pathHypermarket;
        try {
            final String initFile = IOUtils.toString(HypermarketHolder.class.getResourceAsStream("/hypermarketlist.txt"));
            pathHypermarket = initFile.split("[; \n]+");
        } catch (IOException e) {
            LOG.error("Some problems with init file");
            throw new RuntimeException(e.getMessage());
        }

        md5id = new TreeMap<>();

        final List<Hypermarket> listHyper = new ArrayList<>();
        for (int i = 0; i < pathHypermarket.length; ++i) {
            listHyper.add(new Hypermarket(pathHypermarket[i]));
            md5id.put("/" + listHyper.get(i).getMd5hash() + ".jpg", listHyper.get(i).getId());
        }

        final Hypermarket[] copyList = new Hypermarket[listHyper.size()];
        markets = new ArrayMapStructure<>(listHyper.toArray(copyList));
    }

    @NotNull
    public static HypermarketHolder getInstance() {
        if (instance == null) {
            instance = new HypermarketHolder();
        }
        return instance;
    }

    @Nullable
    public Hypermarket getClosest(final GeoPoint possition) {
        return markets.findClosest(possition);
    }
}
