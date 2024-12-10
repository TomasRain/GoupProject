<template>
  <v-container>
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
          <!-- 使用img而非v-img以支持@error事件替换图片 -->
          <img
            :src="product.imageUrl"
            height="200px"
            @error="onImageError"
            @click="goToProductDetail(product.id)"
            class="cursor-pointer"
          ></img>
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
            <!-- 点击购买前先弹出确认对话框 -->
            <v-btn
              color="primary"
              block
              @click="confirmBuyProduct(product.id)"
              :loading="loadingProductId === product.id"
              :disabled="loadingProductId === product.id"
            >
              立即抢购
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- 确认对话框：确认购买 -->
    <v-dialog v-model="dialog" max-width="500">
      <v-card>
        <v-card-title class="headline">确认购买</v-card-title>
        <v-card-text>
          您确定要购买 <strong>{{ selectedProduct.name }}</strong> 吗？
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="green darken-1" text @click="dialog = false">取消</v-btn>
          <!-- 点击确认后发起购买请求，带上quantity参数 -->
          <v-btn color="green darken-1" text @click="buyProduct(selectedProduct.id)">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 分页控件：保持不变，因为后端产品列表支持分页 -->
    <v-row>
      <v-col cols="12" class="text-center">
        <v-pagination
          v-model="currentPage"
          :length="totalPages"
          @update:modelValue="fetchProducts"
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
        <v-btn text @click="snackbar.show = false">
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
  name: 'ProductList',
  data() {
    return {
      products: [],
      currentPage: 1,
      totalPages: 1,
      itemsPerPage: 8,
      searchQuery: '',
      snackbar: {
        show: false,
        message: '',
        color: 'success',
      },
      dialog: false, // 控制对话框显示
      selectedProduct: {}, // 当前选中的商品
      loadingProductId: null, // 当前正在购买的商品ID
    };
  },
  created() {
    this.fetchProducts();
  },
  methods: {
    ...mapActions('auth', ['logout']),
    fetchProducts() {
      this.$axios
        .get('/products', {
          params: {
            page: this.currentPage,
            size: this.itemsPerPage,
            query: this.searchQuery,
          },
        })
        .then((response) => {
          // 后端返回 {"products":[...],"totalPages":N}
          this.products = response.data.products;
          this.totalPages = response.data.totalPages;
        })
        .catch((error) => {
          console.error('Failed to fetch products:', error);
          this.snackbar.message = '获取产品列表失败，请稍后重试。';
          this.snackbar.color = 'error';
          this.snackbar.show = true;
        });
    },
    confirmBuyProduct(productId) {
      this.selectedProduct = this.products.find(p => p.id === productId);
      this.dialog = true;
    },
    buyProduct(productId) {
      this.dialog = false;
      this.loadingProductId = productId;
      // 修改点：增加 { params: { quantity: 1 } } 来指定购买数量
      this.$axios
        .post(`/orders/buy/${productId}`, null, { params: { quantity: 1 } })
        .then(() => {
          this.snackbar.message = '购买成功！';
          this.snackbar.color = 'success';
          this.snackbar.show = true;
          // 购买成功后刷新商品列表
          this.fetchProducts();
        })
        .catch((error) => {
          console.error('Error buying product:', error);
          this.snackbar.message =
            error.response?.data?.message || '购买失败，请重试。';
          this.snackbar.color = 'error';
          this.snackbar.show = true;
        })
        .finally(() => {
          this.loadingProductId = null;
        });
    },
    handleLogout() {
      AuthService.logout();
    },
    onSearch() {
      this.currentPage = 1;
      this.fetchProducts();
    },
    goToProductDetail(productId) {
      this.$router.push({ name: 'ProductDetail', params: { id: productId } });
    },
    goHome() {
      this.$router.push({ name: 'Home' });
    },
    onImageError(event) {
      event.target.src = require('@/assets/placeholder.png');
    },
  },
  watch: {
    currentPage() {
      this.fetchProducts();
    },
  },
};
</script>

<style scoped>
.original-price {
  color: #888;
  text-decoration: line-through;
}

.sale-price {
  color: #e60012;
  font-size: 18px;
  font-weight: bold;
}

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

@media (max-width: 600px) {
  .v-card {
    max-width: 100%;
  }
}
</style>
