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
        <v-col cols="12" class="text-center">
          <v-card class="mx-auto event-card" max-width="600" elevation="4">
            <v-card-title>
              <div class="text-h5 font-weight-bold">{{ eventDetail?.name }}</div>
            </v-card-title>
            <v-card-subtitle>
              <div class="text-subtitle-1">
                开始时间: {{ formatDate(eventDetail?.startTime) }} - 结束时间: {{ formatDate(eventDetail?.endTime) }}
              </div>
            </v-card-subtitle>
            <v-card-text>
              <div>参与商品：{{ eventDetail?.products?.length || 0 }} 件</div>
              <ul>
                <li v-for="product in eventDetail?.products" :key="product.id">
                  {{ product.name }} - 库存: {{ product.stock }} - 秒杀价: {{ product.salePrice }}
                  <v-btn 
                    color="primary" 
                    @click="buyProduct(product.id, 1)" 
                    :disabled="product.stock <= 0">
                    立即购买
                  </v-btn>
                </li>
              </ul>
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
          <v-btn text @click="snackbar.show = false">
            关闭
          </v-btn>
        </template>
      </v-snackbar>
    </v-container>
  </template>
  
  <script>
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
      };
    },
    created() {
      this.fetchEventDetailFromRoute(); // 从路由获取活动对象
    },
    methods: {
      // 从路由中获取传递的活动对象
      fetchEventDetailFromRoute() {
        const event = this.$route.params.event ? JSON.parse(this.$route.params.event) : null;
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
        try {
          const response = await this.$axios.post(`/orders/buy/${productId}`, null, {
            params: { quantity },
          });
          this.snackbar.message = response.data; // 购买成功提示
          this.snackbar.color = 'success';
          this.snackbar.show = true;
        } catch (error) {
          console.error('Error purchasing product:', error);
          this.snackbar.message = '购买失败，请稍后重试。';
          this.snackbar.color = 'error';
          this.snackbar.show = true;
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
  
  ul {
    list-style-type: none;
    padding: 0;
  }
  
  li {
    font-size: 0.9em;
    margin-bottom: 8px;
  }
  
  .v-btn {
    margin-top: 10px;
  }
  </style>
  