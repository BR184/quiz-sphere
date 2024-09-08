package com.kl.quizsphere.manager;

import com.kl.quizsphere.common.ErrorCode;
import com.kl.quizsphere.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-05 16:00:00
 */
@Component
public class AiManager {
    @Resource
    private ClientV4 clientV4;
    //采样温度，控制输出的随机性
    //较低采样温度-稳定
    private static final float LOW_TEMPERATURE = 0.05F;
    //默认采样温度-正常
    private static final float DEFAULT_TEMPERATURE = 0.95F;
    //较高采样温度-创意
    private static final float HIGH_TEMPERATURE = 0.99F;
    //核取样-0.1 意味着模型解码器只考虑从前 10% 的概率的候选集中取 tokens
    //较低核取样-集中
    private static final float LOW_TOP_P = 0.01F;
    //默认核取样-正常
    private static final float DEFAULT_TOP_P = 0.7F;
    //较高核取样-随机
    private static final float HIGH_TOP_P = 0.9F;


    /**
     * 智谱AI一次性异步稳定参数请求
     *
     * @param messageList
     * @return 流式一次性的AI回复
     */
    public Flowable<ModelData> getOneShotAsyncStableResponseByChatMessageList(List<ChatMessage> messageList) {
        return getResponse(messageList, true, LOW_TEMPERATURE, LOW_TOP_P).getFlowable();
    }

    /**
     * 智谱AI一次性异步默认参数请求
     *
     * @param messageList
     * @return 流式一次性的AI回复
     */
    public Flowable<ModelData> getOneShotAsyncDefaultResponseByChatMessageList(List<ChatMessage> messageList) {
        return getResponse(messageList, true, DEFAULT_TEMPERATURE, DEFAULT_TOP_P).getFlowable();
    }

    /**
     * 智谱AI一次性异步创意参数请求
     *
     * @param messageList
     * @return 流式一次性的AI回复
     */
    public Flowable<ModelData> getOneShotAsyncCreativeResponseByChatMessageList(List<ChatMessage> messageList) {
        return getResponse(messageList, true, HIGH_TEMPERATURE, HIGH_TOP_P).getFlowable();
    }


    /**
     * 智谱AI一次性同步稳定参数请求
     *
     * @param messageList
     * @return 一条一次性的AI回复
     */
    public String getOneShotSyncStableResponseByChatMessageList(List<ChatMessage> messageList) {
        return getResponse(messageList, false, LOW_TEMPERATURE, LOW_TOP_P).getData().getChoices().get(0).toString();
    }

    /**
     * 智谱AI一次性同步默认参数请求
     *
     * @param messageList
     * @return 一条一次性的AI回复
     */
    public String getOneShotSyncDefaultResponseByChatMessageList(List<ChatMessage> messageList) {
        return getResponse(messageList, false, DEFAULT_TEMPERATURE, DEFAULT_TOP_P).getData().getChoices().get(0).toString();
    }

    /**
     * 智谱AI一次性同步创意参数请求
     *
     * @param messageList
     * @return 一条一次性的AI回复
     */
    public String getOneShotSyncCreativeResponseByChatMessageList(List<ChatMessage> messageList) {
        return getResponse(messageList, false, HIGH_TEMPERATURE, HIGH_TOP_P).getData().getChoices().get(0).toString();
    }

    /**
     * 智谱AI一次性同步默认参数请求
     *
     * @param messageList
     * @return 一条一次性的AI回复
     */


    /**
     * 智谱AI一次性同步稳定参数请求
     *
     * @param systemMessage
     * @param userMassage
     * @return 一条一次性的AI回复
     */
    public String getOneShotSyncStableResponse(String systemMessage, String userMassage) {
        return getOneShotSyncResponse(systemMessage, userMassage, LOW_TEMPERATURE, LOW_TOP_P);
    }

    /**
     * 智谱AI一次性异步默认参数请求
     *
     * @param systemMessage
     * @param userMassage
     * @return 流式一次性的AI回复
     */
    public Flowable<ModelData> getOneShotAsyncDefaultResponse(String systemMessage, String userMassage) {
        return getOneShotAsyncResponse(systemMessage, userMassage, DEFAULT_TEMPERATURE, DEFAULT_TOP_P);
    }

    /**
     * 智谱AI一次性同步默认参数请求
     *
     * @param systemMessage
     * @param userMassage
     * @return 一条一次性的AI回复
     */
    public String getOneShotSyncDefaultResponse(String systemMessage, String userMassage) {
        return getOneShotSyncResponse(systemMessage, userMassage, DEFAULT_TEMPERATURE, DEFAULT_TOP_P);
    }

    /**
     * 智谱AI一次性同步创意参数请求
     *
     * @param systemMessage
     * @param userMassage
     * @return 一条一次性的AI回复
     */
    public String getOneShotSyncCreativeResponse(String systemMessage, String userMassage) {
        return getOneShotSyncResponse(systemMessage, userMassage, HIGH_TEMPERATURE, HIGH_TOP_P);
    }

    /**
     * 智谱AI一次性异步请求
     *
     * @param systemMessage
     * @param userMassage
     * @param temperature
     * @param topP
     * @return 流式一次性的AI回复
     */
    public Flowable<ModelData> getOneShotAsyncResponse(String systemMessage, String userMassage, Float temperature, Float topP) {
        return getOneShotResponse(systemMessage, userMassage, true, temperature, topP).getFlowable();
    }


    /**
     * 智谱AI一次性同步请求
     *
     * @param systemMessage
     * @param userMassage
     * @param temperature
     * @param topP
     * @return 一条一次性的AI回复
     */
    public String getOneShotSyncResponse(String systemMessage, String userMassage, Float temperature, Float topP) {
        return getOneShotResponse(systemMessage, userMassage, false, temperature, topP).getData().getChoices().get(0).toString();
    }


    /**
     * 智谱AI一次性请求
     *
     * @param systemMessage
     * @param userMassage
     * @param stream
     * @param temperature
     * @param topP
     * @return 一条一次性的AI回复
     */
    public ModelApiResponse getOneShotResponse(String systemMessage, String userMassage, Boolean stream, Float temperature, Float topP) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), userMassage));
        return getResponse(messages, stream, temperature, topP);

    }


    /**
     * 智谱AI通用请求
     *
     * @param messages
     * @param stream
     * @param temperature
     * @param topP
     * @return 聊天模型数据
     */
    public ModelApiResponse getResponse(List<ChatMessage> messages, Boolean stream, Float temperature, Float topP) {
        // 生成ID，注释掉会由服务器生成   String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        //构建请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream) //流式传输
                .temperature(temperature)
                .topP(topP)
                .invokeMethod(Constants.invokeMethod) //调用异步or同步
                .messages(messages)
                //.requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = null;
        try {
            invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI调用异常，请联系网站管理员！");
        }
        return invokeModelApiResp;
    }
}
