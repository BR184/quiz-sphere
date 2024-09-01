<template>
  <div id="basic-layout">
    <a-layout style="height: 100vh">
      <a-layout-header class="header">
        <global-header></global-header>
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
import GlobalHeader from "@/components/GlobalHeader.vue";
</script>

<style scoped>
#basic-layout .header {
}

#basic-layout .content {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto 30px;
  padding: 20px 50px;
  background-color: #fff;
}

#basic-layout .footer {
  padding: 20px;
  text-align: center;
  background-color: #f4f5f6;
}

#basic-layout .footer a {
  color: #a1a5b0;
  text-decoration: none;
  transition: color 0.2s ease-in-out;
}

#basic-layout .footer a:hover {
  color: #7991d1;
}
</style>
