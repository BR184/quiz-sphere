package com.kl.quizsphere.model.enums;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * App评分策略枚举
 *
 * @author KL
 * @version 1.0
 * @since 2024-08-26 04:19:06
 */

public enum AppScoringStrategyEnum {
    CUSTOM("自定义",0),
    AI("AI",1);

    private final String text;
    private final int value;

    AppScoringStrategyEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据值获取枚举
     * @param value
     * @return Enum
     */
    public static AppScoringStrategyEnum getEnumsByValue(Integer value) {
        if(ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (AppScoringStrategyEnum anEnum : AppScoringStrategyEnum.values()) {
            if(anEnum.value == value) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取枚举的值列表
     * @return List<Integer>
     */
    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public int getValue(){
        return value;
    }
    public String getText(){
        return text;
    }

}
