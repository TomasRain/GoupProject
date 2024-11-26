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
        <v-img :src="product.image" height="400px" @error="onImageError"></v-img>
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
  </v-container>
</template>

<script>
export default {
  name: 'ProductDetail',
  data() {
    return {
      product: {},
    };
  },
  created() {
    const productId = this.$route.params.id;
    this.fetchProductDetail(productId);
  },
  methods: {
    fetchProductDetail(productId) {
      this.$axios
        .get(`/api/flashsale/product/${productId}`)
        .then((response) => {
          this.product = response.data;
        })
        .catch((error) => {
          console.error('Error fetching product detail:', error);
          if (error.response && error.response.status === 404) {
            this.$notify({
              type: 'error',
              title: '错误',
              text: '商品不存在。',
            });
            this.$router.push({ name: 'FlashSale' });
          } else if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            this.$router.push({ name: 'Login' });
          } else {
            this.$notify({
              type: 'error',
              title: '错误',
              text: '获取商品详情失败，请稍后重试。',
            });
          }
        });
    },
    buyProduct(productId) {
      this.$axios
        .post(`/api/flashsale/buy/${productId}`)
        .then((response) => {
          this.$notify({ type: 'success', title: '成功', text: '购买成功！' });
          // 购买成功后可以选择返回商品列表或刷新页面
        })
        .catch((error) => {
          console.error('Error buying product:', error);
          if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            this.$router.push({ name: 'Login' });
          } else {
            this.$notify({
              type: 'error',
              title: '错误',
              text: error.response.data.message || '购买失败，请重试。',
            });
          }
        });
    },
    goBack() {
      this.$router.go(-1);
    },
    onImageError(event) {
      event.target.src = '/images/placeholder.png'; // 替换为占位图的路径
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
