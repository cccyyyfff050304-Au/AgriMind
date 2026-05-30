<template>
  <section class="page-section">
    <div class="section-heading">
      <div>
        <p class="section-kicker">{{ kicker }}</p>
        <h2>{{ title }}</h2>
      </div>
      <el-tag effect="plain">{{ status }}</el-tag>
    </div>

    <div class="module-grid">
      <article v-for="item in summary" :key="item.label" class="metric-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.note }}</small>
      </article>
    </div>

    <el-table :data="rows" class="module-table" border>
      <el-table-column prop="name" label="名称" min-width="150" />
      <el-table-column prop="owner" label="关联对象" min-width="140" />
      <el-table-column prop="state" label="状态" width="120" />
      <el-table-column prop="updatedAt" label="更新时间" width="160" />
    </el-table>
  </section>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  kicker: {
    type: String,
    default: '模块入口',
  },
  status: {
    type: String,
    default: '演示数据',
  },
  summary: {
    type: Array,
    required: true,
  },
  rows: {
    type: Array,
    required: true,
  },
})
</script>
