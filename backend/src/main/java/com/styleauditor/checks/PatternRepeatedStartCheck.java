package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;
import com.styleauditor.model.Highlight;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PatternRepeatedStartCheck implements TextCheck {
    @Override
    public CheckResult check(ChunkContext context) {
        CheckResult result = new CheckResult();
        List<Highlight> highlights = new ArrayList<>();

        String previous = "";
        int previousStart = -1;

        for (int i = 0; i < context.sentences().size() && i < context.sentencePositions().size(); i++) {
            String first = AnalyzerUtils.firstWord(context.sentences().get(i)).toLowerCase(Locale.ROOT);
            int[] pos = context.sentencePositions().get(i);

            if (first.isEmpty()) {
                continue;
            }

            if (first.equals(previous)) {
                highlights.add(new Highlight(
                        previousStart >= 0 ? previousStart : pos[0],
                        pos[1],
                        "Повтор начала предложений: «" + first + "»",
                        "low"
                ));
            }

            previous = first;
            previousStart = pos[0];
        }

        if (!highlights.isEmpty()) {
            String severity = highlights.size() >= 2 ? "medium" : "low";
            List<Highlight> coloredHighlights = highlights.stream()
                    .map(highlight -> new Highlight(highlight.start(), highlight.end(), highlight.type(), severity))
                    .toList();

            result.addHighlights(coloredHighlights)
                    .addFlag(
                            "Повтор начала предложений",
                            severity,
                            highlights.size(),
                            "Пункт 9: соседние предложения начинаются одинаково или почти одинаково.",
                            6
                    );
        }

        return result;
    }
}

