package com.kl.quizsphere.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kl.quizsphere.model.dto.statistic.AppAnswerCountDTO;
import com.kl.quizsphere.model.dto.statistic.AppAnswerResultCountDTO;
import com.kl.quizsphere.model.entity.UserAnswer;

import java.util.List;

/**
* @author <a href="https://github.com/BR184">BR184</a>
* @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
* @createDate 2024-08-26 03:16:52
* @Entity com.kl.quizsphere.model.entity.UserAnswer
*/
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {

    List<AppAnswerCountDTO> doAppAnswerCount();

    List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);

    Integer forceDeleteById(Long id);
}




