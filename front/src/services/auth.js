// src/services/auth.js

import api from './api';
import store from '../store';
import router from '../router';

const AuthService = {
  login(username, password) {
    return api.post('/auth/login', { username, password })
      .then(response => {
        const token = response.data.token;
        const role = response.data.role;
        // 将 token 和 role 存储到 Vuex
        store.dispatch('auth/setAuth', { token, role });
        return response.data;
      });
  },
  register(username, password, role) {
    return api.post('/auth/register', { username, password, role })
      .then(response => response.data);
  },
  logout() {
    // 如果有后端登出接口，可以调用这个接口
    // return api.post('/auth/logout').then(() => {
    //   store.dispatch('auth/logout');
    //   router.push({ name: 'Login' });
    // });

    // 直接清除本地存储并更新 Vuex
    localStorage.removeItem('token');  // 如果你存储了 token 在 localStorage
    store.dispatch('auth/logout');    // 更新 Vuex 状态
    router.push({ name: 'Login' });    // 跳转到登录页
  },
};

export default AuthService;
