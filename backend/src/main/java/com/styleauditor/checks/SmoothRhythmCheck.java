package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;

import java.util.ArrayList;
import java.util.List;

public class SmoothRhythmCheck implements TextCheck {
    @Override
    public CheckResult check(ChunkContext context) {
        CheckResult result = new CheckResult();

        // считаем только нарративные предложения — диалоговые реплики (начинаются с —)
        // имеют структурно короткий и равномерный ритм по природе жанра, не по вине AI
        List<Integer> narrativeLengths = new ArrayList<>();
        for (int i = 0; i < context.sentences().size(); i++) {
            if (!context.sentences().get(i).startsWith("—")) {
                narrativeLengths.add(context.sentenceLengths().get(i));
            }
        }

        if (narrativeLengths.size() < 4) {
            return result;
        }

        double avg = narrativeLengths.stream().mapToInt(value -> value).average().orElse(0);
        double std = AnalyzerUtils.std(narrativeLengths, avg);

        if (std < 3.0) {
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

