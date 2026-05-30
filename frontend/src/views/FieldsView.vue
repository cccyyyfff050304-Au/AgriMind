<template>
  <PlaceholderPage title="地块管理" kicker="Field" :summary="summary" :rows="rows" />
</template>

<script setup>
import PlaceholderPage from '@/components/PlaceholderPage.vue'

const summary = [
  { label: '地块总数', value: '12', note: '演示数据' },
  { label: '已登记面积', value: '328 亩', note: '覆盖 2 个基地' },
  { label: '土壤类型', value: '4', note: '壤土/砂土等' },
]

const rows = [
  { name: '北区试验田', owner: '一号基地', state: '正常', updatedAt: '2026-05-30' },
  { name: '温室 A 区', owner: '设施农业', state: '维护中', updatedAt: '2026-05-29' },
]
</script>
