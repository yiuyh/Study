# Day12





### 视图函数

- FBV
  - function base view
- CBV
  - class base view



### 类视图

- CBV
- 继承自View
- 注册的时候使用的as_view()
- 入口
  - 不能使用请求方法的名字作为参数的名字
  - 只能接受已经存在的属性对应的参数
  - 定义了一个view
    - 创建了一个类视图对象
    - 保留，拷贝传递进来的属性和参数
    - 调用dispatch方法
      - 分发
      - 如果请求方法在我们的允许的列表中
        - 从自己这个对象中获取请求方法名字小写对应的属性，如果没有找到，会给一个默认http_method_not_allowded
      - 如果请求方法不在我们允许的列表中，直接就是http_method_not_allowed
      - 之后将参数传递，调用函数
- 默认实现了options
  - 获取接口信息，可以获取接口都允许什么请求
- 简化版流程
  - as_view
  - dispath
  - 调用实现请求方法对应的函数名



### homework

- 小组练习负载均衡
- 编写RESTApi接口
  - 编写书籍的增删改查
  - 前端也要实现



