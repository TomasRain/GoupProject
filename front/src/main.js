// src/main.js

import { createApp } from 'vue';
import App from './App.vue';
import store from './store';
import router from './router';

// 引入 Vuetify
import { createVuetify } from 'vuetify';
import 'vuetify/styles'; // 引入 Vuetify 的样式
import { aliases, mdi } from 'vuetify/iconsets/mdi'; // 使用 Material Design 图标

// 引入图标字体
import 'material-design-icons-iconfont/dist/material-design-icons.css';

// 引入通知插件
import Notifications from '@kyvg/vue3-notification';

// 引入 Axios 实例
import api from './services/api';

// 创建 Vuetify 实例
const vuetify = createVuetify({
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
});

// 创建 Vue 应用
const app = createApp(App);

// 使用通知插件
app.use(Notifications);

// 将 Axios 实例添加到全局
app.config.globalProperties.$axios = api;

// 使用路由、状态管理和 Vuetify
app.use(router);
app.use(store);
app.use(vuetify);

// 挂载应用
app.mount('#app');
