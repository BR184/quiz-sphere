<template>
  <a-space direction="vertical" class="question-created-page">
    <a-typography-title style="font-weight: normal; margin-bottom: 40px">
      设置应用题目
    </a-typography-title>
    <a-form
      :model="questionContent"
      :style="{ width: '1440px' }"
      @submit="handleSubmit"
      label-align="left"
      :label-col-props="{ span: 0, offset: 0 }"
      :wrapper-col-props="{ span: 24, offset: 0 }"
      class="question-created-form"
    >
      <div class="question-form-top">
        <div class="question-form-top-container">
          <a-form-item extra="应用ID"> {{ appId }}</a-form-item>
          <a-space size="medium">
            <!-- AI生成的抽屉组件 -->
            <AiGenerateQuestionDrawer
              :appId="appId"
              :onSuccess="onAiResponseSuccess"
              :onSSESuccess="onAiSSEResponseSuccess"
              :onSSEClose="onSSEClose"
              :onSSEStart="onSSEStart"
            />
            <a-button
              @click="addQuestion(questionContent.length)"
              type="primary"
              status="warning"
              size="medium"
            >
              添加题目
            </a-button>
          </a-space>
        </div>
      </div>
      <div class="question-form-mid">
        <a-form-item
          :content-flex="false"
          :merge-props="false"
          class="grid-questions-container"
        >
          <!--遍历题目列表-->
          <a-grid
            :cols="{ xs: 1, sm: 1, md: 2, lg: 2, xl: 3, xxl: 4 }"
            :colGap="48"
            :rowGap="24"
            class="grid-questions-grid"
            justify="space-between"
          >
            <div v-for="(question, index) in questionContent" :key="index">
              <a-space size="mini" :fill="true" align="center" :wrap="true">
                <a-typography-text
                  :style="{ fontWeight: 'bolder', fontSize: '24px' }"
                  :heading="5"
                  >题目{{ index + 1 }}
                </a-typography-text>
                <a-popconfirm
                  type="warning"
                  content="确定删除？"
                  @ok="deleteQuestion(index)"
                >
                  <a-button size="mini" status="danger">
                    删除题目{{ index + 1 }}
                  </a-button>
                </a-popconfirm>
                <a-button size="mini" @click="addQuestion(index + 1)">
                  插入题目{{ index + 2 }}
                </a-button>
              </a-space>
              <a-form-item
                style="margin-bottom: 5px"
                field="question.title"
                :label="`问题${index + 1}：`"
              >
                <a-input
                  v-model="question.title"
                  placeholder="输入题目 例如:你喜欢广交朋友吗？"
                />
              </a-form-item>
              <a-space
                size="mini"
                :fill="true"
                align="baseline"
                :direction="'horizontal'"
              >
                <a-button
                  type="primary"
                  status="warning"
                  size="mini"
                  @click="addQuestionOption(question, question.options?.length)"
                  shape="circle"
                >
                  <icon-plus />
                </a-button>
                <a-typography-paragraph
                  type="secondary"
                  spacing="close"
                  style="font-size: 10px"
                >
                  共{{ question.options?.length }}个选项
                </a-typography-paragraph>
              </a-space>
              <!--遍历题目选项-->
              <a-form-item
                field="question.title"
                :label="`选项 ${optionIndex + 1}`"
                v-for="(option, optionIndex) in question.options"
                :key="optionIndex"
                :content-flex="false"
                :marge-props="false"
                :label-col-props="{ span: 2, offset: 0 }"
                :wrapper-col-props="{ span: 22, offset: 0 }"
                style="margin-bottom: 3px; color: #1c222f; font-weight: bold"
              >
                <a-form-item
                  label="选项键"
                  extra="选项键"
                  style="margin-bottom: 5px"
                >
                  <a-input
                    style="border: 1px solid #42b983"
                    size="small"
                    v-model="option.key"
                    placeholder="输入选项键 例如:A/B/C/D"
                  />
                </a-form-item>
                <a-form-item
                  label="选项值"
                  style="margin-bottom: 5px"
                  extra="选项值"
                >
                  <a-input
                    style="border: 1px solid #1c222f"
                    size="small"
                    v-model="option.value"
                    placeholder="输入选项值 例如:喜欢/不喜欢"
                  />
                </a-form-item>
                <a-form-item
                  label="选项结果"
                  style="margin-bottom: 5px"
                  extra="选项结果"
                  v-if="
                    getAppType === APP_TYPE_ENUM.TEST &&
                    getAppScoringResult !== APP_SCORING_STRATEGY_ENUM.AI
                  "
                >
                  <a-input
                    size="small"
                    style="border: 1px solid #ff7d00"
                    v-model="option.result"
                    placeholder="输入选项结果 例如:I/E"
                  />
                </a-form-item>
                <a-form-item
                  label="选项得分"
                  style="margin-bottom: 5px"
                  extra="选项得分"
                  v-if="getAppType === APP_TYPE_ENUM.SCORE"
                >
                  <a-input-number
                    size="small"
                    style="border: 1px solid #ff7d00"
                    v-model="option.score"
                    placeholder="输入选项结果(数字) 例如:5/0"
                  />
                </a-form-item>
                <a-space size="mini" :fill="true" align="start" :wrap="true">
                  <a-button
                    size="mini"
                    @click="addQuestionOption(question, optionIndex + 1)"
                  >
                    ↓插入选项{{ optionIndex + 2 }}
                  </a-button>
                  <a-popconfirm
                    type="warning"
                    content="确定删除该选项?"
                    @ok="deleteQuestionOption(question, optionIndex)"
                  >
                    <a-button size="mini" status="danger">
                      ↑删除选项{{ optionIndex + 1 }}
                    </a-button>
                  </a-popconfirm>
                </a-space>
                <hr style="border: 1px solid #a1a5b0" />
              </a-form-item>
              <!--题目选项结尾-->
            </div>
          </a-grid>
        </a-form-item>
      </div>
      <a-form-item>
        <a-space direction="vertical" style="width: 100%; margin-top: 20px">
          <a-button size="large" :long="true" html-type="submit" type="dashed">
            提交
          </a-button>
          <div id="bottom" />
        </a-space>
      </a-form-item>
    </a-form>
  </a-space>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import { IconPlus } from "@arco-design/web-vue/es/icon";
import { withDefaults, defineProps } from "vue";
import { useRouter } from "vue-router";
import {
  addQuestionUsingPost,
  editQuestionUsingPost,
  listQuestionVoByPageUsingPost,
} from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { APP_SCORING_STRATEGY_ENUM, APP_TYPE_ENUM } from "@/constant/app";
import AiGenerateQuestionDrawer from "@/views/add/components/AiGenerateQuestionDrawer.vue";

interface Props {
  appId: string;
}

const router = useRouter();
//题目列表
// eslint-disable-next-line no-undef
const questionContent = ref<API.QuestionContentDTO[]>([]);
/**
 * 添加题目
 * @param index
 */
const addQuestion = (index: number) => {
  questionContent.value.splice(index, 0, {
    title: "",
    options: [],
  });
};

/**
 * 删除题目
 * @param index
 */
const deleteQuestion = (index: number) => {
  questionContent.value.splice(index, 1);
};
/**
 * 添加题目选项
 * @param index
 */
// eslint-disable-next-line no-undef
const addQuestionOption = (question: API.QuestionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 0, {
    key: "",
    value: "",
  });
};

/**
 * 删除题目选项
 * @param index
 */
const deleteQuestionOption = (
  // eslint-disable-next-line no-undef
  question: API.QuestionContentDTO,
  index: number
) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 1);
};

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

// eslint-disable-next-line no-undef
const oldQuestion = ref<API.QuestionVO>();
// eslint-disable-next-line no-undef
const appInfo = ref<API.AppVO>();

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  const res = await listQuestionVoByPageUsingPost({
    appId: props.appId,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0 && res.data.data?.records) {
    oldQuestion.value = res.data.data?.records[0];
    if (oldQuestion.value) {
      questionContent.value = oldQuestion.value.questionContent ?? [];
    }
  } else {
    message.error("获取信息失败！" + res.data.msg);
  }
};
/**
 * 加载应用信息
 * @param appId
 */
const loadAppInfo = async (appId) => {
  const res = await getAppVoByIdUsingGet({
    id: appId,
  });
  if (res.data.code === 0) {
    appInfo.value = res.data.data;
  } else {
    message.error("获取应用信息失败！" + res.data.msg);
  }
};
/**
 * 根据应用信息获取应用类型
 */
const getAppType = computed(() => {
  if (appInfo.value) {
    return appInfo.value.appType;
  }
  return "";
});
const getAppScoringResult = computed(() => {
  if (appInfo.value) {
    return appInfo.value.scoringStrategy;
  }
  return "";
});
//获取旧数据
watchEffect(() => {
  loadData();
  loadAppInfo(props.appId);
});

const handleSubmit = async () => {
  if (!props.appId || !questionContent.value) {
    return;
  }
  let res;
  //如果是修改题目
  if (oldQuestion.value?.id) {
    res = await editQuestionUsingPost({
      id: oldQuestion.value?.id,
      questionContent: questionContent.value,
    });
  } else {
    //如果是创建题目
    res = await addQuestionUsingPost({
      appId: props.appId,
      questionContent: questionContent.value,
    });
  }
  if (res.data.code === 0) {
    message.success("提交成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${props.appId}`);
    }, 3000);
  } else {
    message.error("提交失败！" + res.data.message);
  }
};
/**
 * 当AI生成题目成功时执行
 */
// eslint-disable-next-line no-undef
const onAiResponseSuccess = (result: API.QuestionContentDTO[]) => {
  message.success(`成功！共生成${result.length}道题目，请核查题目！`);
  questionContent.value = [...questionContent.value, ...result];
  setTimeout(() => {
    let divElement = document.getElementById("bottom");
    divElement.scrollIntoView({
      behavior: "smooth",
      block: "end",
      inline: "nearest",
    });
  }, 700);
};

/**
 * 当AI流式实时生成题目成功时执行
 */
// eslint-disable-next-line no-undef
const onAiSSEResponseSuccess = (result: API.QuestionContentDTO) => {
  questionContent.value = [...questionContent.value, result];
  setTimeout(() => {
    let divElement = document.getElementById("bottom");
    divElement.scrollIntoView({
      behavior: "smooth",
      block: "end",
      inline: "nearest",
    });
  }, 50);
};
/**
 * 当AI生成题目开始时执行
 * @param event
 */
const onSSEStart = (event: any) => {
  message.info("AI生成题目中，请稍后...");
};
/**
 * 当AI生成题目关闭时执行
 * @param event
 */
const onSSEClose = (event: any) => {
  message.success("AI生成题目完成！");
};
</script>
<style scoped>
.question-created-page {
  margin-top: 20px;
  padding: 20px 40px 50px;
  background-color: #fff;
  border-radius: 20px;
  transition: all 0.3s ease-out;
}

.question-created-page:hover {
  box-shadow: 0 0 10px #a1a5b0;
}

.grid-questions-grid {
  width: 100%;
}

.grid-questions-container {
  max-width: 95vw;
}

.question-form-top {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-items: center;
  width: 100%;
}

.question-form-top-container {
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  width: 95vw;
  max-width: 100%;
}

.question-form-mid {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-items: center;
  width: 100%;
}

.ai-generator-button {
  margin-right: 20px;
}
</style>
