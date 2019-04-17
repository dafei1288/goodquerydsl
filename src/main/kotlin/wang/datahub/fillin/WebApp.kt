package wang.datahub.fillin

import io.javalin.Javalin
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import javax.script.ScriptEngineManager

fun main(args: Array<String>) {
    var app = Javalin.create()
        .enableStaticFiles("/static")
        .start(7777)

    app.post("/getdata") { ctx ->

        ScriptEngineManager().engineFactories.forEach{it->System.out.println(it.engineName)}


//        System.out.println(ctx.body())
        val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine
        engine.eval("import wang.datahub.fillin.*")
        engine.eval("import wang.datahub.fillin.ChartType.*")
//        engine.eval("import wang.datahub.fillin.查询")
//        engine.eval("import wang.datahub.fillin.到")
        val o  = engine.eval(ctx.body())
        ctx.html(o.toString())
//        if(o is SQL)
//            ctx.html(o.toString())
//        else
//            ctx.html("error")

    }

    app.get("/published/:fid"){ctx->
        ctx.contentType("application/octet-stream")
        var fid = ctx.pathParam("fid")
        System.out.println("fid = "+fid)
        //ctx.resultStream(SQL.publishedFile(fid).inputStream())
        ctx.result(SQL.publishedFile(fid).inputStream())
    }

}