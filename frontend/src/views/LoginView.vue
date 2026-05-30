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
      <el-form ref="loginFormRef" class="login-form" :model="form" :rules="rules" label-position="top" @keyup.enter="submitLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" autocomplete="username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            autocomplete="current-password"
            placeholder="请输入密码"
            show-password
            type="password"
          />
        </el-form-item>
        <el-button :loading="submitting" type="primary" size="large" @click="submitLogin">登录</el-button>
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
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loginFormRef = ref()
const submitting = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function submitLogin() {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  submitting.value = true
  try {
    await userStore.login({
      username: form.username,
      password: form.password,
    })
    ElMessage.success('登录成功')
    router.push(loginRedirect())
  } finally {
    submitting.value = false
  }
}

function loginRedirect() {
  return typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
}
</script>
