<template>
  <v-container>
    <v-row>
      <v-col cols="12" md="4" v-for="product in products" :key="product.id">
        <v-card>
          <v-card-title>{{ product.name }}</v-card-title>
          <v-card-subtitle>Price: ${{ product.price }}</v-card-subtitle>
          <v-card-text>
            <div>Stock: {{ product.stock }}</div>
          </v-card-text>
          <v-card-actions>
            <v-btn color="success" @click="seckillProduct(product)">
              Buy Now
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      products: []
    };
  },
  created() {
    this.fetchProducts();
  },
  methods: {
    fetchProducts() {
      axios
        .get('/api/products')
        .then(response => {
          this.products = response.data;
        })
        .catch(error => {
          console.error('Failed to fetch products:', error);
        });
    },
    seckillProduct(product) {
      alert(`You are attempting to buy ${product.name}`);
    }
  }
};
</script>
