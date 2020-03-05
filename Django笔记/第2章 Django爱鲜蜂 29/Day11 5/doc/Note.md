# Day11



### 部署

- django中自带开发者服务器
  - runserver
    - 路由处理功能，动态资源处理
    - 如果是debug的话，静态资源处理功能
  - 功能健壮，性能是比较低的，仅适用于开发

- 部署不会使用单一服务器
  - Apache
  - Nginx
    - HTTP服务器
      - 处理静态资源
    - 反向代理
      - uWSGI HTTP服务器
      - gunicorn HTTP服务器
    - 邮件服务器
    - 流媒体服务器



### 部署云服务器

- 从零开始做的
- 安装云服务器系统
  - Ubuntu 16.04
- 安装一套开发环境
  - Python
    - 2.x
    - 3.x
  - pip
    - 注意版本兼容
  - virtualenv
    - 版本不兼容
    - workon_home
    - source  xxx
  - mysql
    - apt 直接安装
  - redis
    - 源码安装 
    - make & make test
    - utils/install_server.sh
  - nginx
    - 添加钥匙
    - 添加源
    - update， install
  - 准备进行部署
    - 安装项目所需依赖  
      - pip install -r xxx.txt
    - 修改配置文件到指定路径
    - 从静态文件开始部署
    - 动态资源
      - 处理好数据
      - 创建库，创建表
      - 插入数据
  - 坑点
    - 邮件发送
      - 25端口是非安全端口，阿里不允许使用
      - 使用安全SSL端口 465



### 洋垃圾

- cpu  e5-26xx  8核16线程 300-600
- 搞一个主板
- ecc 专用内存条 8g 80一条
- 电源
- 显卡
- 硬盘

