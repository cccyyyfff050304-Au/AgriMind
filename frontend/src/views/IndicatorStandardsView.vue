<template>
  <PlaceholderPage title="指标标准" kicker="Indicator" :summary="summary" :rows="rows" />
</template>

<script setup>
import PlaceholderPage from '@/components/PlaceholderPage.vue'

const summary = [
  { label: '指标项', value: '6', note: 'pH/N/P/K/有机质/水分' },
  { label: '作物标准', value: '18', note: '演示数据' },
  { label: '启用率', value: '100%', note: '当前配置' },
]

const rows = [
  { name: '小麦 pH', owner: '小麦', state: '6.0 - 7.5', updatedAt: '2026-05-30' },
  { name: '小麦 有效氮', owner: '小麦', state: '80 - 120', updatedAt: '2026-05-30' },
]
</script>
