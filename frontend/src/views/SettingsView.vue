<template>
  <PlaceholderPage title="系统设置" kicker="Settings" :summary="summary" :rows="rows" />
</template>

<script setup>
import PlaceholderPage from '@/components/PlaceholderPage.vue'

const summary = [
  { label: '用户状态', value: '演示', note: 'Pinia 状态' },
  { label: '接口环境', value: '/api', note: 'Axios 默认 baseURL' },
  { label: '主题', value: 'Light', note: '后台工作台' },
]

const rows = [
  { name: 'API Base URL', owner: 'VITE_API_BASE_URL', state: '未配置', updatedAt: '2026-05-30' },
]
</script>
