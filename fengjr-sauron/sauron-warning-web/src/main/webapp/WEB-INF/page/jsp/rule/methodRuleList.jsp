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

    <link rel="stylesheet" href="/resources/css/warm.css">
    <!-- 可选的Bootstrap主题文件（一般不用引入） -->
    <link rel="stylesheet" href="/resources/css/bootstrap-theme.min.css">

    <link href="/resources/css/font-awesome.min.css" rel="stylesheet" />
    <!-- Ionicons -->
    <link href="/resources/css/ionicons.min.css" rel="stylesheet" />
    <link href="/resources/css/iCheck/all.css" rel="stylesheet"/>
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/resources/js/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>

    <script src="/resources/js/app.js"></script>

    <script src="/resources/js/jquery.twbsPagination.js"></script>
    <script src="/resources/js/jquery.dataTables.js" type="text/javascript"></script>
    <script src="/resources/js/dataTables.bootstrap.js" type="text/javascript"></script>


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
                <small>方法配置</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 方法配置</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">

            <div class="box box-primary">
                <p>
                <form id="searchForm" action="/rule/list/1/10/1" method="POST" class="form-inline" style="margin-left: 15px;">

                    <label>应用名: <input value="${appName}" name="appName" type="text" aria-controls="example1"></label>
                    <label>主机名: <input value="${hostName}" name="hostName" type="text" aria-controls="example1"></label>
                    <label>
                        方法名: <input value="${methodName}" name="methodName" type="text" aria-controls="example1">
                        <input type="hidden" name="type" value="1">
                    </label>
                    <label><button type="submit" class="btn btn-success btn-sm">查询</button></label>

                </form>
                </p>
            </div>

            <div class="table-responsive">
                <div class="box-header">
                    <p>
                    <div class="box-body">
                        <a class="btn btn-app" href="/rule/add">
                            <i class="fa fa-edit"></i> 新增
                        </a>
                    <!--
                        <a class="btn btn-app enable-app" value="0">
                            <i class="fa fa-play"></i> 启用
                        </a>
                        <a class="btn btn-app enable-app" value="1">
                            <i class="fa fa-pause"></i> 禁用
                        </a>

                        <a class="btn btn-app enable-app">
                            <i class="fa fa-bitbucket"></i> 删除
                        </a>
                         -->
                    </div>
                    </p>
                </div>

                <div class="box-body">
                    <table class="table table-bordered">
                        <tbody><tr>
                            <th style="width: 10px"><input type="checkbox"  style="opacity: 10" class="flat-red" /></th>
                            <th>应用名称</th>
                            <th>主机名称</th>
                            <th>方法名称</th>
                            <th>操作</th>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="iterm">
                            <tr>
                                <td>
                                    <label class="t_detail">
                                        <input value="${iterm.id}" type="checkbox" name="orderId"  style="opacity: 10" class="flat-red" />
                                    </label>
                                </td>
                                <td>${iterm.appName}</td>
                                <td>${iterm.hostName}</td>
                                <td style="word-break: break-all;">${iterm.methodName}</td>
                                <td width="160px">
                                    <a class="btn btn-warning btn-flat btn-xs" href="/rule/modify/${iterm.id}">编辑</a>&nbsp;&nbsp;<a class="btn btn-warning btn-flat btn-xs" data-ruleid="${iterm.id}"
                                       data-toggle="modal" data-titile="查看详情"
                                       data-target="#ruleDetailModal">查看</a>&nbsp;
                                    <a class="btn btn-warning btn-flat btn-xs" onclick="del(${iterm.id},this)">删除</a>
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
                            <input type="hidden" name="type" value="1">
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


<script>
    function page(number) {
        $("#searchForm").attr("action","../../" + number + "/10/1")
        $("#searchForm").submit();
    }

    $("#paginations").twbsPagination({
        totalPages: ${pageData.totalPage},
        startPage:${pageData.pageNO},
        visiblePages: 5,
        href: "javascript:page({{number}});",
        first: '首页',
        prev: '<<',
        next: '>>',
        last: '末页'
    });
</script>
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
        $.getJSON("/rule/del/" + id, {"type":"1"}, function (data) {
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
