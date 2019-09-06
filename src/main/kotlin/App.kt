
import wang.datahub.fillin.ChartType.*
import wang.datahub.fillin.到
import wang.datahub.fillin.数据库
import wang.datahub.fillin.数据源
import wang.datahub.fillin.查询


object App {
    @JvmStatic
    fun main(args: Array<String>){

       // println(1 到 10)

        var t = 查询 {
            本次 使用
                    数据源{ dsName = "gquerydemo"

            }
            本次  表 组("gtp_user","gtp_user_role")
            本次  字段 组("gtp_user.name","gtp_user_role.role_id")

            本次  条件 "gtp_user.name = 'admin'"
            本次  聚合 组("gtp_user.name","gtp_user_role.role_id")
            本次  排序 组("gtp_user_role.role_id")
            本次  截取 (1 到 11)
            本次  转 "json"

        } 画 线图


//        System.out.println(t.formatChart())
//        System.out.println(t.formatJSON())
    }


}