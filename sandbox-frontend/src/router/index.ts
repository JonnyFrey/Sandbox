import Vue from "vue";
import VueRouter, { RouteConfig } from "vue-router";
import About from "../views/About.vue";
import WaffelSplash from "@/views/WaffelSplash.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
  {
    path: "/",
    name: "Home",
    component: WaffelSplash
  },
  {
    path: "/minesweeper",
    name: "Minesweeper Heatmap",
    component: () => import("../views/Minesweeper.vue")
  },
  {
    path: "/about",
    name: "About",
    component: About
  },
  {
    path: "/not-found",
    name: "Not Found",
    component: () => import("../views/NotFound.vue")
  },
  {
    path: "*",
    redirect: "/not-found"
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

export default router;
