package com.kl.quizsphere.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-05 18:31:32
 */
@Data
public class QuestionAiGenerateRequest implements Serializable {
    /**
     * 应用 id
     */
    private Long appId;
    /**
     * 生成题目数量
     */
    private Integer questionNumber = 10;
    /**
     * 选项数量
     */
    private Integer optionNumber = 2;

    private static final long serialVersionUID = 1L;
}
