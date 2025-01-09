const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave:false,
  devServer: {
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:28080',  // 指向后端服务
        changeOrigin: true,
        //pathRewrite: { '^/api': '' },    // 把前端的 /api 路径重写为空，使其指向后端的根路径
      }
    }
  },

  pluginOptions: {
    vuetify: {
			// https://github.com/vuetifyjs/vuetify-loader/tree/next/packages/vuetify-loader
		}
  }
});
