package com.styleauditor.checks;

import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;
import com.styleauditor.model.Highlight;

import java.util.ArrayList;
import java.util.List;

public class PatternDashOutsideDialogueCheck implements TextCheck {
    @Override
    public CheckResult check(ChunkContext context) {
        CheckResult result = new CheckResult();
        String text = context.text();
        List<int[]> ranges = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch != '—') {
                continue;
            }

            if (isDialogueDash(text, i)) {
                continue;
            }

            ranges.add(new int[]{i, i + 1});
        }

        if (ranges.size() >= 4) {
            String severity = ranges.size() >= 8 ? "high" : "medium";

            for (int[] range : ranges) {
                result.addHighlight(new Highlight(
                        range[0],
                        range[1],
                        "Интонационное тире",
                        severity
                ));
            }

            result.addFlag(
                    "Интонационные тире",
                    severity,
                    ranges.size(),
                    "Пункт 12: частые тире внутри авторской речи. Диалоговые тире в начале строки игнорируются.",
                    12
            );

            result.addSuggestion("Проверьте тире в авторской речи: часть можно заменить запятой, точкой или убрать.");
        }

        return result;
    }

    private boolean isDialogueDash(String text, int dashIndex) {
        int i = dashIndex - 1;

        while (i >= 0) {
            char c = text.charAt(i);

            if (c == '\n' || c == '\r') {
                return true;
            }

            if (!Character.isWhitespace(c)) {
                return false;
            }

            i--;
        }

        return true;
    }
}
