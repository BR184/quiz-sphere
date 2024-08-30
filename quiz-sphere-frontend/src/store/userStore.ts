import { defineStore } from "pinia";
import { getLoginUserUsingGet } from "@/api/userController";
import { ref } from "vue";
import ACCESS_ENUM from "@/access/accessEnum";

/*
 * 用户登录信息
 */
export const useLoginUserStore = defineStore("loginUser", () => {
  const loginUser = ref<API.LoginUserVO>({
    userName: "未登录",
  });

  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser;
  }

  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet();
    if (res.data.code === 0 && res.data.data) {
      // 登录成功
      loginUser.value = res.data.data;
    } else {
      loginUser.value.userRole = ACCESS_ENUM.NOT_LOGIN;
    }
  }

  return {
    loginUser,
    setLoginUser,
    fetchLoginUser,
  };
});
