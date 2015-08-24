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
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 12.08.2015.
 */
public class HypermarketHolder {
    private static final Log LOG = LogFactory.getLog(HypermarketHolder.class);
    private static HypermarketHolder instance = null;
    //@NotNull
    //private final Map<String, Integer> md5id;
    @NotNull
    private final MapStructure<Hypermarket> markets;

    protected HypermarketHolder() {
        final String[] pathHypermarket;
        try {
            final String initFile = IOUtils.toString(HypermarketHolder.class.getResourceAsStream("/hypermarketlist.txt"));
            pathHypermarket = initFile.split("[; \n]+");
        } catch (IOException e) {
            LOG.error("Some problems with init file");
            throw new RuntimeException(e.getMessage());
        }

        // md5id = new TreeMap<>();

        final List<Hypermarket> listHyper = new ArrayList<>();
        for (int i = 0; i < pathHypermarket.length; ++i) {
            final HypermarketReader reader = new HypermarketReader(pathHypermarket[i]);
            listHyper.add(reader.construct());
            //  md5id.put(listHyper.get(i).getUrl(), listHyper.get(i).getId());
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

    public void addHypermarket(@NotNull final Hypermarket hyper) {
        //md5id.put(hyper.getUrl(), hyper.getId());
        markets.add(hyper);
    }

    @Nullable
    public List<Hypermarket> getClosest(final GeoPoint possition, final int k) {
        return markets.find(possition, k);
    }
}
