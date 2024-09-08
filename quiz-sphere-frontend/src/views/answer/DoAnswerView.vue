<template>
  <a-space direction="vertical" class="do-answer-page" align="center">
    <a-typography-title style="font-weight: normal; margin: 0 0 10px">
      {{ app.appName }}
    </a-typography-title>
    <!--做题区域开始-->
    <a-space :direction="'vertical'" align="center" class="do-answer-content">
      <a-typography-title heading="3"
        >{{
          currentQuestion?.title.match(new RegExp(`^${current}[、.:](?!\\d)`))
            ? currentQuestion.title
            : `${current}、${currentQuestion?.title}`
        }}
        <!--移除潜在的重复序号，但是可能会导致题目被错误截取，TODO：更好的题目创建提示！-->
      </a-typography-title>
      <div class="do-answer-choices">
        <a-radio-group
          v-model="currentAnswer"
          :options="questionOptions"
          direction="vertical"
          size="large"
          @change="doValueChange"
        >
          <template #label="{ data }">
            <div class="answer-tag">{{ data.label }}</div>
          </template>
        </a-radio-group>
      </div>
      <div class="do-answer-buttons">
        <a-button v-if="current > 1" circle @click="current -= 1" size="large"
          >上一题
        </a-button>
        <a-button
          type="primary"
          v-if="current === questionContent.length"
          circle
          :disabled="!currentAnswer"
          :loading="waitingResponse"
          @click="checkAnswer"
          size="large"
          >{{ waitingResponse ? "AI评分中..." : "查看结果" }}
        </a-button>
        <a-button
          circle
          status="warning"
          v-if="current < questionContent.length"
          :disabled="!currentAnswer"
          @click="current += 1"
          size="large"
          >下一题
        </a-button>
      </div>
    </a-space>
    <!--做题区域结束-->
    <a-typography-paragraph type="secondary">
      {{ app.appDesc }}
    </a-typography-paragraph>
  </a-space>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watchEffect } from "vue";
import { withDefaults, defineProps } from "vue";
import { useRouter } from "vue-router";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { addUserAnswerUsingPost } from "@/api/userAnswerController";

interface Props {
  appId: string;
}

// eslint-disable-next-line no-undef
const app = ref<API.AppVO>({});

const router = useRouter();

//题目列表
// eslint-disable-next-line no-undef
const questionContent = ref<API.QuestionContentDTO[]>([]);

//当前题目序号
const current = ref(1);
//当前题目
// eslint-disable-next-line no-undef
const currentQuestion = ref<API.QuestionContentDTO>({});
//当前题目选项
const questionOptions = computed(() => {
  return currentQuestion.value?.options
    ? currentQuestion.value?.options.map((option) => {
        return {
          label: `${option.key}.${option.value}`,
          value: option.key,
        };
      })
    : [];
});
//当前答案
const currentAnswer = ref<string>();
//回答列表
const answerList = reactive<string[]>([]);
//等待响应
const waitingResponse = ref(false);

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  //获取APP
  let res: any = await getAppVoByIdUsingGet({
    id: props.appId,
  });
  if (res.data.code === 0) {
    app.value = res.data.data as any;
  } else {
    message.error("获取应用失败！" + res.data.msg);
  }
  //获取题目
  res = await listQuestionVoByPageUsingPost({
    appId: props.appId,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0 && res.data.data?.records) {
    questionContent.value = res.data.data?.records[0].questionContent;
  } else {
    message.error("获取题目失败！" + res.data.msg);
  }
};

//获取旧数据
watchEffect(() => {
  loadData();
});
//监听题目变化，自动更新题目选项
watchEffect(() => {
  currentQuestion.value = questionContent.value[current.value - 1];
  currentAnswer.value = answerList[current.value - 1];
});
/**
 * 保存选择过的选项记录
 * @param value
 */
const doValueChange = (value: string) => {
  answerList[current.value - 1] = value;
};

const checkAnswer = async () => {
  if (!props.appId || !answerList) {
    return;
  }
  waitingResponse.value = true;
  const res = await addUserAnswerUsingPost({
    appId: props.appId,
    choices: answerList,
  });
  if (res.data.code === 0 && res.data.data) {
    await router.push(`/answer/result/${res.data.data}`);
  } else {
    message.error("答案提交失败！" + res.data.message);
  }
  waitingResponse.value = false;
};
</script>

<style scoped>
.do-answer-page {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 90vw;
  max-width: 1440px;
  margin-top: 20px;
  padding: 20px 40px 50px;
  text-align: center;
  background-color: #f4f5f6;
  border-radius: 20px;
  transition: all 0.3s ease-out;
}

.do-answer-page:hover {
  box-shadow: 0 0 10px #f4f5f6;
}

.do-answer-content {
  margin-bottom: 50px;
  padding: 5px 40px 40px;
  background-color: #fff;
  border-radius: 20px;
}

.do-answer-choices {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-items: center;
  margin-bottom: 15vh;
  font-weight: bold;
}

.answer-tag {
  min-width: 200px;
  margin: 10px;
  padding: 5px 20px;
  font-weight: bold;
  font-size: large;
  text-align: center;
  background-color: #f4f5f6;
  border-radius: 5px;
  transition: all 0.2s ease-in-out;
}

.answer-tag:hover {
  margin: 4px;
  padding: 11px 26px;
  color: #fff;
  background-color: #7c7c7c;
  box-shadow: 0px 0px 5px #a1a5b0;
}

.do-answer-buttons {
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  width: 95vh;
  max-width: 360px;
}

.do-answer-buttons > * {
  width: 30%;
}
</style>
