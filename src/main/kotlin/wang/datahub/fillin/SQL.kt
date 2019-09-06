package wang.datahub.fillin


import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.jooq.*
import org.jooq.impl.DSL
import java.io.File
import java.sql.DriverManager
import java.util.*


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
    var formatter:String? = null
    infix fun 命名(name: String){
        this.qid = name
    }

    infix fun 使用(dbc:DBC):Unit{
        System.out.println(dbc.driver)
        var conn = DriverManager.getConnection(dbc.url,dbc.username,dbc.password)
        dslContext = DSL.using(conn, SQLDialect.MYSQL);
    }

    infix fun 使用(ds:DS):Unit{
//        com.google.gson.internal.LinkedTreeMap
        var dsName = ds.dsName
        System.out.println(dsName)
        var url = System.getProperty("resturl")

//        var url = "http://localhost:8080/datasource/$dsName/json";
        url = url+"/$dsName/json"
        System.out.println("restful url  = "+url)
        var r = okhttp3.Request.Builder().url(url).get().build();
        var resp = OkHttpClient().newCall(r).execute()
        var str = resp.body()?.string();
        //System.out.println(str)
        var s = Gson().fromJson(str,Map::class.java)
        var p = s["data"]
        var p1 = p as Map<String, Any>
        var pros = p1["properties"]
        var fs = pros as Collection<Map<String,String>>
        var durl: String? = fs.first { it-> it.get("name").equals("url") }.get("value")
        var username: String? = fs.first { it-> it.get("name").equals("username") }.get("value")
        var password: String? = fs.first { it-> it.get("name").equals("password") }.get("value")
        System.out.println(durl)
        System.out.println(username)
        System.out.println(password)

        var conn = DriverManager.getConnection(durl,username,password)
        dslContext = DSL.using(conn, SQLDialect.MYSQL);
        System.out.println(pros)
        //System.out.println(s)
//        okhttp3.Request request =  okhttp3.Request.Builder().url(url).get().build();
//        OkHttpClient okHttpClient = new OkHttpClient();
//        final Call call = okHttpClient.newCall(request);

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
        formatter = formater
        return when(formater.toLowerCase()){
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

    infix fun 发布(path:String):String{
        //System.getProperty()
//        var dp = System.getProperty("datapath")
//        System.out.println("datapath = "+dp)

        val file = SQL.publishedFile(path)
        file.writeText(this.转(this.formatter+""))
        return path
    }

    companion object {
        fun publishedFile(path:String):File{
            var dp = System.getProperty("datapath")
            System.out.println("datapath = "+dp)
            return File(dp,path)
        }
    }
}

data class DBC(var username:String?="dafei1288",var password:String?="dafei1288",var url:String?="jdbc:mysql://datahub.wang:3306/1",var driver:String?="com.mysql.jdbc.Driver")
data class DS(var dsName:String?="gquerydemo")
fun 查询(body: SQL.() -> Unit) = SQL().apply(body)
fun 数据库(body: DBC.() -> Unit) = DBC().apply(body)
fun 数据源(body: DS.() -> Unit) = DS().apply(body)

infix fun Int.到(i:Int):Pair<Int,Int>{
    return Pair(this,i)
}

enum class ChartType {
    饼图, 线图, 表
}