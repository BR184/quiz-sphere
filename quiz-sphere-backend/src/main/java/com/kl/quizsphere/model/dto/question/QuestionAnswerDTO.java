package com.kl.quizsphere.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-06 20:33:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerDTO {

    /**
     * 题目
     */
    private String title;
    /**
     * 答案
     */
    private String userAnswer;
}
