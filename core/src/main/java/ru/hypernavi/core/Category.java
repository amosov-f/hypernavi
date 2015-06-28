package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * User: amosov-f
 * Date: 23.11.14
 * Time: 16:24
 */
public enum Category {
    DRINKS("Соки и напитки"),
    SUGAR("Сахар, крупы, кофе, чай, чипсы"),
    COSMETICS("Косметика и парфюмерия"),
    SEAFOOD("Рыба и морепродукты"),
    TOOLS("Инструменты, для дачи, автотовары"),
    ALCOHOL("Пиво, алкоголь и сигареты"),
    FRUIT_AND_VEGETABLES("Фрукты и овощи"),
    COOKERY("Майонез, масло, консервы, соусы, кулинария"),
    DAIRIES("Сыры и молочные продукты"),
    HOUSEHOLD_CHEMICALS("Бытовая химия"),
    HOUSEHOLD_APPLIANCES("Бытовая техника и электроника"),
    SPORT("Спортивные товары"),
    OFFICE("Канцелярия"),
    BABY("Товары для детей"),
    ANIMALS("Товары для животных"),
    SEMIS("Полуфабрикаты и заморозка"),
    HOUSEHOLDS("Текстиль и товары для дома"),
    BAKERY("Сладкое и хлебобулочные изделия"),
    MEAT("Мясо и колбасные изделия"),
    WEAR("Одежда и обувь");

    @NotNull
    private final String name;

    Category(@NotNull final String name) {
        this.name = name;
    }

    @Nullable
    public static Category parse(@NotNull final String name) {
        return Arrays.stream(values()).filter(category -> category.getName().equals(name)).findFirst().orElse(null);
    }

    @NotNull
    public String getName() {
        return name;
    }
}
