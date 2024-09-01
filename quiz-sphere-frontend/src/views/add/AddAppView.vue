<template>
  <a-space direction="vertical" class="app-created-page">
    <a-typography-title style="font-weight: normal; margin-bottom: 40px">
      创建应用
    </a-typography-title>
    <a-form
      :model="form"
      :style="{ width: '400px' }"
      @submit="handleSubmit"
      label-align="left"
      auto-label-width
      class="app-created-form"
    >
      <a-form-item field="appName" label="应用名称">
        <a-input
          size="medium"
          v-model="form.appName"
          placeholder="请输入应用名称"
        />
      </a-form-item>
      <a-form-item field="appDesc" label="应用描述">
        <a-input
          size="medium"
          v-model="form.appDesc"
          placeholder="请输入应用描述"
        />
      </a-form-item>
      <a-form-item field="appIcon" label="应用图标">
        <PictureUploader
          :value="form.appIcon"
          :onChange="(value) => (form.appIcon = value)"
          biz="app_icon"
        />
      </a-form-item>
      <a-form-item field="appType" label="应用类型">
        <a-select
          v-model="form.appType"
          :style="{ width: '320px' }"
          placeholder="选择应用类型"
        >
          <a-option
            v-for="(value, key) of APP_TYPE_MAP"
            :value="Number(key)"
            :label="value"
            :key="value"
          />
        </a-select>
      </a-form-item>
      <a-form-item field="scoringStrategy" label="评分策略">
        <a-select
          v-model="form.scoringStrategy"
          :style="{ width: '320px' }"
          placeholder="选择评分模式"
        >
          <a-option
            v-for="(value, key) of APP_SCORING_STRATEGY_MAP"
            :value="Number(key)"
            :label="value"
            :key="value"
          />
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-space direction="vertical" style="width: 285px; margin-top: 20px">
          <a-button type="primary" size="large" :long="true" html-type="submit"
            >提交
          </a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </a-space>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import { addAppUsingPost } from "@/api/appController";
import PictureUploader from "@/components/PictureUploader.vue";

const router = useRouter();

const form = reactive({
  appDesc: "",
  appIcon: "",
  appName: "",
  appType: 0,
  scoringStrategy: 0,
  // eslint-disable-next-line no-undef
} as API.AppAddRequest);

const handleSubmit = async () => {
  const res = await addAppUsingPost(form);
  if (res.data.code === 0) {
    message.success("创建成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${res.data.data}`);
    }, 3000);
  } else {
    message.error("创建失败！" + res.data.message);
  }
};
</script>

<style scoped>
.app-created-page {
  margin-top: 50px;
  padding: 80px 80px 150px;
  background-color: #fff;
  border-radius: 50px;
  transition: all 0.3s ease-out;
}

.app-created-page:hover {
  box-shadow: 0 0 10px #a1a5b0;
}

.app-created-form {
}
</style>
