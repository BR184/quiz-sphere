<template>
  <a-space direction="vertical" class="user-register-page">
    <a-typography-title style="font-weight: normal; margin-bottom: 40px">
      用户注册
    </a-typography-title>
    <a-form
      :model="form"
      :style="{ width: '400px' }"
      @submit="handleSubmit"
      label-align="left"
      auto-label-width
      class="user-login-form"
    >
      <a-form-item field="userAccount" label="账号">
        <a-input
          size="large"
          v-model="form.userAccount"
          placeholder="输入你的用户名"
        />
      </a-form-item>
      <a-form-item
        field="userPassword"
        label="密码"
        tooltip="
          密码长度在8到20之间，且至少包含数字/字母/特殊字符中的两种"
      >
        <a-input-password
          size="large"
          v-model="form.userPassword"
          placeholder="输入你的密码"
        />
      </a-form-item>
      <a-form-item
        field="checkPassword"
        label="确认密码"
        tooltip="
           重复你的密码"
      >
        <a-input-password
          size="large"
          v-model="form.checkPassword"
          placeholder="输入确认密码"
        />
      </a-form-item>
      <a-form-item>
        <a-space direction="vertical" style="width: 220px; margin-top: 20px">
          <a-button
            type="secondary"
            size="large"
            :long="true"
            @click="doLoginButtonClick('/user/login')"
            >登陆
          </a-button>
          <a-button type="primary" size="large" :long="true" html-type="submit"
            >注册
          </a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </a-space>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { userRegisterUsingPost } from "@/api/userController";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const loginUserStore = useLoginUserStore();
const router = useRouter();
//TODO 表单验证+按钮状态调节
const doLoginButtonClick = (key: string) => {
  router.push(key);
};

const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
  // eslint-disable-next-line no-undef
} as API.UserRegisterRequest);

const handleSubmit = async () => {
  const res = await userRegisterUsingPost(form);
  if (res.data.code === 0) {
    await loginUserStore.fetchLoginUser();
    message.success("注册成功");
    await router.push({
      path: "/",
      replace: true,
    });
  } else {
    message.error("注册成功！" + res.data.message);
  }
};
</script>

<style scoped>
.user-register-page {
  margin-top: 50px;
  padding: 80px 80px 60px;
  background-color: white;
  border: 5px solid #f1f1f1;
  border-radius: 50px;

  transition: all 0.3s ease-out;
}

.user-register-page:hover {
  padding: 80px 100px 80px 100px;
  border-top: 5px solid #c5c5c5;
  border-right: 10px solid #c5c5c5;
  border-bottom: 20px solid #c5c5c5;
  border-left: 10px solid #c5c5c5;
  border-radius: 80px;
}

.user-register-form {
}
</style>
