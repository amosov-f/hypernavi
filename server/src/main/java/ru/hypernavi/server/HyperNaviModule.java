package ru.hypernavi.server;

import org.jetbrains.annotations.NotNull;


import com.google.inject.AbstractModule;
import ru.hypernavi.core.classify.GoodsClassifier;
import ru.hypernavi.core.classify.RandomGoodsClassifier;
import ru.hypernavi.core.classify.TomallGoodsClassifier;
import ru.hypernavi.util.Config;

/**
 * User: amosov-f
 * Date: 05.08.15
 * Time: 0:19
 */
public final class HyperNaviModule extends AbstractModule {
    @NotNull
    private final Config config;

    public HyperNaviModule(@NotNull final Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        final Class<? extends GoodsClassifier> goodsClassifierClass;
        if ("tomall".equals(config.getProperty("hypernavi.category.model"))) {
            goodsClassifierClass = TomallGoodsClassifier.class;
        } else {
            goodsClassifierClass = RandomGoodsClassifier.class;
        }
        bind(GoodsClassifier.class).to(goodsClassifierClass);
    }
}
