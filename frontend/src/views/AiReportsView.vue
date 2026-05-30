<template>
  <PlaceholderPage title="AI 报告" kicker="AI Report" :summary="summary" :rows="rows" />
</template>

<script setup>
import PlaceholderPage from '@/components/PlaceholderPage.vue'

const summary = [
  { label: '报告草稿', value: '0', note: '规划中' },
  { label: '模型调用', value: '0', note: '规划中' },
  { label: '可解释建议', value: '-', note: '规划中' },
]

const rows = [
  { name: '土壤分析报告模板', owner: 'AI 模块', state: '规划中', updatedAt: '2026-05-30' },
]
</script>
