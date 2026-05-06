package com.styleauditor.engine;

import com.styleauditor.model.AnalysisResult;
import com.styleauditor.model.ChunkResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnalysisServiceTest {

    private final AnalysisService service = new AnalysisService();

    @Test
    void riskScoreNeverNegative() {
        String text = "Он вдруг понял, что страх и тревога — это просто слова. "
                + "Он почувствовал покой и надежду. Пустота исчезла. "
                + "Он словно увидел свет впереди. Будто всё наладится.";
        AnalysisResult result = service.analyze(text);
        assertThat(result.chunks()).allSatisfy(c ->
                assertThat(c.riskScore()).isGreaterThanOrEqualTo(0));
    }

    @Test
    void riskScoreNeverExceedsHundred() {
        // текст с максимальным количеством паттернов
        String text = "Это не страх — это нечто большее, чем страх. "
                + "Это не просто тревога, а нечто большее. "
                + "Он понял, почувствовал, увидел. Боль. Тоска. Ужас. "
                + "Он шёл — и думал. Ночь была тихой — слишком тихой. "
                + "Словно призрак. Будто тень. Вдруг — тишина.";
        AnalysisResult result = service.analyze(text);
        assertThat(result.chunks()).allSatisfy(c ->
                assertThat(c.riskScore()).isLessThanOrEqualTo(100));
    }

    @Test
    void cleanTextHasLowScore() {
        String text = "Дима нашёл ключ под ковриком у двери. Замок заскрипел и поддался. "
                + "В комнате пахло скипидаром и старыми газетами. "
                + "На столе лежал будильник с треснутым стеклом. "
                + "Форточка была открыта, занавеска вздрагивала. "
                + "Он огляделся и прошёл к окну. За стеклом шёл дождь.";
        AnalysisResult result = service.analyze(text);
        assertThat(result.chunks()).allSatisfy(c ->
                assertThat(c.riskScore()).isLessThan(50));
    }

    @Test
    void emptyChunksNotProduced() {
        String text = "Нормальный текст с несколькими предложениями. "
                + "Второе предложение тут. Третье предложение здесь. "
                + "Четвёртое. Пятое. Шестое и последнее предложение в тексте.";
        AnalysisResult result = service.analyze(text);
        assertThat(result.chunks()).isNotEmpty();
        assertThat(result.chunks()).allSatisfy(c ->
                assertThat(c.text()).isNotBlank());
    }

    @Test
    void highlightPositionsWithinChunkBounds() {
        String text = "Это не страх, а нечто большее, чем просто тревога и боль. "
                + "Он вдруг почувствовал пустоту. Словно всё исчезло разом. "
                + "Это было нечто большее, чем он мог объяснить словами.";
        AnalysisResult result = service.analyze(text);
        for (ChunkResult chunk : result.chunks()) {
            for (var h : chunk.highlights()) {
                assertThat(h.start()).isGreaterThanOrEqualTo(0);
                assertThat(h.end()).isLessThanOrEqualTo(chunk.text().length());
                assertThat(h.start()).isLessThan(h.end());
            }
        }
    }

    @Test
    void suggestionsNoDuplicates() {
        String text = "Это не страх — это нечто большее, чем страх. "
                + "Это не просто тревога, а нечто большее, чем тревога. "
                + "Он понял и почувствовал это. Боль была настоящей. "
                + "Тоска не отпускала его ни днём, ни ночью совсем.";
        AnalysisResult result = service.analyze(text);
        for (ChunkResult chunk : result.chunks()) {
            long distinct = chunk.suggestions().stream().distinct().count();
            assertThat(distinct).isEqualTo(chunk.suggestions().size());
        }
    }
}
