package wang.datahub.fillin


import org.jooq.*
import org.jooq.impl.DSL
import java.sql.DriverManager



class SQL {

    var dslContext: DSLContext?=null;
    var 本次 = this
    var tableNames:List<String>? = null
    var colNames:List<String>? = null
    var condition:String? = null
    var orderCols:List<String>? = null
    var groupCols:List<String>? = null
    var offset:Int? = null;
    var limit: Pair<Int, Int>?=null
    var qid:String? = null

    infix fun 命名(name: String){
        this.qid = name
    }

    infix fun 使用(dbc:DBC):Unit{
        System.out.println(dbc.driver)
        var conn = DriverManager.getConnection(dbc.url,dbc.username,dbc.password)
        dslContext = DSL.using(conn, SQLDialect.MYSQL);
    }
    infix fun 表( tableNames: List<String>):Unit{
        this.tableNames = tableNames

    }
    infix fun 字段(colNames:List<String>):Unit{
        this.colNames = colNames
    }
    infix fun 条件(condition:String):Unit{
        this.condition = condition
    }
    infix fun 排序(orderCols:List<String>):Unit{
        this.orderCols = orderCols
    }

    infix fun 聚合(groupCols:List<String>):Unit{
        this.groupCols = groupCols
    }

    infix fun 截取(offset:Int):Unit{
        this.offset = offset
    }

    infix fun 截取(limit: Pair<Int, Int>):Unit{
        this.limit = limit
    }

    infix fun 转(formater:String):String{
        return when(formater){
            "html"->{
                fetch().formatHTML()
            }
            "csv"->{
                fetch().formatCSV()
            }
            "json"->{
                fetch().formatJSON()
            }
            "xml"->{
                fetch().formatXML()
            }
            else ->{
                fetch().formatCSV()
            }
        }
    }

    infix fun 画(formater:ChartType):String{
        System.out.println("图类型： "+formater)
        var res =   when(formater){

            ChartType.表 ->{
                this.fetchHtmlTable()+"";
            }
            ChartType.线图->{
                Chart().lineChart(this.fetch()).toString()
            }
            ChartType.饼图->{
                Chart().pieChart(this.fetch()).toString()
            }
            else -> {
                this.fetchHtmlTable()+"";
            }
        }
        System.out.println("res ： "+res)
        return  res;
    }


    fun 组(vararg item:String):List<String>{
        return item.asList()
    }

    fun  fetch():Result<Record>{

        var temp = dslContext
            ?.select(colNames?.map { DSL.field(it) })
            ?.from(tableNames?.map { DSL.table(it) })

        if(condition!=null) {
            temp?.where(condition)
        }

        if(groupCols!=null) {
            temp?.groupBy(groupCols?.map { DSL.field(it) })
        }
        if(orderCols!=null) {
            temp?.orderBy(orderCols?.map { DSL.field(it) })
        }
        if(this.offset!=null){
            temp?.limit(this.offset!!)
        }

        if(this.limit!=null){
            temp?.limit(this.limit?.first!!,this.limit?.second!!)
        }

        return   temp?.fetch()!!
    }


    fun fetchHtmlTable():String?{
        return fetch()?.formatHTML()
    }


    fun printSQL():Unit{

        println( fetchSQL())
    }

    fun fetchSQL():String?{
        var temp = dslContext
            ?.select(colNames?.map { DSL.field(it) })
            ?.from(tableNames?.map { DSL.table(it) })

        if(condition!=null) {
            temp?.where(condition)
        }

        if(groupCols!=null) {
            temp?.groupBy(groupCols?.map { DSL.field(it) })
        }
        if(orderCols!=null) {
            temp?.orderBy(orderCols?.map { DSL.field(it) })
        }
        if(this.offset!=null){
            temp?.limit(this.offset!!)
        }

        if(this.limit!=null){
            temp?.limit(this.limit?.first!!,this.limit?.second!!)
        }


        return temp?.sql
    }


}

data class DBC(var username:String?="dafei1288",var password:String?="dafei1288",var url:String?="jdbc:mysql://datahub.wang:3306/gtp-demo-07?useSSL=false",var driver:String?="com.mysql.jdbc.Driver")

fun 查询(body: SQL.() -> Unit) = SQL().apply(body)
fun 数据库(body: DBC.() -> Unit) = DBC().apply(body)

infix fun Int.到(i:Int):Pair<Int,Int>{
    return Pair(this,i)
}

enum class ChartType {
    饼图, 线图, 表
}