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
          max-length="30"
          :show-word-limit="true"
        />
      </a-form-item>
      <a-form-item field="appDesc" label="应用描述">
        <a-textarea
          max-length="1024"
          :show-word-limit="true"
          size="medium"
          v-model="form.appDesc"
          placeholder="请输入应用描述"
        />
      </a-form-item>
      <a-form-item field="appIcon" label="应用图标">
        <a-space class="icon-uploader-container">
          <PictureUploader
            class="icon-uploader"
            :value="form.appIcon"
            :key="form.appIcon"
            :onChange="(value) => (form.appIcon = value)"
            biz="app_icon"
          />
        </a-space>
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
import { ref, watchEffect } from "vue";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import {
  addAppUsingPost,
  editAppUsingPost,
  getAppVoByIdUsingGet,
} from "@/api/appController";
import PictureUploader from "@/components/PictureUploader.vue";
import { withDefaults, defineProps } from "vue";

const router = useRouter();

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

// eslint-disable-next-line no-undef
const oldApp = ref<API.AppVO>();

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.id,
  });
  if (res.data.code === 0 && res.data.data) {
    oldApp.value = res.data.data;
    form.value = res.data.data;
  } else {
    message.error("获取信息失败！" + res.data.msg);
  }
};
//获取旧数据
watchEffect(() => {
  loadData();
});

const form = ref({
  appDesc: "",
  appIcon: "",
  appName: "",
  appType: 0,
  scoringStrategy: 0,
  // eslint-disable-next-line no-undef
} as API.AppAddRequest);

const handleSubmit = async () => {
  let res;
  //如果是修改APP
  if (props.id) {
    res = await editAppUsingPost({
      id: props.id,
      ...form.value,
    });
  } else {
    //如果是创建APP
    res = await addAppUsingPost(form.value);
  }
  if (res.data.code === 0) {
    message.success("提交成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push(`/app/detail/${props.id || res.data.data}`);
    }, 3000);
  } else {
    message.error("提交失败！" + res.data.message);
  }
};
</script>

<style scoped>
.app-created-page {
  margin-top: 20px;
  padding: 20px 100px 50px;
  background-color: #fff;
  border-radius: 50px;
  transition: all 0.3s ease-out;
}

.app-created-page:hover {
  box-shadow: 0 0 10px #a1a5b0;
}

.app-created-form {
}

.icon-uploader-container {
  margin-bottom: 20px;
  transform: scale(1.5);
  transform-origin: left;
}
</style>
