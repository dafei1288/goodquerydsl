package wang.datahub.fillin

import com.github.abel533.echarts.Option
import com.github.abel533.echarts.axis.CategoryAxis
import com.github.abel533.echarts.axis.ValueAxis
import com.github.abel533.echarts.code.Magic
import com.github.abel533.echarts.code.Tool
import com.github.abel533.echarts.feature.MagicType
import com.github.abel533.echarts.series.Line
import org.jooq.Record
import org.jooq.Result
import com.github.abel533.echarts.json.GsonOption
import com.github.abel533.echarts.series.Pie


class Chart {
    fun lineChart(datas:Result<Record>):Option{
        val option = GsonOption()
        //val option = Option()
        //option.legend("高度(km)与气温(°C)变化关系")
        option.toolbox().show(true)
            .feature(Tool.mark, Tool.dataView, MagicType(Magic.line, Magic.bar), Tool.restore, Tool.saveAsImage)

        option.calculable(true)
//        option.tooltip().trigger(Trigger.axis).formatter("Temperature : <br/>{b}km : {c}°C")

        val valueAxis = ValueAxis()
//        valueAxis.axisLabel().formatter("{value} °C")
        option.xAxis(valueAxis)

        val categoryAxis = CategoryAxis()
        categoryAxis.axisLine().onZero(false)
//        categoryAxis.axisLabel().formatter("{value} km")
        categoryAxis.boundaryGap(false)
        categoryAxis.data().addAll(datas.getValues(0))
        option.yAxis(categoryAxis)

        val line = Line()
        line.data().addAll(datas.getValues(1))
        //line.data(datas.getValues(1).toTypedArray())
           // .itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)")
        option.series(line)
        return option
    }


    fun pieChart(datas:Result<Record>):Option{
        val option = GsonOption()
        //val option = Option()
        //option.legend("高度(km)与气温(°C)变化关系")
        option.toolbox().show(true)
            .feature(Tool.mark, Tool.dataView, MagicType(Magic.line, Magic.bar), Tool.restore, Tool.saveAsImage)

        option.calculable(true)
//        option.tooltip().trigger(Trigger.axis).formatter("Temperature : <br/>{b}km : {c}°C")

        val valueAxis = ValueAxis()
//        valueAxis.axisLabel().formatter("{value} °C")
        option.xAxis(valueAxis)

        val categoryAxis = CategoryAxis()
        categoryAxis.axisLine().onZero(false)
//        categoryAxis.axisLabel().formatter("{value} km")
        categoryAxis.boundaryGap(false)
        categoryAxis.data().addAll(datas.getValues(0))
        option.yAxis(categoryAxis)

        val pie = Pie()
        pie.data().addAll(datas.getValues(1))
        //line.data(datas.getValues(1).toTypedArray())
        // .itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)")
        option.series(pie)
        return option
    }
}