package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;
import com.styleauditor.model.Highlight;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatternMirrorSentenceCheck implements TextCheck {
    @Override
    public CheckResult check(ChunkContext context) {
        CheckResult result = new CheckResult();
        List<Highlight> highlights = new ArrayList<>();

        for (int i = 0; i < context.sentences().size() - 1 && i < context.sentencePositions().size() - 1; i++) {
            Set<String> first = AnalyzerUtils.significantWords(context.sentences().get(i));
            Set<String> second = AnalyzerUtils.significantWords(context.sentences().get(i + 1));

            if (first.size() >= 2 && second.size() >= 2) {
                Set<String> copy = new HashSet<>(first);
                copy.retainAll(second);

                if (copy.size() >= 2
                        && context.sentences().get(i).length() < 90
                        && context.sentences().get(i + 1).length() < 90) {
                    int[] start = context.sentencePositions().get(i);
                    int[] end = context.sentencePositions().get(i + 1);

                    highlights.add(new Highlight(
                            start[0],
                            end[1],
                            "Зеркальная пара предложений",
                            "medium"
                    ));
                }
            }
        }

        if (!highlights.isEmpty()) {
            result.addHighlights(highlights)
                    .addFlag(
                            "Зеркальные предложения",
                            "medium",
                            highlights.size(),
                            "Пункт 7: соседние фразы с перестановкой похожих слов.",
                            10
                    );
        }

        return result;
    }
}

