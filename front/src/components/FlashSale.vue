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
        ></v-text-field>
      </v-col>
      <v-col cols="4" class="text-right">
        <v-btn color="error" @click="logout">注销</v-btn>
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
          @click="goToProductDetail(product.id)"
        >
          <v-img
            :src="product.image"
            height="200px"
            @error="onImageError"
          ></v-img>
          <v-card-title>{{ product.name }}</v-card-title>
          <v-card-subtitle class="original-price">
            原价：¥{{ product.originalPrice }}
          </v-card-subtitle>
          <v-card-subtitle class="sale-price">
            秒杀价：¥{{ product.salePrice }}
          </v-card-subtitle>
          <v-card-actions>
            <v-btn color="primary" block @click.stop="buyProduct(product.id)">
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
        ></v-pagination>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'FlashSale',
  data() {
    return {
      products: [],
      role: localStorage.getItem('role') || 'USER', // 获取角色信息，默认为 USER
      currentPage: 1,
      totalPages: 1,
      itemsPerPage: 8, // 每页显示的商品数量
      searchQuery: '', // 搜索关键词
    };
  },
  created() {
    this.fetchFlashSaleProducts();
  },
  methods: {
    fetchFlashSaleProducts() {
      this.$axios
        .get('/api/flashsale/products', {
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
          // 如果未认证，跳转回登录页面
          if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            this.$router.push({ name: 'Login' });
          } else {
            // 其他错误处理
            this.$notify({
              type: 'error',
              title: '错误',
              text: '获取商品列表失败，请稍后重试。',
            });
          }
        });
    },
    buyProduct(productId) {
      this.$axios
        .post(`/api/flashsale/buy/${productId}`)
        .then((response) => {
          this.$notify({ type: 'success', title: '成功', text: '购买成功！' });
          // 购买成功后刷新商品列表
          this.fetchFlashSaleProducts();
        })
        .catch((error) => {
          console.error('Error buying product:', error);
          if (error.response && error.response.status === 401) {
            // 未认证，跳转到登录页面
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
    logout() {
      // 清除本地存储的令牌
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      // 重定向到登录页面
      this.$router.push({ name: 'Login' });
    },
    onSearch() {
      this.currentPage = 1;
      this.fetchFlashSaleProducts();
    },
    goToProductDetail(productId) {
      this.$router.push({ name: 'ProductDetail', params: { id: productId } });
    },
    onImageError(event) {
      event.target.src = '/images/placeholder.png'; // 替换为占位图的路径
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
  text-decoration: line-through;
  color: #888;
}

.sale-price {
  color: #e60012;
  font-size: 18px;
  font-weight: bold;
}

/* 调整卡片样式 */
.product-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

/* 响应式调整 */
@media (max-width: 600px) {
  .v-card {
    max-width: 100%;
  }
}
</style>