<template>
  <div ref="chartRef" class="dashboard-chart" />
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref()
let chart
let resizeObserver

const option = {
  color: ['#2f7d5b', '#d49a32', '#45618c'],
  tooltip: { trigger: 'axis' },
  grid: { top: 28, right: 18, bottom: 28, left: 42 },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    axisLine: { lineStyle: { color: '#d8ded8' } },
    axisTick: { show: false },
  },
  yAxis: {
    type: 'value',
    axisLabel: { color: '#6b756e' },
    splitLine: { lineStyle: { color: '#edf1ed' } },
  },
  series: [
    {
      name: '正常',
      type: 'line',
      smooth: true,
      data: [18, 22, 20, 24, 27, 25, 29],
      areaStyle: { opacity: 0.12 },
    },
    {
      name: '中风险',
      type: 'line',
      smooth: true,
      data: [6, 5, 8, 6, 7, 9, 7],
      areaStyle: { opacity: 0.1 },
    },
    {
      name: '高风险',
      type: 'line',
      smooth: true,
      data: [2, 3, 2, 4, 3, 2, 3],
      areaStyle: { opacity: 0.08 },
    },
  ],
}

onMounted(() => {
  chart = echarts.init(chartRef.value)
  chart.setOption(option)
  resizeObserver = new ResizeObserver(() => chart?.resize())
  resizeObserver.observe(chartRef.value)
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  chart?.dispose()
})
</script>
