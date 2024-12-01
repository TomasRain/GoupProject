<template>
  <v-container fluid>
    <!-- 搜索框和注销按钮 -->
    <v-row class="align-center mb-4">
      <v-col cols="8">
        <v-text-field
          v-model="searchQuery"
          label="搜索商品"
          prepend-inner-icon="mdi-magnify"
          @keyup.enter="onSearch"
          clearable
        ></v-text-field>
      </v-col>
      <v-col cols="4" class="text-right">
        <v-btn color="error" @click="handleLogout">注销</v-btn>
      </v-col>
    </v-row>

    <!-- 商品列表 -->
    <v-row>
      <v-col
        v-for="product in products"
        :key="product.id"
        cols="12"
        sm="6"
        md="4"
        lg="3"
      >
        <v-card
          class="mx-auto product-card"
          max-width="344"
          elevation="4"
        >
          <v-img
            :src="product.imageUrl"
            height="200px"
            @error="onImageError"
            @click="goToProductDetail(product.id)"
            class="cursor-pointer"
            aspect-ratio="1.5"
          ></v-img>
          <v-card-text>
            <div class="text-h6 font-weight-bold">{{ product.name }}</div>
            <div class="original-price">
              原价：<s>¥{{ product.originalPrice }}</s>
            </div>
            <div class="sale-price">
              秒杀价：¥{{ product.salePrice }}
            </div>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" block @click="buyProduct(product.id)">
              立即抢购
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- 分页控件 -->
    <v-row>
      <v-col cols="12" class="text-center">
        <v-pagination
          v-model="currentPage"
          :length="totalPages"
          @update:modelValue="fetchFlashSaleProducts"
        ></v-pagination>
      </v-col>
    </v-row>

    <!-- Snackbar 通知 -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      timeout="3000"
      location="top right"
    >
      {{ snackbar.message }}
      <template #actions>
        <v-btn text @click="goHome">
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import AuthService from '@/services/auth';
import { mapActions } from 'vuex';

export default {
  name: 'FlashSale',
  data() {
    return {
      products: [],
      currentPage: 1,
      totalPages: 1,
      itemsPerPage: 8, // 每页显示的商品数量
      searchQuery: '', // 搜索关键词
      snackbar: {
        show: false,
        message: '',
        color: 'success',
      },
    };
  },
  created() {
    this.fetchFlashSaleProducts();
  },
  methods: {
    ...mapActions('auth', ['logout']),
    fetchFlashSaleProducts() {
      this.$axios
        .get('/api/products', { // 确保后端接口路径正确
          params: {
            page: this.currentPage,
            size: this.itemsPerPage,
            query: this.searchQuery,
          },
        })
        .then((response) => {
          this.products = response.data.products;
          this.totalPages = response.data.totalPages;
        })
        .catch((error) => {
          console.error('Error fetching products:', error);
          this.snackbar.message = '获取产品列表失败，请稍后重试。';
          this.snackbar.color = 'error';
          this.snackbar.show = true;
        });
    },
    buyProduct(productId) {
      this.$axios
        .post(`/api/products/buy/${productId}`)
        .then(() => {
          this.snackbar.message = '购买成功！';
          this.snackbar.color = 'success';
          this.snackbar.show = true;
          // 购买成功后刷新商品列表
          this.fetchFlashSaleProducts();
        })
        .catch((error) => {
          console.error('Error buying product:', error);
          if (error.response && error.response.status !== 401) {
            this.snackbar.message = error.response.data.message || '购买失败，请重试。';
            this.snackbar.color = 'error';
            this.snackbar.show = true;
          }
        });
    },
    handleLogout() {
      AuthService.logout(); // 通过 Vuex action 执行登出操作
    },
    onSearch() {
      this.currentPage = 1;
      this.fetchFlashSaleProducts();
    },
    goToProductDetail(productId) {
      this.$router.push({ name: 'ProductDetail', params: { id: productId } });
    },
    goHome() {
      this.$router.push({ name: 'Home' });
    },
    onImageError(event) {
      event.target.src = require('@/assets/placeholder.png'); // 替换为占位图的路径
    },
  },
  watch: {
    currentPage() {
      this.fetchFlashSaleProducts();
    },
  },
};
</script>

<style scoped>
.original-price {
  color: #888;
}

.sale-price {
  color: #e60012;
  font-size: 18px;
  font-weight: bold;
}

/* 调整卡片样式 */
.product-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

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
