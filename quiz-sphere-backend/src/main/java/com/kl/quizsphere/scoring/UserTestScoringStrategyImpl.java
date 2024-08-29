package com.kl.quizsphere.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import java.util.HashMap;
import java.util.List;

/**
 * 自定义-测评类-应用打分策略
 *
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-08-27 15:53:07
 */
public class UserTestScoringStrategyImpl implements ScoringStrategy {

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

        //2.统计用户选择的属性个数，I=XXX,E=XXX
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        HashMap<String, Integer> testResultCount = new HashMap<>();
        //遍历所有的题目
        for (int i = 0; i < questionContent.size(); i++) {
            QuestionContentDTO questionContentDTO = questionContent.get(i);
            String userChoice = choices.get(i);//1.A 2.B 3.B 4.A 5.A 6.B .......
            //遍历题目的选项
            for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                //用户选择该选项
                if (option.getKey().equals(userChoice)) {
                    if (testResultCount.get(userChoice) == null) {
                        testResultCount.put(option.getResult(), 0);
                    }
                    testResultCount.put(option.getResult(), testResultCount.get(option.getResult()) + 1);
                }
            }
        }
        //3.遍历题目结果信息，计算得分
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResultList.get(0);
        //遍历结果，不断比较出最合适的结果
        for (ScoringResult scoringResult : scoringResultList) {
            List<String> list = JSONUtil.toList(scoringResult.getResultProp(), String.class);//I N T J
            int score = list.stream().mapToInt(s -> testResultCount.getOrDefault(s, 0)).sum();//I:10,N:5,T:3,J:6 --sum--> 24 Point
            //找到最高分答案（即为最匹配的答案）
            if (score>maxScore){
                maxScore=score;
                maxScoringResult = scoringResult;
            }
        }
        //4.构造UserAnswer对象，并返回
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;
    }
}
