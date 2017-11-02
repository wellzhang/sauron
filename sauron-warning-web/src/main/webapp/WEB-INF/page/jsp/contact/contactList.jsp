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
                通讯录
                <small>列表</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 通讯录</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="row">
                <form action="/contact/query" method="POST" class="form-inline">
                    <div class="input-group">
                        <div class="input-group-addon">ID</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" name="id"/>

                        <div class="input-group-addon">手机号</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" name="mobile"/>

                        <div class="input-group-addon">微信号</div>
                        <input type="text" class="form-control" aria-describedby="basic-addon1" name="wechat"/>

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
                                        data-target="#maintainContactModal" data-title="新增联系人信息" data-type='add'
                                        data-button="新增"
                                        onclick="add(this);">新增
                                </button>
                            </div>
                        </tr>
                        <tr>
                            <td>序号</td>
                            <td>姓名</td>
                            <td>手机号</td>
                            <td>微信号</td>
                            <td>角色</td>
                            <td>状态</td>
                            <td></td>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="row">
                            <tr>
                                <td>${row.id}</td>
                                <td>${row.name}</td>
                                <td>${row.mobile}</td>
                                <td>${row.wechat}</td>
                                <td>${row.role}</td>
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
                                                data-target="#maintainContactModal" data-title="修改联系人信息"
                                                onclick="modify(${row.id},this)" data-button="修改" data-type="mod">修改
                                        </button>
                                        <button type="button" class="btn btn-default" onclick="del(${row.id},this)">删除
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

            <div class="modal fade" id="maintainContactModal" tabindex="-1" role="dialog"
                 aria-labelledby="exampleModalLabel">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                    aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="exampleModalLabel">通讯录联系人</h4>
                        </div>
                        <div class="modal-body">
                            <form>
                                <input id="contact-id" hidden="true"/>
                                <div class="form-group">
                                    <label for="contact-name" class="control-label">姓名：</label>
                                    <input type="text" class="form-control" id="contact-name">
                                </div>
                                <div class="form-group">
                                    <label for="contact-mobile" class="control-label">手机号：</label>
                                    <input type="text" class="form-control" id="contact-mobile">
                                </div>
                                <div class="form-group">
                                    <label for="contact-wechat" class="control-label">微信号：</label>
                                    <input type="text" class="form-control" id="contact-wechat">
                                </div>
                                <div class="form-group">
                                    <label for="contact-role" class="control-label">角色:</label>
                                    <input type="text" class="form-control" id="contact-role">
                                </div>
                                <%--<div class="form-group">--%>
                                <%--<label for="contact-status" class="control-label">状态:</label>--%>
                                <%--<input type="text" class="form-control" id="contact-status">--%>
                                <%--</div>--%>
                            </form>
                        </div>
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
    $('#maintainContactModal').on('show.bs.modal', function (event) {
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
                        var name = $('#contact-name').val();
                        var mobile = $('#contact-mobile').val();
                        var wechat = $('#contact-wechat').val();
                        var role = $('#contact-role').val();
                        var status = 0;
                        $.getJSON("/contact/add/" + name + "/" + mobile + "/" + wechat + "/" + role + "/" + status, {}, function (data) {
                            if (data.code == 0) {
                                alert("新增成功！");
                            } else {
                                alert(data.msg);
                            }
                            document.forms[0].action = "/contact/list/${pageData.pageNO}/${pageData.pageSize}";
                            document.forms[0].submit();
                        });
                    }
            );
        }
        if (actType == "mod") {
            $('#confirmBtn').unbind("click"); //移除click
            $("#confirmBtn").click(function () {
                        var id = $('#contact-id').val();
                        var name = $('#contact-name').val();
                        var mobile = $('#contact-mobile').val();
                        var wechat = $('#contact-wechat').val();
                        var role = $('#contact-role').val();
                        $.getJSON("/contact/update/" + id + "/" + name + "/" + mobile + "/" + wechat + "/" + role + "/", {}, function (data) {
                            if (data.code == 0) {
                                alert("修改成功！");
                            } else {
                                alert(data.msg);
                            }
                            document.forms[0].action = "/contact/list/${pageData.pageNO}/${pageData.pageSize}";
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

        document.forms[0].action = "/contact/list/${pageData.pageNO}/${pageData.pageSize}/" + id + "/" + mobile + "/" + wechat;
        document.forms[0].submit();

    })

    function modify(id, obj) {
        $.getJSON("/contact/modify/" + id, {}, function (data) {
            if (data.code == 0) {
                var attach = data.attach;
                $('#contact-id').val(id);
                $('#contact-name').val(attach.name);
                $('#contact-mobile').val(attach.mobile);
                $('#contact-wechat').val(attach.wechat);
                $('#contact-role').val(attach.role);
            } else {
                alert(data.msg);
            }
        });

    }
    function del(id, obj) {
        $.ajaxSettings.async = false;
        $.getJSON("/contact/del/" + id, {}, function (data) {
            if (data.code == 0) {
                alert("删除成功！");
            } else {
                alert(data.msg);
            }
            document.forms[0].action = "/contact/list/${pageData.pageNO}/${pageData.pageSize}";
            document.forms[0].submit();
        });
    }
    function add(obj) {
        $("#maintainContactModal").find(".modal-body input").val("");
    }
</script>
</body>
</html>
