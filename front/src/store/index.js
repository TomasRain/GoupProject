// src/store/index.js

import { createStore } from 'vuex';
import AuthService from '../services/auth';
import { getProducts, createSeckillEvent } from '../services/api'; // 调用秒杀接口

const store = createStore({
  modules: {
    auth: {
      namespaced: true,
      state: {
        token: localStorage.getItem('token') || '',
        role: localStorage.getItem('role') || 'USER',
        isAuthenticated: !!localStorage.getItem('token'),
      },
      mutations: {
        SET_AUTH(state, payload) {
          state.token = payload.token;
          state.role = payload.role;
          state.isAuthenticated = true;
        },
        LOGOUT(state) {
          state.token = '';
          state.role = 'USER';
          state.isAuthenticated = false;
        },
      },
      actions: {
        setAuth({ commit }, payload) {
          localStorage.setItem('token', payload.token);
          localStorage.setItem('role', payload.role);
          commit('SET_AUTH', payload);
        },
        logout({ commit }) {
          commit('LOGOUT');
        },
      },
      getters: {
        isAuthenticated: (state) => state.isAuthenticated,
        role: (state) => state.role,
      },
    },
    seckill: {
      namespaced: true,
      state: {
        products: [],
        message: '',
      },
      mutations: {
        SET_PRODUCTS(state, products) {
          state.products = products;
        },
        SET_MESSAGE(state, message) {
          state.message = message;
        },
      },
      actions: {
        async fetchProducts({ commit }) {
          try {
            const products = await getProducts(); // 调用获取商品列表接口
            commit('SET_PRODUCTS', products);
          } catch (error) {
            commit('SET_MESSAGE', '获取商品列表失败！');
          }
        },
        async createSeckillEvent({ commit }, seckillEvent) {
          try {
            await createSeckillEvent(seckillEvent); // 调用创建秒杀活动接口
            commit('SET_MESSAGE', '秒杀活动创建成功！');
          } catch (error) {
            commit('SET_MESSAGE', '创建秒杀活动失败！');
          }
        },
      },
      getters: {
        products: (state) => state.products,
        message: (state) => state.message,
      },
    },
  },
});

export default store;
