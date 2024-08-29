<template>
  <a-row id="global-header" align="center" :wrap="false" class="grid">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="seletedKey"
        @menu-item-click="doMenuItemClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="" />
            <div class="title">QuizSphere</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px">
      <div v-if="loginUserStore.loginUser.id">
        <a-space>
          欢迎
          <div class="user-name">
            {{ loginUserStore.loginUser.userName ?? "匿名用户" }}
          </div>
        </a-space>
      </div>
      <div v-else>
        <a-button type="primary" href="/user/login">登陆</a-button>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "@/router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { userLoginUserStore } from "@/store/userStore";
import CheckAccess from "@/access/checkAccess";

let loginUserStore = userLoginUserStore();

const router = useRouter();
//当前选择的菜单项
const seletedKey = ref(["/"]);
//监听路由变化，自动更新当前的菜单项
router.afterEach((to, from, failure) => {
  seletedKey.value = [to.path];
});
const doMenuItemClick = (key: string) => {
  router.push(key);
};
//过滤菜单栏路由
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    //根据配置文件判断
    if (item.meta?.hideInMenu) {
      return false;
    }
    //根据权限判断
    if (!CheckAccess(loginUserStore.loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  });
});
</script>

<style scoped>
.grid {
  margin-bottom: 15px;
  box-shadow: #f4f5f6 3px 3px 5px;
}

.title-bar {
  display: flex;
  align-items: center;
}

.title {
  margin-left: 20px;
  color: #000000;
  font-weight: 600;
  font-size: 20px;
}

.logo {
  box-sizing: border-box;
  width: 50px;
  border-radius: 15px;
}

.user-name {
  color: #7991d1;
  font-weight: 800;
  font-size: 15px;
}
</style>
