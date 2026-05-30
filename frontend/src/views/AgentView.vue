<template>
  <PlaceholderPage title="Agent 助手" kicker="Agent" :summary="summary" :rows="rows" />
</template>

<script setup>
import PlaceholderPage from '@/components/PlaceholderPage.vue'

const summary = [
  { label: '工具调用', value: '0', note: '规划中' },
  { label: '数据库查询', value: '0', note: '规划中' },
  { label: '建议生成', value: '-', note: '规划中' },
]

const rows = [
  { name: '查询土壤检测记录', owner: 'Tool', state: '规划中', updatedAt: '2026-05-30' },
]
</script>
