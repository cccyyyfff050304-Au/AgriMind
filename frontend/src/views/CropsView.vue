<template>
  <PlaceholderPage title="作物管理" kicker="Crop" :summary="summary" :rows="rows" />
</template>

<script setup>
import PlaceholderPage from '@/components/PlaceholderPage.vue'

const summary = [
  { label: '作物档案', value: '8', note: '演示数据' },
  { label: '配置 pH', value: '6 项', note: '适宜区间' },
  { label: '分类', value: '3', note: '粮食/蔬菜/经济作物' },
]

const rows = [
  { name: '小麦', owner: '粮食作物', state: '启用', updatedAt: '2026-05-30' },
  { name: '番茄', owner: '蔬菜作物', state: '启用', updatedAt: '2026-05-28' },
]
</script>
