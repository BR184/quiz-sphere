package com.kl.quizsphere.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/BR184">BR184</a>
 * @version 1.0
 * @since 2024-08-27 14:34:16
 */
@Data
public class ReviewRequest implements Serializable {

    /**
     * id
     */
    private long id;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    private static final long serialVersionUID = 1L;

}
