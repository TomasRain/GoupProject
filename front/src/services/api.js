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
        // 令牌解析失败，清除令牌并重定向
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
      // 令牌过期或未授权，清除令牌并重定向
      store.dispatch('auth/logout');
      router.push({ name: 'Login' });
    }
    return Promise.reject(error);
  }
);

export default api;
