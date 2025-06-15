# KAce 服务端部署文档

本文档提供了部署 KAce 服务端的详细步骤。KAce 服务端采用微服务架构，使用 Docker 和 Docker Compose 进行容器化部署。

## 系统要求

- Docker 20.10.0 或更高版本
- Docker Compose 2.0.0 或更高版本
- 至少 4GB RAM
- 至少 20GB 可用磁盘空间

## 部署步骤

### 1. 准备环境

确保您的服务器上已安装 Docker 和 Docker Compose。如果尚未安装，可以按照以下步骤进行安装：

#### 安装 Docker

```bash
# 更新包索引
sudo apt-get update

# 安装依赖
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common

# 添加 Docker 官方 GPG 密钥
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

# 添加 Docker 仓库
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

# 更新包索引
sudo apt-get update

# 安装 Docker
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# 将当前用户添加到 docker 组
sudo usermod -aG docker $USER

# 启动 Docker 服务
sudo systemctl start docker
sudo systemctl enable docker
```

#### 安装 Docker Compose

```bash
# 下载 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.18.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 添加可执行权限
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker-compose --version
```

### 2. 克隆项目代码

```bash
git clone https://github.com/yourusername/kace.git
cd kace/server
```

### 3. 配置环境变量

创建 `.env` 文件，设置必要的环境变量：

```bash
cp .env.example .env
```

编辑 `.env` 文件，根据您的环境配置以下变量：

```
# JWT 配置
JWT_SECRET=your-secure-jwt-secret-key

# 数据库配置
POSTGRES_USER=kace
POSTGRES_PASSWORD=your-secure-password

# Redis 配置
REDIS_PASSWORD=your-secure-redis-password

# RabbitMQ 配置
RABBITMQ_DEFAULT_USER=kace
RABBITMQ_DEFAULT_PASS=your-secure-rabbitmq-password

# MinIO 配置
MINIO_ROOT_USER=kace
MINIO_ROOT_PASSWORD=your-secure-minio-password

# SMTP 配置（用于发送邮件通知）
SMTP_HOST=smtp.example.com
SMTP_PORT=587
SMTP_USERNAME=noreply@example.com
SMTP_PASSWORD=your-smtp-password
```

### 4. 构建和启动服务

```bash
# 确保脚本有执行权限
chmod +x scripts/init-multiple-db.sh

# 构建和启动所有服务
docker-compose up -d
```

首次启动可能需要一些时间，因为需要下载基础镜像并构建服务。

### 5. 验证服务状态

```bash
# 检查所有容器是否正常运行
docker-compose ps

# 检查服务日志
docker-compose logs -f gateway
```

如果一切正常，您应该能够通过 API 网关访问 KAce 服务：

```bash
curl http://localhost:8080/health
```

应该返回类似以下的响应：

```json
{
  "status": "UP"
}
```

### 6. 访问服务

- API 网关: http://localhost:8080
- RabbitMQ 管理界面: http://localhost:15672
- MinIO 控制台: http://localhost:9001

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| API 网关 | 8080 | 所有 API 请求的入口点 |
| 认证服务 | 8081 | 处理用户认证和授权 |
| 用户服务 | 8082 | 管理用户、角色和权限 |
| 内容服务 | 8083 | 管理内容、分类和标签 |
| 媒体服务 | 8084 | 管理媒体文件和处理任务 |
| 分析服务 | 8085 | 处理事件跟踪和指标收集 |
| 通知服务 | 8086 | 管理通知和消息发送 |
| PostgreSQL | 5432 | 数据库服务 |
| Redis | 6379 | 缓存和会话存储 |
| RabbitMQ | 5672, 15672 | 消息队列和管理界面 |
| MinIO | 9000, 9001 | 对象存储和控制台 |

## 生产环境部署注意事项

### 安全配置

1. 修改所有默认密码
2. 配置 HTTPS
3. 限制端口访问
4. 设置防火墙规则

### 高可用配置

1. 使用负载均衡器
2. 配置服务自动扩展
3. 设置数据库复制
4. 配置服务健康检查和自动恢复

### 监控和日志

1. 配置集中式日志收集（如 ELK Stack）
2. 设置监控系统（如 Prometheus + Grafana）
3. 配置告警通知

### 备份策略

1. 定期备份数据库
2. 备份对象存储数据
3. 设置自动备份计划

## 故障排除

### 服务无法启动

1. 检查日志：`docker-compose logs <service-name>`
2. 确保所有依赖服务已启动
3. 检查环境变量配置
4. 检查网络连接

### 数据库连接问题

1. 确保 PostgreSQL 容器正在运行
2. 检查数据库连接配置
3. 验证数据库用户权限

### API 请求失败

1. 检查网关服务日志
2. 确保目标服务正在运行
3. 验证认证信息是否正确

## 更新部署

当有新版本发布时，按照以下步骤更新部署：

```bash
# 拉取最新代码
git pull

# 重新构建和启动服务
docker-compose down
docker-compose build
docker-compose up -d

# 检查服务状态
docker-compose ps
```

## 支持和反馈

如有任何部署问题或反馈，请联系 KAce 技术支持团队：support@kace.example.com 