<template>
  <v-container fluid class="pa-5">
    <v-row justify="center" class="mb-8">
      <v-col cols="12" class="text-center">
        <h1 class="display-2 font-weight-bold text-primary">秒杀活动</h1>
        <p class="subtitle-1 text-muted">限时优惠，手快有手慢无！</p>
      </v-col>
    </v-row>

    <v-row justify="center" class="mb-4 text-center">
      <v-col cols="12">
        <!-- 显示活动状态 -->
        <div v-if="!hasStarted">
          活动未开始，距离开始还有 {{ countdown }} 秒
        </div>
        <div v-else>
          活动已开始！
        </div>
      </v-col>
    </v-row>

    <!-- 商品列表 -->
    <v-row>
      <v-col
        v-for="product in products"
        :key="product.productId"
        cols="12"
        sm="6"
        md="4"
        lg="3"
      >
        <v-card>
          <v-img
            :src="product.imageUrl || placeholderImage"
            height="200px"
            @error="onImageError"
            class="cursor-pointer"
            @click="goToProductDetail(product.productId)"
          ></v-img>
          <v-card-title>{{ product.name }}</v-card-title>
          <v-card-subtitle>
            秒杀价：¥{{ product.salePrice }}
          </v-card-subtitle>
          <v-card-text>
            <div>库存: {{ product.stock }}</div>
          </v-card-text>
          <v-card-actions>
            <v-btn 
              :disabled="!hasStarted || isQueueing || isProcessing" 
              color="success" 
              @click="seckillProduct(product.productId)"
            >
              {{ hasStarted ? (isQueueing ? '排队中...' : '立即抢购') : '未开始' }}
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
import placeholderImage from '@/assets/placeholder.png';

export default {
  name: 'Seckill',
  data() {
    return {
      products: [],
      hasStarted: false,
      countdown: 0,
      activityStartTime: null,
      intervalId: null,
      isQueueing: false,  // 标记用户点击抢购后处于排队状态
      isProcessing: false, // 标记正在发送请求时的短暂禁用状态
      snackbar: {
        show: false,
        message: '',
        color: 'success',
      },
      placeholderImage,
    };
  },
  created() {
    this.fetchActivityInfo();
  },
  beforeUnmount() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  },
  methods: {
    fetchActivityInfo() {
      // 后端需要提供活动信息接口，如 /api/seckill/activity-info
      // 假设返回格式：
      // { "hasStarted": false, "startTime": 1679999999999, "products":[{productId, name, salePrice, stock, imageUrl},...] }
      this.$axios
        .get('/api/seckill/activity-info')
        .then(response => {
          const data = response.data;
          this.products = data.products || [];
          this.hasStarted = data.hasStarted;
          if (!this.hasStarted) {
            this.activityStartTime = data.startTime;
            this.startCountdown();
          }
        })
        .catch(error => {
          console.error('Failed to fetch activity info:', error);
          // 错误在 Axios 拦截器中处理
        });
    },
    startCountdown() {
      if (!this.activityStartTime) return;
      this.updateCountdown();
      this.intervalId = setInterval(() => {
        this.updateCountdown();
      }, 1000);
    },
    updateCountdown() {
      const now = Date.now();
      const diff = Math.floor((this.activityStartTime - now) / 1000);
      if (diff <= 0) {
        this.countdown = 0;
        this.hasStarted = true;
        if (this.intervalId) {
          clearInterval(this.intervalId);
          this.intervalId = null;
        }
      } else {
        this.countdown = diff;
      }
    },
    seckillProduct(productId) {
      this.isProcessing = true;
      // 假设后端秒杀接口为 /api/seckill/execute/{productId}/{userId}
      // userId 应从已登录用户信息中获取（后端可从 token中解析），若需要可在调用时附加
      // 此处简单假设 userId=1，实际需从已登录用户状态中获取
      const userId = 1; 
      this.$axios
        .post(`/api/seckill/execute/${productId}/${userId}`)
        .then(response => {
          this.snackbar.message = response.data.message || '排队中，请稍后查看订单结果。';
          this.snackbar.color = 'success';
          this.snackbar.show = true;
          this.isQueueing = true;
          // 进入排队状态后，不需要立即刷新商品列表，
          // 可以定期轮询或者在订单历史页面查看是否已下单成功
        })
        .catch(error => {
          console.error('Error during seckill:', error);
          if (error.response && error.response.status !== 401) {
            this.snackbar.message = error.response.data.message || '抢购失败，请重试。';
            this.snackbar.color = 'error';
            this.snackbar.show = true;
          }
        })
        .finally(() => {
          this.isProcessing = false;
        });
    },
    goToProductDetail(productId) {
      this.$router.push({ name: 'ProductDetail', params: { id: productId } });
    },
    onImageError(event) {
      event.target.src = this.placeholderImage;
    },
  },
};
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
</style>
