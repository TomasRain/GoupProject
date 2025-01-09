<template>
  <v-container class="pa-5">
    <!-- 标题 -->
    <v-row justify="center" class="mb-8">
      <v-col cols="12" class="text-center">
        <h1 class="display-2 font-weight-bold text-primary">创建秒杀活动</h1>
      </v-col>
    </v-row>
    <!--
    <pre>{{ this.products }}</pre>
    -->
    <!-- 表单卡片 -->
    <v-row justify="center" class="mt-4">
      <v-col cols="12" md="8">
        <v-card class="pa-6" elevation="12" max-width="500px">
          <v-card-title class="text-h5">活动信息</v-card-title>

          <v-form @submit.prevent="createSeckillEvent">
            <!-- 活动名称 -->
            <v-text-field
              v-model="seckillEvent.name"
              label="活动名称"
              placeholder="请输入活动名称"
              required
            ></v-text-field>

            <!-- 开始时间选择 -->
            <v-text-field
              v-model="seckillEvent.startTime"
              label="开始时间"
              prepend-icon="mdi-calendar-clock"
              type="datetime-local"
              required
              :min="minDateTime"
            ></v-text-field>

            <!-- 结束时间选择 -->
            <v-text-field
              v-model="seckillEvent.endTime"
              label="结束时间"
              prepend-icon="mdi-calendar-clock"
              type="datetime-local"
              required
              :min="seckillEvent.startTime || minDateTime"
            ></v-text-field>

            <!-- 商品选择 -->
            <v-select
              v-model="seckillEvent.productIds"
              :items="products"
              item-title="name"
              item-value="id"
              label="选择商品"
              multiple
              required
            ></v-select>

            <!-- 提交按钮 -->
            <v-btn
              :disabled="isSubmitting"
              color="primary"
              class="mt-4"
              block
              type="submit"
            >
              创建秒杀活动
            </v-btn>
          </v-form>
        </v-card>
      </v-col>
    </v-row>

    <!-- 提示信息 -->
    <v-row v-if="message" justify="center" class="mt-4">
      <v-col cols="12" md="8">
        <v-alert :type="messageClass" elevation="2" class="text-center">
          {{ message }}
        </v-alert>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { getProducts, createSeckillEvent } from "@/services/api";
import dayjs from "dayjs"; // 用于日期时间格式化（可选）

export default {
  data() {
    return {
      seckillEvent: {
        name: "",
        startTime: "",
        endTime: "",
        productIds: [],
      },
      products: [],
      message: "",
      isSubmitting: false,
      // 最小日期时间（当前时间）
      minDateTime: dayjs().format("YYYY-MM-DDTHH:mm"),
    };
  },
  computed: {
    messageClass() {
      return this.message.includes("成功") ? "success" : "error";
    },
  },
  created() {
    this.fetchProducts();
  },
  methods: {
    async fetchProducts() {
      try {
        const response = await getProducts();
        // 访问 response.products 而不是 response
        if (response.products && Array.isArray(response.products) && response.products.length > 0) {
          this.products = response.products;
        } else {
          this.message = "商品列表为空或格式不正确！";
          console.warn("API 返回的数据不符合预期:", response);
        }
      } catch (error) {
        this.message = "获取商品列表失败！";
        console.error("获取商品列表失败:", error);
      }
      //console.log("Loaded products:", this.products);  // 查看加载的商品数据
    },
    async createSeckillEvent() {
      this.isSubmitting = true;
      try {
        // 验证日期和时间是否完整
        if (!this.seckillEvent.startTime) {
          throw new Error("请选择开始日期和时间！");
        }
        if (!this.seckillEvent.endTime) {
          throw new Error("请选择结束日期和时间！");
        }

        // 使用 dayjs 转换为 ISO 格式
        const formattedStartTime = dayjs(this.seckillEvent.startTime).toISOString();
        const formattedEndTime = dayjs(this.seckillEvent.endTime).toISOString();

        // 验证结束时间是否晚于开始时间
        if (dayjs(formattedEndTime).isBefore(dayjs(formattedStartTime))) {
          throw new Error("结束时间必须晚于开始时间！");
        }

        // 构建请求数据
        const requestData = {
          name: this.seckillEvent.name,
          startTime: formattedStartTime,
          endTime: formattedEndTime,
          productIds: this.seckillEvent.productIds,
        };

        const response = await createSeckillEvent(requestData);
        this.message = response.message || "秒杀活动创建成功！";
        this.resetForm();
      } catch (error) {
        this.message = error.message || "创建秒杀活动失败！";
      } finally {
        this.isSubmitting = false;
      }
    },
    resetForm() {
      this.seckillEvent = {
        name: "",
        startTime: "",
        endTime: "",
        productIds: [],
      };
      // 更新最小日期时间为当前时间
      this.minDateTime = dayjs().format("YYYY-MM-DDTHH:mm");
    },
  },
};
</script>

<style scoped>
.v-container {
  background: linear-gradient(to bottom right, #81c784, #4caf50);
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

h1 {
  font-weight: bold;
  color: #1976d2;
}

.v-card {
  border-radius: 16px;
}

.v-btn {
  font-weight: 600;
  border-radius: 30px;
}

.v-alert {
  font-weight: bold;
}

.success {
  background-color: #81c784;
}

.error {
  background-color: #ff8a80;
}
</style>
