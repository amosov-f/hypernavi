package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.core.geoindex.DummyGeoIndex;
import ru.hypernavi.core.geoindex.GeoIndex;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 12.08.2015.
 */
public class HypermarketHolder {
    private static final Log LOG = LogFactory.getLog(HypermarketHolder.class);

    @NotNull
    private final GeoIndex<Hypermarket> markets = new DummyGeoIndex<>();

    private final DataLoader loader;
    private final String service;

    public int size() {
        return loader.getNames(service).length;
    }

    public void edit(final int id, @NotNull final Hypermarket hypermarket) {
        final JSONObject hypermarketJSON = hypermarket.toJSON();
        if (hypermarketJSON != null) {
            loader.save(service, "/" + id + ".json", hypermarketJSON.toString().getBytes(StandardCharsets.UTF_8));
            markets.add(hypermarket);
        }
    }

    @Inject
    public HypermarketHolder(@Named("hypernavi.server.servicemarkets") final String service, @NotNull final DataLoader loader) {
        this.loader = loader;
        this.service = service;

        for (final String path: loader.getNames(service)) {
            final byte[] data = loader.load(service, path);
            if (data != null) {
                try {
                    final Hypermarket market = Hypermarket.construct(new JSONObject(IOUtils.toString(data, "UTF-8")));

                    if (market != null) {
                        addHypermarket(market, path);
                        LOG.info("Hypermarket loaded from " + service + path);
                    } else {
                        LOG.warn("Can't load from " + service + path);
                    }
                } catch (JSONException | IOException e) {
                    LOG.warn(e.getMessage());
                }
            }
        }
    }

    public void addHypermarket(@NotNull final Hypermarket hyper, @NotNull final String name) {
        final JSONObject hypermarket = hyper.toJSON();
        if (hypermarket != null) {
            loader.save(service, name, hypermarket.toString().getBytes(StandardCharsets.UTF_8));
            markets.add(hyper);
        }
    }

    @NotNull
    public List<Hypermarket> getClosest(final GeoPoint position, final int k) {
        return markets.getNN(position, 0, k);
    }

    @NotNull
    public List<Hypermarket> getAll() {
        return markets.getAll();
    }

}
