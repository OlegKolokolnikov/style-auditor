package com.styleauditor.model;

public record Highlight(
        int start,
        int end,
        String type,
        String severity
) {
}

