<template>
  <div id="user-layout">
    <a-layout style="height: 100vh">
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();
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
  padding: 5px;
  background-color: #f4f5f6;
}

#user-layout .header {
  margin-top: 20px;
  margin-bottom: 10px;
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
