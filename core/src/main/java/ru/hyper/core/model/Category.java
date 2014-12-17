package ru.hyper.core.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private Category(@NotNull final String name) {
        this.name = name;
    }

    @Nullable
    public static Category parse(@NotNull final String name) {
        for (final Category category : values()) {
            if (name.equals(category.getName())) {
                return category;
            }
        }
        return null;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        for (final Category category : Category.values()) {
            System.out.println(category.getName());
        }
    }
}
