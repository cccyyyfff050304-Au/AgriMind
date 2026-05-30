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

开发环境已在 `vite.config.js` 中配置代理：

```text
/api -> http://localhost:8080
```

因此前端请求 `/api/auth/login`、`/api/user/profile` 时会由 Vite 转发到本地后端，浏览器侧不需要额外 CORS 配置。

## 构建

```powershell
npm run build
```

## 当前说明

第 9 阶段已完成登录联调：登录页调用后端 `/api/auth/login`，token 保存到 localStorage，Axios 请求自动携带 `Authorization: Bearer <token>`，Dashboard 调用 `/api/user/profile` 获取当前用户信息。业务模块页面仍使用演示数据，尚未联调地块、作物、土壤检测等 CRUD 接口。
