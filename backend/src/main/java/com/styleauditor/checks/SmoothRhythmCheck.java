package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;

public class SmoothRhythmCheck implements TextCheck {
    @Override
    public CheckResult check(ChunkContext context) {
        CheckResult result = new CheckResult();

        if (context.sentenceLengths().size() < 4) {
            return result;
        }

        double avg = context.sentenceLengths().stream().mapToInt(value -> value).average().orElse(0);
        double std = AnalyzerUtils.std(context.sentenceLengths(), avg);

        if (std > 0 && std < 3.0) {
            result.addHighlight(
                            0,
                            context.text().length(),
                            "Одинаковая длина фраз по всему чанку",
                            "medium"
                    )
                    .addFlag(
                            "Монотонный ритм предложений",
                            "medium",
                            1,
                            "Предложения в чанке близки по длине. Это глобальное свойство чанка, поэтому подсвечен весь фрагмент.",
                            12
                    )
                    .addSuggestion("Сломайте ритм: объедините пару соседних предложений или оставьте одно очень короткое.");
        }

        return result;
    }
}

