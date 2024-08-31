//审核状态枚举
export const REVIEW_STATUS_ENUM = {
  REVIEWING: 0, //审核中
  PASS: 1, //审核通过
  REFUSE: 2, //审核拒绝
};
//审核状态映射
export const REVIEW_STATUS_MAP = {
  0: "审核中",
  1: "审核通过",
  2: "审核拒绝",
};

// 应用类型枚举
export const APP_TYPE_ENUM = {
  // 得分类
  SCORE: 0,
  // 测评类
  TEST: 1,
};

// 应用类型映射
export const APP_TYPE_MAP = {
  0: "得分类",
  1: "测评类",
};

// 应用评分策略枚举
export const APP_SCORING_STRATEGY_ENUM = {
  // 自定义
  CUSTOM: 0,
  // AI
  AI: 1,
};

// 应用评分策略映射
export const APP_SCORING_STRATEGY_MAP = {
  0: "自定义",
  1: "AI",
};
