// src/router/index.js

import { createRouter, createWebHashHistory } from 'vue-router';
import store from '../store'; // 添加这一行


import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import FlashSale from '../components/FlashSale.vue';
import ProductDetail from '../components/ProductDetail.vue';
import Home from '../components/Home.vue';
import ProductList from '../components/ProductList.vue';
import Seckill from '../components/Seckill.vue';

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
  const isAuthenticated = store.state.auth.isAuthenticated;

  if (requiresAuth && !isAuthenticated) {
    next({ name: 'Login' });
  } else {
    next();
  }
});

export default router;
