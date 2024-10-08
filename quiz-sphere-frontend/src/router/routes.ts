import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import NoAuthPage from "@/views/NoAuthPage.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import AdminUserPage from "@/views/admin/AdminUserPage.vue";
import AdminQuestionPage from "@/views/admin/AdminQuestionPage.vue";
import AdminAppPage from "@/views/admin/AdminAppPage.vue";
import AdminScoringResultPage from "@/views/admin/AdminScoringResultPage.vue";
import AdminUserAnswerPage from "@/views/admin/AdminUserAnswerPage.vue";
import AppDetailView from "@/views/app/AppDetailView.vue";
import AddAppView from "@/views/add/AddAppView.vue";
import AddQuestionView from "@/views/add/AddQuestionView.vue";
import AddScoringResultView from "@/views/add/AddScoringResultView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import DoAnswerView from "@/views/answer/DoAnswerView.vue";
import MyAnswerView from "@/views/answer/MyAnswerView.vue";
import AnswerResultView from "@/views/answer/AnswerResultView.vue";
import AppStatisticPage from "@/views/statistic/AppStatisticPage.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "主页",
    component: HomeView,
  },
  {
    path: "/add/app",
    name: "创建应用",
    component: AddAppView,
  },
  {
    path: "/add/app/:id",
    name: "修改应用",
    props: true,
    component: AddAppView,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/add/question/:appId",
    name: "创建题目",
    component: AddQuestionView,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/add/scoring_result/:appId",
    name: "创建评分",
    component: AddScoringResultView,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/app/detail/:id",
    name: "应用详情页",
    props: true,
    component: AppDetailView,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/answer/do/:appId",
    name: "答题",
    component: DoAnswerView,
    props: true,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/result/:id",
    name: "答题结果",
    component: AnswerResultView,
    props: true,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/my",
    name: "我的答题",
    component: MyAnswerView,
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/noAuth",
    name: "无权限",
    component: NoAuthPage,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/admin/user",
    name: "用户管理",
    component: AdminUserPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/question",
    name: "题目管理",
    component: AdminQuestionPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/app",
    name: "应用管理",
    component: AdminAppPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/scoring_result",
    name: "评分管理",
    component: AdminScoringResultPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/admin/user_answer",
    name: "回答管理",
    component: AdminUserAnswerPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/static/app_static",
    name: "应用统计",
    component: AppStatisticPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/user",
    name: "用户",
    children: [
      {
        path: "/user/register",
        name: "用户注册",
        component: UserRegisterView,
      },
      {
        path: "/user/login",
        name: "用户登录",
        component: UserLoginView,
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
];
