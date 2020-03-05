# Day13



### 重量级RESTful

- django-rest-framework

- REST难点

  - 模型序列化
    - 正向序列化
      - 将模型转换成JSON
    - 反向序列化
      - 将JSON转换成模型
  - serialization
    - 在模块serializers
      - HyperLinkedModelSerializer
        - 序列化模型，并添加超链接
      - Serializer
        - 手动序列化
  - 双R
    - Request
      - rest_framework.request
      - 将Django中的Request作为了自己的一个属性 _request
      - 属性和方法
        - content_type
        - stream
        - query_params
        - data
          - 同时兼容  POST,PUT,PATCH
        - user
          - 可以直接在请求上获取用户
          - 相当于在请求上添加一个属性，用户对象
        - auth
          - 认证
          - 相当于请求上添加了一个属性，属性值是token
        - successful_authenticator
          - 认证成功
    - Response
      - 依然是HttpResponse的子类
      - 自己封装的
        - data 直接接受字典转换成JSON
        - status  状态码
      - 属性和方法
        - rendered_content
        - status_text
  - APIView
    - renderer_classes
      - 渲染的类
    - parser_classes
      - 解析转换的类
    - authentication_classes
      - 认证的类
    - throttle_classes
      - 节流的类
      - 控制请求频率的
    - permission_classes
      - 权限的类
    - content_negotiation_class
      - 内容过滤类
    - metadata_class
      - 元信息的类
    - versioning_class
      - 版本控制的类
    - as_view()
      - 调用父类中的as_view -> dispatch
        - dispatch被重写
        - initialize_request
          - 使用django的request构建了一个REST中的Request
        - initial
          - perform_authentication
            - 执行用户认证
            - 遍历我们的认证器
              - 如果认证成功会返回一个元组
              - 元组中的第一个元素就是 user
              - 第二个元素就是 auth，token
          - check_permissions
            - 检查权限
            - 遍历我们的权限检测器
              - 只要有一个权限检测没通过
              - 就直接显示权限被拒绝
              - 所有权限都满足，才算是拥有权限
          - check_throttles
            - 检测频率
            - 遍历频率限制器
              - 如果验证不通过，就需要等待
        - csrf_exempt
          - 所有APIView的子类都是csrf豁免的
  - 错误码
    - 封装 status模块中
    - 实际上就是一个常量类
  - 针对视图函数的包装
    - CBV
      - APIView
    - FBV
      - 添加 @api_view装饰器
      - 必须手动指定允许的请求方法


### Bug

- 将Request内容转换成JSON数据出现bug



### homework

- 自己梳理APIView
- 使用REST编写接口
  - 直接对接昨天作业的前端
  - 不能使用viewsets



#### REST-Framework

- 序列化器
  - Serializer



### HelloREST

- 序列化器
- 视图函数
  - viewsets.ModelViewSet
  - CBV
  - 视图集合
- 路由
  - routers.DefaultRouter
- 记得在INSTALLED_APPS添加 rest_framework
- runserver
  - 所有Api变成可视化
  - 超链接
    - HyperLinkedModelSerializer
  - 对数据集合实现了
    - 路由  /users/， /groups/
    - get
    - post
  - 对单个实现了
    - 路由   /users/id/,  /groups/id/
    - get
    - post
    - put
    - delelte
    - patch
  - viewsets做了视图函数的实现
  - router做了路由的注册



### 命名规范

- 拒绝 中文，空格，特殊字符，关键字，保留字在命名和路径中
- 拒绝数字开头，拒绝$开头
- 小写字母或大写字母开头，驼峰命名
- 使用框架，注意原理框架的名字



### Admin

- django内置后台管理
- User和Group
- 自带权限



### 虚拟环境

- virtualenvwrapper
  - 对virtualenv的包装
  - mkvirtualenv
  - rmvirtualenv
  - workon
  - deactivate
- vitualenv
  - virtualenv   xxx
  - source  /xx/xx/activate
  - deactivate
  - rm -rf xxx
  - 指令在哪调用，虚拟就在哪生成





### 类视图

- View
  - 核心
  - dispatch
- TemplateView
  - 多继承的子类
  - View
    - 分发
    - 函数 dispatch
  - ContextMixin
    - 接收上下文
    - 从视图函数传递到模板的内容
    - 函数 get_context_data
  - TemplateResponseMixin
    - 将内容渲染到模板中
    - template_name
    - template_engine
    - response_class
    - content_type
    - 函数 render_to_response
- ListView
  - MultipleObjectTemplateResponseMixin
    - TemplateResponseMixin
    - 获取模板名字
      - 首先根据template_name 
      - 如果没找到
        - 自己根据 应用的名字，关联模型的名字， _list.html 去查找
        - App/book_list.html
  - BaseListView
    - MultipleObjectMixin
      - ContextMixin
      - get_queryset
      - model
    - View
    - 默认实现了get，渲染成了response
- DetailView
  - SingleObjectTemplateResponseMixin
    - TemplateResponseMixin
    - 重写了获取模板名字的方法
  - BaseDetailView
    - View
    - SingleObjectMixin



