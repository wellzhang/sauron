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
                ZK组地址
                <small>列表</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> ZK组地址</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="table-responsive">
                    <table class="table">
                        <tr>
                            <div class="btn-group" role="group" aria-label="...">
                                <button type="button" class="btn btn-default" data-toggle="modal"
                                        data-target="#zkIpsModal" data-title="新增ZK组" data-type='add'
                                        data-button="新增"
                                        onclick="add(this);">新增
                                </button>
                            </div>
                        </tr>
                        <tr>
                            <td>名称</td>
                            <td>zk</td>
                            <td>描述</td>
                            <td>操作</td>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="row">
                            <tr>
                                <td>${row.name}</td>
                                <td>${row.zkIp}</td>
                                <td>${row.describes}</td>
                                <td>
                                    <div class="btn-group" role="group" aria-label="...">


                                        <button type="button" class="btn btn-default" data-toggle="modal"
                                                data-target="#zkIpsModal" data-title="修改ZK组" data-type='mod'
                                                onclick="modify(${row.id})"
                                                data-button="修改"
                                                d_id="${row.id}">修改
                                        </button>

                                        <button type="button" class="btn btn-default" onclick="del(${row.id},this)">
                                            删除
                                        </button>
                                    </div>
                                </td>

                            </tr>
                        </c:forEach>
                    </table>
            </div>
        </section>
    </aside>
    <div class="modal fade" id="zkIpsModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">








        <div class="box box-solid box-info modal-dialog">
            <div class="box-header">
                <h3 class="box-title">新增ZK组</h3>
                <div class="box-tools pull-right">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                </div>
            </div>
            <div class="box-body" style="display: block;">

                <p>
                <form>
                    <input type="hidden" id="zk-id" />
                <input class="form-control" type="text" placeholder="User ID" name="userid">
                    <div class="form-group" contenteditable="false">
                        <label for="zk-name" class="control-label">名称</label>
                        <input type="text" class="form-control"
                               id="zk-name"/>
                    </div>
                    <div class="form-group" contenteditable="false">
                        <label for="zk-zkIp" class="control-label">ZK组</label>
                        <input type="text" class="form-control"
                               id="zk-zkIp"/>
                    </div>
                    <div class="form-group" contenteditable="false">
                        <label for="zk-describes" class="control-label">描述</label>
                        <input type="text" class="form-control"
                               id="zk-describes"/>
                    </div>
                </form>

                </p>
            </div><!-- /.box-body -->
            <div class="modal-footer">
                <button id="confirmBtn" type="button" class="btn btn-primary">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
        <!--
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="exampleModalLabel">新增ZK组</h4>
                </div>
                <div class="modal-body">
                    <form>
                        <input type="hidden" id="zk-id" />
                        <div class="form-group" contenteditable="false">
                            <label for="zk-name" class="control-label">名称</label>
                            <input type="text" class="form-control"
                                   id="zk-name"/>
                        </div>
                        <div class="form-group" contenteditable="false">
                            <label for="zk-zkIp" class="control-label">ZK组</label>
                            <input type="text" class="form-control"
                                   id="zk-zkIp"/>
                        </div>
                        <div class="form-group" contenteditable="false">
                            <label for="zk-describes" class="control-label">描述</label>
                            <input type="text" class="form-control"
                                   id="zk-describes"/>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button id="confirmBtn" type="button" class="btn btn-primary">确定</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
        -->
    </div>
</div>


<%@include file="../common/pager.jsp" %>
<script>
    $('#zkIpsModal').on('show.bs.modal', function (event) {
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
                        var name = $('#zk-name').val();
                        var zkIp = $('#zk-zkIp').val();
                        var describes = $('#zk-describes').val();
                        $.post("/zk/add/",
                                {"name":name,"zkIp":zkIp,"describes":describes},
                                function (data) {
                            if (data.code == 0) {
                                alert("新增成功！");
                            } else {
                                alert(data.msg);
                            }
                            document.forms[0].action = "/zk/list/1/100";
                            document.forms[0].submit();
                        });
                    }
            );
        }
        if (actType == "mod") {
            $('#confirmBtn').unbind("click"); //移除click
            $("#confirmBtn").click(function () {
                        var id = $("#zk-id").val();
                        var name = $('#zk-name').val();
                        var zkIp = $('#zk-zkIp').val();
                        var describes = $('#zk-describes').val();
                        $.post("/zk/update/",
                                {"id":id,"name":name,"zkIp":zkIp,"describes":describes},
                                function (data) {
                            if (data.code == 0) {
                                alert("修改成功！");
                            } else {
                                alert(data.msg);
                            }
                            document.forms[0].action = "/zk/list/1/100";
                            document.forms[0].submit();
                        });
                    }
            );
        }

    })
    function modify(id) {
        $.getJSON("/zk/detail/" + id, {}, function (data) {
            if (data.code == 0) {
                $('#zk-name').val(data.attach.name);
                $('#zk-zkIp').val(data.attach.zkIp);
                $('#zk-describes').val(data.attach.describes);
                $('#zk-id').val(data.attach.id);
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
        });
    }
    function del(id, obj) {
        $.getJSON("/zk/del/" + id, {}, function (data) {
            if (data.code == 0) {
                alert("删除成功！");
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
            document.forms[0].action = "/zk/list/1/100";
            document.forms[0].submit();
        });
    }
</script>
</body>
</html>
