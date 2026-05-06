package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;
import com.styleauditor.model.Highlight;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class PatternTwoAdjectivesCheck implements TextCheck {
    private static final Pattern PATTERN = Pattern.compile(
            "\\b[а-яё]+(?:ый|ий|ой|ая|яя|ое|ее|ые|ие|ым|им|ой|ою|ыми|ими)\\b\\s*,\\s*\\b[а-яё]+(?:ый|ий|ой|ая|яя|ое|ее|ые|ие|ым|им|ой|ою|ыми|ими)\\b\\s+\\b[а-яё]{3,}\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS
    );

    private static final Set<String> PREPOSITIONS = Set.of(
            "в", "во", "за", "на", "по", "к", "от", "до", "из", "со", "при", "без",
            "под", "над", "про", "для", "у", "с", "о", "об", "обо", "перед",
            "между", "через", "около", "после", "вместо", "кроме"
    );

    @Override
    public CheckResult check(ChunkContext context) {
        CheckResult result = new CheckResult();
        List<Highlight> found = AnalyzerUtils.regexHighlights(context.text(), PATTERN, "Два определения перед существительным", "medium");

        // отфильтровываем совпадения, где первое слово стоит после предлога —
        // там это скорее существительное в косвенном падеже, а не прилагательное
        List<Highlight> filtered = found.stream()
                .filter(h -> !precededByPreposition(context.text(), h.start()))
                .toList();

        if (!filtered.isEmpty()) {
            String severity = filtered.size() >= 3 ? "high" : "medium";
            List<Highlight> highlights = filtered.stream()
                    .map(h -> new Highlight(h.start(), h.end(), h.type(), severity))
                    .toList();

            result.addHighlights(highlights)
                    .addFlag(
                            "Переизбыток определений",
                            severity,
                            highlights.size(),
                            "Пункт 11: рядом с существительным стоят два определения через запятую.",
                            9
                    );
        }

        return result;
    }

    private boolean precededByPreposition(String text, int pos) {
        int i = pos - 1;
        while (i >= 0 && text.charAt(i) == ' ') i--;
        int wordEnd = i + 1;
        while (i >= 0 && Character.isLetter(text.charAt(i))) i--;
        if (i + 1 == wordEnd) return false;
        String word = text.substring(i + 1, wordEnd).toLowerCase(Locale.ROOT);
        return PREPOSITIONS.contains(word);
    }
}

