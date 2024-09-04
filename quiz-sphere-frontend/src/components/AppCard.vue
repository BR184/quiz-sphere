<template>
  <a-space class="app-card-cotainer">
    <a-card class="app-card" hoverable @click="doCardClick">
      <template #actions>
        <span class="icon-hover"> <IconThumbUp /> </span>
        <span class="icon-hover"> <IconShareInternal /> </span>
        <span class="icon-hover"> <IconMore /> </span>
      </template>
      <template #cover>
        <div
          :style="{
            height: '204px',
            overflow: 'hidden',
          }"
        >
          <img
            :style="{ width: '100%', transform: 'translateY(0px)' }"
            :alt="app.appName"
            :src="app.appIcon"
          />
        </div>
      </template>
      <a-card-meta
        :title="
          app.appName?.length > 17
            ? app.appName?.substring(0, 17) + '...'
            : app.appName
        "
        :description="
          app.appDesc.length > 38
            ? app.appDesc.substring(0, 38) + '...'
            : app.appDesc
        "
      >
        <template #avatar>
          <div
            :style="{ display: 'flex', alignItems: 'center', color: '#1D2129' }"
          >
            <a-avatar
              :size="24"
              :image-url="app.user?.userAvatar"
              :style="{ marginRight: '8px' }"
            />
            <a-typography-text
              >{{
                app.user?.userName ??
                (app.user?.id
                  ? "用户" +
                    app.user?.id.toString().substring(0, 4) +
                    "······" +
                    app.user?.id
                      .toString()
                      .substring(
                        app.user?.id.toString().length - 4,
                        app.user?.id.toString().length
                      )
                  : "神秘用户")
              }}
            </a-typography-text>
          </div>
        </template>
      </a-card-meta>
    </a-card>
  </a-space>
</template>

<script setup lang="ts">
import {
  IconThumbUp,
  IconShareInternal,
  IconMore,
} from "@arco-design/web-vue/es/icon";
import { defineProps, withDefaults } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const doCardClick = () => {
  router.push(`/app/detail/${props.app.id}`);
};

interface Props {
  // eslint-disable-next-line no-undef
  app: API.AppVO;
}

const props = withDefaults(defineProps<Props>(), {
  app: () => {
    return {
      id: 0,
      appName: "加载失败",
      appDesc: "暂无描述",
    };
  },
});
</script>
<style scoped>
.icon-hover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  transition: all 0.1s;
}

.icon-hover:hover {
  background-color: rgb(var(--gray-2));
}

.app-card-cotainer {
  margin-top: 50px;
  margin-bottom: 50px;
  margin-left: 12px;
}

.app-card {
  width: 320px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
}

.app-card:hover {
  box-shadow: 0px 0px 6px #dcdcdc;
  transform: scale(1.05);
}

.arco-list-content-wrapper {
  padding-bottom: 50px;
}
</style>
