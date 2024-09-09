package com.kl.quizsphere.model.dto.statistic;

import lombok.Data;

/**
 * 应用答案结果统计DTO
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-09 20:44:38
 */
@Data
public class AppAnswerResultCountDTO {
    /**
     * 结果名称
     */
    private String resultName;
    /**
     * 结果数量
     */
    private Long resultCount;
}
