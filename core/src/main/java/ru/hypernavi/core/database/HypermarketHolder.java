package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.HypermarketSerializer;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 12.08.2015.
 */
public class HypermarketHolder {
    private static final Log LOG = LogFactory.getLog(HypermarketHolder.class);
    @NotNull
    private final MapStructure<Hypermarket> markets;

    private final DataLoader loader;

    public int size() {
        return loader.getPaths().length;
    }

    public HypermarketHolder() {
        this.loader = new FileDataLoader("./data/hypermarkets/");
        final String[] paths = loader.getPaths();

//        Arrays.stream(paths).forEach(LOG::info);

        final List<Hypermarket> listHyper = new ArrayList<>();
        for (int i = 0; i < paths.length; ++i) {
            final byte[] data = loader.load(paths[i]);
            if (data != null) {
                Hypermarket market = null;
                try {
                    final String json = IOUtils.toString(data, "UTF-8");
                    market = HypermarketSerializer.deserialize(new JSONObject(json));
                    LOG.info("Hypermarket loaded from " + paths[i]);
                } catch (JSONException | IOException e) {
                    LOG.warn(e.getMessage());
                }
                if (market != null) {
                    listHyper.add(market);
                }
            }
        }

        final Hypermarket[] copyList = new Hypermarket[listHyper.size()];
        markets = new ArrayMapStructure<>(listHyper.toArray(copyList));
    }

    public void addHypermarket(@NotNull final Hypermarket hyper, @NotNull final String name) {
        loader.save("data/hypermarkets/" + name, HypermarketSerializer.serialize(hyper).toString().getBytes(StandardCharsets.UTF_8));
        markets.add(hyper);
    }

    @NotNull
    public List<Hypermarket> getClosest(final GeoPoint possition, final int k) {
        return markets.find(possition, k);
    }
}
