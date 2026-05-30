import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/token'

const AdminLayout = () => import('@/layout/AdminLayout.vue')
const LoginView = () => import('@/views/LoginView.vue')
const DashboardView = () => import('@/views/DashboardView.vue')
const FieldsView = () => import('@/views/FieldsView.vue')
const CropsView = () => import('@/views/CropsView.vue')
const SoilRecordsView = () => import('@/views/SoilRecordsView.vue')
const IndicatorStandardsView = () => import('@/views/IndicatorStandardsView.vue')
const AiReportsView = () => import('@/views/AiReportsView.vue')
const KnowledgeView = () => import('@/views/KnowledgeView.vue')
const AgentView = () => import('@/views/AgentView.vue')
const SettingsView = () => import('@/views/SettingsView.vue')

const routes = [
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
  },
  {
    path: '/',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', name: 'dashboard', component: DashboardView, meta: { title: 'Dashboard' } },
      { path: 'fields', name: 'fields', component: FieldsView, meta: { title: '地块管理' } },
      { path: 'crops', name: 'crops', component: CropsView, meta: { title: '作物管理' } },
      { path: 'soil-records', name: 'soil-records', component: SoilRecordsView, meta: { title: '土壤检测记录' } },
      { path: 'indicator-standards', name: 'indicator-standards', component: IndicatorStandardsView, meta: { title: '指标标准' } },
      { path: 'ai-reports', name: 'ai-reports', component: AiReportsView, meta: { title: 'AI 报告' } },
      { path: 'knowledge', name: 'knowledge', component: KnowledgeView, meta: { title: '知识库' } },
      { path: 'agent', name: 'agent', component: AgentView, meta: { title: 'Agent 助手' } },
      { path: 'settings', name: 'settings', component: SettingsView, meta: { title: '系统设置' } },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const hasToken = Boolean(getToken())
  if (to.name === 'login' && hasToken) {
    return '/dashboard'
  }
  if (to.meta.requiresAuth && !hasToken) {
    return {
      path: '/login',
      query: { redirect: to.fullPath },
    }
  }
  return true
})

export default router
