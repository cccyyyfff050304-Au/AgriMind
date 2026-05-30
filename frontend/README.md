# AgriMind Frontend

Vue 3 + Vite 前端工程，用于 AgriMind 智慧农业土壤检测与 AI 决策平台。

## 技术栈

- Vue 3
- Vite
- Element Plus
- Vue Router
- Pinia
- Axios
- ECharts

## 安装依赖

```powershell
cd D:\AgriMind\frontend
npm install
```

## 启动开发服务

```powershell
npm run dev
```

默认访问地址以终端输出为准，通常是：

```text
http://localhost:5173/
```

## 构建

```powershell
npm run build
```

## 当前说明

第 8 阶段只初始化前端骨架和静态页面占位。当前页面使用 mock 数据，`src/api/http.js` 只提供 Axios 基础封装，尚未联调后端接口。
