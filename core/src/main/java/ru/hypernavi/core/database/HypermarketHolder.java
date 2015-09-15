package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 12.08.2015.
 */
public class HypermarketHolder {
    private static final Log LOG = LogFactory.getLog(HypermarketHolder.class);
    @NotNull
    private final MapStructure<Hypermarket> markets;

    private final DataLoader loader;
    private final String service;

    public int size() {
        return loader.getNames(service).length;
    }

    @Inject
    public HypermarketHolder(@Named("hypernavi.server.servicemarkets") final String service, @NotNull final DataLoader loader) {
        this.loader = loader;
        this.service = service;

        final String[] paths = loader.getNames(service);

        final List<Hypermarket> listHyper = new ArrayList<>();
        for (int i = 0; i < paths.length; ++i) {
            final byte[] data = loader.load(service, paths[i]);
            if (data != null) {
                Hypermarket market = null;
                try {
                    final String json = IOUtils.toString(data, "UTF-8");
                    market = Hypermarket.construct(new JSONObject(json));
                    if (market != null)
                        LOG.info("Hypermarket loaded from " + service + paths[i]);
                } catch (JSONException | IOException e) {
                    LOG.warn(e.getMessage());
                }
                if (market != null) {
                    listHyper.add(market);
                    //LOG.info(paths[i] + " : " +  market.toJSON().toString());
                    loader.save(service, paths[i], market.toJSON().toString().getBytes(StandardCharsets.UTF_8));
                }
            }


        }

        final Hypermarket[] copyList = new Hypermarket[listHyper.size()];
        markets = new ArrayMapStructure<>(listHyper.toArray(copyList));
    }

    public void addHypermarket(@NotNull final Hypermarket hyper, @NotNull final String name) {
        loader.save(service, name, hyper.toJSON().toString().getBytes(StandardCharsets.UTF_8));
        markets.add(hyper);
    }

    @NotNull
    public List<Hypermarket> getClosest(final GeoPoint possition, final int k) {
        return markets.find(possition, k);
    }
}
