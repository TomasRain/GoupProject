// src/services/api.js

import axios from 'axios';
import router from '../router';
import store from '../store';
import jwt_decode from 'jwt-decode';

// 创建 Axios 实例
const api = axios.create({
  baseURL: '/api', // 通过 vue.config.js 的 proxy 转发到后端
});

// 请求拦截器：自动在每个请求中添加 JWT 令牌
api.interceptors.request.use(
  (config) => {
    const token = store.state.auth.token;
    if (token) {
      try {
        const decodedToken = jwt_decode(token);
        const currentTime = Date.now() / 1000; // 当前时间（秒）
        if (decodedToken.exp < currentTime) {
          // 令牌已过期，清除令牌并重定向
          store.dispatch('auth/logout');
          router.push({ name: 'Login' });
          return Promise.reject(new Error('令牌已过期，请重新登录'));
        } else {
          // 令牌有效，添加到请求头
          config.headers['Authorization'] = `Bearer ${token}`;
        }
      } catch (e) {
        console.error('Token parsing error:', e);
        store.dispatch('auth/logout');
        router.push({ name: 'Login' });
        return Promise.reject(new Error('无效的令牌'));
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器：处理全局错误，如 401 未认证
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      store.dispatch('auth/logout');
      router.push({ name: 'Login' });
    }
    return Promise.reject(error);
  }
);

// **获取秒杀活动列表**
export const getSeckillEvents = async () => {
  try {
    const response = await api.get('/seckill/events'); // 后端秒杀活动列表接口
    return response.data; // 返回秒杀活动列表数据
  } catch (error) {
    console.error('获取秒杀活动列表失败:', error);
    throw new Error('获取秒杀活动列表失败');
  }
};

// **获取商品列表**
export const getProducts = async () => {
  try {
    const response = await api.get('/products'); // 后端商品列表接口
    return response.data; // 返回商品数据
  } catch (error) {
    console.error('获取商品列表失败:', error);
    throw new Error('获取商品列表失败');
  }
};

// **创建秒杀活动**
export const createSeckillEvent = async (seckillEventData) => {
  try {
    const response = await api.post('/seckill/create', seckillEventData); // 后端秒杀创建接口
    return response.data; // 返回成功消息
  } catch (error) {
    console.error('创建秒杀活动失败:', error);
    throw new Error('创建秒杀活动失败');
  }
};

export default api;
