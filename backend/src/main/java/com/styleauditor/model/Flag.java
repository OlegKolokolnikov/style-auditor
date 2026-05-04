package com.styleauditor.model;

public record Flag(
        String type,
        String severity,
        int count,
        String comment
) {
}

