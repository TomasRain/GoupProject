<template>
  <div>
    <h1>Seckill Products</h1>
    <ul>
      <li v-for="product in products" :key="product.id">
        {{ product.name }} - Price: ${{ product.price }} - Stock: {{ product.stock }}
        <button @click="seckillProduct(product)">Buy Now</button>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      products: []  // 存储从后端获取的产品数据
    };
  },
  created() {
    this.fetchProducts();  // 组件创建时调用 API 获取数据
  },
  methods: {
    fetchProducts() {
      // 直接请求后端 API，而不使用代理
      axios.get('http://localhost:8080/api/products')
        .then(response => {
          this.products = response.data;  // 将数据存储到 products 中
        })
        .catch(error => {
          console.error('Failed to fetch products:', error);  // 捕获并打印错误
        });
    },
    seckillProduct(product) {
      alert(`You are attempting to buy ${product.name}`);
    }
  }
};
</script>
