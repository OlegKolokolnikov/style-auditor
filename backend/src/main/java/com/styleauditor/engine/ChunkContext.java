package com.styleauditor.engine;

import java.util.List;

public record ChunkContext(
        int index,
        String text,
        List<String> sentences,
        List<int[]> sentencePositions,
        List<Integer> sentenceLengths
) {
}

