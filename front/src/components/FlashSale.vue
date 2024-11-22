<template>
    <div class="flash-sale">
      <div class="header">
        <h1>秒杀商品列表</h1>
        <button @click="logout" class="logout-button">注销</button>
      </div>
      <div class="product-list">
        <div v-for="product in products" :key="product.id" class="product-card">
          <img :src="product.image" :alt="product.name" />
          <h3>{{ product.name }}</h3>
          <p class="original-price">原价：¥{{ product.originalPrice }}</p>
          <p class="sale-price">秒杀价：¥{{ product.salePrice }}</p>
          <button @click="buyProduct(product.id)" class="buy-button">立即抢购</button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'FlashSale',
    data() {
      return {
        products: []
      };
    },
    created() {
      this.fetchFlashSaleProducts();
    },
    methods: {
      fetchFlashSaleProducts() {
        this.$axios
          .get('/api/flashsale/products')
          .then(response => {
            this.products = response.data;
          })
          .catch(error => {
            console.error('Error fetching products:', error);
            // 如果未认证，跳转回登录页面
            if (error.response && error.response.status === 401) {
              this.$router.push({ name: 'Login' });
            }
          });
      },
      buyProduct(productId) {
        this.$axios
          .post(`/api/flashsale/buy/${productId}`)
          .then(response => {
            alert('购买成功！');
          })
          .catch(error => {
            console.error('Error buying product:', error);
            alert('购买失败，请重试。');
          });
      },
      logout() {
        // 清除本地存储的令牌
        localStorage.removeItem('token');
        // 重定向到登录页面
        this.$router.push({ name: 'Login' });
      }
    }
  };
  </script>
  
  <style scoped>
  .flash-sale {
    padding: 20px;
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .logout-button {
    padding: 10px;
    background-color: #555;
    color: white;
    border: none;
    cursor: pointer;
  }
  
  .logout-button:hover {
    background-color: #333;
  }
  
  .product-list {
    display: flex;
    flex-wrap: wrap;
  }
  
  .product-card {
    width: 200px;
    border: 1px solid #ddd;
    margin: 10px;
    padding: 10px;
    text-align: center;
  }
  
  .product-card img {
    width: 100%;
    height: auto;
  }
  
  .original-price {
    text-decoration: line-through;
    color: #888;
  }
  
  .sale-price {
    color: #e60012;
    font-size: 18px;
    font-weight: bold;
  }
  
  .buy-button {
    padding: 10px;
    background-color: #e60012;
    color: white;
    border: none;
    cursor: pointer;
    margin-top: 10px;
  }
  
  .buy-button:hover {
    background-color: #d40010;
  }
  </style>
  