package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;
import com.styleauditor.model.Highlight;

import java.util.ArrayList;
import java.util.List;

public class PatternShortPhraseSeriesCheck implements TextCheck {
    @Override
    public CheckResult check(ChunkContext context) {
        CheckResult result = new CheckResult();
        List<int[]> ranges = new ArrayList<>();

        int runStart = -1;
        int runCount = 0;

        for (int i = 0; i < context.sentences().size() && i < context.sentencePositions().size(); i++) {
            int[] pos = context.sentencePositions().get(i);
            int length = AnalyzerUtils.wordCount(context.sentences().get(i));

            if (length > 0 && length <= 4) {
                if (runCount == 0) {
                    runStart = pos[0];
                }

                runCount++;

                if (runCount == 3) {
                    ranges.add(new int[]{runStart, pos[1]});
                } else if (runCount > 3 && !ranges.isEmpty()) {
                    int[] last = ranges.remove(ranges.size() - 1);
                    ranges.add(new int[]{last[0], pos[1]});
                }
            } else {
                runStart = -1;
                runCount = 0;
            }
        }

        if (!ranges.isEmpty()) {
            String severity = ranges.size() >= 2 ? "high" : "medium";

            for (int[] range : ranges) {
                result.addHighlight(new Highlight(
                        range[0],
                        range[1],
                        "Серия коротких фраз",
                        severity
                ));
            }

            result.addFlag(
                    "Серии коротких фраз",
                    severity,
                    ranges.size(),
                    "Пункты 2 и 9: несколько коротких фраз подряд или похожая монтажная серия.",
                    11
            );

            result.addSuggestion("Разбавьте серию коротких фраз одним более длинным предложением или бытовой деталью.");
        }

        return result;
    }
}
