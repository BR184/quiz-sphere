package com.kl.quizsphere.scoring;

import com.kl.quizsphere.common.ErrorCode;
import com.kl.quizsphere.exception.BusinessException;
import com.kl.quizsphere.exception.ThrowUtils;
import com.kl.quizsphere.model.entity.App;
import com.kl.quizsphere.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评分策略执行器
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-08-29 12:48:06
 */
@Service
public class ScoringStrategyExecutor{

    @Resource
    private List<ScoringStrategy> scoringStrategyList;


    public UserAnswer doScore(List<String> choices, App app) throws BusinessException {
        Integer appType = app.getAppType();
        Integer scoringStrategy = app.getScoringStrategy();
        ThrowUtils.throwIf(appType==null||scoringStrategy==null, ErrorCode.PARAMS_ERROR,"应用配置有误，未找到评分策略！");
        for (ScoringStrategy strategy : scoringStrategyList) {
            if(strategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)){
                ScoringStrategyConfig strategyConfig = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                if(strategyConfig.appType()==appType&&strategyConfig.scoringStrategy()==scoringStrategy)
                    return strategy.doScore(choices,app);
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR,"应用配置有误，未找到评分策略！");
    }
}
