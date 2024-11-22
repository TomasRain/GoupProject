<template>
  <div class="login-container">
    <div class="login-card">
      <h2>登录</h2>
      <form @submit.prevent="login">
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
        <button type="submit" class="login-button">登录</button>
      </form>
      <p v-if="message" class="alert-message">{{ message }}</p>
      <p class="register-link">
        还没有账号？<router-link to="/register">点击注册</router-link>
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      message: ''
    };
  },
  methods: {
    login() {
      this.$axios
        .post('/api/auth/login', {
          username: this.username,
          password: this.password
        })
        .then(response => {
          console.log('Login successful:', response.data);
          const token = response.data.token;
          const role = response.data.role; // 获取角色信息
          localStorage.setItem('token', token);
          localStorage.setItem('role', role); // 将角色信息存储到 localStorage
          this.$router.push({ name: 'FlashSale' });
        })
        .catch(error => {
          console.error('Login error:', error.response);
          if (error.response && error.response.status === 401) {
            this.message = '用户名或密码错误。';
          } else {
            this.message = '发生了意外错误，请稍后重试。';
          }
        });
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
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

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.login-button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

.login-button:hover {
  background-color: #0056b3;
}

.alert-message {
  color: red;
  margin-top: 15px;
}

.register-link {
  margin-top: 15px;
}

.register-link a {
  color: #007bff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
