import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '../components/Login.vue'       // 登录页面组件
import Home from '../components/Home.vue'         // 主界面组件

const routes = [
  {
    path: '/',
    name: 'Login',            // 将主页设为登录页面
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home           // 登录成功后的主界面
  },
  {
    path: '/about',
    name: 'about',
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
