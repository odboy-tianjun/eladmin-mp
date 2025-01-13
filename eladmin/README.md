<h1 style="text-align: center">ELADMIN 后台管理系统</h1>

#### 项目简介

一个基于 Spring Boot 2.7.18 、 Mybatis-Plus、 JWT、Spring Security、Redis、Vue的前后端分离的后台管理系统

**开发文档：**  [https://eladmin.vip](https://eladmin.vip)

**体验地址：**  [https://eladmin.vip/demo](https://eladmin.vip/demo)

**账号密码：** `admin / 123456`

#### 项目源码

|        | 后端源码                                        | 前端源码                                        |
|--------|---------------------------------------------|---------------------------------------------|
| github | https://github.com/odboy-tianjun/eladmin-mp | https://github.com/odboy-tianjun/eladmin-mp |

#### 主要特性

- 使用最新技术栈，社区资源丰富。
- 高效率开发，代码生成器可一键生成前后端代码
- 支持数据字典，可方便地对一些状态进行管理
- 支持接口限流，避免恶意请求导致服务层压力过大
- 支持接口级别的功能权限与数据权限，可自定义操作
- 自定义权限注解与匿名接口注解，可快速对接口拦截与放行
- 对一些常用地前端组件封装：表格数据请求、数据字典等
- 前后端统一异常拦截处理，统一输出异常，避免繁琐的判断
- 支持在线用户管理与服务器性能监控，支持限制单用户登录
- 支持运维管理，可方便地对远程服务器的应用进行部署与管理

#### 系统功能

- 用户管理：提供用户的相关配置，新增用户后，默认密码为123456
- 角色管理：对权限与菜单进行分配，可根据部门设置角色的数据权限
- 菜单管理：已实现菜单动态路由，后端可配置化，支持多级菜单
- 部门管理：可配置系统组织架构，树形表格展示
- 岗位管理：配置各个部门的职位
- 字典管理：可维护常用一些固定的数据，如：状态，性别等
- 系统日志：记录用户操作日志与异常日志，方便开发人员定位排错
- SQL监控：采用druid 监控数据库访问性能，默认用户名admin，密码123456
- 定时任务：整合Quartz做定时任务，加入任务日志，任务运行情况一目了然
- 代码生成：高灵活度生成前后端代码，减少大量重复的工作任务
- 邮件工具：配合富文本，发送html格式的邮件
- 七牛云存储：可同步七牛云存储的数据到系统，无需登录七牛云直接操作云数据
- 支付宝支付：整合了支付宝支付并且提供了测试账号，可自行测试
- 服务监控：监控服务器的负载情况
- 运维管理：一键部署你的应用

#### 项目结构

项目采用按功能分模块的开发方式，结构如下

- `eladmin-infra-base` 为系统的基础依赖模块

- `eladmin-infra-exception` 为系统的异常处理模块

- `eladmin-common` 为系统的公共模块，各种工具类，公共配置存在该模块

- `eladmin-system` 为系统核心模块也是项目入口模块，也是最终需要打包部署的模块

- `eladmin-logging` 为系统的日志模块，其他模块如果需要记录日志需要引入该模块

- `eladmin-tools` 为第三方工具模块，包含：邮件、七牛云存储、本地存储、支付宝

- `eladmin-generator` 为系统的代码生成模块，支持生成前后端CRUD代码

- `eladmin-gitlab-boot-starter` 为系统支持Gitlab功能的模块

- `eladmin-k8s-boot-starter` 为系统支持Kubernetes功能的模块

#### 详细结构

```
- eladmin-infra-base 基础模型、常量、工具类
- eladmin-infra-exception 全局异常处理模块
- eladmin-common 公共模块
    - base 提供了 Entity 基类
    - infra 基础设施层
        - cache redis配置
        - context 系统上下文
        - doc swagger配置
        - druid 数据源中间件配置
        - limit 接口限流切面
        - monitor 系统监控、健康检查等
        - mybatisplus mybatis-plus配置
        - security 安全类的系统自定义注解
        - upload 文件上传
    - util 系统通用工具类
- eladmin-logging 系统日志模块
- eladmin-tools 系统第三方工具模块
- eladmin-generator 系统代码生成模块
- eladmin-gitlab-boot-starter 支持管控Gitlab
    - constant 常量
    - contenxt 自动装配，连接初始化
    - model 模型
    - repository gitlab控制类
- eladmin-k8s-boot-starter 支持管控Kubernetes
    - constant 常量
    - contenxt 自动装配，连接初始化
    - model 模型
    - repository k8s控制类
    - util 工具/帮助类
- eladmin-system 系统核心模块（系统启动入口）
    - infra 基础设施层
        - k8s k8s相关
        - server 配置跨域与静态资源，与数据权限
        - thread 线程池相关
        - websocket WebSocket相关
	- modules 系统相关模块(登录授权、系统监控、定时任务、运维管理等)
```

#### 特别鸣谢

- 感谢 [PanJiaChen](https://github.com/PanJiaChen/vue-element-admin) 大佬提供的前端模板

- 感谢 [Moxun](https://github.com/moxun1639) 大佬提供的前端 Curd 通用组件

- 感谢 [zhy6599](https://gitee.com/zhy6599) 大佬提供的后端运维管理相关功能

- 感谢 [j.yao.SUSE](https://github.com/everhopingandwaiting) 大佬提供的匿名接口与Redis限流等功能
