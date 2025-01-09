<template>
  <div>
    <pre>{{ seckillEvents }}</pre>
  </div>
  <div>
    <v-container>
    <!-- 标题 -->
    <v-row class="mb-4">
      <v-col>
        <h1 class="display-2 font-weight-bold text-primary">秒杀活动</h1>
      </v-col>
    </v-row>

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
          </v-card-text>
          <v-card-actions>
            <v-btn
              color="primary"
              block
              @click="goToSeckillEventDetail(event.id)"
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
  </div>
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
    fetchSeckillEvents() {
      this.$axios
        .get('/seckill/events')  // 调用后端接口获取秒杀活动列表
        .then((response) => {
          this.seckillEvents = response.data;
        })
        .catch((error) => {
          console.error('Error fetching flash sale events:', error);
          this.snackbar.message = '获取秒杀活动列表失败，请稍后重试。';
          this.snackbar.color = 'error';
          this.snackbar.show = true;
        });
        //console.log(seckillEvents);
    },

    // 格式化日期
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString(); // 可以根据需求调整日期格式
    },

    // 查看秒杀活动详情
    goToSeckillEventDetail(eventId) {
      this.$router.push({ name: 'SeckillEventDetail', params: { id: eventId } });
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
</style>
