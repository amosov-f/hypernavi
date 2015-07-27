package ru.hypernavi.core.classify;

import org.jetbrains.annotations.NotNull;

import java.util.Random;


import ru.hypernavi.core.Category;

/**
 * User: amosov-f
 * Date: 29.06.15
 * Time: 1:02
 */
public final class RandomGoodsClassifier implements GoodsClassifier {
    @NotNull
    @Override
    public Category classify(@NotNull final String text) {
        return Category.values()[new Random().nextInt(Category.values().length)];
    }
}
