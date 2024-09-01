<template>
  <div id="app">
    <template
      v-if="route.path.startsWith('/user') || route.path.startsWith('/add')"
    >
      <user-layout />
    </template>
    <template v-else>
      <basic-layout />
    </template>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from "@/layout/BasicLayout.vue";
import { useRoute } from "vue-router";
import "@/access";
import { onMounted } from "vue";
import UserLayout from "@/layout/UserLayout.vue";

const route = useRoute();

/**
 * 全局初始化
 */
const doInit = () => {
  // TODO: 全局初始化
  console.log("doInit");
};

onMounted(() => {
  doInit();
});
</script>
<style scoped></style>
