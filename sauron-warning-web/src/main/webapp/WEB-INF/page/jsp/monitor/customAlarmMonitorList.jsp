<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>


    <link rel="stylesheet" href="/resources/css/bootstrap.min.css">


    <!-- Ionicons -->
    <link href="/resources/css/ionicons.min.css" rel="stylesheet" />
    <link href="/resources/css/iCheck/all.css" rel="stylesheet"/>
    <link rel="stylesheet" href="/resources/css/warm.css">
    <!-- 可选的Bootstrap主题文件（一般不用引入） -->
    <link rel="stylesheet" href="/resources/css/bootstrap-theme.min.css">
    <link href="/resources/css/font-awesome.min.css" rel="stylesheet" />


    <link href="/resources/css/timepicker/bootstrap-timepicker.min.css" rel="stylesheet"/>
    <link href="/resources/css/timepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css" />


    <link href="/resources/css/AdminLTE.css" rel="stylesheet" type="text/css">
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/resources/js/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>
    <%--<link href="/resources/css/dashboard.css" rel="stylesheet">--%>
    <script src="/resources/js/app.js"></script>
    <script src="/resources/js/timepicker/bootstrap-timepicker.min.js"></script>

    <script src="/resources/js/timepicker/daterangepicker.js"></script>


</head>


<body>













<body class="skin-blue">
<%@include file="../common/head.jsp" %>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <%@include file="../common/leftMenu.jsp" %>
    <aside class="right-side">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                <small>自定义KEY监控</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i>自定义KEY监控</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">

            <div class="box box-primary">
                <form method="post" action="/monitor/getMetricsOriDataCodebulkAlarm">
                    <p>
                    <div class="row">
                        <div class="col-xs-3">
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <i class="fa fa-laptop"> 应用名：</i>
                                </div>
                                <form:select path="appNameList" name="appName" id="appNameId"
                                             cssClass="form-control">
                                    <c:forEach items="${appNameList}" var="item">
                                        <form:option value="${item.name}">${item.name}</form:option>
                                    </c:forEach>
                                </form:select>
                                <!--
                        <input type="text" value="${appName}" name="appName"  placeholder="请输入应用名称(必填)" class="form-control">
                        -->
                            </div>
                        </div>
                        <div class="col-xs-3">
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <i class="fa fa-laptop"> traceId：</i>
                                </div>
                                <input type="text" value="${traceId}" name="traceId" placeholder="请输入traceId" class="form-control">
                            </div>
                        </div>

                        <div class="col-xs-3">
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <i class="fa fa-laptop">Key：</i>
                                </div>
                                <input type="text" value="${Key}" name="Key" placeholder="请输入Key" class="form-control">
                            </div>
                        </div>

                        <div class="col-xs-3">
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <i class="fa fa-laptop">方法：</i>
                                </div>
                                <input type="text" value="${methodName}" name="methodName" placeholder="请输入methodName" class="form-control">
                            </div>
                        </div>

                    </div>
                    </p>
                    <p>
                    <div class="row">
                        <div class="col-xs-6">
                            <div class="input-group">
                                <div class="input-group-addon">
                                    <i class="fa fa-clock-o"> 时间段：</i>
                                </div>
                                <input type="text" value="${dTime}" placeholder="时间" name="dTime" id="reservationtime" class="form-control pull-right">
                            </div>
                        </div>
                        <div class="col-xs-3">
                            <div class="input-group">
                                <button type="submit" class="fa fa-search-minus btn-sm">查询</button>
                            </div>
                        </div>
                    </div>
                    </p>
                </form>
            </div>

            <div class="table-responsive">
                <div class="box-body">
                    <!-- 定位到具体节点-->
                    <!--
                    <form id="reveal">
                        <input type="text" name="nodeId" placeholder="nodeId" id="revealNodeId"/>
                        <input type="submit" value="Reveal"/><br/>
                        <small>Examples: <tt>2-1-1</tt>, <tt>3-1-1-2-2</tt> and <tt>3-2-1-2-3-1-2-2-1-1-1-1-2-5</tt></small>
                    </form>
                    -->

                    <table class="table table-bordered">
                        <tbody><tr>
                            <th>traceID</th>
                            <th>应用名</th>
                            <th>主机</th>
                            <th>方法名</th>
                            <th>KEY</th>
                            <th>发生时间</th>
                            <th>详情</th>
                        </tr>
                        <c:forEach items="${methodMonitorList}" var="iterm">
                            <tr>
                                <td>${iterm.traceId}</td>
                                <td>${iterm.appName}</td>
                                <td>${iterm.hostName}</td>
                                <td>${iterm.methodName}</td>
                                <td>${iterm.key}</td>
                                <td>${iterm.logTime}</td>
                                <td>
                                    <%--<a target="_blank" class="btn btn-primary btn-xs" href="/monitor/getCustomAlarmMonitorDetail/${iterm.AppName}/${iterm.Traceid}">详情</a>--%>
                                    <a target="_blank" class="btn btn-primary btn-xs" href="/index/trace?traceID=${iterm.traceId}&appName=${iterm.appName}">详情</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody></table>
                </div>
                <div class="box-footer clearfix">
                    <nav>
                        <ul class="pagination pagination-sm no-margin pull-right" id="paginations">
                        </ul>
                    </nav>
                </div>


            </div>

        </section>
    </aside>







</div>












</body>
<script>
    $(function () {
        if('${appName}'!=''){
            $("#appNameId").val('${appName}');
        }
        $('#reservationtime').daterangepicker({timePicker: true, timePickerIncrement: 30, format: 'YYYY-MM-DD HH:mm:ss'});
    })



</script>

</html>
