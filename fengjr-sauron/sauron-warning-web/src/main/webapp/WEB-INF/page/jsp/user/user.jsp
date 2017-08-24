<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>

    <link href="/resources/css/AdminLTE.css" rel="stylesheet" />
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="/resources/css/bootstrap.min.css" />

    <link rel="stylesheet" href="/resources/css/warm.css" />
    <!-- 可选的Bootstrap主题文件（一般不用引入） -->
    <link rel="stylesheet" href="/resources/css/bootstrap-theme.min.css" />

    <link href="/resources/css/font-awesome.min.css" rel="stylesheet" />
    <!-- Ionicons -->
    <link href="/resources/css/ionicons.min.css" rel="stylesheet" />
    <link href="/resources/css/iCheck/all.css" rel="stylesheet"/>
    <link rel="stylesheet" href="/resources/css/bootstrapValidator.min.css"/>
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/resources/js/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>

    <script src="/resources/js/app.js"></script>

    <script src="/resources/js/jquery.twbsPagination.js"></script>
    <script src="/resources/js/jquery.dataTables.js" type="text/javascript"></script>
    <script src="/resources/js/dataTables.bootstrap.js" type="text/javascript"></script>
    <script type="text/javascript" src="/resources/js/bootstrapValidator.min.js"></script>
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
                <small>ZK组管理</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i>ZK组管理</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="table-responsive">
                <div class="box-header box box-primary">
                    <p>
                    <div class="box-body">
                        <a class="btn btn-social-icon btn-linkedin" data-toggle="modal"
                           data-target="#zkIpsModal" data-title="新增用户" data-type='add'
                           data-button="新增" onclick="add(this);"
                           ><i class="fa fa-fw fa-plus-square-o"></i></a>
                        <a class="btn btn-social-icon btn-linkedin enable-app"><i class="fa fa-bitbucket"></i></a>
                    </div>
                    </p>
                </div>
                <div class="box-body">
                    <table class="table table-bordered">
                        <tbody><tr>
                            <th style="width: 10px"><input type="checkbox"  style="opacity: 10" class="flat-red" /></th>
                            <th>用户名</th>
                            <th>用户ID</th>
                            <th style="width: 40px">操作</th>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="iterm">
                            <tr>
                                <td>
                                    <label class="t_detail">
                                        <input value="${iterm.id}" type="checkbox" name="orderId"  style="opacity: 10" class="flat-red" />
                                    </label>
                                </td>
                                <td>${iterm.name}</td>
                                <td>${iterm.userId}</td>
                                <td>
                                    <a class="btn btn-warning btn-flat btn-xs" data-toggle="modal"
                                       data-target="#zkIpsModal" data-title="修改用户" data-type='mod'
                                       onclick="modify(${iterm.id})"
                                       data-button="修改"
                                       d_id="${iterm.id}">编辑</a></td>
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
    <div class="modal fade" id="zkIpsModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
        <form id="addZkForm">
        <div class="box box-solid box-info modal-dialog">
            <div class="box-header">
                <h3 class="box-title">新增用户</h3>
                <div class="box-tools pull-right">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                </div>
            </div>
            <div class="box-body" style="display: block;">

                <p>


                <p>
                <div class="row">
                    <div class="col-xs-12">
                        <div class="input-group">
                            <div class="input-group-addon">
                                <i class="fa fa-pencil"> 用户名</i>
                            </div>
                            <input type="text" class="form-control" name="name" placeholder="请输入用户名"
                                   id="zk-name"/>
                            <input type="hidden" id="subType">
                            <input type="hidden" id="zk-id">
                        </div>
                    </div>
                </div>
                </p>

                <p>
                <div class="row">
                    <div class="col-xs-12">
                        <div class="input-group">
                            <div class="input-group-addon">
                                <i class="fa fa-pencil"> 用户ID</i>
                            </div>
                            <input type="text" class="form-control" name="userId" placeholder="请输入用户ID"
                                   id="zk-userId"/>
                        </div>
                    </div>
                </div>
                </p>


                </p>
            </div><!-- /.box-body -->
            <div class="modal-footer">
                <button id="confirmBtn" type="submit" class="btn btn-primary">确定</button>
                <button type="button" id="deleteConfirmBtn" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
        </form>
    </div>
</div>

<%@include file="../common/pager.jsp" %>


<script>


    $('#addZkForm').bootstrapValidator({
        message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            name: {
                message: 'The name is not valid',
                validators: {
                    notEmpty: {
                        message: '用户名不能为空'
                    },
                    stringLength: {
                        min: 3,
                        max: 30,
                        message: '用户名由3到30个字符组成'
                    }
                }
            },
            userId: {
                validators: {
                    notEmpty: {
                        message: '用户ID不能为空'
                    }
                }
            }
        }
    }).on('success.form.bv',function(e){
        e.preventDefault();
        var subType = $("#subType").val();
        if (subType == "add") {
            var name = $('#zk-name').val();
            var userId = $('#zk-userId').val();
            $.post("/user/add/",
                    {"name":name,"userId":userId},
                    function (data) {
                        if (data.code == 0) {
                            alert("新增成功！");
                            $("#zkIpsModal").popover('destroy')
                        } else {
                            alert(data.msg);
                        }
                        document.forms[0].action = "/user/list/1/100";
                        document.forms[0].submit();
                    });
        }
        if (subType == "mod") {
            var id = $("#zk-id").val();
            var name = $('#zk-name').val();
            var userId = $('#zk-userId').val();
            $.post("/user/update/",
                    {"id":id,"name":name,"userId":userId},
                    function (data) {
                        if (data.code == 0) {
                            alert("修改成功！");
                        } else {
                            alert(data.msg);
                        }
                        document.forms[0].action = "/user/list/1/100";
                        document.forms[0].submit();
                    });
        }

    });


    $(function () {
        $("#deleteConfirmBtn").click(function () {
            document.forms[0].action = "/user/list/1/100";
            document.forms[0].submit();
        })

        $(".enable-app").click(function () {
            var status = $(this).attr("value");
            var ids = '';
            if($("input[name=orderId]:checkbox:checked").size()==0){
                alert("未选择相应的应用");
                return
            }
            $("input[name=orderId]:checkbox:checked").each(function(i){
                if(i>0) ids+=";";
                ids+=$(this).val();
            })
            if(confirm("确定要删除？")){
                del(ids);
            }

        });
    })


    $('#zkIpsModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var title = button.data('title') // Extract info from data-* attributes
        var actType = button.data('type');
        var buttonTitle = button.data('button');
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this)
        modal.find('.modal-title').text(title)
        $("#subType").val(actType);
        $("#confirmBtn").text(buttonTitle);
//        if (actType == "add") {
//            $('#confirmBtn').unbind("click"); //移除click
//            $("#confirmBtn").click(function () {
//                        var name = $('#zk-name').val();
//                        var zkIp = $('#zk-zkIp').val();
//                        var describes = $('#zk-describes').val();
//                        $.post("/zk/add/",
//                                {"name":name,"zkIp":zkIp,"describes":describes},
//                                function (data) {
//                                    if (data.code == 0) {
//                                        alert("新增成功！");
//                                    } else {
//                                        alert(data.msg);
//                                    }
//                                    document.forms[0].action = "/zk/list/1/100";
//                                    document.forms[0].submit();
//                                });
//                    }
//            );
//        }
//        if (actType == "mod") {
//            $('#confirmBtn').unbind("click"); //移除click
//            $("#confirmBtn").click(function () {
//                        var id = $("#zk-id").val();
//                        var name = $('#zk-name').val();
//                        var zkIp = $('#zk-zkIp').val();
//                        var describes = $('#zk-describes').val();
//                        $.post("/zk/update/",
//                                {"id":id,"name":name,"zkIp":zkIp,"describes":describes},
//                                function (data) {
//                                    if (data.code == 0) {
//                                        alert("修改成功！");
//                                    } else {
//                                        alert(data.msg);
//                                    }
//                                    document.forms[0].action = "/zk/list/1/100";
//                                    document.forms[0].submit();
//                                });
//                    }
//            );
//        }

    })
    function modify(id) {
        $.getJSON("/user/detail/" + id, {}, function (data) {
            if (data.code == 0) {
                $('#zk-name').val(data.attach.name);
                $('#zk-userId').val(data.attach.userId);
                $('#zk-id').val(id);
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
        });
    }
    function del(ids) {
        $.getJSON("/user/del", {"ids":ids}, function (data) {
            if (data.code == 0) {
                alert("删除成功！");
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
            document.forms[0].action = "/user/list/1/100";
            document.forms[0].submit();
        });
    }

    function add(obj) {
        $("#zkIpsModal").find("input").val("");
    }

</script>
</body>
</html>
