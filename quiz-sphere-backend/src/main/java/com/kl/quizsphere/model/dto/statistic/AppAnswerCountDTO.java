package com.kl.quizsphere.model.dto.statistic;

import lombok.Data;

/**
 * APP作答次数统计
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-09 20:23:49
 */

/**
 * APP 用户提交答案数统计
 */
@Data
public class AppAnswerCountDTO {

    private Long appId;

    /**
     * 用户提交答案数
     */
    private Long answerCount;
}
