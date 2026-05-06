package com.styleauditor.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnalyzeControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final String VALID_TEXT = """
            Дима нашёл ключ под ковриком у двери. Замок заскрипел и поддался с третьей попытки.
            В комнате пахло скипидаром и старыми газетами. На столе лежал будильник с треснутым стеклом.
            Форточка была открыта, занавеска вздрагивала от сквозняка. Он огляделся и прошёл к окну.
            За стеклом шёл дождь. Улица блестела от луж. Прохожих не было видно совсем нигде.
            """;

    @Test
    void validTextReturns200WithChunks() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"" + VALID_TEXT.replace("\n", "\\n").replace("\"", "\\\"") + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chunks").isArray())
                .andExpect(jsonPath("$.chunks[0].riskScore").isNumber());
    }

    @Test
    void emptyTextReturns400() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void blankTextReturns400() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingBodyReturns400() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void textOverLimitReturns400() throws Exception {
        String huge = "{\"text\":\"" + "А".repeat(200_001) + "\"}";
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(huge))
                .andExpect(status().isBadRequest());
    }

    @Test
    void riskScoreInBounds() throws Exception {
        mvc.perform(post("/api/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"" + VALID_TEXT.replace("\n", "\\n").replace("\"", "\\\"") + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chunks[0].riskScore").value(
                        org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.greaterThanOrEqualTo(0),
                                org.hamcrest.Matchers.lessThanOrEqualTo(100)
                        )
                ));
    }
}
