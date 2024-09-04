<template>
  <div class="answer-result-container">
    <a-card>
      <a-row style="margin-bottom: 16px">
        <a-col flex="auto" class="content-wapper">
          <a-typography-title
            v-if="data.appType == APP_TYPE_ENUM.TEST"
            style="font-weight: bold; max-width: 900px; text-wrap: auto"
          >
            {{ data.resultName }}
          </a-typography-title>
          <a-typography-title
            v-if="data.appType == APP_TYPE_ENUM.SCORE"
            style="font-weight: bold"
          >
            得分：{{ data.resultScore }}
          </a-typography-title>
          <hr style="width: 500px; margin-left: 0; border: 1px solid #d7d7d7" />
          <a-typography-title :heading="4" class="userAnswer-desc-container">
            <!--{{ "\xa0\xa0\xa0\xa0\xa0\xa0" }}-->
            {{ data.resultDesc }}
          </a-typography-title>
          <a-typography-paragraph class="userAnswer-author-wapper">
            题目发布者：
            <a-tag
              class="userAnswer-author-container"
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
          <a-typography-paragraph spacing="close" type="secondary">
            结果ID:{{ data.id }}
          </a-typography-paragraph>
          <a-typography-paragraph spacing="close" type="secondary">
            应用ID:{{ data.appId }}
          </a-typography-paragraph>
          <a-typography-paragraph type="secondary">
            创建时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </a-typography-paragraph>
          <a-typography-paragraph type="secondary">
            更新时间：{{ dayjs(data.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
          </a-typography-paragraph>
          <a-space>
            <a-button type="primary" :href="`/answer/do/${data.appId}`"
              >再次答题
            </a-button>
            <a-button>分享应用</a-button>
          </a-space>
        </a-col>
        <a-col flex="400px">
          <a-image
            v-if="data.resultPicture != null"
            width="100%"
            style="margin-top: 50px"
            :src="data.resultPicture"
          />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref, watchEffect, withDefaults } from "vue";
import message from "@arco-design/web-vue/es/message";
import { getUserAnswerVoByIdUsingGet } from "@/api/userAnswerController";
import { useRouter } from "vue-router";
import ACCESS_ENUM from "@/access/accessEnum";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import {
  APP_SCORING_STRATEGY_MAP,
  APP_TYPE_ENUM,
  APP_TYPE_MAP,
} from "../../constant/app";

// eslint-disable-next-line no-undef
const data = ref<API.UserAnswerVO>({});

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
  const res = await getUserAnswerVoByIdUsingGet({
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
.userAnswer-author-container {
  font-weight: bold;
  border-radius: 10px;
  cursor: pointer;
  user-select: none;
}

.content-wapper > .userAnswer-author-wapper {
  margin-top: 5px;
}

.userAnswer-desc-container {
  width: 90vw;
  max-width: 900px;
  text-wrap: auto;
}
</style>
