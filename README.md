# SpringBoot 项目初始模板

基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。该模板提供了完整的用户管理功能、权限认证、文件上传等常见后端功能，便于快速开发各类Web应用。

![GitHub](https://img.shields.io/github/license/aiaicoder/springboot-init)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green)
![Java](https://img.shields.io/badge/Java-17-blue)

## 目录

- [项目特点](#项目特点)
- [技术栈](#技术栈)
- [功能模块](#功能模块)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [部署指南](#部署指南)
- [API文档](#api文档)
- [贡献](#贡献)
- [许可证](#许可证)

## 项目特点

- 🚀 **快速开发**：集成常用框架，减少重复工作
- 🔐 **权限认证**：基于 Sa-Token 的安全认证机制
- 🗃️ **数据存储**：支持 MySQL、Redis 等多种数据存储
- 📦 **文件存储**：集成腾讯云 COS 对象存储
- 🧠 **AI能力**：集成 LangChain4j 提供 AI 功能
- 📋 **接口文档**：集成 Knife4j 自动生成 API 文档
- 🧪 **测试支持**：提供完整的单元测试示例
- 🐳 **容器化**：支持 Docker 部署

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.3 | 核心框架 |
| Sa-Token | 1.44.0 | 权限认证框架 |
| MyBatis Plus | 3.5.14 | ORM框架 |
| Redis | - | 缓存数据库 |
| MySQL | 8.0+ | 关系型数据库 |
| LangChain4j | 1.4.0 | AI集成框架 |
| Knife4j | 4.4.0 | API文档工具 |

## 功能模块

- 用户注册、登录、登出
- 用户信息管理
- 权限角色管理
- 文件上传（支持腾讯云COS）
- AI问答功能
- 全局异常处理
- 统一响应封装
- 分页查询支持
- 数据校验

## 项目结构

```
.
├── sql                           # 数据库脚本
│   └── create_table.sql          # 建表SQL
├── src                           # 源码目录
│   └── main                      # 主代码目录
│       ├── java                  # Java源码
│       │   └── com.xin.springbootinit
│       │       ├── MainApplication.java     # 启动类
│       │       ├── common        # 通用类
│       │       │   ├── BaseResponse.java    # 统一响应结果
│       │       │   ├── DeleteRequest.java   # 删除请求
│       │       │   ├── ErrorCode.java       # 错误码
│       │       │   ├── PageRequest.java     # 分页请求
│       │       │   └── ResultUtils.java     # 响应工具类
│       │       ├── config        # 配置类
│       │       │   ├── COS       # 腾讯云COS配置
│       │       │   ├── Mp        # MyBatis Plus配置
│       │       │   ├── Redis     # Redis配置
│       │       │   ├── SaToken   # 权限认证配置
│       │       │   └── JsonConfig.java      # JSON配置
│       │       ├── constant      # 常量类
│       │       ├── controller    # 控制器
│       │       │   ├── AdminUserController.java
│       │       │   ├── FileController.java
│       │       │   └── UserController.java
│       │       ├── exception     # 异常处理
│       │       │   ├── BusinessException.java
│       │       │   ├── GlobalExceptionHandler.java
│       │       │   └── ThrowUtils.java
│       │       ├── factory       # 工厂类
│       │       ├── manager       # 通用业务处理层
│       │       │   └── CosManager.java
│       │       ├── mapper        # 数据访问层
│       │       │   └── UserMapper.java
│       │       ├── model         # 数据模型
│       │       │   ├── dto       # 数据传输对象
│       │       │   ├── entity    # 实体类
│       │       │   ├── enums     # 枚举类
│       │       │   └── vo        # 视图对象
│       │       ├── service       # 业务逻辑层
│       │       │   ├── impl      # 业务实现
│       │       │   ├── AiService.java
│       │       │   └── UserService.java
│       │       └── utils         # 工具类
│       └── resources             # 资源文件
│           ├── mapper            # MyBatis Mapper XML
│           ├── application.yml   # 主配置文件
│           ├── application-dev.yml  # 开发环境配置
│           └── application-prod.yml # 生产环境配置
├── Dockerfile                    # Docker配置文件
├── pom.xml                       # Maven依赖配置
└── README.md                     # 项目说明文档
```

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 数据库配置

1. 创建数据库并执行建表脚本：

```sql
-- 创建数据库
create database if not exists my_db;

-- 使用数据库
use my_db;

-- 创建用户表
create table if not exists user
(
    id           varchar(32) auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    Sex          tinyint(1)   default 1                 null comment '1：男,0:女',
    Email        varchar(50)                            not null comment '邮箱',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    constraint idx_Email
        unique (Email)
) comment '用户' collate = utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application-dev.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/my_db
    username: your_username
    password: your_password
```

### Redis配置

修改 `src/main/resources/application-dev.yml` 中的Redis配置：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password
```

### 启动项目

```bash
# 克隆项目
git clone https://github.com/aiaicoder/springboot-init.git

# 进入项目目录
cd springboot-init

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

或者直接运行 [MainApplication.java](file:///D:/www/planetProject/back-end-Template/springboot-init-master/src/main/java/com/xin/springbootinit/MainApplication.java) 启动项目。

## 配置说明

### 应用配置

项目使用多环境配置，通过 `spring.profiles.active` 指定当前环境：

- `application.yml` - 主配置文件
- `application-dev.yml` - 开发环境配置
- `application-prod.yml` - 生产环境配置

### 权限认证配置

```yaml
sa-token:
  token-name: satoken              # token名称
  timeout: 2592000                 # token有效期（秒）
  active-timeout: -1               # 活跃超时时间
  is-concurrent: true              # 是否允许并发登录
  is-share: true                   # 是否共享token
  token-style: random-64           # token风格
  is-log: true                     # 是否输出日志
```

### 腾讯云COS配置

```yaml
cos:
  client:
    accessKey: your_access_key
    secretKey: your_secret_key
    region: your_region
    bucket: your_bucket
```

## 部署指南

### 本地部署

```bash
# 打包项目
mvn clean package

# 运行jar包
java -jar target/springboot-init-0.0.1-SNAPSHOT.jar
```

### Docker部署

```bash
# 构建镜像
docker build -t springboot-init .

# 运行容器
docker run -d -p 8101:8101 springboot-init
```

## API文档

项目集成了 Knife4j API 文档工具，启动项目后访问：

```
http://localhost:8101/api/doc.html
```

可以在线查看和测试所有API接口。

## 贡献

欢迎提交 Issue 和 Pull Request 进行贡献。

## 许可证

[MIT](LICENSE) © 程序员小新