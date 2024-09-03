<template>
  <a-space direction="vertical" class="scoringResult-created-page">
    <a-typography-title style="font-weight: normal; margin-bottom: 40px">
      创建结果
    </a-typography-title>
    <a-form
      :model="form"
      :style="{ maxWidth: '1440px', width: '95vw' }"
      @submit="handleSubmit"
      label-align="left"
      auto-label-width
      class="scoringResult-created-form"
    >
      <a-form-item label="应用id">
        {{ appId }}
      </a-form-item>
      <a-form-item field="resultName" label="结果名称">
        <a-input
          size="medium"
          v-model="form.resultName"
          placeholder="请输入结果名称"
          max-length="30"
          :show-word-limit="true"
        />
      </a-form-item>
      <a-form-item field="resultDesc" label="结果描述">
        <a-textarea
          max-length="1024"
          :show-word-limit="true"
          size="medium"
          v-model="form.resultDesc"
          placeholder="请输入结果描述"
        />
      </a-form-item>
      <a-form-item field="resultIcon" label="结果图标">
        <a-space class="icon-uploader-container">
          <PictureUploader
            class="icon-uploader"
            :value="form.resultPicture"
            :key="form.resultPicture"
            :onChange="(value) => (form.resultPicture = value)"
            biz="scoring_result_picture"
          />
        </a-space>
      </a-form-item>
      <a-form-item field="resultProps" label="结果集">
        <a-input-tag
          v-model="form.resultProp"
          :style="{ width: '100%' }"
          placeholder="请输入结果集，回车确认"
          size="large"
          unique-value
          allow-clear
        />
      </a-form-item>
      <a-form-item field="resultScoreRange" label="结果得分范围">
        <a-input-number
          size="large"
          v-model="form.resultScoreRange"
          placeholder="请输入结果得分范围"
        />
      </a-form-item>
    </a-form>
    <a-typography-title :heading="4">评分管理</a-typography-title>
    <ScoringResultTable :appId="appId" :do-update="doUpdate" />
  </a-space>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import {
  addScoringResultUsingPost,
  editScoringResultUsingPost,
  getScoringResultVoByIdUsingGet,
} from "@/api/scoringResultController";
import PictureUploader from "@/components/PictureUploader.vue";
import { withDefaults, defineProps } from "vue";
import ScoringResultTable from "@/views/add/components/scoringResultTable.vue";

const router = useRouter();

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

// eslint-disable-next-line no-undef
const oldScoringResult = ref<API.ScoringResultVO>();

const form = ref({
  resultDesc: "",
  resultName: "",
  resultPicture: "",
  // eslint-disable-next-line no-undef
} as API.ScoringResultAddRequest);

const updateId = ref<any>();

// eslint-disable-next-line no-undef
const doUpdate = (scoringResult: API.ScoringResultVO) => {
  updateId.value = scoringResult.id;
  form.value = scoringResult;
};

/**
 * 加载数据
 */
// const loadData = async () => {
//   if (!props.id) {
//     return;
//   }
//   const res = await getScoringResultVoByIdUsingGet({
//     id: props.id,
//   });
//   if (res.data.code === 0 && res.data.data) {
//     oldScoringResult.value = res.data.data;
//     form.value = res.data.data;
//   } else {
//     message.error("获取信息失败！" + res.data.msg);
//   }
// };
// //获取旧数据
// watchEffect(() => {
//   loadData();
// });
//
//
// const handleSubmit = async () => {
//   let res;
//   //如果是修改APP
//   if (props.id) {
//     res = await editScoringResultUsingPost({
//       id: props.id,
//       ...form.value,
//     });
//   } else {
//     //如果是创建APP
//     res = await addScoringResultUsingPost(form.value);
//   }
//   if (res.data.code === 0) {
//     message.success("提交成功，即将跳转到结果详情页");
//     setTimeout(() => {
//       router.push(`/app/detail/${props.id ?? res.data.data}`);
//     }, 3000);
//   } else {
//     message.error("提交失败！" + res.data.message);
//   }
// };
</script>

<style scoped>
.scoringResult-created-page {
  margin-top: 20px;
  padding: 20px 100px 50px;
  background-color: #fff;
  border-radius: 50px;
  transition: all 0.3s ease-out;
}

.scoringResult-created-page:hover {
  box-shadow: 0 0 10px #a1a5b0;
}

.scoringResult-created-form {
}

.icon-uploader-container {
  margin-bottom: 20px;
  transform: scale(1.5);
  transform-origin: left;
}
</style>
