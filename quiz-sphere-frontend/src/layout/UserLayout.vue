<template>
  <div id="user-layout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <a-space class="header-inner" @click="doIndexClick()">
          <img class="logo" src="@/assets/logo.png" alt="logo" />
          <a-space
            style="margin-right: 40px"
            direction="vertical"
            align="center"
          >
            <div class="title-main">测试鸭-AI打分中心</div>
            <div class="title-sub">QuziSphere With AI GPT</div>
          </a-space>
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://github.com/BR184" target="_blank">
          我的个人仓库地址
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();
const doIndexClick = () => {
  router.push("/");
};
</script>

<style scoped>
#user-layout {
  text-align: center;
  background-color: #f4f5f6;
}

#user-layout .logo {
  width: 80px;
  height: 80px;
  margin-right: 20px;
  border-radius: 25px;
}

#user-layout .content {
  display: flex;
  justify-content: center;
  padding: 20px;
  background-color: #f4f5f6;
}

#user-layout .header {
  margin-top: 40px;
  font-size: 50px;
}

#user-layout .header-inner {
  padding: 20px;
  background-color: #ffffff;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
  user-select: none;
}

#user-layout .header-inner:hover {
  background-color: #f1f1f1;
  box-shadow: 0 0 12px #c5c5c5;
}

#user-layout .footer {
  padding: 20px;
  text-align: center;
  background-color: #f4f5f6;
}

#user-layout .footer a {
  color: #a1a5b0;
  text-decoration: none;
  transition: color 0.2s ease-in-out;
}

#user-layout .footer a:hover {
  color: #7991d1;
}

#user-layout .title-main {
  color: #1c222f;
  font-weight: bold;
  font-size: 30px;
  white-space: nowrap;
  text-align: center;
}

#user-layout .title-sub {
  color: #a1a5b0;
  font-size: 18px;
  white-space: nowrap;
  text-align: center;
}
</style>
