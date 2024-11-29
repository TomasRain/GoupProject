<template>
  <v-container fluid>
    <!-- 返回按钮 -->
    <v-row>
      <v-col cols="12">
        <v-btn icon @click="goBack">
          <v-icon>mdi-arrow-left</v-icon>
        </v-btn>
      </v-col>
    </v-row>

    <!-- 商品详情 -->
    <v-row>
      <v-col cols="12" sm="6">
        <img :src="product.imageUrl" height="400px" @error="onImageError"></img>
      </v-col>
      <v-col cols="12" sm="6">
        <h1>{{ product.name }}</h1>
        <p class="original-price">原价：¥{{ product.originalPrice }}</p>
        <p class="sale-price">秒杀价：¥{{ product.salePrice }}</p>
        <v-divider></v-divider>
        <p>{{ product.description }}</p>
        <v-btn color="primary" @click="buyProduct(product.id)">立即抢购</v-btn>
      </v-col>
    </v-row>

    <!-- Snackbar 通知 -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      timeout="3000"
      top
      right
    >
      {{ snackbar.message }}
      <v-btn color="white" text @click="goHome">
        关闭
      </v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
export default {
  name: 'ProductDetail',
  props: {
    id: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      product: {},
      snackbar: {
        show: false,
        message: '',
        color: 'success',
      },
    };
  },
  created() {
    this.fetchProductDetail(this.id);
  },
  methods: {
    fetchProductDetail(productId) {
      this.$axios
        .get(`/products/${productId}`)
        .then((response) => {
          this.product = response.data;
        })
        .catch((error) => {
          console.error('Error fetching product detail:', error);
          if (error.response && error.response.status === 404) {
            this.snackbar.message = '商品不存在。';
            this.snackbar.color = 'error';
            this.snackbar.show = true;
            setTimeout(() => {
              this.$router.push({ name: 'FlashSale' });
            }, 3000);
          }
          // 401 错误已在 Axios 响应拦截器中处理
        });
    },
    buyProduct(productId) {
      this.$axios
        .post(`/products/buy/${productId}`)
        .then((response) => {
          this.snackbar.message = '购买成功！';
          this.snackbar.color = 'success';
          this.snackbar.show = true;
          // 购买成功后刷新商品详情
          this.fetchProductDetail(productId);
        })
        .catch((error) => {
          console.error('Error buying product:', error);
          if (error.response && error.response.status !== 401) {
            this.snackbar.message = error.response.data.message || '购买失败，请重试。';
            this.snackbar.color = 'error';
            this.snackbar.show = true;
          }
          // 401 错误已在 Axios 响应拦截器中处理
        });
    },
    goBack() {
      this.$router.back();
    },
    goHome() {
    this.$router.push({ name: 'Home' });
    },
    onImageError(event) {
      event.target.src = require('@/assets/placeholder.png'); // 替换为占位图的路径
    },
  },
};
</script>

<style scoped>
.original-price {
  text-decoration: line-through;
  color: #888;
}

.sale-price {
  color: #e60012;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

/* 响应式调整 */
@media (max-width: 600px) {
  h1 {
    font-size: 24px;
  }
}
</style>
