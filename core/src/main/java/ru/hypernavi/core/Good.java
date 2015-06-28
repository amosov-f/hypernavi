package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 23.11.14
 * Time: 0:55
 */
public final class Good {
    private final int id;
    @NotNull
    private final String name;
    @NotNull
    private final Category category;

    public Good(final int id, @NotNull String name, @NotNull Category category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Category getCategory() {
        return category;
    }

    @NotNull
    @Override
    public String toString() {
        return id + "\t" + name + "\t" + category.getName();
    }
}
