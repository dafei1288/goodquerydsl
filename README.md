
# 实例

从技术上讲，这也是一门DSL，只是用中文来做了关键字。下面我们来看一个实例：


```
         查询 {
            本次 使用 数据库{
//                username="1"
//                password="1"
//                url="jdbc:mysql://localhost:3306/1"
//                driver="com.mysql.jdbc.Driver"
            }
            本次  表 组("gtp_user","gtp_user_role")
            本次  字段 组("gtp_user.name","gtp_user_role.role_id")

            本次  条件 "gtp_user.name = 'admin'"
            本次  聚合 组("gtp_user.name","gtp_user_role.role_id")
            本次  排序 组("gtp_user_role.role_id")
            本次  截取 (1 到 11)

        } 画 线图
```



下面我们来逐步解析这个案例，首先构建一个`查询`，这其实是在构建一个查询对象，在这个查询对象的作用域内，`本次`代表当前的查询。



# 说明



首先需要指定使用的数据库，`本次 使用  数据库 {}` 里面可以使用你自己的数据源，去掉注释，修改成你自己的数据源即可。目前只设置了基础属性。



现在支持的查询谓词： `表`、`字段`、`条件`、`聚合`、`排序`、`截取`

支持的动作谓词： `画`（`线图`、`饼图`、`表`）、`转` （`CSV`、`HTML`、`JSON`）

其他： `组`、`到`



`表`：用来设置查询的表，输入 表名，必填属性，需要使用双引号括起来。可与组联用。

`字段`：用来设置查询字段，输入 表名.字段名 ，必填属性，需要使用双引号括起来。可与组联用。

`条件`：用来设置查询条件表达式，暂时只能将所有条件一起输入， 可选属性，需要使用双引号括起来。

`聚合`：用来设置查询的聚合字段，输入 表名.字段名， 可选属性，需要使用双引号括起来。可与组联用。

`排序`：用来设置查询的排序字段，输入 表名.字段名， 可选属性，需要使用双引号括起来。可与组联用。

`截取` ：用来设置查询的结果集数量，输入 数字，例如：到 10，就是取前10条，或是输入 （数字 到 数字） ， 例如： （5 到 10），获取从第五条开始的10条数据。


`画`：用来将结果输出，目前支持，线图、饼图和表

`转`：用来讲结果格式化输出，目前支持格式CSV、HTML、JSON



# 实现



这个DSL整体的执行流程，如下图​：

![架构图](img/arch.png)

首先DSL，会提交给解析程序，解析成携带数据的节点，然后根据节点进行逻辑计划及​优化程序，然后交给物理引擎取执行。


目前解析器，使用了kotlin作为语法解析器，JOOQ充当物理计划​执行器。