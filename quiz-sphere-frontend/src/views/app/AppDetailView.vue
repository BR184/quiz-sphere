<template>
  <div class="app-detail-container">
    <a-card>
      <a-row style="margin-bottom: 16px">
        <a-col flex="auto" class="content-wrapper">
          <a-typography-title
            style="font-weight: bold; max-width: 900px; text-wrap: auto"
          >
            {{ data.appName }}
          </a-typography-title>
          <hr style="width: 500px; margin-left: 0; border: 1px solid #d7d7d7" />
          <a-typography-title :heading="6" class="app-desc-container">
            <!--{{ "\xa0\xa0\xa0\xa0\xa0\xa0" }}-->
            {{ data.appDesc }}
          </a-typography-title>
          <a-typography-paragraph class="app-author-wrapper">
            作者：
            <a-tag
              class="app-author-container"
              :color="
                data.user?.userRole == ACCESS_ENUM.USER ? '#d7d7d7' : '#ff5722'
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
                      ? '#1c222f'
                      : '#ffffff',
                }"
                >{{
                  data.user?.userName ??
                  (data.user?.id
                    ? "用户" +
                      data.user?.id.toString().substring(0, 4) +
                      "······" +
                      data.user?.id
                        .toString()
                        .substring(
                          data.user?.id.toString().length - 4,
                          data.user?.id.toString().length
                        )
                    : "神秘用户")
                }}
              </a-typography-text>
            </a-tag>
          </a-typography-paragraph>
          <a-typography-paragraph>
            应用类型： {{ APP_TYPE_MAP[data.appType] }}
          </a-typography-paragraph>
          <a-typography-paragraph>
            评分模式： {{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}
          </a-typography-paragraph>
          <a-typography-paragraph type="secondary">
            创建时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </a-typography-paragraph>
          <a-typography-paragraph type="secondary">
            更新时间：{{ dayjs(data.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
          </a-typography-paragraph>
          <a-space>
            <a-button type="primary" :href="`/answer/do/${id}`"
              >开始答题
            </a-button>
            <a-button @click="doShare">分享应用</a-button>
            <a-button
              type="dashed"
              status="warning"
              v-if="isAuthor"
              :href="`/add/question/${id}`"
              >设置题目
            </a-button>
            <a-button
              type="dashed"
              status="warning"
              v-if="isAuthor"
              :href="`/add/scoring_result/${id}`"
              >设置评分
            </a-button>
            <a-button
              type="outline"
              status="warning"
              v-if="isAuthor"
              :href="`/add/app/${id}`"
              >修改应用
            </a-button>
          </a-space>
        </a-col>
        <a-col flex="420px">
          <a-image width="100%" style="margin-top: 50px" :src="data.appIcon" />
        </a-col>
      </a-row>
    </a-card>
    <share-modal
      :link="sharelink"
      :title="`分享:${data.appName}`"
      ref="shareModalRef"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps, ref, watchEffect, withDefaults } from "vue";
import message from "@arco-design/web-vue/es/message";
import { getAppVoByIdUsingGet } from "@/api/appController";
import { useRouter } from "vue-router";
import ACCESS_ENUM from "@/access/accessEnum";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { useLoginUserStore } from "@/store/userStore";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import ShareModal from "@/components/ShareModal.vue";

// eslint-disable-next-line no-undef
const data = ref<API.AppVO>({});

//获取登录用户
const loginUserStore = useLoginUserStore();
let loginUserId = loginUserStore.loginUser?.id;
//是否为本人

const isAuthor = computed(() => {
  return loginUserId && loginUserId === data.value.userId;
});

const router = useRouter();

interface Props {
  id: string;
}

/**
 * 组件初始化值
 */
const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
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

//分享模块引用
const shareModalRef = ref();
//分享链接
const sharelink = `${window.location.protocol}//${window.location.host}/app/detail/${props.id}`;
//分享
const doShare = (e: Event) => {
  if (shareModalRef.value) {
    shareModalRef.value.openModal();
  }
  //阻止冒泡，防止向上传播点击事件
  e.stopPropagation();
};
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
  width: 90vw;
  max-width: 900px;
  text-wrap: auto;
}
</style>
