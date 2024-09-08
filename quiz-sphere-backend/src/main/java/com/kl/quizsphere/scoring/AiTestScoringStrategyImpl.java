package com.kl.quizsphere.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kl.quizsphere.common.ErrorCode;
import com.kl.quizsphere.exception.BusinessException;
import com.kl.quizsphere.exception.ThrowUtils;
import com.kl.quizsphere.manager.AiManager;
import com.kl.quizsphere.model.dto.question.QuestionAnswerDTO;
import com.kl.quizsphere.model.dto.question.QuestionContentDTO;
import com.kl.quizsphere.model.entity.App;
import com.kl.quizsphere.model.entity.Question;
import com.kl.quizsphere.model.entity.UserAnswer;
import com.kl.quizsphere.model.vo.QuestionVO;
import com.kl.quizsphere.service.QuestionService;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AI-测评类-应用打分策略
 *
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-08-27 15:53:07
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiTestScoringStrategyImpl implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedissonClient redissonClient;

    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    /**
     * 本地缓存 缓存AI评分结果
     */
    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder().initialCapacity(1024)
                    //缓存保存5分钟
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .build();


    //region String-测试类题目打分系统prompt
    private static final String SCORING_TEST_TITLERESULT_GENERATE_SYSTEM_PROMPT = "你是一位严谨的心理学专家，通过我的回答给出评测结果（如MBTI的ESFJ—执行官/INFJ—倡导者等），我会给你如下信息：\n" +
            "```\n" +
            "模型名称，\n" +
            "【【【模型描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对我进行评价：\n" +
            "1.给出一个明确的结果，和描述，\n" +
            "2.结果必须回答模型中的关键提问，必须简短精辟（如MBTI测试：INFJ—倡导者等，编程语言偏好调查：Python—简洁高效等，猜你喜欢甜还是咸：甜食爱好者等，你的狗狗爱你吗：关系平平等，末日你能生存几天：约一个月等）\n" +
            "3.描述必须详细，字数控制在200-300字之间，包括我的核心特征、行为偏好、可能的优缺点及其与模型的相关性。\n" +
            "4.结果需唯一，描述需依据评价结果且亲切，叫我您，最后可简短提到其他接近的结果\n" +
            "5.返回格式必须为 JSON 对象：{\"resultName\": \"结果\", \"resultDesc\": \"描述\"}";
    //endregion

    //region String-测试类题目仅回复打分系统prompt
    private static final String SCORING_TEST_RESULTONLY_GENERATE_SYSTEM_PROMPT = "你是一位严谨的心理学专家，通过我的回答给出评测结果（如MBTI的ESFJ—执行官/INFJ—倡导者等），我会给你如下信息：\n" +
            "```\n" +
            "模型名称，\n" +
            "【【【模型描述】】】，\n" +
            "题目和用户回答的列表：格式为[{\"用户回答1\", \"用户回答2\"...}]\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1.给出一个明确的结果，和描述，\n" +
            "2.结果必须回答模型中的关键提问，必须简短精辟（如MBTI测试：INFJ(倡导者)等，编程语言偏好调查：Python—简洁高效等，猜你喜欢甜还是咸：甜食爱好者等，你的狗狗爱你吗：关系平平等，末日你能生存几天：约一年等）\n" +
            "3.描述必须详细，字数控制在200-300字之间，包括我的核心特征、行为偏好、可能的优缺点及其与模型的相关性。\n" +
            "4.结果需唯一，描述需依据评价结果且亲切，叫我您，最后可简短提到其他接近的结果\n" +
            "5.返回格式必须为 JSON 对象：{\"resultName\": \"结果\", \"resultDesc\": \"描述\"}";
    //endregion

    /**
     * AI-测评类-消息封装-更详实
     *
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @return
     */
    private String getScoringTestGenerateUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("，\n");
        userMessage.append("【【【").append(app.getAppDesc()).append("】】】").append("，\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            int finalI = i;
            questionAnswerDTO.setUserAnswer(questionContentDTOList.get(i).getOptions().stream()
                    .filter(o -> choices.get(finalI).equals(o.getKey()))
                    .findFirst().map(QuestionContentDTO.Option::getValue).orElse(null));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();

    }

    /**
     * AI-测评类-无标题消息封装-更节省Tokens
     *
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @return
     */
    private String getScoringTestNoTitleGenerateUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("，\n");
        userMessage.append("【【【").append(app.getAppDesc()).append("】】】").append("，\n");
        StringBuilder answers = new StringBuilder();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            int finalI = i;
            answers.append("\"").append(questionContentDTOList.get(i).getOptions().stream()
                    .filter(o -> choices.get(finalI).equals(o.getKey()))
                    .findFirst().map(QuestionContentDTO.Option::getValue).orElse(null)).append("\", ");
        }
        userMessage.append(answers);
        return userMessage.toString();

    }

    /**
     * AI-测评类-自选消息封装
     *
     * @param app
     * @param questionContentDTOList
     * @param choices
     * @param isDetail
     * @return
     */
    private List<ChatMessage> getMessageByDetailStrategy(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices, Boolean isDetail) {
        List<ChatMessage> messages = new ArrayList<>();
        if (isDetail) {
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), SCORING_TEST_TITLERESULT_GENERATE_SYSTEM_PROMPT));
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), getScoringTestGenerateUserMessage(app, questionContentDTOList, choices)));
        } else {
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), SCORING_TEST_RESULTONLY_GENERATE_SYSTEM_PROMPT));
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), getScoringTestNoTitleGenerateUserMessage(app, questionContentDTOList, choices)));
        }
        return messages;
    }

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws BusinessException {
        //0.使用Caffeine缓存
        Long appId = app.getId();
        String jsonStr = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(appId, jsonStr);
        String cacheAnswerJson = answerCacheMap.getIfPresent(cacheKey);
        //如果有缓存
        if (StrUtil.isNotBlank(cacheAnswerJson)) {
            //构造UserAnswer对象，并返回
            UserAnswer userAnswer = JSONUtil.toBean(cacheAnswerJson, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            return userAnswer;
        }
        //定义Redisson锁
        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + cacheKey);//定义Redisson锁

        try {
            //竞争锁
            boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
            if (!res) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"服务器繁忙，请于10秒后重试！");
            }
            //1.根据id查询题目和题目结果信息
            Question question = questionService.getOne(
                    Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
            );
            QuestionVO questionVO = QuestionVO.objToVo(question);
            List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

            //2.调用AI获取结果

            List<ChatMessage> chatMessages = getMessageByDetailStrategy(app, questionContent, choices, true);
            String result = aiManager.getOneShotSyncDefaultResponseByChatMessageList(chatMessages);

            //3.解析结果
            int left = result.indexOf("{");
            int right = result.lastIndexOf("}");
            ThrowUtils.throwIf(left == -1 || right == -1, ErrorCode.OPERATION_ERROR, "参数错误匹配错误，请重试！");
            result = result.substring(left, right + 1);

            //缓存
            answerCacheMap.put(cacheKey, result);

            //4.构造UserAnswer对象，并返回
            UserAnswer userAnswer = JSONUtil.toBean(result, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            return userAnswer;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null && lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 构建缓存key
     *
     * @param appId
     * @param choices
     * @return
     */
    private String buildCacheKey(Long appId, String choices) {
        return "AI_RESULT:" + appId + ":" + DigestUtil.md5Hex(choices);
    }
}
