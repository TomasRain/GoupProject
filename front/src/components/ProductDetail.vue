<template>
  <v-container class="product-detail" fluid>
    <!-- 返回按钮 -->
    <v-btn icon @click="goBack" class="mt-4 ml-2">
      <v-icon>mdi-arrow-left</v-icon>
    </v-btn>

    <!-- 商品详情卡片 -->
    <v-card class="mx-auto my-4 pa-4" max-width="800">
      <v-row>
        <!-- 商品图片 -->
        <v-col cols="12" sm="6">
          <v-img
            :src="product.imageUrl"
            :alt="product.name"
            :lazy-src="placeholderImage"
            :aspect-ratio="1"
            class="rounded"
            height="100%"
          ></v-img>
        </v-col>

        <!-- 商品信息 -->
        <v-col cols="12" sm="6">
          <h2 class="font-weight-bold">{{ product.name }}</h2>
          <div class="price-section my-3">
            <div class="original-price">
              原价：<s>¥{{ product.originalPrice }}</s>
            </div>
            <div class="sale-price">
              秒杀价：¥{{ product.salePrice }}
            </div>
          </div>
          <v-divider></v-divider>
          <p class="mt-4">{{ product.description }}</p>
          <v-btn
            color="primary"
            class="mt-4"
            large
            @click="buyProduct(product.id)"
          >
            立即抢购
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <!-- Snackbar 通知 -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      timeout="3000"
      location="top right"
    >
      {{ snackbar.message }}
      <template #actions>
        <v-btn text @click="snackbar.show = false">关闭</v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>


<script>
import placeholderImage from '@/assets/placeholder.png';

export default {
  name: 'ProductDetail',
  data() {
    return {
      product: {},
      snackbar: {
        show: false,
        message: '',
        color: 'success',
      },
      placeholderImage,
    };
  },
  created() {
    const productId = this.$route.params.id;
    this.fetchProductDetail(productId);
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
        });
    },
    buyProduct(productId) {
      this.$axios
        .post(`/products/buy/${productId}`)
        .then(() => {
          this.snackbar.message = '购买成功！';
          this.snackbar.color = 'success';
          this.snackbar.show = true;
          this.fetchProductDetail(productId);
        })
        .catch((error) => {
          console.error('Error buying product:', error);
          if (error.response && error.response.status !== 401) {
            this.snackbar.message =
              error.response.data.message || '购买失败，请重试。';
            this.snackbar.color = 'error';
            this.snackbar.show = true;
          }
        });
    },
    goBack() {
      this.$router.back();
    },
    goHome() {
      this.$router.push({ name: 'Home' });
    },
  },
};
</script>


<style scoped>
.product-detail {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.v-card {
  background-color: #ffffff;
}

h2 {
  font-size: 28px;
  margin-bottom: 10px;
}

.price-section {
  display: flex;
  flex-direction: column;
}

.original-price {
  color: #888;
  font-size: 16px;
}

.sale-price {
  color: #e60012;
  font-size: 24px;
  font-weight: bold;
}

p {
  font-size: 16px;
  line-height: 1.5;
}

.v-btn {
  width: 100%;
}

@media (max-width: 600px) {
  h2 {
    font-size: 24px;
  }
}
</style>
