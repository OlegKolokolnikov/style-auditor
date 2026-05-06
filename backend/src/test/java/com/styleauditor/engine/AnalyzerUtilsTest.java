package com.styleauditor.engine;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyzerUtilsTest {

    // ── parseSentences ──────────────────────────────────────────────────────

    @Test
    void parseSentences_sentencesAndPositionsAlwaysSameSize() {
        String text = "Первое предложение. Второе предложение! Третье предложение?";
        var parsed = AnalyzerUtils.parseSentences(text);
        assertThat(parsed.sentences()).hasSameSizeAs(parsed.positions());
    }

    @Test
    void parseSentences_positionsPointToCorrectText() {
        String text = "Привет мир. Как дела?";
        var parsed = AnalyzerUtils.parseSentences(text);
        for (int i = 0; i < parsed.sentences().size(); i++) {
            int[] pos = parsed.positions().get(i);
            String slice = text.substring(pos[0], pos[1]);
            assertThat(slice.strip()).isEqualTo(parsed.sentences().get(i));
        }
    }

    @Test
    void parseSentences_emptyTextReturnsEmpty() {
        var parsed = AnalyzerUtils.parseSentences("");
        assertThat(parsed.sentences()).isEmpty();
        assertThat(parsed.positions()).isEmpty();
    }

    @Test
    void parseSentences_blankSentencesSkipped() {
        String text = "Нормальное предложение.   \n\n  Второе.";
        var parsed = AnalyzerUtils.parseSentences(text);
        assertThat(parsed.sentences()).hasSize(2);
        assertThat(parsed.sentences()).hasSameSizeAs(parsed.positions());
    }

    // ── splitChunks ─────────────────────────────────────────────────────────

    @Test
    void splitChunks_shortTextIsOneChunk() {
        String text = "Короткий текст без разбивки.";
        assertThat(AnalyzerUtils.splitChunks(text, 1100)).hasSize(1);
    }

    @Test
    void splitChunks_noChunkIsBlank() {
        String text = "А".repeat(5000);
        List<String> chunks = AnalyzerUtils.splitChunks(text, 1100);
        assertThat(chunks).allMatch(c -> !c.isBlank());
    }

    @Test
    void splitChunks_allTextPreserved() {
        String text = "Первое предложение. Второе предложение. Третье предложение.";
        List<String> chunks = AnalyzerUtils.splitChunks(text, 20);
        int total = chunks.stream().mapToInt(String::length).sum();
        // допускаем потерю пробелов по краям чанков (strip)
        assertThat(total).isGreaterThanOrEqualTo(text.strip().length() - chunks.size() * 2);
    }

    @Test
    void splitChunks_nullTextReturnsSingleEmptyChunk() {
        // null и пустая строка → один пустой чанк (поведение по дизайну)
        assertThat(AnalyzerUtils.splitChunks(null, 1100)).containsExactly("");
    }

    // ── mergeOverlappingHighlights ───────────────────────────────────────────

    @Test
    void mergeHighlights_nonOverlappingStaysSeparate() {
        var h1 = new com.styleauditor.model.Highlight(0, 5, "A", "low");
        var h2 = new com.styleauditor.model.Highlight(10, 15, "B", "low");
        var merged = AnalyzerUtils.mergeOverlappingHighlights(List.of(h1, h2));
        assertThat(merged).hasSize(2);
    }

    @Test
    void mergeHighlights_overlappingMergesIntoOne() {
        var h1 = new com.styleauditor.model.Highlight(0, 10, "A", "low");
        var h2 = new com.styleauditor.model.Highlight(5, 15, "B", "low");
        var merged = AnalyzerUtils.mergeOverlappingHighlights(List.of(h1, h2));
        assertThat(merged).hasSize(1);
        assertThat(merged.get(0).start()).isEqualTo(0);
        assertThat(merged.get(0).end()).isEqualTo(15);
        assertThat(merged.get(0).severity()).isEqualTo("high");
    }

    @Test
    void mergeHighlights_threeOverlappingAllTypesPresent() {
        var h1 = new com.styleauditor.model.Highlight(0, 10, "A", "low");
        var h2 = new com.styleauditor.model.Highlight(5, 15, "B", "low");
        var h3 = new com.styleauditor.model.Highlight(8, 20, "C", "low");
        var merged = AnalyzerUtils.mergeOverlappingHighlights(List.of(h1, h2, h3));
        assertThat(merged).hasSize(1);
        assertThat(merged.get(0).type()).contains("A").contains("B").contains("C");
    }

    @Test
    void mergeHighlights_emptyListReturnsEmpty() {
        assertThat(AnalyzerUtils.mergeOverlappingHighlights(List.of())).isEmpty();
    }

    // ── clamp / round ────────────────────────────────────────────────────────

    @Test
    void clamp_belowMinReturnsMin() {
        assertThat(AnalyzerUtils.clamp(-5, 0, 100)).isEqualTo(0.0);
    }

    @Test
    void clamp_aboveMaxReturnsMax() {
        assertThat(AnalyzerUtils.clamp(150, 0, 100)).isEqualTo(100.0);
    }

    @Test
    void clamp_withinRangeUnchanged() {
        assertThat(AnalyzerUtils.clamp(42, 0, 100)).isEqualTo(42.0);
    }
}
