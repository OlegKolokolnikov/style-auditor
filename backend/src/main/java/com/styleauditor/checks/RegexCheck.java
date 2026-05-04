package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.CheckResult;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.TextCheck;
import com.styleauditor.model.Highlight;

import java.util.List;
import java.util.regex.Pattern;

public abstract class RegexCheck implements TextCheck {
    protected abstract String type();

    protected abstract String severity();

    protected abstract Pattern pattern();

    protected abstract String comment();

    protected abstract double weight();

    protected boolean highlight() {
        return true;
    }

    @Override
    public CheckResult check(ChunkContext context) {
        List<Highlight> found = AnalyzerUtils.regexHighlights(context.text(), pattern(), type(), severity());
        CheckResult result = new CheckResult();

        if (!found.isEmpty()) {
            if (highlight()) {
                result.addHighlights(found);
            }

            result.addFlag(type(), severity(), found.size(), comment(), weight());

            if (!highlight()) {
                result.addSuggestion("Флаг «" + type() + "» считается по всему чанку и не подсвечивается поштучно.");
            }
        }

        return result;
    }
}

