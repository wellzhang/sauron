(function (window, echart, jquery) {
    var root = window;
    var echarts = echart;
    var $ = jquery;

    var mychart = new Object();

    root.mychart = mychart;
    mychart.option = {
        title: {},
        color: ["#3fb1e3"],
        grid: {
            height: 250,
            show: true,
            backgroundColor: '#ffffff'
        },
        tooltip: {
            show: true
        },
        toolbox: {
            show: true,
            feature: {
                dataView: {readOnly: false},
                restore: {},
                saveAsImage: {},
                magicType: {type: ['line', 'bar']}, //视图样式切换，折线/柱形
                dataZoom: {
                    show: true
                }
            }
        },
        legend: {
            data: ['time']
        },
        xAxis: {
            // data: gramData.x
        },
        yAxis: {
            type: 'value'
        },
        series: []
    };

    mychart.new = function (domid, url, param, dataFormatter) {
        var chart = echarts.init(document.getElementById(domid));
        $.getJSON(url, param, function (data) {
            var options = $.extend({}, mychart.option);
            if ($.isFunction(dataFormatter)) {
                var lineData = dataFormatter(data);
                options.xAxis.data = lineData.x
                options.series = lineData.y;
            }
            console.info(options);
            chart.setOption(options);
        });
        return chart;
    };


}).call(this, window, echarts, jQuery);