<!-- front/src/components/SeckillProductDetail.vue -->
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
            <!-- 点击购买前先弹出确认对话框 -->
            <v-btn
              color="primary"
              class="mt-4"
              large
              @click="confirmBuyProduct(product.id)"
              :loading="loading"
              :disabled="loading"
            >
              立即秒杀
            </v-btn>
          </v-col>
        </v-row>
      </v-card>
  
      <!-- 确认对话框：确认购买 -->
      <v-dialog v-model="dialog" max-width="500">
        <v-card>
          <v-card-title class="headline">确认购买</v-card-title>
          <v-card-text>
            您确定要秒杀 <strong>{{ product.name }}</strong> 吗？
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="green darken-1" text @click="dialog = false">取消</v-btn>
            <v-btn color="green darken-1" text @click="buyProduct(product.id)">确认</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
  
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
  import { mapGetters, mapActions } from 'vuex';
  
  export default {
    name: 'SeckillProductDetail',
    props: ['eventId', 'id'],
    data() {
      return {
        product: {},
        snackbar: {
          show: false,
          message: '',
          color: 'success',
        },
        placeholderImage,
        dialog: false, // 控制确认对话框显示
        loading: false, // 控制购买按钮的加载状态
      };
    },
    computed: {
      ...mapGetters('auth', ['userId', 'isAuthenticated', 'role']),
    },
    created() {
      this.fetchProductDetail();
    },
    methods: {
      ...mapActions('auth', ['logout']),
      fetchProductDetail() {
        const productId = this.id;
        const eventId = this.eventId;
        this.$axios
          .get(`/api/seckill/${eventId}/products/${productId}`)
          .then((response) => {
            this.product = response.data;
          })
          .catch((error) => {
            console.error('Error fetching seckill product detail:', error);
            if (error.response && error.response.status === 404) {
              this.snackbar.message = '商品不存在。';
              this.snackbar.color = 'error';
              this.snackbar.show = true;
              setTimeout(() => {
                this.$router.push({ name: 'SeckillProductList', params: { eventId: this.eventId } });
              }, 3000);
            }
          });
      },
      confirmBuyProduct(productId) {
        // 弹出对话框，等待用户确认
        this.dialog = true;
      },
      buyProduct(productId) {
        // 用户确认后，真正调用秒杀购买接口
        this.dialog = false;
        this.loading = true;
        this.$axios
          .post(`/api/seckill/execute/${productId}/${this.userId}`)
          .then(() => {
            this.snackbar.message = '秒杀成功！';
            this.snackbar.color = 'success';
            this.snackbar.show = true;
            // 购买成功后刷新商品详情
            this.fetchProductDetail();
          })
          .catch((error) => {
            console.error('Error executing seckill:', error);
            this.snackbar.message =
              error.response?.data?.message || '秒杀失败，请重试。';
            this.snackbar.color = 'error';
            this.snackbar.show = true;
          })
          .finally(() => {
            this.loading = false;
          });
      },
      handleLogout() {
        this.logout();
      },
      goBack() {
        this.$router.back();
      },
      onImageError(event) {
        event.target.src = placeholderImage;
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
    text-decoration: line-through;
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
  