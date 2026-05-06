package com.styleauditor.checks;

import com.styleauditor.engine.AnalyzerUtils;
import com.styleauditor.engine.ChunkContext;
import com.styleauditor.engine.CheckResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PatternDashOutsideDialogueCheckTest {

    private final PatternDashOutsideDialogueCheck check = new PatternDashOutsideDialogueCheck();

    private CheckResult run(String text) {
        var parsed = AnalyzerUtils.parseSentences(text);
        var ctx = new ChunkContext(0, text, parsed.sentences(), parsed.positions(),
                AnalyzerUtils.sentenceLengths(parsed.sentences()));
        return check.check(ctx);
    }

    @Test
    void dialogueAttributionDashesNotFlagged() {
        // все тире здесь диалоговые — не должно быть флагов
        String text = "— Ты уверен? — спросил мужчина. — Ты точно в этом уверен?! — он переспросил, будто его крик могли не услышать и с первого раза.";
        var result = run(text);
        assertThat(result.flags()).isEmpty();
    }

    @Test
    void intonationalDashesAreFlagged() {
        // 4 интонационных тире подряд в авторской речи
        String text = "Он шёл — и думал. Ночь была долгой — слишком долгой. Тишина — почти звенящая. Страх — привычный.";
        var result = run(text);
        assertThat(result.flags()).isNotEmpty();
    }

    @Test
    void singleDashNotFlagged() {
        String text = "Он подавил тревогу — и сделал ещё один глоток.";
        var result = run(text);
        // один случай — ниже порога в 4
        assertThat(result.flags()).isEmpty();
    }

    @Test
    void nonDialogueLineWithDashAfterQuestionFlagged() {
        // строка не начинается с — значит это НЕ диалог, тире после ? должно считаться интонационным
        String text = "Он был прав? — Конечно. Так всегда — без исключений. Всё шло — по плану. Ничего — не изменилось.";
        var result = run(text);
        assertThat(result.flags()).isNotEmpty();
    }
}
