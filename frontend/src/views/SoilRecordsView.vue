<template>
  <PlaceholderPage title="土壤检测记录" kicker="Soil Record" :summary="summary" :rows="rows" />
</template>

<script setup>
import PlaceholderPage from '@/components/PlaceholderPage.vue'

const summary = [
  { label: '本月检测', value: '31', note: '演示数据' },
  { label: '中风险', value: '9', note: '规则判断结果' },
  { label: '高风险', value: '7', note: '后续重点处理' },
]

const rows = [
  { name: 'SOIL20260530001', owner: '北区试验田 / 小麦', state: 'MEDIUM', updatedAt: '2026-05-30' },
  { name: 'SOIL20260529002', owner: '温室 A 区 / 番茄', state: 'LOW', updatedAt: '2026-05-29' },
]
</script>
