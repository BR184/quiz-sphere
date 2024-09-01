<template>
  <div class="app-detail-container">
    <a-card>
      <a-row style="margin-bottom: 16px">
        <a-col flex="auto" class="content-wrapper">
          <a-typography-title style="font-weight: bold">
            {{ data.appName }}
          </a-typography-title>
          <hr style="width: 500px; margin-left: 0; border: 1px solid #d7d7d7" />
          <a-typography-title :heading="6" class="app-desc-container">
            {{ "\xa0\xa0\xa0\xa0\xa0\xa0" }}
            {{ data.appDesc }}
          </a-typography-title>
          <a-typography-paragraph class="app-author-wrapper">
            作者：
            <a-tag
              class="app-author-container"
              :color="
                data.user?.userRole == ACCESS_ENUM.USER ? 'grey' : '#ff5722'
              "
              size="large"
              checkable
            >
              <a-avatar
                :size="24"
                :image-url="data.user?.userAvatar"
                :style="{
                  marginRight: '12px',
                }"
              />
              <a-typography-text
                :style="{
                  color:
                    data.user?.userRole == ACCESS_ENUM.USER
                      ? '#2c354b'
                      : '#ffffff',
                }"
                >{{
                  data.user?.userName ??
                  "用户" +
                    data.user?.id.toString().substring(0, 4) +
                    "······" +
                    data.user?.id
                      .toString()
                      .substring(
                        data.user?.id.toString().length - 4,
                        data.user?.id.toString().length
                      )
                }}
              </a-typography-text>
            </a-tag>
          </a-typography-paragraph>
          <a-typography-paragraph>
            应用类型： {{ data.appType }}
          </a-typography-paragraph>
          <a-typography-paragraph>
            评分模式： {{ data.scoringStrategy }}
          </a-typography-paragraph>
          <a-typography-paragraph type="secondary">
            创建时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </a-typography-paragraph>
          <a-typography-paragraph type="secondary">
            更新时间：{{ dayjs(data.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
          </a-typography-paragraph>
          <a-space>
            <a-button type="primary">开始答题</a-button>
            <a-button>分享应用</a-button>
          </a-space>
        </a-col>
        <a-col flex="400px">
          <a-image width="100%" :src="data.appIcon" />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref, watchEffect, withDefaults } from "vue";
import message from "@arco-design/web-vue/es/message";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { useRouter } from "vue-router";
import ACCESS_ENUM from "@/access/accessEnum";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";

// eslint-disable-next-line no-undef
const data = ref<API.AppVO>({});

const router = useRouter();

interface Props {
  id: string;
}

/**
 * 组件初始化值
 */
const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return 0;
  },
});

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
  if (res.data.code === 0) {
    data.value = res.data.data;
  } else {
    message.error("获取信息失败！" + res.data.msg);
  }
};

/**
 * 监听 searchParams 变化,改变时重新加载数据
 */
watchEffect(() => {
  loadData();
});
</script>

<style scoped>
.app-author-container {
  font-weight: bold;
  border-radius: 10px;
  cursor: pointer;
  user-select: none;
}

.content-wrapper > .app-author-wrapper {
  margin-top: 30px;
}

.app-desc-container {
  width: 100vh;
  max-width: 1000px;
  text-wrap: auto;
}
</style>
