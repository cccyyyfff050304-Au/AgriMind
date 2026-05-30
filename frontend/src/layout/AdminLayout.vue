<template>
  <div class="admin-shell">
    <SidebarNav />
    <div class="workspace">
      <TopHeader />
      <main class="workspace-main">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import SidebarNav from '@/components/SidebarNav.vue'
import TopHeader from '@/components/TopHeader.vue'
</script>
