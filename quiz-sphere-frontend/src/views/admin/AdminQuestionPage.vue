<template>
  <a-space direction="vertical" align="end" :fill="true">
    <a-form
      :model="formSearchParams"
      :style="{ marginBottom: '20px' }"
      layout="inline"
      @submit="doSearch"
    >
      <a-form-item field="appId" label="应用 id">
        <a-input
          v-model="formSearchParams.appId"
          placeholder="请输入应用 id"
          allow-clear
          @blur="doSearch"
          @clear="doSearch"
        />
      </a-form-item>
      <a-form-item field="userId" label="用户 id">
        <a-input
          v-model="formSearchParams.userId"
          placeholder="请输入用户 id"
          allow-clear
          @blur="doSearch"
          @clear="doSearch"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100px">
          搜索
        </a-button>
      </a-form-item>
    </a-form>
  </a-space>
  <a-table
    :columns="columns"
    :data="dataList"
    :pagination="{
      showTotal: true,
      pageSize: searchParams.pageSize,
      current: searchParams.current,
      total,
    }"
    @page-change="onPageChange"
  >
    <template #questionContent="{ record }">
      <div
        class="single-question-cotainer"
        v-for="question in JSON.parse(record.questionContent)"
        :key="question.title"
      >
        {{ formatJSON(question) }}
      </div>
    </template>
    <template #createTime="{ record }">
      <a-date-picker
        :default-value="formatDate(record.createTime)"
        style="width: 180px"
        :readonly="true"
        format="YYYY-MM-DD HH:mm:ss"
        :suffix-icon="false"
      />
    </template>
    <template #updateTime="{ record }">
      <a-date-picker
        :default-value="formatDate(record.updateTime)"
        style="width: 180px"
        :readonly="true"
        format="YYYY-MM-DD HH:mm:ss"
        :suffix-icon="false"
      />
    </template>
    <template #optional="{ record }">
      <a-space>
        <a-button status="danger" @click="doDelete(record)">删除</a-button>
      </a-space>
    </template>
  </a-table>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import {
  deleteQuestionUsingPost,
  listQuestionByPageUsingPost,
} from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";

/**
 * 格式化日期
 */
const formatDate = (date: string) => {
  return new Date(date);
};
// eslint-disable-next-line no-undef
const formSearchParams = ref<API.QuestionQueryRequest>({});

// 初始化搜索条件（不应该被修改）
const initSearchParams = {
  current: 1,
  pageSize: 10,
};
// eslint-disable-next-line no-undef
const searchParams = ref<API.QuestionQueryRequest>({
  ...initSearchParams,
});
// eslint-disable-next-line no-undef
const dataList = ref<API.Question[]>([]);
const total = ref<number>(0);

// 定义一个函数来格式化输出
function formatJSON(input): string {
  input = JSON.stringify(input);
  if (!input || typeof input !== "string") {
    return input;
  }
  // 使用正则表达式解析标题
  const titleRegex = /"title":\s*"([^"]+)"/;
  const titleMatch = input.match(titleRegex);
  const title = titleMatch ? titleMatch[1] : "Unknown Title";

  // 使用正则表达式解析选项，匹配 "result" 或 "score"
  const optionRegex =
    /(?:"result":\s*"([^"]+)"|"score":\s*(\d+)),\s*"value":\s*"([^"]+)",\s*"key":\s*"([^"]+)"/g;
  let match;
  const options = [];

  // 循环匹配所有选项
  while ((match = optionRegex.exec(input)) !== null) {
    const resultOrScore = match[1] || match[2]; // 使用 result 或 score
    const value = match[3];
    const key = match[4];
    options.push(`${resultOrScore}→${value}（${key}）`);
  }

  // 如果没有找到选项，返回默认值
  if (options.length === 0 || title === "Unknown Title") {
    return "*格式匹配失败*" + input;
  }

  // 将标题和选项组合成所需的格式
  return `${title}：${options.join(", ")}`;
}

/**
 * 加载数据
 */
const loadData = async () => {
  const res = await listQuestionByPageUsingPost(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};

/**
 * 执行搜索
 */
const doSearch = () => {
  searchParams.value = {
    ...initSearchParams,
    ...formSearchParams.value,
  };
};

/**
 * 当分页变化时，改变搜索条件，触发数据加载
 * @param page
 */
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

/**
 * 删除
 * @param record
 */
// eslint-disable-next-line no-undef
const doDelete = async (record: API.Question) => {
  if (!record.id) {
    return;
  }
  const res = await deleteQuestionUsingPost({
    id: record.id,
  });
  if (res.data.code === 0) {
    loadData();
  } else {
    message.error("删除失败，" + res.data.message);
  }
};

/**
 * 监听 searchParams 变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
});

// 表格列配置
const columns = [
  {
    title: "id",
    dataIndex: "id",
  },
  {
    title: "题目内容",
    dataIndex: "questionContent",
    slotName: "questionContent",
  },
  {
    title: "应用 id",
    dataIndex: "appId",
  },
  {
    title: "用户 id",
    dataIndex: "userId",
  },
  {
    title: "创建时间",
    dataIndex: "createTime",
    slotName: "createTime",
  },
  {
    title: "更新时间",
    dataIndex: "updateTime",
    slotName: "updateTime",
  },
  {
    title: "操作",
    slotName: "optional",
  },
];
</script>
<style scoped>
.single-question-cotainer {
  margin-top: 4px;
  font-weight: normal;
  background-color: #ececec;
  border-radius: 5px;
}

.single-question-cotainer:hover {
  font-weight: bold;
}
</style>
