package com.kl.quizsphere.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-09-09 16:41:17
 */
@Data
public class UserAnswerCreatedVo implements Serializable {
    /**
     * 后端生成的用于保证幂等性的雪花ID
     */
    private Long id;
    /**
     * 使用HMAC SHA256
     */
    private String signature;
    private static final long serialVersionUID = 1L;
}
