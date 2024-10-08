import { createApp } from "vue";
import App from "./App.vue";
import ArcoVue from "@arco-design/web-vue";
import "@arco-design/web-vue/dist/arco.css";
import router from "./router";
import { createPinia } from "pinia";

createApp(App).use(ArcoVue).use(router).use(createPinia()).mount("#app");
