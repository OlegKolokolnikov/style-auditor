package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;
import com.styleauditor.model.Highlight;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PatternEmotionLabelCheck implements TextCheck {
    private static final Set<String> EMOTIONS = Set.of(
            "боль", "гнев", "отчаяние", "страх", "ужас",
            "пустота", "тоска", "ярость", "надежда", "покой"
    );

    @Override
    public CheckResult check(ChunkContext context) {
        CheckResult result = new CheckResult();
        List<Highlight> highlights = new ArrayList<>();

        for (int i = 0; i < context.sentences().size() && i < context.sentencePositions().size(); i++) {
            String normalized = context.sentences().get(i)
                    .toLowerCase(Locale.ROOT)
                    .replaceAll("[^\\p{L}]", "")
                    .trim();

            if (EMOTIONS.contains(normalized)) {
                int[] pos = context.sentencePositions().get(i);
                highlights.add(new Highlight(pos[0], pos[1], "Эмоция-ярлык", "high"));
            }
        }

        if (!highlights.isEmpty()) {
            result.addHighlights(highlights)
                    .addFlag(
                            "Эмоции-ярлыки через точку",
                            "high",
                            highlights.size(),
                            "Пункт 5: абстрактные эмоции отдельными короткими предложениями.",
                            12
                    )
                    .addSuggestion("Часть эмоций-ярлыков можно заменить жестом, действием или предметной деталью.");
        }

        return result;
    }
}

