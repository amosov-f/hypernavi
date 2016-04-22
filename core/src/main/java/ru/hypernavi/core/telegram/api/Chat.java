package ru.hypernavi.core.telegram.api;

/**
 * Created by amosov-f on 18.10.15.
 */
public final class Chat {
    private final int id;

    public Chat(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
