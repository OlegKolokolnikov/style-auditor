package com.styleauditor.model;

import java.util.List;

public record ChunkResult(
        int index,
        String text,
        int chars,
        int sentenceCount,
        int avgSentenceLength,
        int sentenceLengthStd,
        int lexicalPredictability,
        int rhythmMonotony,
        int riskScore,
        String label,
        List<Flag> flags,
        List<Highlight> highlights,
        List<String> suggestions
) {
}

