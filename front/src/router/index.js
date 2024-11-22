import { createRouter, createWebHashHistory } from 'vue-router';
import Login from '../components/Login.vue';       // 登录页面组件
import FlashSale from '../components/FlashSale.vue'; // 秒杀系统组件

const routes = [
  {
    path: '/',
    name: 'Home',
    redirect: '/flashsale' // 将根路径重定向到秒杀页面
  },
  {
    path: '/login',
    name: 'Login',            // 登录页面
    component: Login
  },
  {
    path: '/flashsale',
    name: 'FlashSale',        // 秒杀系统页面
    component: FlashSale,
    meta: {
      requiresAuth: true     // 需要认证
    }
  },
  {
    path: '/about',
    name: 'about',
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  }
];

const router = createRouter({
  history: createWebHashHistory(), // 如果希望使用 HTML5 模式，可以改为 createWebHistory()
  routes
});

// 路由守卫，检查认证状态
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('token'); // 检查本地是否有 token
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isLoggedIn) {
      next({ name: 'Login' }); // 未登录，跳转到登录页面
    } else {
      next(); // 已登录，允许访问
    }
  } else {
    next(); // 不需要认证，允许访问
  }
});

export default router;
