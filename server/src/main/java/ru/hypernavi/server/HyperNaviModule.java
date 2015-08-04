package ru.hypernavi.server;

import org.jetbrains.annotations.NotNull;


import java.util.Properties;


import com.google.inject.AbstractModule;
import ru.hypernavi.core.classify.GoodsClassifier;
import ru.hypernavi.core.classify.RandomGoodsClassifier;
import ru.hypernavi.core.classify.TomallGoodsClassifier;

/**
 * User: amosov-f
 * Date: 05.08.15
 * Time: 0:19
 */
public final class HyperNaviModule extends AbstractModule {
    @NotNull
    private final Properties properties;

    public HyperNaviModule(@NotNull final Properties properties) {
        this.properties = properties;
    }

    @Override
    protected void configure() {
        final Class<? extends GoodsClassifier> goodsClassifierClass;
        if ("tomall".equals(properties.getProperty("hypernavi.category.model"))) {
            goodsClassifierClass = TomallGoodsClassifier.class;
        } else {
            goodsClassifierClass = RandomGoodsClassifier.class;
        }
        bind(GoodsClassifier.class).to(goodsClassifierClass);
    }
}
