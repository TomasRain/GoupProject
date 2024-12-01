// src/store/index.js

import { createStore } from 'vuex';
import AuthService from '../services/auth';

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
          //AuthService.logout();
          commit('LOGOUT');
        },
      },
      getters: {
        isAuthenticated: (state) => state.isAuthenticated,
        role: (state) => state.role,
      },
    },
  },
});

export default store;
