package com.styleauditor.engine;

import com.styleauditor.model.Flag;
import com.styleauditor.model.Highlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckResult {
    private final List<Flag> flags = new ArrayList<>();
    private final List<Highlight> highlights = new ArrayList<>();
    private final List<String> suggestions = new ArrayList<>();
    private final Map<String, Double> metrics = new HashMap<>();

    private double score = 0;

    public List<Flag> flags() {
        return flags;
    }

    public List<Highlight> highlights() {
        return highlights;
    }

    public List<String> suggestions() {
        return suggestions;
    }

    public double score() {
        return score;
    }

    public Map<String, Double> metrics() {
        return metrics;
    }

    public double metric(String name) {
        return metrics.getOrDefault(name, 0.0);
    }

    public CheckResult addMetric(String name, double value) {
        if (name != null && !name.isBlank()) {
            metrics.put(name, value);
        }

        return this;
    }

    public CheckResult addFlag(String type, String severity, int count, String comment, double scoreWeight) {
        if (count > 0) {
            flags.add(new Flag(type, severity, count, comment));
            score += count * scoreWeight;
        }

        return this;
    }

    public CheckResult addHighlight(int start, int end, String type, String severity) {
        if (start >= 0 && end > start) {
            highlights.add(new Highlight(start, end, type, severity));
        }

        return this;
    }

    public CheckResult addHighlight(Highlight highlight) {
        if (highlight != null) {
            addHighlight(highlight.start(), highlight.end(), highlight.type(), highlight.severity());
        }

        return this;
    }

    public CheckResult addHighlights(List<Highlight> items) {
        if (items != null) {
            for (Highlight highlight : items) {
                addHighlight(highlight);
            }
        }

        return this;
    }

    public CheckResult addSuggestion(String suggestion) {
        if (suggestion != null && !suggestion.isBlank()) {
            suggestions.add(suggestion);
        }

        return this;
    }

    public CheckResult addScore(double value) {
        score += value;
        return this;
    }
}