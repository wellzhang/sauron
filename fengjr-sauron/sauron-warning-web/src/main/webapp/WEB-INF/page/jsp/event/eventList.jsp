<%--
  Created by IntelliJ IDEA.
  User: Liuyb
  Date: 2015/12/15
  Time: 13:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <link href="/resources/css/AdminLTE.css" rel="stylesheet" type="text/css">
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="/resources/css/bootstrap.min.css">

    <!-- 可选的Bootstrap主题文件（一般不用引入） -->
    <link rel="stylesheet" href="/resources/css/bootstrap-theme.min.css">

    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/resources/js/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>

    <script src="/resources/js/app.js"></script>

    <script src="/resources/js/jquery.twbsPagination.js"></script>


    <%--<link href="/resources/css/dashboard.css" rel="stylesheet">--%>
</head>

<body class="skin-blue">
<%@include file="../common/head.jsp" %>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <%@include file="../common/leftMenu.jsp" %>
    <aside class="right-side">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                报警事件
                <small>列表</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 报警事件</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="row placeholders">
                <form action="/event/query" method="post" class="form-inline">
                    <div class="input-group">

                        <div class="input-group-addon">应用名称</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" id="queryAppName"
                               name="appName"
                                />

                        <div class="input-group-addon">方法名称</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" id="queryMethodName"
                               name="methodName"/>

                        <div class="input-group-addon">主机名称</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" id="queryHostName"
                               name="hostName"/>

                        <div class="input-group-addon">实例名称</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" id="queryInstantName"
                               name="instantName"/>

                    </div>
                    <input id="queryBtn" type="submit" class="btn btn-primary" value="查询">
                </form>
            </div>
            <div class="table-responsive">
                <tr>
                    <table id="resultTab" class="table">
                        <tr>
                            <td>序号</td>
                            <td>应用名称</td>
                            <td>方法名</td>
                            <td>主机名</td>
                            <td>实例名称</td>
                            <td>发生时间</td>
                            <td></td>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="row">
                            <tr>
                                <td>${row.id}</td>
                                <td>${row.appName}</td>
                                <td>${row.methodName}</td>
                                <td>${row.hostName}</td>
                                <td>${row.instantName}</td>
                                <td><fmt:formatDate value="${row.occurTime}" pattern="yyyy年MM月dd日 HH:mm"/></td>
                                <td>
                                    <div class="btn-group" role="group" aria-label="...">
                                        <button type="button" id="viewBtn" class="btn btn-default" data-toggle="modal"
                                                data-target="#maintaineventModal" data-titile="查看详情"
                                                onclick="detail(this)"
                                                data-eventid='${row.id}'>查看
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                    <div>

                        <nav>
                            <ul class="pagination" id="paginations"></ul>
                        </nav>
                    </div>

                </tr>
            </div>
        </section>
    </aside>
</div>

<div class="modal fade" id="maintaineventModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="exampleModalLabel">测量指标</h4>
            </div>
            <div class="modal-body">
                <form>
                    <c:forEach items="${metricOptList}" var="metric">
                        <div class="form-group">
                            <label for="metric_${metric.id}" class="control-label">${metric.metricName}</label>
                            <input type="text" class="form-control" data-metricid="${metric.id}"
                                   id="metric_${metric.id}">
                        </div>
                    </c:forEach>

                </form>
            </div>
            <div class="modal-footer">
                <%--<button id="addConfirmBtn" type="button" class="btn btn-primary">确定</button>--%>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>

<%@include file="../common/pager.jsp" %>


<script>
    function detail(obj) {
        var eventId = $(obj).data('eventid') // Extract info from data-* attributes
        var modal = $('.modal');
        modal.find('.modal-body input').val("");
        $.getJSON('/event/detail/' + eventId, {}, function (data) {
                    if (data.code == 0) {
                        var metricsArray = data.attach;
                        for (i = 0; i < metricsArray.length; i++) {
                            var metric = metricsArray[i];
                            modal.find('.modal-body input').each(function () {
                                var metric_id = $(this).data("metricid");
                                if (metric_id == metric.metricOptId) {
                                    $(this).val(metric.value);
                                }
                            })
                        }
                    } else {
                        alert(data.msg + ':' + data.exceptionMsg);
            }

                }
        );
    }
    $('#maintaineventModal').on('show.bs.modal', function (event) {

    })
    $('#queryBtn').click(function () {
        var id = $('#queryId').val();
        var mobile = $('#queryMobile').val();
        var wechat = $('#queryWechat').val();

        document.forms[0].action = "../${pageData.pageNO}/${pageData.pageSize}/" + id + "/" + mobile + "/" + wechat;
        document.forms[0].submit();

    })
</script>
</body>
</html>
