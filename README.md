# 简介

##  dbrest-spring-boot-starter

> 极简的crud接口开发、sql接口开发框架，与spring boot无缝集成。
>
> 思路来源于mybatis，在controller基于声明式注解以及sql，实现api接口开发。
>
> @DbCrud：一个注解实现单表crud接口开发
>
> @DbQuery、@DbUpdate、@DbQueryPage等：基于注解实现动态sql接口开发
>
> 如果您觉的好用的话，麻烦给个star支持，万分感谢！！

# 特点

- 通过注解声明式，实现controller接口开发
- 与spring boot无缝集成
- 支持mysql、oracle
- 安全性，防止sql注入
- 轻量级、可与mybatisPlus、hibernate框架等共用

# 使用方法

## 快速开始

### 依赖包引入

在项目的pom.xml的dependencies中加入以下内容:

```
<dependency>
    <groupId>xyz.zeozheng</groupId>
    <artifactId>dbrest-spring-boot-starter</artifactId>
    <version>0.98</version>
</dependency>
```

### 数据库配置（可选，已集成mybatis、jpa等数据库dao框架可跳过，会自动识别数据源）

添加db.setting配置文件（本项目使用hutool db模块操作数据库），样例如下：

```
## 基本配置信息
# JDBC URL，根据不同的数据库，使用相应的JDBC连接字符串
url = jdbc:mysql://<host>:<port>/<database_name>
# 用户名，此处也可以使用 user 代替
username = 用户名
# 密码，此处也可以使用 pass 代替
password = 密码
# JDBC驱动名，可选（Hutool会自动识别）
driver = com.mysql.jdbc.Driver
## 可选配置
# 是否在日志中显示执行的SQL
showSql = true
# 是否格式化显示的SQL
formatSql = false
# 是否显示SQL参数
showParams = true
# 打印SQL的日志等级，默认debug
sqlLevel = debug
```

- 如需集成数据库连接池等其他配置，参考[Hutool](https://www.hutool.cn/docs/#/db/%E6%95%B0%E6%8D%AE%E6%BA%90%E9%85%8D%E7%BD%AEdb.setting%E6%A0%B7%E4%BE%8B)

### 开始使用

- spring boot启动类添加 @EnableDbRest

- Controller层添加以下接口( book表名需修改为自己的表名 )

  ```java
  @DbRestController //@DbRestController注解
  public interface BookController {
  
      /**
       * 生成crud接口：/book/save、/book/findList、/book/findPage、
       *    /book/getById、/book/delete
       */
      @DbCrud(tableName = "book", keyField = "id",idtype = IdType.AUTO)
      public Object book();
  
      /**
       * 根据name查询，请求路径 /book/findByName
       */
      @RequestMapping(value = "/book/findByName", method = RequestMethod.GET)
      @DbQuery(value = "select * from book where 1=1 {name? and name = [name]}")
      public Object findByName(@RequestParam(value = "name", required = false) String name);
  }
  ```

- 试试请求 接口：
  - /book/getById?id=xx
  - /book/findList、/book/findPage
  - /book/findByName?name=xxx

## 详细用法

### 注解说明

|       注解        |              目标对象               |                             作用                             |
| :---------------: | :---------------------------------: | :----------------------------------------------------------: |
|   @EnableDbRest   |         spring boot 启动类          |                启用dbrest-spring-boot-starter                |
| @DbRestController |          controller层接口           |     继承于Spring的@Controller注解，用于标记dbrest类接口      |
|      @DbCrud      | 添加@DbRestController接口的对象方法 |          通过注解声明，自动生成对应表的增删改查方法          |
|     @DbQuery      |              同@DbCrud              | 列表查询：注解value为sql语句，可通过@RequestParam，@RequestBody等传入sql参数变量，同时支持条件判断 |
|  @DbQuerySingle   |              同@DbCrud              |                   单条查询：其他同@DbQuery                   |
|   @DbQueryPage    |              同@DbCrud              | 分页查询：参数添加Page对象或Map，传入page、size分页参数，实现分页，其他同@DbQuery |
|     @DbUpdate     |              同@DbCrud              |            更新接口：用法同@DbQuery，返回更新条数            |
|     @DbInsert     |              同@DbCrud              | 插入接口：value为sql，如id为自增，可设置IdType = IdType.AUTO返回自增的key |

### @DbCrud注解接口

​	通过声明@DbCrud注解，dbrest会自动生成 save、getById、findList、findPage、delete接口；接口请求路径为：/{tableName}/{接口名}，如 /book/save。

​	注1：@DbCrud的tableName取数据库表实际大小写，接口请求路径tableName默认小写

​    注2：接口类支持@RequestMapping("xxx")，接口路径前缀需添加相应路径（@DbCrud方法上不支持@RequestMapping注解）

#### save

- 请求方式：get、普通post、post (application/json)

 *  请求参数：对应数据库字段名（区分大小写）
 *  说明：传入主键时为update，无主键时为insert，为空的字段不修改和新增

#### getById

- 请求方式：get、普通post

 *  请求参数：{ "id": "xxxxxx" }

#### findList

- 请求方式：get、普通post、post (application/json)

 *  请求参数： 
     *     { 字段名1_条件："xx", 字段名2_条件："xx"}，如： { "id_eq": 3, "name_like": "aaa" }
     *     条件支持：_eq、_neq、_like、_in、_gt、_gte、_lt、_lte、_bet（同between）
     *     特殊字段：orderby, 可传入 { orderby: '字段名 desc/asc'} 进行排序

#### findPage

 *     用法同findList接口，可传入 page、size字段分页，如 { page:1, size:10}

#### delete

- 请求方式：get、普通post、delete

 *  请求参数：{ "id": "xxxxxx" }

### Sql特殊语法

- Sql中获取参数：
    ```
    使用[key]（同mybatis #{key}），如：@DbQuery(value = "Select * from [key]")
    使用 $[key]  （同mybatis ${key}），如：@DbQuery(value = "Select * from $[key]")   
    如无权限验证机制，谨慎使用 $[key]，存在sql注入风险
    可获取map、entity、@RequestParam、@RequestBody字段
    ```

- Sql条件判断

  - 语法如：select * from  book where 1 = 1 { name? name = [name] }，大括号包含为条件代码块，当问号前面成立时，则拼接问号后面的sql

  - 参照mybatis写法：

    ```xml
    <if test="name != null and name != ''">
        AND name = ''
    </if>
    
    dbrest写法：
    { name != null and name != ''? AND name = ''}
    可简写为：{ name? AND name = ''}
    ```

  - 同时和mybatis类似，test语句及获取参数表达式支持ognl语法：

    - 如执行方法

      ```
      Select * from book where 1=1 { @zzh.dbrest.demo.dbrestdemo.DbrestDemoApplication@check(name) ? and name = [@zzh.dbrest.demo.dbrestdemo.DbrestDemoApplication@getName(name)]
      }
      ```

### 使用demo

​			[dbrest-demo-mysql](https://gitee.com/zzhtop/dbrest-demo-mysql/tree/master)

## 推荐用法

- dbrest + mybatis plus
  - 使用dbrest生成crud，简易查询，统计等无业务逻辑的api接口
  - mybatis plus处理service层，及复杂业务数据库逻辑处理
- 简易项目可直接使用 dbrest + Hutool db模块

## 常见问题

- @DbCrud数据库表名区分大小写

# 原理篇

  完善中。。。

# 感谢

- Mybatis：思路和源码参考

- Hutool：数据库查询基于Hutool Db模块
- Ognl：Sql条件及参数使用Ognl
- Spring boot：基于Spring 框架体系