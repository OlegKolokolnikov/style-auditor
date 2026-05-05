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

        // skip spaces (not newlines) to find the preceding non-space character
        while (i >= 0 && text.charAt(i) == ' ') {
            i--;
        }

        if (i < 0) return true;

        char prev = text.charAt(i);

        // dash at start of line — dialogue opener
        if (prev == '\n' || prev == '\r') return true;

        // dash after closing punctuation of direct speech — attribution dash
        // e.g. "Ты уверен? — спросил он." / "Уходи! — крикнул он." / "Хорошо. — Он кивнул."
        if (prev == '?' || prev == '!' || prev == '.' || prev == ',' || prev == '»') return true;

        return false;
    }
}
