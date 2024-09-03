<template>
  <div id="home-container">
    <a-space
      :direction="'vertical'"
      class="search-bar"
      align="center"
      :fill="true"
    >
      <a-input-search
        :style="{ width: '360px' }"
        placeholder="寻找属于你的测试"
        search-button
        size="large"
      />
    </a-space>
    <a-list
      class="list-demo-action-layout"
      :grid-props="{ gutter: [20, 20], sm: 24, md: 12, lg: 8, xl: 6 }"
      :bordered="false"
      :data="dataList"
      :pagination-props="{
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="onPageChange"
    >
      <template #item="{ item }">
        <AppCard :app="item" />
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import AppCard from "@/components/AppCard.vue";
import message from "@arco-design/web-vue/es/message";
import { listAppVoByPageUsingPost } from "@/api/appController";

const initSearchParams = {
  current: 1,
  pageSize: 12,
};
// eslint-disable-next-line no-undef
const searchParams = ref<API.UserQueryRequest>({
  ...initSearchParams,
});
// eslint-disable-next-line no-undef
const dataList = ref<API.AppVO[]>([]);
const total = ref<number>(0);

/**
 * 加载数据
 */
const loadData = async () => {
  const res = await listAppVoByPageUsingPost(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取信息失败！" + res.data.msg);
  }
};
/**
 * 格式化日期
 */
const formatDate = (date: string) => {
  return new Date(date);
};

const onPageChange = (page: number) => {
  searchParams.value = {
    //这里的三个点详细的功能是：
    //将 searchParams.value 对象中的所有属性展开，并将 current 属性设置为 page 参数的值。
    //这样做的目的是为了创建一个新的对象，而不是直接修改 searchParams.value 对象。
    ...searchParams.value,
    current: page,
  };
};
/**
 * 监听 searchParams 变化,改变时重新加载数据
 */
watchEffect(() => {
  loadData();
});
</script>

<style scoped>
.list-demo-action-layout .image-area {
  width: 183px;
  height: 119px;
  overflow: hidden;
  border-radius: 2px;
}

.list-demo-action-layout .list-demo-item {
  padding: 20px 0;
  border-bottom: 1px solid var(--color-fill-3);
}

.list-demo-action-layout .image-area img {
  width: 100%;
}

.list-demo-action-layout .arco-list-item-action .arco-icon {
  margin: 0 4px;
}
</style>
