package com.kl.quizsphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kl.quizsphere.annotation.AuthCheck;
import com.kl.quizsphere.common.BaseResponse;
import com.kl.quizsphere.common.DeleteRequest;
import com.kl.quizsphere.common.ErrorCode;
import com.kl.quizsphere.common.ResultUtils;
import com.kl.quizsphere.constant.UserConstant;
import com.kl.quizsphere.exception.BusinessException;
import com.kl.quizsphere.exception.ThrowUtils;
import com.kl.quizsphere.manager.AiManager;
import com.kl.quizsphere.model.dto.question.*;
import com.kl.quizsphere.model.entity.App;
import com.kl.quizsphere.model.entity.Question;
import com.kl.quizsphere.model.entity.User;
import com.kl.quizsphere.model.enums.AppTypeEnum;
import com.kl.quizsphere.model.enums.UserRoleEnum;
import com.kl.quizsphere.model.vo.QuestionVO;
import com.kl.quizsphere.service.AppService;
import com.kl.quizsphere.service.QuestionService;
import com.kl.quizsphere.service.UserService;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 题目接口
 *
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    @Resource
    private AiManager aiManager;

    @Resource
    private Scheduler vipScheduler;

    // region 增删改查

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        question.setQuestionContent(JSONUtil.toJsonStr(questionAddRequest.getQuestionContent()));
        // 数据校验
        questionService.validQuestion(question, true);
        // 填充默认值
        User loginUser = userService.getLoginUser(request);

        question.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目（仅管理员可用）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        question.setQuestionContent(JSONUtil.toJsonStr(questionUpdateRequest.getQuestionContent()));
        // 数据校验
        questionService.validQuestion(question, false);
        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取题目列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取题目列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑题目（给用户使用）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        question.setQuestionContent(JSONUtil.toJsonStr(questionEditRequest.getQuestionContent()));
        // 数据校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    // region AI 题目生成请求
    // region String-测试类题目系统prompt
    public static final String QUESTION_TEST_GENERATE_SYSTEM_PROMPT = "你是一位严谨的心理学专家，生成4组两两对立的理论模型考察用户（如MBTI的I-E/S-N/T-F/J-P）。我会给你如下信息：\n" +
            "```\n" +
            "模型名称，\n" +
            "【【【模型描述】】】，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1. 要求：题目和选项简短，不能重复，不包含序号，不直接问答案，生成隐含测试问题。\n" +
            "2. 严格按照下面的 JSON 格式输出：\n" +
            "```\n" +
            "[{\"options\":[{\"value\":\"让情感支配理智\",\"key\":\"A\"},{\"value\":\"让理智主导情感\",\"key\":\"B\"},......],\"title\":\"你经常\"},......]\n" +
            "```\n" +
            "options 是选项且内部数量按照选项数生成，value 是选项内容，key是选项且每题从'A'开始，title 是题目且需要在options之后\n" +
            "3. 确保题目不含序号，维度为两两对立的四组模型，题目以隐含方式测试。\n" +
            "4. 返回的题目列表格式必须为 JSON 数组";
    // endregion
    // region String-问答类题目系统prompt
    public static final String QUESTION_ANSWER_GENERATE_SYSTEM_PROMPT = "你是一位经验丰富的出卷专家，负责生成完整严谨且丰富的选拔性考试试题，我会给你如下信息：\n" +
            "```\n" +
            "试卷名称，\n" +
            "【【【试卷描述】】】，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1. 要求：题目和选项简短，不能重复，不包含序号，每道题只有一个答案\n" +
            "2. 严格按照下面的 JSON 格式输出：\n" +
            "```\n" +
            "[{\"options\":[{\"value\":\"影响卫星导航、空间通讯\",\"key\":\"A\"},{\"value\":\"诱发地球出现地震灾害\",\"key\":\"B\"},......],\"title\":\"太阳活动对地球的影响主要有：\"},......]\n" +
            "```\n" +
            "options 是选项且内部数量按照选项数生成，value 是选项内容，key是选项且每题从'A'开始，title 是题目且需要在options之后\n" +
            "3. 确保题目不含序号，题目难度需要逐渐递增且需要具有选拔性。\n" +
            "4. 返回的题目列表格式必须为 JSON 数组";

    // endregion

    /**
     * AI同步生成题目请求
     *
     * @param generateRequest
     * @return
     */
    @PostMapping("/ai/generate_question")
    public BaseResponse<List<QuestionContentDTO>> aiGenerateQuestion(@RequestBody QuestionAiGenerateRequest generateRequest) {
        //字段校验
        ThrowUtils.throwIf(generateRequest == null, ErrorCode.PARAMS_ERROR, "参数错误");
        ThrowUtils.throwIf(generateRequest.getQuestionNumber() > 20 || generateRequest.getQuestionNumber() < 5, ErrorCode.PARAMS_ERROR, "请求题目数量超出范围");
        ThrowUtils.throwIf(generateRequest.getOptionNumber() < 0 || generateRequest.getOptionNumber() > 6, ErrorCode.PARAMS_ERROR, "请求选项数量超出范围");
        //数据获取+数据校验
        App app = appService.getById(generateRequest.getAppId());
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        //封装Prompt
        String prompt = getQuestionGenerateMessage(app, generateRequest.getQuestionNumber(), generateRequest.getOptionNumber());
        //Ai生成
        String result = "";
        if (app.getAppType() == AppTypeEnum.TEST.getValue()) {
            result = aiManager.getOneShotSyncDefaultResponse(QUESTION_TEST_GENERATE_SYSTEM_PROMPT, prompt);
        } else if (app.getAppType() == AppTypeEnum.SCORE.getValue()) {
            result = aiManager.getOneShotSyncDefaultResponse(QUESTION_ANSWER_GENERATE_SYSTEM_PROMPT, prompt);
        }
        //数据校验
        int left = result.indexOf("[");
        int right = result.lastIndexOf("]");
        ThrowUtils.throwIf(left == -1 || right == -1, ErrorCode.OPERATION_ERROR, "参数错误匹配错误，请重试！");
        result = result.substring(left, right + 1);
        List<QuestionContentDTO> list = JSONUtil.toList(result, QuestionContentDTO.class);
        System.out.println(JSONUtil.toJsonStr(list));
        return ResultUtils.success(list);
    }

    /**
     * AI流式生成题目请求
     *
     * @param generateRequest
     * @return
     */
    @GetMapping("/ai/generate_question/flow")
    public SseEmitter aiFlowableGenerateQuestion(QuestionAiGenerateRequest generateRequest,HttpServletRequest request) {
        //字段校验
        ThrowUtils.throwIf(generateRequest == null, ErrorCode.PARAMS_ERROR, "参数错误");
        ThrowUtils.throwIf(generateRequest.getQuestionNumber() > 20 || generateRequest.getQuestionNumber() < 5, ErrorCode.PARAMS_ERROR, "请求题目数量超出范围");
        ThrowUtils.throwIf(generateRequest.getOptionNumber() < 0 || generateRequest.getOptionNumber() > 6, ErrorCode.PARAMS_ERROR, "请求选项数量超出范围");
        //数据获取+数据校验
        App app = appService.getById(generateRequest.getAppId());
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        //封装Prompt
        String prompt = getQuestionGenerateMessage(app, generateRequest.getQuestionNumber(), generateRequest.getOptionNumber());
        //建立SSE连接对象
        SseEmitter sseEmitter = new SseEmitter(0L);
        //获取登录用户
        User user = userService.getLoginUser(request);
        //默认共享线程池
        Scheduler scheduler = Schedulers.io();
        if (user.getUserRole().equals(UserRoleEnum.ADMIN.getValue())||user.getUserRole().equals(UserRoleEnum.VIP.getValue())){
            scheduler = vipScheduler;
        }

        //AI流式请求处理返回
        if (app.getAppType() == AppTypeEnum.TEST.getValue()) {
            Flowable<ModelData> flowable = aiManager.getOneShotAsyncDefaultResponse(QUESTION_TEST_GENERATE_SYSTEM_PROMPT, prompt);
            //“{}”计数器{+1 }-1，相等时截取
            AtomicInteger counter = new AtomicInteger(0);
            //用于拼接单条题目的JSON
            StringBuilder questionJSON = new StringBuilder();
            flowable
                    .observeOn(scheduler)
                    .map(modelData -> modelData.getChoices().get(0).getDelta().getContent())
                    .map(strMsg -> strMsg.replaceAll("\\s|\\n", ""))
                    .filter(strMsg -> !strMsg.isEmpty())
                    .concatMap(strMsg -> {
                        List<Character> charList = new ArrayList<>(strMsg.length());
                        for (char c : strMsg.toCharArray()) {
                            charList.add(c);
                        }
                        return Flowable.fromIterable(charList);
                    })
                    .doOnNext(c -> {
                        if (c == '{') {
                            counter.incrementAndGet();
                        }
                        if (counter.get() > 0) {
                            questionJSON.append(c);
                        }
                        if (c == '}') {
                            counter.decrementAndGet();
                            if (counter.get() == 0) {
                                sseEmitter.send(JSONUtil.toJsonStr(questionJSON.toString()));
                                questionJSON.setLength(0);
                            }
                        }
                    })
                    .doOnError((e) -> log.error("AI流式生成题目请求异常", e))
                    .doOnComplete(sseEmitter::complete)
                    .subscribe();
        }
        return sseEmitter;
    }


    /**
     * AI 题目生成用户消息构造器
     *
     * @param app
     * @param questionNumber
     * @param optionsNumber
     * @return
     */
    public String getQuestionGenerateMessage(App app, Integer questionNumber, Integer optionsNumber) {
        return app.getAppName() + "，\n【【【" + app.getAppDesc() + "】】】，\n" + questionNumber + "，\n" + optionsNumber + "\n";
    }
}
