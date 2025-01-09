<template>
    <v-container class="pa-5">
      <!-- 标题 -->
      <v-row justify="center" class="mb-8">
        <v-col cols="12" class="text-center">
          <h1 class="display-2 font-weight-bold text-primary">{{ eventDetail?.name }}</h1>
        </v-col>
      </v-row>
  
      <!-- 活动详情 -->
      <v-row>
        <v-col
          v-for="(product, index) in eventDetail?.products"
          :key="product.id"
          cols="12"
          sm="6"
          md="4"
        >
          <v-card class="mx-auto event-card" max-width="300" elevation="4">
            <!-- 商品图片 -->
            <v-img
              :src="product.imageUrl || 
              'https://i1.hdslb.com/bfs/archive/b4b9094f9d1eb96c04415f8babc4149972a2c845.jpg'"
              height="200px"
              width="100%"
              alt="Product Image"
              contain
            ></v-img>
            <!-- 商品标题 -->
            <v-card-title>
              <div class="text-h6 font-weight-bold">{{ product.name }}</div>
            </v-card-title>
            <!-- 商品副标题 -->
            <v-card-subtitle>
              <div class="text-subtitle-1">秒杀价: ¥{{ product.salePrice }}</div>
              <div class="text-subtitle-2">库存: {{ product.stock }} 件</div>
            </v-card-subtitle>
            <!-- 商品描述 -->
            <v-card-text>
              <v-btn
                color="primary"
                @click="buyProduct(product.id, 1)"
                :disabled="product.stock <= 0 || loadingProductId === product.id"
                :loading="loadingProductId === product.id"
              >
                立即购买
              </v-btn>
            </v-card-text>
          </v-card>
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
          <v-btn text @click="snackbar.show = false">关闭</v-btn>
        </template>
      </v-snackbar>
    </v-container>
  </template>
  
  <script>
  import placeholderImage from '@/assets/placeholder.png';
  
  export default {
    name: 'SeckillEventDetail',
    data() {
      return {
        eventDetail: null, // 当前活动的详细信息
        snackbar: {
          show: false,
          message: '',
          color: 'success',
        },
        loadingProductId: null, // 正在购买的商品 ID
      };
    },
    created() {
      this.fetchEventDetailFromRoute(); // 从路由获取活动对象
    },
    methods: {
      // 从路由中获取传递的活动对象
      fetchEventDetailFromRoute() {
        const event = this.$route.params.event
          ? JSON.parse(this.$route.params.event)
          : null;
        if (event) {
          this.eventDetail = event; // 如果活动对象存在，直接使用传递的活动数据
        } else {
          this.snackbar.message = '活动详情加载失败，请重试。';
          this.snackbar.color = 'error';
          this.snackbar.show = true;
        }
      },
  
      // 格式化日期
      formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleString(); // 可以根据需求调整日期格式
      },
  
      // 调用后端购买接口
      async buyProduct(productId, quantity) {
        this.loadingProductId = productId; // 设置正在购买的商品 ID，显示 loading 状态
        try {
          const response = await this.$axios.post(`/orders/buy/${productId}`, null, {
            params: { quantity },
          });
          this.snackbar.message = response.data; // 购买成功提示
          this.snackbar.color = 'success';
          this.snackbar.show = true;
  
          // 更新活动商品库存
          const product = this.eventDetail.products.find((p) => p.id === productId);
          if (product) {
            product.stock -= quantity; // 扣减库存
          }
        } catch (error) {
          console.error('Error purchasing product:', error);
          this.snackbar.message = '购买失败，请稍后重试。';
          this.snackbar.color = 'error';
          this.snackbar.show = true;
        } finally {
          this.loadingProductId = null; // 结束 loading 状态
        }
      },
    },
  };
  </script>
  
  <style scoped>
  .event-card {
    transition: transform 0.2s, box-shadow 0.2s;
  }
  
  .event-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  }
  
  .v-btn {
    width: 100%;
  }
  
  .v-card-subtitle {
    padding-bottom: 16px;
  }
  
  .v-card-text {
    text-align: center;
  }
  
  .v-img {
    border-radius: 8px;
  }
  </style>
  