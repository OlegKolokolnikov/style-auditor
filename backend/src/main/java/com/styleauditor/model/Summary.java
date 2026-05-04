package com.styleauditor.model;

import java.util.List;

public record Summary(
        int chunkCount,
        int suspiciousChunks,
        int strongSuspiciousChunks,
        int averageRisk,
        int averageSentenceLength,
        int averageSentenceStd,
        List<ProblemStat> commonProblems
) {
}

