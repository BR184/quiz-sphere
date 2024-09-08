package com.kl.quizsphere.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kl.quizsphere.constant.ScoringResultIdConstant;
import com.kl.quizsphere.exception.BusinessException;
import com.kl.quizsphere.model.dto.question.QuestionContentDTO;
import com.kl.quizsphere.model.entity.App;
import com.kl.quizsphere.model.entity.Question;
import com.kl.quizsphere.model.entity.ScoringResult;
import com.kl.quizsphere.model.entity.UserAnswer;
import com.kl.quizsphere.model.vo.QuestionVO;
import com.kl.quizsphere.service.QuestionService;
import com.kl.quizsphere.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 定义-测分类-应用打分策略
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-08-27 15:53:07
 */
@ScoringStrategyConfig(appType = 0,scoringStrategy = 0)
public class UserScoreScoringStrategyImpl implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws BusinessException {
        //1.根据id查询题目和题目结果信息
        Long appId = app.getId();
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, appId)
        );
        //2.遍历题目结果信息，计算得分
        //获取每个题目的option列表
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        //记录用户总分
        Integer totalScore = 0;
        for (int i = 0;i < questionContent.size(); i++) {
            if (choices.size()==i){
                break;
            }
            QuestionContentDTO questionContentDTO = questionContent.get(i);
            List<QuestionContentDTO.Option> options = questionContentDTO.getOptions();
            //获取单个题目  DCBDACCBCA
            for (QuestionContentDTO.Option option : options) {
                if (choices.get(i).equalsIgnoreCase(option.getKey())) {   //choices like:[A,B,C,A,C,A]
                    totalScore += Optional.of(option.getScore()).orElse(0);
                }
            }
        }
        //3.根据得分获取最高分的结果

        Integer finalTotalScore = totalScore;
        Optional<ScoringResult> max = scoringResultList.stream().filter(s -> s.getResultScoreRange() < finalTotalScore).max((s1, s2) -> s1.getResultScoreRange() - s2.getResultScoreRange());
        ScoringResult scoringResult = max.orElseGet(this::getDefaultScoringResult);
        //4.构造UserAnswer对象，并返回
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(scoringResult.getId());
        userAnswer.setResultName(scoringResult.getResultName());
        userAnswer.setResultDesc(scoringResult.getResultDesc());
        userAnswer.setResultPicture(scoringResult.getResultPicture());
        userAnswer.setResultScore(finalTotalScore);
        return userAnswer;
    }

    public ScoringResult getDefaultScoringResult() {
        return scoringResultService.getById(ScoringResultIdConstant.DEFAULT_RESULT_ID);
    }
}
