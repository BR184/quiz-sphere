<template>
  <a-modal v-model:visible="visible" @cancel="closeModal" :footer="false">
    <template #title>
      {{ title }}
    </template>
    <div class="share-container">
      <a-space :direction="'vertical'" align="center" style="height: 100%">
        <a-typography-title heading="4">手机扫码查看</a-typography-title>
        <img :src="codeImg" style="scale: 1.15" />
      </a-space>
      <hr style="border: 1px solid #d0d0d0; width: 200px" />
      <a-space :direction="'vertical'" align="center" style="height: 100%">
        <a-typography-title heading="4">复制分享链接</a-typography-title>
        <a-typography-paragraph style="margin-top: 10px" copyable
          >{{ link }}
        </a-typography-paragraph>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { defineExpose, withDefaults, defineProps, ref } from "vue";
import QRCode from "qrcode";
import message from "@arco-design/web-vue/es/message";

/**
 * 自定义组件属性类型
 */
interface Props {
  //分享链接
  link: string;
  //分享标题
  title: string;
}

//分享二维码
const codeImg = ref();

//模态框显示
const visible = ref(false);

//打开模态框
const openModal = () => {
  visible.value = true;
};

/**
 * 暴露给父组件
 */
defineExpose({
  openModal,
});

//确认按钮
const closeModal = () => {
  visible.value = false;
};

/**
 * 组件初始化值
 */
const props = withDefaults(defineProps<Props>(), {
  title: () => "分享",
  link: () => "分享链接加载中...请稍后...",
});
//生成二维码
QRCode.toDataURL(props.link)
  .then((url: any) => {
    codeImg.value = url;
  })
  .catch((err: any) => {
    message.error("生成二维码失败" + err.message);
  });
</script>

<style scoped>
.share-container {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  width: 100%;
  height: 100%;
}
</style>
