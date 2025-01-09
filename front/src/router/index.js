import { createRouter, createWebHashHistory } from 'vue-router';
import store from '../store';

import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import FlashSale from '../components/FlashSale.vue';
import ProductDetail from '../components/ProductDetail.vue';
import Home from '../components/Home.vue';
import ProductList from '../components/ProductList.vue';
import Seckill from '../components/Seckill.vue';
import OrderHistory from '../components/OrderHistory.vue';
import OrderDetail from '../components/OrderDetail.vue';
import AdminPage from '../components/AdminPage.vue'; // 新增管理员页面组件
import SeckillEventDetail from '../components/SeckillEventDetail.vue'; // 导入秒杀活动详情组件

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true },
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
  },
  {
    path: '/flashsale',
    name: 'FlashSale',
    component: FlashSale,
    meta: { requiresAuth: true },
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail,
    meta: { requiresAuth: true },
    props: true,
  },
  {
    path: '/products',
    name: 'ProductList',
    component: ProductList,
    meta: { requiresAuth: true },
  },
  {
    path: '/seckill',
    name: 'Seckill',
    component: Seckill,
    meta: { requiresAuth: true },
  },
  {
    path: '/seckill/:event', // 添加秒杀活动详情页面路由
    name: 'SeckillEventDetail',
    component: SeckillEventDetail,
    meta: { requiresAuth: true },
    props: true,
  },
  {
    path: '/admin',
    name: 'AdminPage',
    component: AdminPage,
    meta: { requiresAuth: true, role: 'ADMIN' },
  },
  {
    path: '/orders',
    name: 'OrderHistory',
    component: OrderHistory,
    meta: { requiresAuth: true },
  },
  {
    path: '/orders/:id',
    name: 'OrderDetail',
    component: OrderDetail,
    meta: { requiresAuth: true },
    props: true,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

// 导航守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth;
  const role = store.state.auth.role;

  if (requiresAuth && !store.state.auth.isAuthenticated) {
    next({ name: 'Login' });
  } else if (to.meta.role && to.meta.role !== role) {
    // 检查用户角色是否匹配
    next(false);
  } else {
    next();
  }
});

export default router;
