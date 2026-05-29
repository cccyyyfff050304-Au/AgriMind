# AgriMind

AgriMind 是一个智慧农业土壤检测与 AI 决策平台，目标是按真实项目流程逐步完成后端、前端、AI 报告、RAG 知识库、Agent 工具调用、GitHub 协作和部署。

## 技术目标

- 后端：Java 17、Spring Boot 3、MyBatis-Plus、MySQL、Redis
- 前端：Vue 3、Vite、Element Plus、ECharts
- AI：大模型 API、土壤分析报告、RAG 农业知识库问答、Agent 工具调用
- 工程化：Git、Docker、Nginx、云服务器、云数据库

## 当前环境检查

- Git：2.54.0.windows.1
- Java/Javac：11.0.6（后续需要切换或安装 Java 17）
- Maven：3.9.4
- Node.js：24.16.0
- npm：11.13.0
- MySQL Client：8.0.34
- pnpm/yarn/Docker/Redis 命令：当前未检测到

## 目录结构

```text
AgriMind/
  backend/      # 后端工程目录，后续创建 Spring Boot 项目
  frontend/     # 前端工程目录，后续创建 Vue3 + Vite 项目
  docs/         # 需求、设计、接口、部署等文档
  sql/          # 数据库初始化脚本、迁移脚本、示例数据
  notebooks/    # AI/RAG 相关实验笔记或数据探索文件
  docker/       # Docker、Nginx、部署编排配置
```

## 安全约定

数据库密码、API Key、云服务器密码等敏感信息不能写入代码仓库。项目配置应优先使用环境变量，并只提交示例配置文件，例如 `.env.example`。

## 开发流程

每次只完成一个小任务。开始前明确目标、技术点、涉及文件和验证方式；修改前检查项目结构；完成后运行测试、查看 `git status`，再给出规范提交信息。
