<template>
    <v-container fluid>
      <v-row class="align-center mb-4">
        <v-col cols="8">
          <h2>订单历史</h2>
        </v-col>
        <v-col cols="4" class="text-right">
          <v-btn color="error" @click="handleLogout">注销</v-btn>
        </v-col>
      </v-row>
  
      <!-- 显示从后端获取的订单列表。后端返回的是一个数组，不支持分页和查询。 -->
      <v-row>
        <v-col
          v-for="order in orders"
          :key="order.id"
          cols="12"
          sm="6"
          md="4"
          lg="3"
        >
          <v-card
            class="mx-auto order-card"
            max-width="344"
            elevation="4"
          >
            <v-card-text>
              <div class="text-h6 font-weight-bold">订单 #{{ order.id }}</div>
              <div class="order-date">
                创建时间：{{ formatDate(order.createdAt) }}
              </div>
              <div class="order-status">
                状态：<span :class="statusClass(order.status)">{{ order.status }}</span>
              </div>
              <div class="order-total">
                总金额：¥{{ order.totalAmount }}
              </div>
            </v-card-text>
            <v-card-actions>
              <v-btn color="primary" block @click="goToOrderDetail(order.id)">
                查看详情
              </v-btn>
              <v-btn color="red" block @click="deleteOrder(order.id)">
                删除订单
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
  
      <!-- 去掉分页和搜索框，因为后端未实现分页和查询订单的功能 -->
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
  import AuthService from '@/services/auth';
  import { mapActions } from 'vuex';
  
  export default {
    name: 'OrderHistory',
    data() {
      return {
        orders: [], // 后端返回一个订单数组
        snackbar: {
          show: false,
          message: '',
          color: 'success',
        },
      };
    },
    created() {
      this.fetchOrders();
    },
    methods: {
      ...mapActions('auth', ['logout']),
      fetchOrders() {
        // 后端 GET /api/orders 返回 List<OrderDTO>
        // 不包含分页和查询功能，所以无需传递参数
        this.$axios
          .get('/orders')
          .then((response) => {
            this.orders = response.data; // 直接使用数组作为订单列表
          })
          .catch((error) => {
            console.error('Error fetching orders:', error);
            this.snackbar.message = '获取订单列表失败，请稍后重试。';
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
      goToOrderDetail(orderId) {
        this.$router.push({ name: 'OrderDetail', params: { id: orderId } });
      },
      deleteOrder(orderId) {
        // 后端 DELETE /api/orders/{id} 删除订单
        this.$axios
          .delete(`/orders/${orderId}`)
          .then(() => {
            this.snackbar.message = `订单 #${orderId} 已删除。`;
            this.snackbar.color = 'success';
            this.snackbar.show = true;
            this.fetchOrders();
          })
          .catch((error) => {
            console.error('Error deleting order:', error);
            this.snackbar.message =
              error.response?.data?.message || '删除订单失败，请重试。';
            this.snackbar.color = 'error';
            this.snackbar.show = true;
          });
      },
      handleLogout() {
        AuthService.logout();
      },
    },
  };
  </script>
  
  <style scoped>
  .order-card {
    transition: transform 0.2s, box-shadow 0.2s;
  }
  
  .order-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  }
  
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
  </style>
  