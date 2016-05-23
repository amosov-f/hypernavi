package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Created by amosov-f on 18.05.16.
 */
public final class Score {
    private final int authorUid;
    private final int budget;

    private int markedPlans;
    private int plans;
    private int hints;

    private int totalMarkedPlans;

    public Score(final int authorUid, final int budget) {
        this.authorUid = authorUid;
        this.budget = budget;
    }

    public int getAuthorUid() {
        return authorUid;
    }

    public int getMarkedPlans() {
        return markedPlans;
    }

    public void incrementMarkedPlans() {
        this.markedPlans++;
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

    public void setTotalMarkedPlans(final int totalMarkedPlans) {
        this.totalMarkedPlans = totalMarkedPlans;
    }

    public int getProfit() {
        return profit(budget, markedPlans, totalMarkedPlans);
    }

    public int getNextCost() {
        return profit(budget, markedPlans + 1, totalMarkedPlans + 1) - getProfit();
    }

    private static int profit(final int budget, final int markedPlans, final int totalMarkedPlans) {
        return budget * markedPlans / totalMarkedPlans;
    }

    @NotNull
    public static Comparator<Score> comparator() {
        return Comparator.comparing(Score::getMarkedPlans).reversed();
    }
}
