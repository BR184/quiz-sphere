<template>
  <div id="home">
    <a-space
      :direction="'vertical'"
      class="search-bar"
      align="center"
      :fill="true"
    >
      <a-input-search
        :style="{ width: '320px' }"
        placeholder="Please enter something"
        search-button
      />
    </a-space>
  </div>
</template>

<script setup lang="ts">
const a = 1;
</script>

<style scoped>
#home {
}
</style>
