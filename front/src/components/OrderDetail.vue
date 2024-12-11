<template>
  <v-container fluid>
    <!-- 返回按钮 -->
    <v-btn icon @click="goBack" class="mt-4 ml-2">
      <v-icon>mdi-arrow-left</v-icon>
    </v-btn>

    <v-card class="mx-auto my-4 pa-4" max-width="800">
      <v-card-title>
        <div class="text-h5">订单 #{{ order.id }}</div>
        <v-spacer></v-spacer>
        <div :class="statusClass(order.status)">{{ order.status }}</div>
      </v-card-title>
      <v-divider></v-divider>
      <v-card-text>
        <p>创建时间：{{ formatDate(order.createdAt) }}</p>
        <p>更新时间：{{ formatDate(order.updatedAt) }}</p>
        <p>总金额：¥{{ order.totalAmount }}</p>
        <v-divider class="my-4"></v-divider>
        
        <!-- 如果订单还未取消，可以显示“取消订单”按钮 -->
        <div class="mb-4" v-if="showCancelButton">
          <v-btn color="red" @click="confirmCancelOrder">取消订单</v-btn>
        </div>

        <div class="order-items">
          <h3>订单项</h3>
          <v-row>
            <v-col
              v-for="item in order.orderItems"
              :key="item.productId"
              cols="12"
              sm="6"
              md="4"
              lg="3"
            >
              <v-card class="mx-auto my-2 pa-2" max-width="200">
                <img
                  :src="item.imageUrl || placeholderImage"
                  height="150px"
                  @error="onImageError"
                  class="rounded"
                ></img>
                <v-card-text>
                  <div class="text-h6">{{ item.productName }}</div>
                  <div class="quantity">数量：{{ item.quantity }}</div>
                  <div class="price">单价：¥{{ item.price }}</div>
                  <div class="total-price">总价：¥{{ item.totalPrice }}</div>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </div>
      </v-card-text>
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
  name: 'OrderDetail',
  data() {
    return {
      order: {},
      snackbar: {
        show: false,
        message: '',
        color: 'success',
      },
      placeholderImage,
    };
  },
  computed: {
    // 当订单状态不是 CANCELLED 时显示取消按钮（PENDING、CONFIRMED 都可以取消）
    showCancelButton() {
      return this.order.status !== 'CANCELLED';
    },
  },
  created() {
    const orderId = this.$route.params.id;
    this.fetchOrderDetail(orderId);
  },
  methods: {
    fetchOrderDetail(orderId) {
      // 后端 GET /api/orders/{id} 返回单个 OrderDTO
      this.$axios
        .get(`/orders/${orderId}`)
        .then((response) => {
          this.order = response.data;
        })
        .catch((error) => {
          console.error('Error fetching order detail:', error);
          this.snackbar.message = '订单不存在。';
          this.snackbar.color = 'error';
          this.snackbar.show = true;
          setTimeout(() => {
            this.$router.push({ name: 'OrderHistory' });
          }, 3000);
        });
    },
    confirmCancelOrder() {
      // 弹出确认对话框或直接调用取消逻辑
      const confirmed = window.confirm("确定要取消此订单吗？"); 
      if (confirmed) {
        this.cancelOrder();
      }
    },
    cancelOrder() {
      // 调用后端 DELETE /api/orders/{id} 接口
      this.$axios
        .delete(`/orders/${this.order.id}`)
        .then(() => {
          this.snackbar.message = '订单已取消。';
          this.snackbar.color = 'success';
          this.snackbar.show = true;
          setTimeout(() => {
            this.$router.push({ name: 'OrderHistory' }); // 返回订单列表页
          }, 1500);
        })
        .catch((error) => {
          console.error('Error cancelling order:', error);
          this.snackbar.message =
            error.response?.data?.message || '取消订单失败，请重试。';
          this.snackbar.color = 'error';
          this.snackbar.show = true;
        });
    },
    formatDate(dateStr) {
      const date = new Date(dateStr);
      return date.toLocaleString();
    },
    statusClass(status) {
      switch (status) {
        case 'CONFIRMED':
          return 'status-confirmed';
        case 'PENDING':
          return 'status-pending';
        case 'CANCELLED':
          return 'status-cancelled';
        default:
          return '';
      }
    },
    goBack() {
      this.$router.back();
    },
    onImageError(event) {
      event.target.src = require('@/assets/placeholder.png');
    },
  },
};
</script>

<style scoped>
.status-confirmed {
  color: green;
  font-weight: bold;
}
.status-pending {
  color: orange;
  font-weight: bold;
}
.status-cancelled {
  color: red;
  font-weight: bold;
}
.order-items h3 {
  margin-bottom: 20px;
}
.quantity, .price, .total-price {
  font-size: 14px;
  margin-top: 5px;
}
</style>
