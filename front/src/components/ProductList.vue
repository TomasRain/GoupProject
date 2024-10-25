<template>
    <div>
      <h1>Product List</h1>
      <ul>
        <li v-for="product in products" :key="product.id">
          {{ product.name }} - Price: ${{ product.price }} - Stock: {{ product.stock }}
        </li>
      </ul>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        products: []  // 用来存储从后端获取到的产品数据
      };
    },
    created() {
      // 组件创建后调用方法获取产品数据
      this.fetchProducts();
    },
    methods: {
      fetchProducts() {
        // 调用后端 API 获取产品数据
        axios.get('/api/products')
          .then(response => {
            this.products = response.data;  // 将响应数据赋值给 products
          })
          .catch(error => {
            console.error('Failed to fetch products:', error);
          });
      }
    }
  };
  </script>
  