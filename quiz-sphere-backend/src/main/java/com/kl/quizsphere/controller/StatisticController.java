package com.kl.quizsphere.controller;

import cn.hutool.core.io.FileUtil;
import com.kl.quizsphere.common.BaseResponse;
import com.kl.quizsphere.common.ErrorCode;
import com.kl.quizsphere.common.ResultUtils;
import com.kl.quizsphere.constant.FileConstant;
import com.kl.quizsphere.exception.BusinessException;
import com.kl.quizsphere.exception.ThrowUtils;
import com.kl.quizsphere.manager.CosManager;
import com.kl.quizsphere.mapper.UserAnswerMapper;
import com.kl.quizsphere.model.dto.file.UploadFileRequest;
import com.kl.quizsphere.model.dto.statistic.AppAnswerCountDTO;
import com.kl.quizsphere.model.dto.statistic.AppAnswerResultCountDTO;
import com.kl.quizsphere.model.entity.User;
import com.kl.quizsphere.model.enums.FileUploadBizEnum;
import com.kl.quizsphere.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * APP 统计分析
 *
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-09 21:19:06
 */
@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class StatisticController {
    @Resource
    private UserAnswerMapper userAnswerMapper;

    /**
     * 热门应用使用数统计
     *
     * @return
     */
    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    /**
     * 应用答题结果统计
     *
     * @param appId
     * @return
     */
    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }
}
