import { createApp } from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'



// 引入 Vuetify
import { createVuetify } from 'vuetify'
import 'vuetify/styles'  // 引入 Vuetify 的样式
import { aliases, mdi } from 'vuetify/iconsets/mdi' // 使用 Material Design 图标

// 初始化 Vuetify
const vuetify = createVuetify({
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
})

// 使用 Vuetify 并启动 Vue 应用
const app = createApp(App)
app.use(router)
app.use(store)
app.use(vuetify)
app.mount('#app')
