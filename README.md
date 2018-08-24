# Yplat
本平台基于servlet3.0和spring4完成api请求和响应数据的加解密、请求的防重、会话的创建和检测等功能，并能生成api文档。

本平台的构建意图是为了让开发人员专注于业务逻辑的处理，只需定义好api请求参数的model和返回参数的model即可，其余的事情交给平台去处理，以此来提

高开发效率以及统一api请求和响应数据的格式，使开发更加的规范。

本平台的实现思路如下：

1、工程启动时初始化servlet以及spring扫描和装配各种定义的bean到spring的ApplicationContext中；

2、在spring工作完成后从已经完成初始化的ApplicationContext中遍历并获取被@ApiMapping注解标注的方法，然后把@ApiMapping以及对应的方法储存在m
ap中(以@ApiMapping的value值为k值，以@ApiMapping和方法组成的model为v值);

3、在找到被@ApiMapping注解标注的方法后，继续获取方法入参和出参的model,把这些model信息也存放到另外一个map中，便于生成api文档使用。注：此步骤在生产中可以省略，以减少项目启动后对内存的占用。关闭的方式在配置文件中可配置；

4、完成项目的启动。

在本项目中只初始化了一个servlet，故对外的统一访问地址为 http://localhost:8080/Yplat/api

请求参数和响应规定必须为json的方式 

请求json格式：{"head":{"mark":"nbEi2mnOwZbikGwg","bussCode":"displayUserInfo"},"body":{"userName":"11","phoneNo":"11111"}}

响应json格式：{"body":{"userName":"11","phoneNo":"11111"},"head":{"retCode":"0000","retMsg":"交易成功","retSign":"xm8Iq0RhwI_XMnNM"}}

json格式都包括head和body两部分，

其中请求的json的head中mark是需要防重时使用，bussCode是业务代号，此代号对应服务端的某个处理方法；body中的数据为开发人员自己定义的请求参数。响应的json中的head中retCode：是状态码，retMsg：返回的信息（有成功也有错误信息），retSign是服务的生成的16位随机码，是需要防重时使用；body中的数据为开发人员自己定义的响应参数。
请求工作的流程如下：

获取请求参数中的bussCode，并根据bussCode的值在map中找到对应的执行方法，在把请求json的body中的参数赋值到成执行方法入参model中，然后映射的方法的处理逻辑由开发人员完成，执行完方法后，获取到返回的model数据，并生成响应。现在可以根据@ApiMapping中outPutType的参数指定返回到前端的是json还是html，目前html是有freemarker模板引擎实现的。

此外为了提高本系统的吞吐量和并发量，采用了servlet3的异步并配合线程池来处理请求。

项目结构目录

Yplat.annotation  ----- 项目使用的注解

Yplat.apidoc      ----- api文档处理

Yplat.cache       ----- 项目缓存

Yplat.common      ----- 公共的类

Yplat.configManager --- 配置文件加载

Yplat.core        ----- 项目的核心的几个处理类

Yplat.exception   ----- 异常封装的类

Yplat.freemarker  ----- freemarker自定义标签

Yplat.model       ----- 请求和响应等model封装

Yplat.session     ----- 会话的实现，现主要分redis和httpSession两种方式储存会话，前者适用于线上多个工程部署实现会话共享

Yplat.util        ----- 封装一些工具类

最后，笔者深知此平台在功能的代码实现以及扩展方面还有诸多不足之处，希望大家能一起参与并补充，使之变得更加完善。