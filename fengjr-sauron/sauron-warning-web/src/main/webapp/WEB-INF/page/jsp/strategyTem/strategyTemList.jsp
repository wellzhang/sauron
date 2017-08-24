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
    <link rel="stylesheet" href="/resources/css/bootstrap-clockpicker.min.css">

    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/resources/js/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>

    <script src="/resources/js/app.js"></script>

    <script src="/resources/js/jquery.twbsPagination.js"></script>
    <%--<link href="/resources/css/dashboard.css" rel="stylesheet">--%>

    <script src="/resources/js/bootstrap-checkbox.js"></script>
    <script src="/resources/js/bootstrap-clockpicker.min.js"></script>

</head>

<body class="skin-blue">
<%@include file="../common/head.jsp" %>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <%@include file="../common/leftMenu.jsp" %>
    <aside class="right-side">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                短信模板发送策略
                <small>列表</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 短信模板发送策略</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="row">
                <form id="searchForm" action="/strategyTem/list/1/10" method="POST" class="form-inline">
                    <div class="input-group">

                        <div class="input-group-addon">模板名称</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" name="template"/>

                        <div class="input-group-addon">策略ID</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" name="strategyId"/>

                        <div class="input-group-addon">短信渠道</div>
                        <select name="channelId" class="selectpicker form-control" aria-describedby="basic-addon1">
                            <option value="">所有渠道</option>
                            <option value="1">漫道科技</option>
                            <option value="2">玄武科技</option>
                            <option value="3">玄武营销</option>
                            <option value="4">亿美</option>
                            <option value="5">亿美营销</option>
                            <option value="6">维亚泰克</option>
                        </select>
                    </div>
                    <input id="queryBtn" type="submit" class="btn btn-primary" value="查询">
                </form>
            </div>
            <div class="table-responsive">
                <tr>
                    <table id="resultTab" class="table">
                        <tr>
                            <div class="btn-group" role="group" aria-label="...">
                                <button type="button" class="btn btn-default" data-toggle="modal"
                                        data-target="#maintainstrategyTemModal" data-title="新增报警策略" data-type='add'
                                        data-button="新增"
                                        onclick="add(this);">新增
                                </button>
                            </div>
                        </tr>
                        <tr>
                            <td>模板名称</td>
                            <td>关联报警策略</td>
                            <td>短信渠道ID</td>
                            <td>状态</td>
                            <td>操作</td>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="row">
                            <tr>
                                <td>${row.template}</td>
                                <td>${row.strategyId}</td>
                                <td>
                                    <c:if test="${row.channelId == 1}">
                                        漫道
                                    </c:if>
                                    <c:if test="${row.channelId == 2}">
                                        玄武
                                    </c:if>
                                    <c:if test="${row.channelId == 3}">
                                        玄武营销
                                    </c:if>
                                    <c:if test="${row.channelId == 4}">
                                        亿美
                                    </c:if>
                                    <c:if test="${row.channelId == 5}">
                                        亿美营销
                                    </c:if>
                                    <c:if test="${row.channelId == 6}">
                                        维亚泰克
                                    </c:if>
                                </td>
                                <td>${row.createTime}</td>
                                <td>
                                    <c:if test="${row.status == 0}">
                                        正常
                                    </c:if>
                                    <c:if test="${row.status != 0}">
                                        停用
                                    </c:if>
                                </td>
                                <td>
                                    <div class="btn-group" role="group" aria-label="...">
                                        <button type="button" id="modifyBtn" class="btn btn-default" data-toggle="modal"
                                                data-target="#maintainstrategyTemModal" data-title="修改报警策略"
                                                onclick="modify('${row.template}',this)" data-button="修改"
                                                data-type="mod">查看/修改
                                        </button>
                                        <button type="button" class="btn btn-default"
                                                onclick="del('${row.template}',this)">删除
                                        </button>
                                    </div>
                                </td>

                            </tr>
                        </c:forEach>
                    </table>
                    <div>

                        <nav>
                            <ul class="pagination" id="paginations">
                            </ul>
                        </nav>
                    </div>

                </tr>
            </div>

            <div class="modal fade" id="maintainstrategyTemModal" tabindex="-1" role="dialog"
                 aria-labelledby="exampleModalLabel">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                    aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="exampleModalLabel">短信模板关联策略</h4>
                        </div>
                        <div class="modal-body">
                            <form>
                                <div class="input-group">
                                    <span class="input-group-addon">模板名称：</span>
                                    <input type="text" class="form-control" id="strategyTem-template">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">发送策略ID：</span>
                                    <select id="strategyTem-strategyId" class="selectpicker form-control">
                                        <c:forEach items="${strategyList}" var="row">
                                            <option value="${row.id}">${row.stgyName}</option>
                                        </c:forEach>
                                    </select>
                                    <%--<input type="text" class="form-control" id="strategyTem-strategyId">--%>
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">短信渠道：</span>
                                    <%--<input type="text" class="form-control" id="strategyTem-channelId">--%>
                                    <select id="strategyTem-channelId" class="selectpicker form-control">
                                        <option value="1">漫道科技</option>
                                        <option value="2">玄武科技</option>
                                        <option value="3">玄武营销</option>
                                        <option value="4">亿美</option>
                                        <option value="5">亿美营销</option>
                                        <option value="6">维亚泰克</option>
                                    </select>
                                </div>
                                <%--<div class="input-group">--%>
                                <%--<span class="input-group-addon">状态：</span>--%>
                                <input type="hidden" class="form-control" id="strategyTem-status">
                                <%--</div>--%>
                            </form>
                        </div>
                        .
                        <div class="modal-footer">
                            <button id="confirmBtn" type="button" class="btn btn-primary">更新</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </aside>
</div>
<%@include file="../common/pager.jsp" %>


<script>
    $('#maintainstrategyTemModal').on('show.bs.modal', function (event) {
        $("input:checkbox").each(function () {
            $(this).prop('checked', true);
        });
        var button = $(event.relatedTarget) // Button that triggered the modal
        var title = button.data('title') // Extract info from data-* attributes
        var actType = button.data('type');
        var buttonTitle = button.data('button');
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this)
        modal.find('.modal-title').text(title)
        $("#confirmBtn").text(buttonTitle);
        if (actType == "add") {
            $('#confirmBtn').unbind("click"); //移除click
            $("#confirmBtn").click(function () {
                        var template = $('#strategyTem-template').val();
                        var strategyId = $('#strategyTem-strategyId option:selected').val();
                        var channelId = $('#strategyTem-channelId option:selected').val();

                        if (template.length == 0 || strategyId.length == 0 || channelId.length == 0) {
                            alert("页面所有字段均为必填项，请输入");
                            return;
                        }

                        $.getJSON("/strategyTem/add", {
                            template: template,
                            strategyId: strategyId,
                            content: '',
                            channelId: channelId,
                            status: 0
                        }, function (data) {
                            if (data.code == 0) {
                                alert("新增成功！");
                            } else {
                                alert(data.msg + ':' + data.exceptionMsg);
                            }
                            document.forms[0].action = "/strategyTem/list/${pageData.pageNO}/${pageData.pageSize}";
                            document.forms[0].submit();
                        });
                    }
            );
        }
        if (actType == "mod") {
            $('#confirmBtn').unbind("click"); //移除click
            $("#confirmBtn").click(function () {
                        var template = $('#strategyTem-template').val();
                        var strategyId = $('#strategyTem-strategyId option:selected').val();
                        var channelId = $('#strategyTem-channelId option:selected').val();

                        if (template.length == 0 || strategyId.length == 0 || channelId.length == 0) {
                            alert("页面所有字段均为必填项，请输入");
                            return;
                        }
                        $.getJSON("/strategyTem/update", {
                            template: template,
                            strategyId: strategyId,
                            content: '',
                            channelId: channelId,
                            status: 0
                        }, function (data) {
                            if (data.code == 0) {
                                alert("修改成功！");
                            } else {
                                alert(data.msg + ':' + data.exceptionMsg);
                            }
                            document.forms[0].action = "/strategyTem/list/${pageData.pageNO}/${pageData.pageSize}";
                            document.forms[0].submit();
                        });
                    }
            );
        }

    })
    $('#queryBtn').click(function () {
        var id = $('#queryId').val();
        var mobile = $('#queryMobile').val();
        var wechat = $('#queryWechat').val();

        document.forms[0].action = "/strategyTem/list/${pageData.pageNO}/${pageData.pageSize}/" + id + "/" + mobile + "/" + wechat;
        document.forms[0].submit();

    })

    function modify(template, obj) {
        $.getJSON("/strategyTem/modify", {
            template: template
        }, function (data) {
            if (data.code == 0) {
                var attach = data.attach;
                $('#strategyTem-template').val(attach.template);
                $('#strategyTem-strategyId').val(attach.strategyId);
                $('#strategyTem-channelId').val(attach.channelId);
                $('#strategyTem-status').val(attach.status);
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
        });

    }
    function del(template, obj) {
        if (!confirm("删除短信模板与策略关联关系可能会造成信息无法发送，是否继续？")) {
            return;
        }
        $.ajaxSettings.async = false;
        $.getJSON("/strategyTem/del", {template: template}, function (data) {
            if (data.code == 0) {
                alert("删除成功！");
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
            document.forms[0].action = "/strategyTem/list/${pageData.pageNO}/${pageData.pageSize}";
            document.forms[0].submit();
        });
    }
    function add(obj) {
        $("#maintainstrategyTemModal").find(".modal-body input").val("");
    }
</script>
</body>
</html>
