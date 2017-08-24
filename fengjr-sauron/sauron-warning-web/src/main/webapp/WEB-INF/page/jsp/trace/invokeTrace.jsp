<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Liuyb
  Date: 2016/1/11
  Time: 19:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link href="/resources/css/AdminLTE.css" rel="stylesheet" type="text/css">
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="/resources/css/bootstrap-theme.min.css">
    <%--<link rel="stylesheet" href="/resources/css/bootstrap-datetimepicker.min.css">--%>


    <!-- 可选的Bootstrap主题文件（一般不用引入） -->
    <script type="text/javascript" src="/resources/js/jquery-2.2.0.min.js"></script>
    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>

    <script src="/resources/js/app.js"></script>

    <script type="text/javascript" src="https://code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">

    <script type="text/javascript" src="/resources/js/tabulator.js"></script>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/resources/css/bootstrap-table.min.css">

    <!-- Latest compiled and minified JavaScript -->
    <script src="/resources/js/bootstrap-table.js"></script>

    <!-- Latest compiled and minified Locales -->
    <script src="/resources/js/locale/bootstrap-table-zh-CN.js"></script>
    <%--<script src="/resources/js/bootstrap-datetimepicker.js"/>--%>
</head>
<body class="skin-blue">
<%@include file="../common/head.jsp" %>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <%@include file="../common/leftMenu.jsp" %>
    <aside class="right-side">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                调用过程追踪
                <small>列表</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 调用过程追踪</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="row">
                <form class="form-inline" id="searchTID">
                    <div class="input-group">

                        <div class="input-group-addon">应用名</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" id="_appName"
                               name="_appName"/>

                        <div class="input-group-addon">主机名</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" id="_hostName"
                               name="_hostName"/>

                        <div class="input-group-addon">开始时间</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" id="_startTime"
                               name="_startTime"/>

                        <div class="input-group-addon">结束时间</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" id="_endTime"
                               name="_endTime"/>
                    </div>
                    <input id="queryTIDBtn" type="button" class="btn btn-primary" value="查询">
                </form>
                <table id="tracesTable" data-toggle="table" data-height="300" data-pagination="true">
                    <thead>
                    <tr>
                        <th data-field="AppName">应用名</th>
                        <th data-field="hostName">主机名</th>
                        <th data-field="Traceid">追踪ID</th>
                        <th data-field="logtime">记录时间</th>
                    </tr>
                    </thead>
                </table>

            </div>

            <div class="row">
                <form action="/common/getInfoByTraceid" method="POST" class="form-inline" id="traceTID">
                    <div class="input-group">

                    <div class="input-group-addon">追踪ID</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" id="traceId"
                               name="traceId"/>
                    </div>
                    <input id="queryBtn" type="button" class="btn btn-primary" value="查询">
                </form>
            </div>
            <div class="row" id="tracing-table" style="height: 1000px"></div>
        </section>
    </aside>


    <script>
        $('#tracesTable').on('click-row.bs.table', function (row, $element) {
//            alert(row);
            $('#traceId').val($element.Traceid);
            $('#queryBtn').click();
        });
        //        $('#_startTime').datetimepicker({
        //            format: "yyyy-mm-dd hh:mm:ss", //选择日期后，文本框显示的日期格式
        //            language: 'zh-CN', //汉化
        //            pickerPosition:'bottom-left',
        //            beforeShow: function (input, inst) {
        //                $.datepicker._pos = $.datepicker._findPos(input);
        //                $.datepicker._pos[1] += input.offsetHeight + document.body.scrollTop; // 加入body的scrollTop修正jquery在ie7中定位不準的問題
        //                inst.dpDiv.css('font-size' ,'70%');
        //            }
        //        });
        //        $('#_endTime').datetimepicker({
        //            format: "yyyy-mm-dd hh:mm:ss", //选择日期后，文本框显示的日期格式
        //            language: 'zh-CN', //汉化
        //            pickerPosition:'bottom-left',
        //            beforeShow: function (input, inst) {
        //                $.datepicker._pos = $.datepicker._findPos(input);
        //                $.datepicker._pos[1] += input.offsetHeight + document.body.scrollTop; // 加入body的scrollTop修正jquery在ie7中定位不準的問題
        //                inst.dpDiv.css('font-size' ,'70%');
        //            }
        //        });
        $('#queryTIDBtn').click(function () {
            var appName = $('#_appName').val();
            var hostName = $('#_hostName').val();
            var startTime = $('#_startTime').val();
            var endTime = $('#_endTime').val();
            $.getJSON("/common/getTracers", {
                appName: appName,
                hostName: hostName,
                startTime: startTime,
                endTime: endTime
            }, function (data) {
                if (data.code == 0) {

                    $('#tracesTable').bootstrapTable('load', data.attach);
                } else {
                    alert(data.msg + ':' + data.exceptionMsg);
                }
            });
        });
        function traverseTracing(obj, tableData, indx) {
            for (var k in obj) {
                var v = obj[k];
                if (typeof(v) == "object") {
                    if (k == 'document') {
                        v.id = indx;
                        if (v.InvokeResult == "0")
                            v.InvokeResult = true;
                        else {
                            v.InvokeResult = false;
                        }
                        tableData.push(v);
                    } else {
                        traverseTracing(v, tableData, ++indx);
                    }
                } else {

                }
            }
            return tableData
        }
        $('#queryBtn').click(function () {
            var traceId = $('#traceId').val();
            $.getJSON("/common/getInfoByTraceid", {traceId: traceId}, function (data) {
                if (data.code == 0) {
                    var tableData = new Array();
                    traverseTracing(data.attach, tableData, 0);
                    $("#tracing-table").tabulator("setData", tableData);

                } else {
                    alert(data.msg + ':' + data.exceptionMsg);
                }
            });
        });
        $("#tracing-table").tabulator({
            columns: [
                {title: "SPANID", field: "Spanid", sortable: true, sorter: "string", width: 100, editable: false},
                {title: "应用名称", field: "AppName", sortable: true, sorter: "string"},
                {title: "方法名称", field: "methodName", sortable: true, sorter: "string", align: "center"},
                {title: "主机名称", field: "hostName", sorter: "string", sortable: false, width: 100},
                {
                    title: "耗时",
                    field: "duration",
                    sortable: true,
                    sorter: "number",
                    align: "left",
                    formatter: "progress"
                },
                {title: "耗时(ms)", field: "duration", sortable: true, sorter: "number", align: "left", width: 50,},
                {title: "发生时间", field: "logtime", sortable: true, sorter: "date", align: "center"},
                {
                    title: "调用结果",
                    field: "InvokeResult",
                    sortable: true,
                    sorter: "boolean",
                    align: "center",
                    formatter: "tickCross",
                    width: 30,
                },
                {title: "异常信息", field: "Exception", sortable: false, sorter: "string", align: "left"},
                {
                    title: "输入参数",
                    field: "Params",
                    sortable: false,
                    align: "left",
                    formatter: function (value, data, cell, row, options, formatterParams) {
                        var cellText = JSON.stringify(value);
                        return "<div>" + cellText + "</div>";
                    },
                }
            ],
            fitColumns: true,
            height: 500,
        });
        $("#tracing-table").tabulator("sort", "Spanid", "asc");
    </script>
</body>
</html>
