package com.kl.quizsphere.scoring;

import com.kl.quizsphere.exception.BusinessException;
import com.kl.quizsphere.model.entity.App;
import com.kl.quizsphere.model.entity.UserAnswer;

import java.util.List;

/**
 * 评分策略
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-08-27 15:47:28
 */
public interface ScoringStrategy {
    /**
     * 执行评分策略接口
     * 交给Spring管理注入执行器
     * @param choices
     * @param app
     * @return
     * @throws BusinessException
     */
    UserAnswer doScore(List<String> choices, App app, Long userAnswerId) throws BusinessException;
}
