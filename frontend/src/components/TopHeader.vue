<template>
  <header class="top-header">
    <div>
      <p class="header-eyebrow">AgriMind Console</p>
      <h1>{{ pageTitle }}</h1>
    </div>
    <div class="header-actions">
      <el-tag effect="plain" type="success">本地开发</el-tag>
      <el-button :icon="Refresh" circle aria-label="刷新" />
      <el-button :icon="Bell" circle aria-label="通知" />
      <div class="user-chip">
        <el-avatar :size="32">A</el-avatar>
        <div>
          <strong>{{ userStore.profile?.displayName }}</strong>
          <span>{{ userStore.profile?.role }}</span>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Bell, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const userStore = useUserStore()
const pageTitle = computed(() => route.meta.title || 'Dashboard')
</script>
