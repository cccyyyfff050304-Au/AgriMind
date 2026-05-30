<template>
  <section class="dashboard-page">
    <div class="summary-strip">
      <article class="metric-card profile-card">
        <span>当前用户</span>
        <strong>{{ userStore.displayName }}</strong>
        <small>{{ profileLine }}</small>
      </article>
      <article v-for="item in metrics" :key="item.label" class="metric-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.note }}</small>
      </article>
    </div>

    <div class="dashboard-grid">
      <section class="chart-panel">
        <div class="section-heading">
          <div>
            <p class="section-kicker">近 7 日检测概览</p>
            <h2>土壤风险趋势</h2>
          </div>
          <el-tag type="success" effect="plain">演示数据</el-tag>
        </div>
        <DashboardChart />
      </section>

      <section class="task-panel">
        <div class="section-heading">
          <div>
            <p class="section-kicker">项目进度</p>
            <h2>当前开发阶段</h2>
          </div>
        </div>
        <el-timeline>
          <el-timeline-item v-for="item in timeline" :key="item.title" :timestamp="item.stage">
            <strong>{{ item.title }}</strong>
            <p>{{ item.text }}</p>
          </el-timeline-item>
        </el-timeline>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import DashboardChart from '@/components/DashboardChart.vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const profileLoading = ref(false)
const profileLine = computed(() => {
  const profile = userStore.profile
  if (!profile) {
    return profileLoading.value ? '正在加载用户信息' : '尚未获取用户信息'
  }
  return `${profile.username} / ${profile.roleCode || '普通用户'}`
})

const metrics = [
  { label: '作物', value: '8', note: '包含粮食与蔬菜' },
  { label: '检测记录', value: '126', note: '本月新增 31 条' },
  { label: '高风险记录', value: '7', note: '需优先复核' },
]

const timeline = [
  { stage: 'Stage 6', title: '基础业务 CRUD', text: '地块、作物、土壤检测记录已完成。' },
  { stage: 'Stage 7', title: '规则判断', text: '指标标准和风险等级计算已完成。' },
  { stage: 'Stage 8', title: '前端工作台', text: '管理端路由、布局和核心模块入口已就绪。' },
]

onMounted(async () => {
  profileLoading.value = true
  try {
    await userStore.fetchProfile()
  } catch {
    // Axios interceptor handles user-facing message and 401 redirect.
  } finally {
    profileLoading.value = false
  }
})
</script>
