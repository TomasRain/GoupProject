<template>
  <v-container class="pa-5">
    <v-row justify="center" class="mb-8">
      <v-col cols="12" class="text-center">
        <h1 class="display-2 font-weight-bold text-primary">Welcome to the Seckill System</h1>
        <p class="subtitle-1 text-muted">You have successfully logged in!</p>
      </v-col>
    </v-row>

    <v-row justify="center" class="mt-4">
      <v-col
        v-for="(item, index) in cardItems"
        :key="index"
        cols="12"
        sm="6"
        md="4"
        class="d-flex justify-center mb-4"
      >
        <v-card :class="['mx-auto', 'pa-4', 'card-item']" elevation="12" max-width="350">
          <v-card-title class="justify-center text-h6">{{ item.title }}</v-card-title>
          <v-card-text class="text-center">
            <v-btn :color="item.buttonColor" dark @click="goToPage(item.route)" class="ma-2" block>
              {{ item.buttonText }}
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'Home',
  computed: {
    ...mapGetters('auth', ['isAuthenticated', 'role']),
    isAdmin() {
      return this.role === 'ADMIN';
    },
    cardItems() {
      return [
        {
          title: '秒杀活动',
          buttonText: '进入秒杀',
          buttonColor: 'primary',
          route: 'FlashSale',
        },
        {
          title: '商品列表',
          buttonText: '浏览商品',
          buttonColor: 'secondary',
          route: 'ProductList',
        },
        {
          title: '管理页面',
          buttonText: '管理员入口',
          buttonColor: 'error',
          route: 'AdminDashboard',
          show: this.isAdmin,  // Only show if admin
        },
      ].filter(item => item.show !== false);  // Filter out items based on the `show` property
    },
  },
  methods: {
    goToPage(route) {
      this.$router.push({ name: route });
    },
  },
};
</script>

<style scoped>
/* Set background gradient */
.v-container {
  background: linear-gradient(to bottom right, #81c784, #4caf50);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

h1 {
  font-weight: bold;
  color: #1976D2;
}

.subtitle-1 {
  color: #424242;
}

/* Card Styles */
.card-item {
  border-radius: 16px;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.2);
}

/* Button Styles */
.v-btn {
  text-transform: none;
  font-weight: 600;
  border-radius: 30px;
}

/* Responsive adjustments */
@media (max-width: 600px) {
  .v-card {
    max-width: 100%;
  }
}
</style>
