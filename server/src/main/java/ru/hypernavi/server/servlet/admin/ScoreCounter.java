package ru.hypernavi.server.servlet.admin;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.Site;
import ru.hypernavi.commons.hint.Hint;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.util.MoreCollections;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amosov-f on 23.05.16.
 */
public final class ScoreCounter {
    private final int budget;

    @Inject
    public ScoreCounter(@Named("hypernavi.telegram.data.budget") final int budget) {
        this.budget = budget;
    }

    @NotNull
    public Map<Integer, Score> count(@NotNull final Site... sites) {
        final Map<Integer, Score> author2score = new HashMap<>();
        for (final Site site : sites) {
            for (final Hint hint : site.getHints()) {
                final Integer authorUid = hint.getAuthorUid();
                if (authorUid == null) {
                    continue;
                }
                final Score score = author2score.computeIfAbsent(authorUid, uid -> new Score(uid, budget));
                score.incrementHints();
                if (hint instanceof Plan) {
                    score.incrementPlans();
                    if (((Plan) hint).getPoints().length > 0) {
                        score.incrementMarkedPlans();
                    }
                }
            }
        }
        final int totalMarkedPlans = author2score.values().stream().mapToInt(Score::getMarkedPlans).sum();
        author2score.values().forEach(score -> score.setTotalMarkedPlans(totalMarkedPlans));
        return MoreCollections.sortByValue(author2score, Score.comparator());
    }
}
