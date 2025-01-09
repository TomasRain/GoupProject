<template>
  <v-container class="pa-5">
    <!-- 标题 -->
    <v-row justify="center" class="mb-8">
      <v-col cols="12" class="text-center">
        <h1 class="display-2 font-weight-bold text-primary">秒杀活动</h1>
      </v-col>
    </v-row>
    <pre>{{ seckillEvents }}</pre>
    <!-- 秒杀活动列表 -->
    <v-row>
      <v-col
        v-for="event in seckillEvents"
        :key="event.id"
        cols="12"
        sm="6"
        md="4"
        lg="3"
      >
        <v-card class="mx-auto event-card" max-width="344" elevation="4">
          <v-card-title>
            <div class="text-h6 font-weight-bold">{{ event.name }}</div>
          </v-card-title>
          <v-card-subtitle>
            <div class="text-subtitle-1">
              开始时间: {{ formatDate(event.startTime) }} - 结束时间: {{ formatDate(event.endTime) }}
            </div>
          </v-card-subtitle>
          <v-card-text>
            <div>参与商品：{{ event.products?.length || 0 }} 件</div>
            <ul>
              <li v-for="product in event.products" :key="product.id">
                {{ product.name }} - 库存: {{ product.stock }} - 秒杀价: {{ product.salePrice }}
              </li>
            </ul>
          </v-card-text>
          <v-card-actions>
            <v-btn
              color="primary"
              block
              @click="goToSeckillEventDetail(event)"
            >
              查看活动
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
  name: 'FlashSale',
  data() {
    return {
      seckillEvents: [], // 秒杀活动列表
      snackbar: {
        show: false,
        message: '',
        color: 'success',
      },
    };
  },
  created() {
    this.fetchSeckillEvents(); // 获取秒杀活动列表
  },
  methods: {
    // 获取所有秒杀活动
    async fetchSeckillEvents() {
      try {
        const response = await this.$axios.get('/seckill/events'); // 调用后端接口获取秒杀活动列表
        const events = response.data;

        // 收集所有商品 ID
        const allProductIds = [];
        events.forEach(event => {
          allProductIds.push(...event.productIds); // 将每个活动的 productIds 合并到一个数组中
        });

        // 去重处理商品 ID
        const uniqueProductIds = [...new Set(allProductIds)];

        // 获取所有商品详情
        const products = await this.fetchProductsByIds(uniqueProductIds);

        // 将商品信息附加到每个秒杀活动中
        events.forEach(event => {
          event.products = event.productIds.map(id => products.find(product => product.id === id) || {});
        });

        this.seckillEvents = events; // 更新秒杀活动列表
      } catch (error) {
        console.error('Error fetching flash sale events:', error);
        this.snackbar.message = '获取秒杀活动列表失败，请稍后重试。';
        this.snackbar.color = 'error';
        this.snackbar.show = true;
      }
    },

    // 根据商品 ID 列表批量获取商品信息
    async fetchProductsByIds(productIds) {
      try {
        const response = await this.$axios.post('/products/details', { ids: productIds });
        return response.data; // 返回商品详细信息列表
      } catch (error) {
        console.error('Error fetching product details:', error);
        this.snackbar.message = '获取商品信息失败，请稍后重试。';
        this.snackbar.color = 'error';
        this.snackbar.show = true;
        return []; // 返回空数组，避免前端报错
      }
    },

    // 格式化日期
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString(); // 可以根据需求调整日期格式
    },

    // 查看秒杀活动详情
    goToSeckillEventDetail(event) {
      this.$router.push({ name: 'SeckillEventDetail', params: { event: JSON.stringify(event) } });
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
  margin-bottom: 4px;
}
</style>
