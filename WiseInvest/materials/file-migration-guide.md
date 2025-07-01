# WiseInvest 项目文件移动指南

本文档详细说明了从fund-trading-system项目到WiseInvest项目的文件移动路径对应关系。

## 1. 网关服务（Gateway）

### 源目录：`fund-trading-system/fts-gateway`
### 目标目录：`WiseInvest/gateway`

```
gateway/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── wiseinvest/
│       │           └── gateway/
│       │               ├── GatewayApplication.java        <- com.fts.gateway.GatewayApplication
│       │               ├── config/                        <- com.fts.gateway.config/*
│       │               │   ├── CorsConfig.java
│       │               │   ├── GatewayConfig.java
│       │               │   └── RouteConfig.java
│       │               ├── filter/                        <- com.fts.gateway.filter/*
│       │               │   ├── AuthFilter.java
│       │               │   ├── LoggingFilter.java
│       │               │   └── RateLimitFilter.java
│       │               └── handler/                       <- com.fts.gateway.handler/*
│       │                   ├── GlobalExceptionHandler.java
│       │                   └── FallbackHandler.java
│       └── resources/
│           ├── application.yml                           <- fts-gateway/src/main/resources/application.yml
│           ├── application-dev.yml                       <- fts-gateway/src/main/resources/application-dev.yml
│           └── application-prod.yml                      <- fts-gateway/src/main/resources/application-prod.yml
└── pom.xml                                              <- fts-gateway/pom.xml

## 2. 公共模块（Common）

### 源目录：`fund-trading-system/fts-common`
### 目标目录：`WiseInvest/common`

```
common/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── wiseinvest/
│       │           └── common/
│       │               ├── config/                       <- com.fts.common.config/*
│       │               │   ├── RedisConfig.java
│       │               │   ├── SwaggerConfig.java
│       │               │   └── WebMvcConfig.java
│       │               ├── exception/                    <- com.fts.common.exception/*
│       │               │   ├── BusinessException.java
│       │               │   ├── GlobalExceptionHandler.java
│       │               │   └── ErrorCode.java
│       │               ├── utils/                        <- com.fts.common.utils/*
│       │               │   ├── DateUtils.java
│       │               │   ├── JwtUtils.java
│       │               │   └── StringUtils.java
│       │               └── response/                     <- com.fts.common.response/*
│       │                   ├── Result.java
│       │                   └── PageResult.java
│       └── resources/
│           └── common.properties                         <- fts-common/src/main/resources/common.properties
└── pom.xml                                              <- fts-common/pom.xml

## 3. API模块

### 源目录：`fund-trading-system/fts-api`
### 目标目录：`WiseInvest/api`

```
api/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── wiseinvest/
│                   └── api/
│                       ├── dto/                          <- com.fts.api.dto/*
│                       │   ├── user/
│                       │   │   ├── UserDTO.java
│                       │   │   └── LoginDTO.java
│                       │   ├── product/
│                       │   │   └── ProductDTO.java
│                       │   └── order/
│                       │       └── OrderDTO.java
│                       └── feign/                        <- com.fts.api.feign/*
│                           ├── UserFeignClient.java
│                           ├── ProductFeignClient.java
│                           └── OrderFeignClient.java
└── pom.xml                                              <- fts-api/pom.xml

## 4. 配置文件

### 1. 数据库配置

在每个服务模块的 `resources` 目录下创建数据库配置文件：

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wiseinvest_db
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 2. 日志配置

在每个服务模块的 `resources` 目录下创建日志配置文件：

```yaml
# logback-spring.xml
logging:
  file:
    path: /logs/wiseinvest/${spring.application.name}
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
```

### 3. 注册中心配置

在每个服务模块的 `application.yml` 中添加：

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    instance-id: ${spring.cloud.client.ip-address}:${server.port}
    prefer-ip-address: true
```

### 4. 网关路由配置

在 `gateway/src/main/resources/application.yml` 中：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: wiseinvest-user
          uri: lb://wiseinvest-user
          predicates:
            - Path=/api/user/**
        - id: wiseinvest-product
          uri: lb://wiseinvest-product
          predicates:
            - Path=/api/product/**
        - id: wiseinvest-order
          uri: lb://wiseinvest-order
          predicates:
            - Path=/api/order/**
```

## 5. 数据库脚本

在 `WiseInvest` 根目录下创建 `sql` 文件夹：

```
sql/
├── schema/
│   ├── wiseinvest_user.sql                              <- fund-trading-system/sql/schema/fts_user.sql
│   ├── wiseinvest_product.sql                           <- fund-trading-system/sql/schema/fts_product.sql
│   └── wiseinvest_order.sql                             <- fund-trading-system/sql/schema/fts_order.sql
└── data/
    ├── wiseinvest_user_init.sql                         <- fund-trading-system/sql/data/fts_user_init.sql
    ├── wiseinvest_product_init.sql                      <- fund-trading-system/sql/data/fts_product_init.sql
    └── wiseinvest_order_init.sql                        <- fund-trading-system/sql/data/fts_order_init.sql
```

## 6. 注意事项

1. 文件移动时需要修改的内容：
   - 包名：从 `com.fts` 改为 `com.wiseinvest`
   - 类名中的FTS前缀改为WiseInvest
   - 配置文件中的服务名称前缀
   - 数据库表名前缀
   - 日志文件路径
   - 缓存key前缀

2. 配置文件修改检查清单：
   - [ ] application.yml中的服务名称
   - [ ] 数据库连接配置
   - [ ] 日志配置
   - [ ] 注册中心配置
   - [ ] 网关路由配置
   - [ ] Redis配置
   - [ ] MQ配置（如果有）
   - [ ] 其他中间件配置

3. 代码修改检查清单：
   - [ ] 包名修改
   - [ ] 类名修改
   - [ ] 常量值修改
   - [ ] 注解中的服务名称
   - [ ] Feign接口的服务名称
   - [ ] 配置类中的前缀
   - [ ] 数据库表名引用 