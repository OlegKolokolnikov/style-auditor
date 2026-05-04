package com.styleauditor.checks;

import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;
import com.styleauditor.model.Highlight;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmoothGenericWordsCheck implements TextCheck {
    private static final Set<String> GENERAL_WORDS = Set.of(
            "странный", "странная", "странное", "странные", "странно",
            "красивый", "красивая", "красивое", "красивые",
            "приятный", "приятная", "приятное",
            "обычный", "обычная", "обычное", "обычные",
            "ужасный", "ужасная", "ужасное",
            "тяжёлый", "тяжёлая", "тяжёлое", "тяжёлые",
            "тёмный", "тёмная", "тёмное", "тёмные",
            "страшный", "страшная", "страшное",
            "необычный", "необычная", "необычное",
            "важный", "важная", "важное",
            "хороший", "хорошая", "хорошее",
            "плохой", "плохая", "плохое",
            "сильный", "сильная", "сильное"
    );

    @Override
    public CheckResult check(ChunkContext context) {
        List<Highlight> highlights = new ArrayList<>();
        Matcher matcher = Pattern.compile("[\\p{L}\\p{N}]+", Pattern.UNICODE_CHARACTER_CLASS).matcher(context.text());

        while (matcher.find()) {
            String word = matcher.group().toLowerCase(Locale.ROOT);

            if (GENERAL_WORDS.contains(word)) {
                highlights.add(new Highlight(matcher.start(), matcher.end(), "Общее оценочное слово", "low"));
            }
        }

        CheckResult result = new CheckResult();

        if (highlights.size() >= 5) {
            String severity = highlights.size() >= 9 ? "medium" : "low";
            List<Highlight> coloredHighlights = highlights.stream()
                    .map(highlight -> new Highlight(highlight.start(), highlight.end(), highlight.type(), severity))
                    .toList();

            result.addHighlights(coloredHighlights)
                    .addFlag(
                            "Общая оценочная лексика",
                            severity,
                            highlights.size(),
                            "Много общих оценочных слов. Часть можно заменить конкретикой.",
                            2
                    )
                    .addScore(Math.min(18, highlights.size() * 2.0))
                    .addSuggestion("Замените часть общих слов на конкретные признаки.");
        }

        return result;
    }
}

