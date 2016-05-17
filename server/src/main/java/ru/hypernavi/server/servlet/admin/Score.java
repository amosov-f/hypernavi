package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Created by amosov-f on 18.05.16.
 */
public final class Score {
    private int markedSchemes;
    private int plans;
    private int hints;

    public int getMarkedPlans() {
        return markedSchemes;
    }

    public void incrementMarkedPlans() {
        this.markedSchemes++;
    }

    public int getPlans() {
        return plans;
    }

    public void incrementPlans() {
        this.plans++;
    }

    public int getHints() {
        return hints;
    }

    public void incrementHints() {
        this.hints++;
    }

    @NotNull
    public static Comparator<Score> comparator() {
        return Comparator.comparing(Score::getMarkedPlans).reversed();
    }
}
