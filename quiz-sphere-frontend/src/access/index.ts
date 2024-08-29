import router from "@/router";
import { userLoginUserStore } from "@/store/userStore";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";
//进入页面前进行全局校验
router.beforeEach(async (to, from, next) => {
  //获取当前登录用户
  const loginUserStore = userLoginUserStore();
  let loginUser = loginUserStore.loginUser;
  //如果之前没有尝试过获取用户信息，则自动登录
  if (!loginUser || !loginUser.userRole) {
    //加await是因为需要等待获取用户信息
    await loginUserStore.fetchLoginUser();
    loginUser = loginUserStore.loginUser;
  }
  //当前页面需要的权限
  const requireAuth = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN;
  //如果需要登录
  if (requireAuth !== ACCESS_ENUM.NOT_LOGIN) {
    //如果当前用户未登录
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
    ) {
      next(`/user/login?redirect=${to.fullPath}`);
    }
    //如果当前用户已登录，判断权限是否足够，不足跳转到无权限页面
    if (!checkAccess(loginUser, requireAuth)) {
      next(`/noAuth`);
      return;
    }
  }
  next();
});
