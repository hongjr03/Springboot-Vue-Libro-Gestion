import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import LoginView from "@/views/LoginView.vue";
import NavView from "@/views/NavView.vue";
import BookView from "@/views/BookView.vue";
import UserView from "@/views/UserView.vue";
import RegisterView from "@/views/RegisterView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "登录",
    component: LoginView,
    meta: { show: false },
  },
  {
    path: "/login",
    name: "用户登录",
    component: LoginView,
    meta: { show: false },
  },
  {
    path: "/nav",
    name: "导航",
    component: NavView,
    redirect: "/book",
    meta: { show: true },
    children: [
      {
        path: "/book",
        name: "图书信息",
        component: BookView,
        meta: { show: true },
      },
      {
        path: "/user",
        name: "用户信息",
        component: UserView,
        meta: { show: true },
      },
      {
        path: "/return",
        name: "归还信息",
        component: () => import("@/views/ReturnView.vue"),
        meta: { show: true },
      },
      {
        path: "/overtime",
        name: "超期信息",
        component: () => import("@/views/OvertimeView.vue"),
        meta: { show: true },
      },
    ],
  },
  {
    path: "/register",
    name: "用户注册",
    component: RegisterView,
    meta: { show: false },
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
