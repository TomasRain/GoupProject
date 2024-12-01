<template>
  <div class="register-container">
    <div class="register-card">
      <h2>注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            type="text"
            id="username"
            v-model="username"
            required
            placeholder="请输入用户名"
          />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input
            type="password"
            id="password"
            v-model="password"
            required
            placeholder="请输入密码"
          />
        </div>
        <div class="form-group">
          <label for="role">角色</label>
          <select id="role" v-model="role" required>
            <option value="USER">用户</option>
            <option value="ADMIN">管理员</option>
          </select>
        </div>
        <button type="submit" class="register-button">注册</button>
      </form>
      <p v-if="message" :class="{'success-message': success, 'error-message': !success}">
        {{ message }}
      </p>
      <p class="login-link">
        已有账号？<router-link to="/login">点击登录</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import AuthService from '@/services/auth';

export default {
  name: 'Register',
  data() {
    return {
      username: '',
      password: '',
      role: 'USER', // 默认角色为 USER
      message: '',
      success: false,
    };
  },
  methods: {
    handleRegister() {
      AuthService.register(this.username, this.password, this.role)
        .then(response => {
          console.log('Register successful:', response);
          this.success = true;
          this.message = '注册成功！2秒后跳转到登录页面。';
          setTimeout(() => {
            this.$router.push({ name: 'Login' });
          }, 2000);
        })
        .catch(error => {
          console.error('Register error:', error);
          this.success = false;
          if (error.response && error.response.status === 409) {
            this.message = '用户名已存在！';
          } else if (error.response && error.response.status === 400) {
            this.message = '角色不合法！';
          } else {
            this.message = '发生了意外错误，请稍后重试。';
          }
        });
    },
  },
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.register-card {
  width: 100%;
  max-width: 400px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: white;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

h2 {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.register-button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 4px;
  background-color: #28a745;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

.register-button:hover {
  background-color: #218838;
}

.success-message {
  color: green;
  margin-top: 15px;
}

.error-message {
  color: red;
  margin-top: 15px;
}

.login-link {
  margin-top: 15px;
}

.login-link a {
  color: #007bff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
