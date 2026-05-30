<template>
  <div class="login-page">
    <section class="login-panel">
      <div class="login-brand">
        <div class="brand-mark">A</div>
        <div>
          <strong>AgriMind</strong>
          <span>Soil Intelligence Console</span>
        </div>
      </div>
      <h1>智慧农业土壤检测与决策平台</h1>
      <el-form class="login-form" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="demo_admin" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="demo123456" />
        </el-form-item>
        <el-button type="primary" size="large" @click="enterDashboard">进入控制台</el-button>
      </el-form>
    </section>
    <section class="login-side">
      <div class="field-map">
        <span v-for="cell in 36" :key="cell" :class="{ active: cell % 4 === 0 || cell % 7 === 0 }" />
      </div>
      <div class="login-note">
        <strong>面向土壤检测、指标判断和农业决策</strong>
        <span>以清晰的后台工作台组织地块、作物、检测记录和智能分析入口。</span>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = reactive({
  username: 'demo_admin',
  password: '',
})

function enterDashboard() {
  router.push('/dashboard')
}
</script>
