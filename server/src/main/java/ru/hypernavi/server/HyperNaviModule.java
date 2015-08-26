package ru.hypernavi.server;

import org.jetbrains.annotations.NotNull;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import ru.hypernavi.commons.Platform;
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
        bind(Platform.class).toInstance(Platform.parse(config.getProperty("hypernavi.server.platform")));
        bind(String.class).annotatedWith(Names.named("hypernavi.web.bundle")).toInstance(config.getProperty("hypernavi.web.bundle"));

        bindGoodsClassifier();

    }

    private void bindGoodsClassifier() {
        final Class<? extends GoodsClassifier> goodsClassifierClass;
        if ("tomall".equals(config.getProperty("hypernavi.category.model"))) {
            goodsClassifierClass = TomallGoodsClassifier.class;
        } else {
            goodsClassifierClass = RandomGoodsClassifier.class;
        }
        bind(GoodsClassifier.class).to(goodsClassifierClass);
    }
}
