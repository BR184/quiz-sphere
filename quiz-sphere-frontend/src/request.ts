import axios from "axios";

const myAxios = axios.create({
  baseURL: "http://localhost:8101",
  timeout: 10000,
  withCredentials: true,
});

// 添加请求拦截器
myAxios.interceptors.request.use(
  function (config) {
    // 在发送请求之前
    return config;
  },
  function (error) {
    // 对请求错误
    return Promise.reject(error);
  }
);

// 添加响应拦截器
myAxios.interceptors.response.use(
  function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 响应数据处理
    const { data } = response;
    // 未登录
    if (data.code === 40100) {
      // 非获取信息的请并且不在登录页面则跳转
      if (
        !response.request.responseURL.includes("user/get/login") &&
        window.location.pathname.includes("/user/login")
      ) {
        window.location.href = `/user/login?redirect=${window.location.href}`;
      }
    }
    return response;
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误
    return Promise.reject(error);
  }
);

export default myAxios;
