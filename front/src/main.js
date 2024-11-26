import { createApp } from 'vue';
import App from './App.vue';
import store from './store';
import router from './router';

// 引入 Vuetify
import { createVuetify } from 'vuetify';
import 'vuetify/styles'; // 引入 Vuetify 的样式
import { aliases, mdi } from 'vuetify/iconsets/mdi'; // 使用 Material Design 图标

// 导入 Axios 和其他模块
import axios from 'axios';
import jwt_decode from 'jwt-decode';

// 引入图标字体
import 'material-design-icons-iconfont/dist/material-design-icons.css';

// 引入通知插件
import Notifications from '@kyvg/vue3-notification';

// 初始化 Vuetify
const vuetify = createVuetify({
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
});

// 请求拦截器
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decodedToken = jwt_decode(token);
        const currentTime = Date.now() / 1000; // 当前时间（秒）
        if (decodedToken.exp < currentTime) {
          // 令牌已过期，清除令牌并重定向
          localStorage.removeItem('token');
          router.push({ name: 'Login' });
          return Promise.reject('令牌已过期，请重新登录');
        } else {
          // 令牌有效，添加到请求头
          config.headers['Authorization'] = `Bearer ${token}`;
        }
      } catch (e) {
        console.error('Token parsing error:', e);
        // 令牌解析失败，清除令牌并重定向
        localStorage.removeItem('token');
        router.push({ name: 'Login' });
        return Promise.reject('无效的令牌');
      }
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
axios.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 令牌过期或未授权，清除令牌并重定向
      localStorage.removeItem('token');
      router.push({ name: 'Login' });
    }
    return Promise.reject(error);
  }
);

// 创建 Vue 应用
const app = createApp(App);

// 使用通知插件
app.use(Notifications);

// 将 Axios 添加到全局
app.config.globalProperties.$axios = axios;

// 使用路由、状态管理和 Vuetify
app.use(router);
app.use(store);
app.use(vuetify);

// 挂载应用
app.mount('#app');
