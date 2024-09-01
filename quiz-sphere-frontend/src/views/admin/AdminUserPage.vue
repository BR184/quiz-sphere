<template>
  <div id="admin-user-page">
    <a-space direction="vertical" align="end" :fill="true">
      <a-form
        :model="formSearchParams"
        :style="{ marginBottom: '20px' }"
        label-align="left"
        auto-label-width
        layout="inline"
        @submit="doSearch"
      >
        <a-form-item field="userAccount" label="用户账号">
          <a-input
            size="large"
            v-model="formSearchParams.userAccount"
            placeholder="搜索用户账号"
            :allow-clear="true"
            @blur="doSearch"
            @clear="doSearch"
          />
        </a-form-item>
        <a-form-item field="userName" label="用户昵称">
          <a-input
            size="large"
            v-model="formSearchParams.userName"
            placeholder="搜索用户昵称"
            :allow-clear="true"
            @blur="doSearch"
            @clear="doSearch"
          />
        </a-form-item>
        <a-form-item field="userName" label="用户简介">
          <a-input
            size="large"
            v-model="formSearchParams.userProfile"
            placeholder="搜索用户简介"
            :allow-clear="true"
            @blur="doSearch"
            @clear="doSearch"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" size="large" :long="true" html-type="submit"
            >搜索
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
      <template #userAvatar="{ record }">
        <a-avatar shape="square" size="large" :image-url="record.userAvatar" />
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
          <a-popconfirm content="确定要删除吗？" @ok="doDelete(record)">
            <a-button status="danger">删除</a-button>
          </a-popconfirm>
        </a-space>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import {
  deleteUserUsingPost,
  listUserByPageUsingPost,
} from "@/api/userController";
import message from "@arco-design/web-vue/es/message";
// eslint-disable-next-line no-undef
const formSearchParams = ref<API.UserQueryRequest>({});
// 初始化搜索条件（默认值）
const initSearchParams = {
  current: 1,
  pageSize: 10,
};

/**
 * 执行搜索
 */
const doSearch = () => {
  searchParams.value = {
    ...searchParams,
    ...formSearchParams.value,
  };
};

// eslint-disable-next-line no-undef
const searchParams = ref<API.UserQueryRequest>({
  ...initSearchParams,
});
// eslint-disable-next-line no-undef
const dataList = ref<API.User[]>([]);
const total = ref<number>(0);

/**
 * 加载数据
 */
const loadData = async () => {
  const res = await listUserByPageUsingPost(searchParams.value);
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
// eslint-disable-next-line no-undef
const doDelete = async (record: API.User) => {
  if (!record.id) {
    message.error("id为空，无法删除！");
    return;
  }
  const res = await deleteUserUsingPost({
    id: record.id,
  });
  if (res.data.code === 0) {
    loadData();
    message.success("删除用户成功！");
  } else {
    message.error("删除用户失败！" + res.data.msg);
  }
};
/**
 * 监听 searchParams 变化,改变时重新加载数据
 */
watchEffect(() => {
  loadData();
});

const columns = [
  {
    title: "id",
    dataIndex: "id",
  },
  {
    title: "账号",
    dataIndex: "userAccount",
  },
  {
    title: "微信开放平台id",
    dataIndex: "unionId",
  },
  {
    title: "公众号openId",
    dataIndex: "mpOpenId",
  },
  {
    title: "用户昵称",
    dataIndex: "userName",
  },
  {
    title: "用户头像",
    dataIndex: "userAvatar",
    slotName: "userAvatar",
  },
  {
    title: "用户简介",
    dataIndex: "userProfile",
  },
  {
    title: "用户角色",
    dataIndex: "userRole",
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
    dataIndex: "optional",
    slotName: "optional",
  },
];
</script>

<style scoped>
#home-container {
}
</style>
