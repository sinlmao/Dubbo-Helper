# Sinlmao Commons Dubbo Helper

> 一个简单地、轻量级的 Java Dubbo 集成、封装的操作类库。
>
>`update：2019-08-16` `ver：1.0.1`

----------

这是**一个简单地并且轻量级的Java用于Dubbo的操作类库**。最初是因为需要符合个人使用习惯而集成并封装的小型类库，后来在公司项目和内部有一些使用。最初通过IDE打包成jar的方式供项目使用，但是由于基本所有项目都使用Maven构建，使用本地引用jar包的方式方便也不符合Maven的推荐，需要提交至Maven仓库并开源至GitHub。只是一些简单便利地封装，*不算什么技术*。

目前发布的功能（或提供的操作功能）如下：

>  - 支持注册服务；
>  - 支持动态引用服务；

通过Maven引入，直接在POM中设置如下：

    <dependency>
        <groupId>cn.sinlmao.commons</groupId>
        <artifactId>dubbo-helper</artifactId>
        <version>1.0.1</version>
    </dependency>

文档将在后续尽快完善。