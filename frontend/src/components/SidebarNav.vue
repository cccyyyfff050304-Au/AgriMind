<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="brand-mark">A</div>
      <div>
        <strong>AgriMind</strong>
        <span>智慧农业决策平台</span>
      </div>
    </div>

    <el-menu class="sidebar-menu" :default-active="route.path" router>
      <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
        <el-icon><component :is="item.icon" /></el-icon>
        <span>{{ item.label }}</span>
      </el-menu-item>
    </el-menu>
  </aside>
</template>

<script setup>
import { useRoute } from 'vue-router'
import {
  Collection,
  Crop,
  Cpu,
  DataAnalysis,
  DataBoard,
  DocumentChecked,
  Files,
  MapLocation,
  Notebook,
  Setting,
} from '@element-plus/icons-vue'

const route = useRoute()

const menuItems = [
  { path: '/dashboard', label: 'Dashboard', icon: DataBoard },
  { path: '/fields', label: '地块管理', icon: MapLocation },
  { path: '/crops', label: '作物管理', icon: Crop },
  { path: '/soil-records', label: '土壤检测', icon: Files },
  { path: '/indicator-standards', label: '指标标准', icon: DocumentChecked },
  { path: '/ai-reports', label: 'AI 报告', icon: DataAnalysis },
  { path: '/knowledge', label: '知识库', icon: Collection },
  { path: '/agent', label: 'Agent 助手', icon: Cpu },
  { path: '/settings', label: '系统设置', icon: Setting },
]
</script>
