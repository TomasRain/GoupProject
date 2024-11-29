<template>
  <v-container fluid>
    <!-- 秒杀商品列表 -->
    <v-row>
      <v-col
        v-for="product in products"
        :key="product.id"
        cols="12"
        sm="6"
        md="4"
        lg="3"
      >
        <v-card>
          <v-img
            :src="product.image"
            height="200px"
            @error="onImageError"
            @click="goToProductDetail(product.id)"
            class="cursor-pointer"
          ></v-img>
          <v-card-title>{{ product.name }}</v-card-title>
          <v-card-subtitle>
            秒杀价：¥{{ product.salePrice }}
          </v-card-subtitle>
          <v-card-text>
            <div>库存: {{ product.stock }}</div>
          </v-card-text>
          <v-card-actions>
            <v-btn color="success" @click="seckillProduct(product.id)">
              立即抢购
            </v-btn>
          </v-card-actions>
        </v-card>
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
      <v-btn color="white" text @click="snackbar.show = false">
        关闭
      </v-btn>
    </v-snackbar>
  </v-container>
</template>

<script>
export default {
  name: 'Seckill',
  data() {
    return {
      products: [],
      snackbar: {
        show: false,
        message: '',
        color: 'success',
      },
    };
  },
  created() {
    this.fetchSeckillProducts();
  },
  methods: {
    fetchSeckillProducts() {
      this.$axios
        .get('/flashsale/seckill-products') // 根据后端 API 路径调整
        .then((response) => {
          this.products = response.data.products;
        })
        .catch((error) => {
          console.error('Failed to fetch seckill products:', error);
          // 错误已在 Axios 响应拦截器中处理
        });
    },
    seckillProduct(productId) {
      this.$axios
        .post(`/flashsale/buy/${productId}`)
        .then((response) => {
          this.snackbar.message = '抢购成功！';
          this.snackbar.color = 'success';
          this.snackbar.show = true;
          // 购买成功后刷新秒杀商品列表
          this.fetchSeckillProducts();
        })
        .catch((error) => {
          console.error('Error during seckill:', error);
          if (error.response && error.response.status !== 401) {
            this.snackbar.message = error.response.data.message || '抢购失败，请重试。';
            this.snackbar.color = 'error';
            this.snackbar.show = true;
          }
          // 401 错误已在 Axios 响应拦截器中处理
        });
    },
    goToProductDetail(productId) {
      this.$router.push({ name: 'ProductDetail', params: { id: productId } });
    },
    onImageError(event) {
      event.target.src = require('@/assets/placeholder.png'); // 替换为占位图的路径
    },
  },
};
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

/* 响应式调整 */
@media (max-width: 600px) {
  .v-card {
    max-width: 100%;
  }
}
</style>
