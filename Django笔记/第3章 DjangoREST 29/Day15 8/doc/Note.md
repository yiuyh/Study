# Day15





### 需求

- 存在级联数据
- 用户和收货地址
- 节流



### 分析

- 数据开始
  - 模型定义
  - 用户和收货地址  一对多
    - 用户表
    - 地址表
      - ForeignKey
  - 序列化
    - 级联数据如何实现序列化???
  - 节流



### 节流器

- BaseThrottle
  - allow_request
    - 是否允许的请求的核心
  - get_ident
    - 获取客户端唯一标识
  - wait
- SimpleRateThrottle
  - get_cache_key
    - 获取缓存标识
  - get_rate
    - 获取频率
  - parse_rate
    - 转换频率
      - num/duration
        - duration
          - s
          - m
          - h
          - d
  - allow_request
    - 是否允许请求
    - 重写的方法
  - throttle_success
    - 允许请求，进行请求记录
  - throttle_failure
    - 不允许请求
  - wait
    - 还有多少时间之后允许
- AnonRateThrottle
  - get_cache_key
    - 获取缓存key的原则
- UserRateThrottle
  - 和上面一模一样
- ScopedRateThrottle
  - 和上面一样
  - 多写了从属性中获取频率



### 技能点

- HTTP_X_FORWARDED_FOR
  - 获取你的原始IP
    - 通过的普通的代理发送请求的请求
    - 如果获取REMOTE_ADDR获取到的是代理IP
- 代理
  - 普通代理
  - 高匿代理
    - 效率越低，请求速度越慢





### RESTful

- django-rest-framework
  - serializers 
    - 序列化工具
      - 序列化与反序列化
    - 级联模型
      - 添加级联字段
      - nested
      - 级联字段的key原来必须就是存在的
        - 隐性属性
        - 自定义related_name
  - APIView
    - CBV
    - 实现各种的请求处理
  - mixins
    - CRUDL
    - 对模型操作
  - viewsets
    - 对APIView和Mixins高度封装
    - 可以对接 router
  - router
    - DefaultRouter
    - 可以直接批量注册路由
  - authentication
    - APIView中会自动认证
    - 自己创建认证类，实现认证方法
      - 认证成功返回元组，用户和令牌
  - permission
    - 添加权限控制
    - 用户所拥有的权限
  - throttle
    - 节流
    - 控制访问频率



### Celery

- 消息队列
  - 异步任务
  - 定时任务
- 需要了解的知识
  - 选择并安装消息容器（载体）
  - 安装Celery并创建第一个任务
  - 开启工作进程并调用任务
  - 记录工作状态和返回的结果





### Log

- info
- debug
- warning
- error
- critical







### 端

- 用户端
- 公司自己的后台
- 商家端



### 后台管理

- 快速实现自己的后台
- 内置了一个admin
- 也有第三方的
  - xadmin
  - suit



### Django内置模型

- 用户
- 组



### homework

- REST整明白
- 后台管理自己研究一个







