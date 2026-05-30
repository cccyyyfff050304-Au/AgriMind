import { createApp } from 'vue'
import {
  ElAvatar,
  ElButton,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMenu,
  ElMenuItem,
  ElTable,
  ElTableColumn,
  ElTag,
  ElTimeline,
  ElTimelineItem,
} from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

const app = createApp(App)

app
  .use(createPinia())
  .use(router)
  .use(ElAvatar)
  .use(ElButton)
  .use(ElForm)
  .use(ElFormItem)
  .use(ElIcon)
  .use(ElInput)
  .use(ElMenu)
  .use(ElMenuItem)
  .use(ElTable)
  .use(ElTableColumn)
  .use(ElTag)
  .use(ElTimeline)
  .use(ElTimelineItem)
  .mount('#app')
