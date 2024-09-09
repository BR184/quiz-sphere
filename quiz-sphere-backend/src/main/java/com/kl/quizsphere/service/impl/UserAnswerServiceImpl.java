package com.kl.quizsphere.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kl.quizsphere.common.ErrorCode;
import com.kl.quizsphere.constant.CommonConstant;
import com.kl.quizsphere.exception.BusinessException;
import com.kl.quizsphere.exception.ThrowUtils;
import com.kl.quizsphere.mapper.UserAnswerMapper;
import com.kl.quizsphere.model.dto.useranswer.UserAnswerQueryRequest;
import com.kl.quizsphere.model.entity.User;
import com.kl.quizsphere.model.entity.UserAnswer;
import com.kl.quizsphere.model.enums.AppScoringStrategyEnum;
import com.kl.quizsphere.model.vo.UserAnswerVO;
import com.kl.quizsphere.model.vo.UserVO;
import com.kl.quizsphere.service.AppService;
import com.kl.quizsphere.service.UserAnswerService;
import com.kl.quizsphere.service.UserService;
import com.kl.quizsphere.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户答案服务实现
 *
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 */
@Service
@Slf4j
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer> implements UserAnswerService {

    @Resource
    private UserService userService;
    @Resource
    private AppService appService;
    /**
     * 校验数据
     *
     * @param userAnswer
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validUserAnswer(UserAnswer userAnswer, boolean add) {
        ThrowUtils.throwIf(userAnswer == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long appId = userAnswer.getAppId();
        Integer appType = userAnswer.getAppType();
        Integer scoringStrategy = userAnswer.getScoringStrategy();
        String choices = userAnswer.getChoices();
        Long resultId = userAnswer.getResultId();
        String resultName = userAnswer.getResultName();
        String resultDesc = userAnswer.getResultDesc();
        String resultPicture = userAnswer.getResultPicture();
        Integer resultScore = userAnswer.getResultScore();
        Long userId = userAnswer.getUserId();
        Long id = userAnswer.getId();

        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            ThrowUtils.throwIf(appId==null, ErrorCode.PARAMS_ERROR, "题目所属应用不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(choices), ErrorCode.PARAMS_ERROR, "题目选项不能为空");
            // 判断雪花id是否合法
            ThrowUtils.throwIf(id==null||id<100000000000000000L,ErrorCode.PARAMS_ERROR,"用户答案id错误，请刷新网页！");
            // 判断雪花id是否篡改
        }
        // 修改数据时，有参数则校验
        // 补充校验规则
        if (StringUtils.isNotBlank(resultName)) {
            ThrowUtils.throwIf(resultName.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (scoringStrategy!= null) {
        AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumsByValue(scoringStrategy);
            if (appScoringStrategyEnum == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评分策略不存在");
        }
        if (appType!= null) {
            AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumsByValue(scoringStrategy);
            if (appScoringStrategyEnum == null)
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目所属类型不存在");
        }
        ThrowUtils.throwIf(resultDesc!=null && resultDesc.length()>256, ErrorCode.PARAMS_ERROR, "题目描述过长");
        ThrowUtils.throwIf(resultName!=null && resultName.length()>128, ErrorCode.PARAMS_ERROR, "题目名称不能大于128");
    }

    /**
     * 获取查询条件
     *
     * @param userAnswerQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest userAnswerQueryRequest) {
        QueryWrapper<UserAnswer> queryWrapper = new QueryWrapper<>();
        if (userAnswerQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = userAnswerQueryRequest.getId();
        Long appId = userAnswerQueryRequest.getAppId();
        Integer appType = userAnswerQueryRequest.getAppType();
        Integer scoringStrategy = userAnswerQueryRequest.getScoringStrategy();
        String choices = userAnswerQueryRequest.getChoices();
        Long resultId = userAnswerQueryRequest.getResultId();
        String resultName = userAnswerQueryRequest.getResultName();
        String resultDesc = userAnswerQueryRequest.getResultDesc();
        String resultPicture = userAnswerQueryRequest.getResultPicture();
        Integer resultScore = userAnswerQueryRequest.getResultScore();
        Long userId = userAnswerQueryRequest.getUserId();
        Date createTime = userAnswerQueryRequest.getCreateTime();
        Date updateTime = userAnswerQueryRequest.getUpdateTime();
        Long notId = userAnswerQueryRequest.getNotId();
        String searchText = userAnswerQueryRequest.getSearchText();
        String sortField = userAnswerQueryRequest.getSortField();
        String sortOrder = userAnswerQueryRequest.getSortOrder();

        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("resultDesc", searchText).or().like("resultName", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(resultDesc), "resultDesc", resultDesc);
        queryWrapper.like(StringUtils.isNotBlank(choices), "choices", choices);
        queryWrapper.like(StringUtils.isNotBlank(resultName), "resultName", resultName);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultPicture), "resultPicture", resultPicture);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultId), "resultId", resultId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appType), "appType", appType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(scoringStrategy), "scoringStrategy", scoringStrategy);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取用户答案封装
     *
     * @param userAnswer
     * @param request
     * @return
     */
    @Override
    public UserAnswerVO getUserAnswerVO(UserAnswer userAnswer, HttpServletRequest request) {
        // 对象转封装类
        UserAnswerVO userAnswerVO = UserAnswerVO.objToVo(userAnswer);

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = userAnswer.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        userAnswerVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long userAnswerId = userAnswer.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<UserAnswerThumb> userAnswerThumbQueryWrapper = new QueryWrapper<>();
//            userAnswerThumbQueryWrapper.in("userAnswerId", userAnswerId);
//            userAnswerThumbQueryWrapper.eq("userId", loginUser.getId());
//            UserAnswerThumb userAnswerThumb = userAnswerThumbMapper.selectOne(userAnswerThumbQueryWrapper);
//            userAnswerVO.setHasThumb(userAnswerThumb != null);
//            // 获取收藏
//            QueryWrapper<UserAnswerFavour> userAnswerFavourQueryWrapper = new QueryWrapper<>();
//            userAnswerFavourQueryWrapper.in("userAnswerId", userAnswerId);
//            userAnswerFavourQueryWrapper.eq("userId", loginUser.getId());
//            UserAnswerFavour userAnswerFavour = userAnswerFavourMapper.selectOne(userAnswerFavourQueryWrapper);
//            userAnswerVO.setHasFavour(userAnswerFavour != null);
//        }
//        // endregion

        return userAnswerVO;
    }

    /**
     * 分页获取用户答案封装
     *
     * @param userAnswerPage
     * @param request
     * @return
     */
    @Override
    public Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage, HttpServletRequest request) {
        List<UserAnswer> userAnswerList = userAnswerPage.getRecords();
        Page<UserAnswerVO> userAnswerVOPage = new Page<>(userAnswerPage.getCurrent(), userAnswerPage.getSize(), userAnswerPage.getTotal());
        if (CollUtil.isEmpty(userAnswerList)) {
            return userAnswerVOPage;
        }
        // 对象列表 => 封装对象列表
        List<UserAnswerVO> userAnswerVOList = userAnswerList.stream().map(userAnswer -> {
            return UserAnswerVO.objToVo(userAnswer);
        }).collect(Collectors.toList());

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = userAnswerList.stream().map(UserAnswer::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
//        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> userAnswerIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> userAnswerIdHasFavourMap = new HashMap<>();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> userAnswerIdSet = userAnswerList.stream().map(UserAnswer::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<UserAnswerThumb> userAnswerThumbQueryWrapper = new QueryWrapper<>();
//            userAnswerThumbQueryWrapper.in("userAnswerId", userAnswerIdSet);
//            userAnswerThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<UserAnswerThumb> userAnswerUserAnswerThumbList = userAnswerThumbMapper.selectList(userAnswerThumbQueryWrapper);
//            userAnswerUserAnswerThumbList.forEach(userAnswerUserAnswerThumb -> userAnswerIdHasThumbMap.put(userAnswerUserAnswerThumb.getUserAnswerId(), true));
//            // 获取收藏
//            QueryWrapper<UserAnswerFavour> userAnswerFavourQueryWrapper = new QueryWrapper<>();
//            userAnswerFavourQueryWrapper.in("userAnswerId", userAnswerIdSet);
//            userAnswerFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<UserAnswerFavour> userAnswerFavourList = userAnswerFavourMapper.selectList(userAnswerFavourQueryWrapper);
//            userAnswerFavourList.forEach(userAnswerFavour -> userAnswerIdHasFavourMap.put(userAnswerFavour.getUserAnswerId(), true));
//        }
        // 填充信息
        userAnswerVOList.forEach(userAnswerVO -> {
            Long userId = userAnswerVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            userAnswerVO.setUser(userService.getUserVO(user));
//            userAnswerVO.setHasThumb(userAnswerIdHasThumbMap.getOrDefault(userAnswerVO.getId(), false));
//            userAnswerVO.setHasFavour(userAnswerIdHasFavourMap.getOrDefault(userAnswerVO.getId(), false));
        });
        // endregion

        userAnswerVOPage.setRecords(userAnswerVOList);
        return userAnswerVOPage;
    }

}
