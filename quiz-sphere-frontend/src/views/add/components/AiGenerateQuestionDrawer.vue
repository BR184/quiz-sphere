<template>
  <a-button type="primary" @click="handleClick">AI题目生成</a-button>
  <a-drawer
    :width="'420px'"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    unmountOnClose
  >
    <template #title>使用AI一键生成题目</template>
    <div>
      <!--抽屉内部开始-->
      <a-form
        :model="form"
        :style="{ width: '400px' }"
        @submit="handleSubmit"
        label-align="left"
        auto-label-width
        class="app-created-form"
      >
        <a-form-item label="应用ID">
          {{ appId }}
        </a-form-item>
        <a-form-item field="questionNumber" label="生成题目数量">
          <a-input
            min="5"
            max="20"
            size="medium"
            v-model="form.questionNumber"
            placeholder="生成题目数量"
          />
        </a-form-item>
        <a-form-item field="optionNumber" label="生成选项数量">
          <a-input
            min="2"
            max="6"
            size="medium"
            v-model="form.optionNumber"
            placeholder="生成题目数量"
          />
        </a-form-item>
        <a-form-item>
          <a-space direction="vertical" style="width: 100%; margin-top: 20px">
            <a-button
              :loading="waitingResponse"
              size="large"
              html-type="submit"
            >
              {{ waitingResponse ? "AI生成中..." : "生成题目" }}
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
      <!--抽屉内部结束-->
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { withDefaults, defineProps } from "vue";
import message from "@arco-design/web-vue/es/message";
import { aiGenerateQuestionUsingPost } from "@/api/questionController";

interface Props {
  appId: string;
  // eslint-disable-next-line no-undef
  onSuccess: (result: API.QuestionContentDTO[]) => void;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const form = reactive({
  optionNumber: 2,
  questionNumber: 10,
  // eslint-disable-next-line no-undef
} as API.QuestionAiGenerateRequest);

const handleSubmit = async () => {
  if (!props.appId) {
    message.error("请先创建应用！");
    return;
  }
  waitingResponse.value = true;
  const res = await aiGenerateQuestionUsingPost({
    appId: props.appId,
    ...form,
  });
  if (res.data.code === 0 && res.data.data.length > 0) {
    if (props.onSuccess) {
      props.onSuccess(res.data.data);
      //关闭抽屉
      handleCancel();
    } else {
      message.error("题目格式化失败，请重试！");
    }
  } else {
    message.error("生成失败，请重试!,原因:" + res.data.message);
  }
  waitingResponse.value = false;
};

const visible = ref(false);

const waitingResponse = ref(false);

const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};
</script>
