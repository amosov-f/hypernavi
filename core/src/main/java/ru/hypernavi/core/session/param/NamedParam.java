package ru.hypernavi.core.session.param;

import org.jetbrains.annotations.NotNull;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by amosov-f on 24.10.15.
 */
public abstract class NamedParam<T> implements Param<T> {
    private static final Log LOG = LogFactory.getLog(NamedParam.class);

    @NotNull
    private final String name;

    protected NamedParam(@NotNull final String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }
}