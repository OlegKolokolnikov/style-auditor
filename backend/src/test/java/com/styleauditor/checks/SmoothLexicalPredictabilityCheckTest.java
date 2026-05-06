package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.CheckResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SmoothLexicalPredictabilityCheckTest {

    private final SmoothLexicalPredictabilityCheck check = new SmoothLexicalPredictabilityCheck();

    private CheckResult run(String text) {
        var parsed = AnalyzerUtils.parseSentences(text);
        var ctx = new ChunkContext(0, text, parsed.sentences(), parsed.positions(),
                AnalyzerUtils.sentenceLengths(parsed.sentences()));
        return check.check(ctx);
    }

    @Test
    void scoreNeverNegative() {
        // текст с умеренной предсказуемостью, ранее давал отрицательный взнос
        String text = "Он вдруг понял, что страх и тревога — это просто слова. "
                + "Он почувствовал покой. Он увидел свет. Он осознал правду.";
        var result = run(text);
        assertThat(result.score()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void highlyPredictableTextFlagged() {
        // насыщен абстрактными эмоциями и частотными словами
        String text = "Страх и боль наполнили его душу. Тревога не отступала. "
                + "Пустота была повсюду. Отчаяние захлестнуло его с головой. "
                + "Он вдруг почувствовал ужас. Надежда исчезла. Ярость сменила боль. "
                + "Он словно увидел конец. Будто страх вернулся снова.";
        var result = run(text);
        assertThat(result.flags()).isNotEmpty();
    }

    @Test
    void specificConcreteTextNotFlagged() {
        String text = "Дима нашёл ключ под ковриком. Замок заскрипел и поддался. "
                + "В комнате пахло скипидаром и старыми газетами. "
                + "На столе лежал сломанный будильник с треснутым стеклом. "
                + "Форточка была открыта, занавеска вздрагивала от сквозняка.";
        var result = run(text);
        assertThat(result.flags()).isEmpty();
    }
}
