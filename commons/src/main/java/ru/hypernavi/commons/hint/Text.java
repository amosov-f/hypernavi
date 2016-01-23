package ru.hypernavi.commons.hint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by amosov-f on 17.01.16.
 */
public class Text extends Hint {
    protected Text(@Nullable final String description) {
        super(description);
    }

    @NotNull
    @Override
    public Type getType() {
        return Type.TEXT;
    }
}
