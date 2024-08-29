import ACCESS_ENUM from "@/access/accessEnum";

/**
 * 检查登陆用户权限
 * @param loginUser 当前登陆用户
 * @param needAccess 需要检查的权限
 * @return boolean 是否有权限
 */
const checkAccess = (
  loginUser: API.LoginUserVO,
  needAccess: string = ACCESS_ENUM.NOT_LOGIN
) => {
  //获取当前用户的权限，获取不到则为未登录
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true;
  }
  //如果需要登录
  if (needAccess === ACCESS_ENUM.USER) {
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false;
    }
    return true;
  }
  //如果需要管理员权限
  if (needAccess === ACCESS_ENUM.ADMIN) {
    if (loginUserAccess === ACCESS_ENUM.ADMIN) {
      return true;
    }
    return false;
  }
  //如果都没有匹配
  return false;
};

export default checkAccess;
