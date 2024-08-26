package com.kl.quizsphere.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author KL
 * @version 1.0
 * @since 2024-08-26 05:08:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionContentDTO {

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目选项
     */
    private List<Option> options;

    /**
     * 题目列表
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Option {
        private String result;
        private int score;
        private String value;
        private String key;
    }
}
