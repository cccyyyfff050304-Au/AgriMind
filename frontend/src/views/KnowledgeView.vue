<template>
  <PlaceholderPage title="知识库" kicker="Knowledge" :summary="summary" :rows="rows" />
</template>

<script setup>
import PlaceholderPage from '@/components/PlaceholderPage.vue'

const summary = [
  { label: '文档', value: '0', note: 'RAG 阶段接入' },
  { label: '文本片段', value: '0', note: '规划中' },
  { label: '向量索引', value: '-', note: '规划中' },
]

const rows = [
  { name: '农业知识库入口', owner: 'RAG 模块', state: '规划中', updatedAt: '2026-05-30' },
]
</script>
