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
                报警规则
                <small>列表</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 报警规则</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="row placeholders">
                <form action="/rule/query" method="post" class="form-inline">
                    <div class="input-group">

                        <div class="input-group-addon">主机名</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" name="hostName"
                               id="hostName"/>

                        <div class="input-group-addon">应用名</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" name="appName"
                               id="appName"/>

                        <div class="input-group-addon">方法名</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" name="methodName"
                               id="methodName"/>
                    </div>
                    <input type="submit" class="btn btn-primary" value="查询">
                </form>
            </div>
            <div class="table-responsive">
                <tr>
                    <table class="table">
                        <tr>
                            <div class="btn-group" role="group" aria-label="...">
                                <a href="/rule/add">
                                    <button type="button" class="btn btn-default">新增</button>
                                </a>
                            </div>
                        </tr>
                        <tr>
                            <td>序号</td>
                            <td>主机名称</td>
                            <td>应用名称</td>
                            <td style="word-break: break-all">方法名称</td>
                            <td>操作</td>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="row">
                            <tr>
                                <td>${row.id}</td>
                                <td>${row.hostName}</td>
                                <td>${row.appName}</td>
                                <td>${row.methodName}</td>
                                <td>
                                    <div class="btn-group" role="group" aria-label="...">
                                        <button type="button" class="btn btn-default" data-ruleid="${row.id}"
                                                data-toggle="modal" data-titile="查看详情"
                                                data-target="#ruleDetailModal">
                                            查看
                                        </button>
                                        <a href="/rule/modify/${row.id}">
                                            <button type="button" class="btn btn-default">修改</button>
                                        </a>
                                        <button type="button" class="btn btn-default" onclick="del(${row.id},this)">
                                            删除
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
    <div class="modal fade" id="ruleDetailModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="exampleModalLabel">规则详情</h4>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group" contenteditable="false">
                            <label for="rule-hostname" class="control-label">主机名</label>
                            <input type="text" class="form-control"
                                   id="rule-hostname" readOnly="true"/>
                        </div>
                        <div class="form-group" contenteditable="false">
                            <label for="rule-appname" class="control-label">应用名</label>
                            <input type="text" class="form-control"
                                   id="rule-appname" readOnly="true"/>
                        </div>
                        <div class="form-group" contenteditable="false">
                            <label for="rule-methodname" class="control-label">方法名</label>
                            <input type="text" class="form-control"
                                   id="rule-methodname" readOnly="true"/>
                        </div>
                        <div class="form-group" contenteditable="false">
                            <label for="rule-clauses" class="control-label">规则条款:</label>
                            <textarea class="form-control" id="rule-clauses" readOnly="true"></textarea>
                        </div>
                        <div class="form-group" contenteditable="false">
                            <label for="rule-receivers" class="control-label">报警信息接收人:</label>
                            <textarea class="form-control" id="rule-receivers" readOnly="true"></textarea>
                        </div>
                        <div>
                            <label for="rule-strategy" class="control-label">报警策略:</label>
                            <input class="form-control" id="rule-strategy" readOnly="true"> </input>
                        </div>

                    </form>
                </div>
                <div class="modal-footer">
                    <%--<button id="addConfirmBtn" type="button" class="btn btn-primary">确定</button>--%>
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>
</div>


<%@include file="../common/pager.jsp" %>
<script>
    function getMetricOptName(metricOpts, metricOptId) {
        for (indx = 0; indx < metricOpts.length; indx++) {
            if (metricOpts[indx].id == metricOptId) {
                return metricOpts[indx].metricName;
            }
        }
    }
    $('#ruleDetailModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var ruleId = button.data('ruleid') // Extract info from data-* attributes
        var modal = $(this)
        modal.find('.modal-body input').val("");
        modal.find('.modal-body textarea').val("");
        $.getJSON('/rule/detail/' + ruleId, {}, function (data) {
            if (data.code == 0) {
                var resultMap = data.attach;
                var rule = resultMap.rule;
                var metricOpts = resultMap.metricOpts;
                var clausesStr = "";
                var receiversStr = "";
                for (i = 0; i < resultMap.clauses.length; i++) {
                    var clause = resultMap.clauses[i];

                    clausesStr += ";" + getMetricOptName(metricOpts, clause.metricOptId) + clause.operator + clause.varible;
                }
                for (j = 0; j < resultMap.receivers.length; j++) {
                    var receiver = resultMap.receivers[j];
                    receiversStr += ";" + receiver.name;
                }

                $("#rule-appname").val(rule.appName);
                $("#rule-hostname").val(rule.hostName);
                $("#rule-methodname").val(rule.methodName);
                $("#rule-clauses").val(clausesStr);
                $("#rule-receivers").val(receiversStr);
                $('#rule-strategy').val(rule.template);
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
                return;
            }


        });
    })
    function modify(id, obj) {
//        alert("修改数据？" + id);
    }
    function del(id, obj) {
        $.getJSON("/rule/del/" + id, {}, function (data) {
            if (data.code == 0) {
                alert("删除成功！");
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
            document.forms[0].action = "../${pageData.pageNO}/${pageData.pageSize}";
            document.forms[0].submit();
        });
    }
    function add(obj) {
        $.get("/rule/add", null, null);
    }
</script>
</body>
</html>
