<template>
  <div id="appStatisticPage">
    <h2>热门应用统计</h2>
    <v-chart :option="appAnswerCountOptions" style="height: 300px" />
    <h2>作答结果统计</h2>
    <div class="search-bar">
      <a-input-search
        :style="{ width: '360px' }"
        placeholder="输入APP ID"
        search-button
        size="large"
        @search="(value) => loadAppAnswerResultCountData(value)"
      />
    </div>
    <v-chart :option="appAnswerResultCountOptions" style="height: 300px" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import message from "@arco-design/web-vue/es/message";
import {
  getAppAnswerCountUsingGet,
  getAppAnswerResultCountUsingGet,
} from "@/api/statisticController";
import "echarts";
import VChart from "vue-echarts";
// eslint-disable-next-line no-undef
const appAnswerCountList = ref<API.AppAnswerCountDTO[]>([]);
// eslint-disable-next-line no-undef
const appAnswerResultCountList = ref<API.AppAnswerResultCountDTO[]>([]);

/**
 * 加载APP热度数据
 */
const loadAppAnswerCountData = async () => {
  const res = await getAppAnswerCountUsingGet();
  if (res.data.code === 0) {
    appAnswerCountList.value = res.data.data || [];
  } else {
    message.error("获取信息失败！" + res.data.msg);
  }
};
/**
 * 加载选项结果数据
 */
const loadAppAnswerResultCountData = async (appId: string) => {
  if (!appId) {
    return;
  }
  const res = await getAppAnswerResultCountUsingGet({
    appId: appId as any,
  });
  if (res.data.code === 0) {
    appAnswerResultCountList.value = res.data.data || [];
  } else {
    message.error("获取信息失败！" + res.data.msg);
  }
};

const appAnswerCountOptions = computed(() => {
  return {
    xAxis: {
      type: "category",
      data: appAnswerCountList.value.map((item) => item.appId),
      name: "应用ID",
    },
    yAxis: {
      type: "value",
      name: "作答数",
    },
    series: [
      {
        data: appAnswerCountList.value.map((item) => item.answerCount),
        type: "bar",
      },
    ],
  };
});
const appAnswerResultCountOptions = computed(() => {
  return {
    tooltip: {
      trigger: "item",
    },
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        name: "结果占比图",
        type: "pie",
        radius: "50%",
        data: appAnswerResultCountList.value.map((item) => {
          return { value: item.resultCount, name: item.resultName };
        }),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };
});

/**
 * 获取APP答案选择数列表
 */
watchEffect(() => {
  loadAppAnswerResultCountData("");
});
/**
 * 获取APP作答数列表
 */
watchEffect(() => {
  loadAppAnswerCountData();
});
</script>

<style scoped></style>
