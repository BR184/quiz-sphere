package com.kl.quizsphere.scoring;

import com.kl.quizsphere.exception.BusinessException;
import com.kl.quizsphere.model.entity.App;
import com.kl.quizsphere.model.entity.UserAnswer;

import java.util.List;

/**
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-08-27 15:53:07
 */
public class UserScoreScoringStrategyImpl implements ScoringStrategy {
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws BusinessException {
        return null;
    }
}
