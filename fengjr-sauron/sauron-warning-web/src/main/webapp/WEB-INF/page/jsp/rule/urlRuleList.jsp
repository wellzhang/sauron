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
                <small>URL配置</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i>URL配置</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="box box-primary">
                <p>
                <form id="searchForm"  action="/urlRule/list/1/10" method="POST" class="form-inline" style="margin-left: 15px;">

                    <label>应用名称: <input value="${appName}" name="appName" type="text" aria-controls="example1"></label>
                    <label>接入KEY: <input value="${monitorKey}" name="monitorKey" type="text" aria-controls="example1"></label>
                    <label><button type="submit" class="btn btn-success btn-sm">查询</button></label>

                </form>
                </p>
            </div>
            <div class="table-responsive">
                <div class="box-header">
                    <p>
                    <div class="box-body">
                    <a class="btn btn-app" href="/urlRule/add">
                        <i class="fa fa-edit"></i> 新增
                    </a>
                    <a class="btn btn-app enable-app" value="0">
                        <i class="fa fa-play"></i> 启用
                    </a>
                    <a class="btn btn-app enable-app" value="1">
                        <i class="fa fa-pause"></i> 禁用
                    </a>
                    <a class="btn btn-app enable-app">
                        <i class="fa fa-bitbucket"></i> 删除
                    </a>
                </div>
                    </p>
                </div>
                <div class="box-body">
                    <table class="table table-bordered">
                        <tbody><tr>
                            <th style="width: 10px"><input type="checkbox"  style="opacity: 10" class="flat-red" /></th>
                            <th>应用名称</th>
                            <th>接入KEY</th>
                            <th>监控地址</th>
                            <th>访问频率</th>
                            <th>是否启用</th>
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
                            <td>${iterm.monitorKey}</td>
                            <td style="word-break: break-all">${iterm.monitorUrl}</td>
                            <td>${iterm.requestInterval} 分</td>
                            <td>
                                <c:if test="${iterm.isEnabled == 0}">
                                    启用
                                </c:if>
                                <c:if test="${iterm.isEnabled != 0}">
                                    停用
                                </c:if>
                            </td>
                            <td><a class="btn btn-warning btn-flat btn-xs" href="/urlRule/modify/${iterm.id}">编辑</a></td>
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
<%@include file="../common/pager.jsp" %>


<script>

    $(function () {
        $('.t_detail').find('.iCheck-helper').on('click',function () {
           //alert("..");
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

            if(undefined != status){
                $.post("/urlRule/enableOrDisable",
                        {"ids":ids,"status":status},
                        function (data) {
                            if (data.code == 0) {
                                alert("操作成功");
                                document.forms[0].action = "/urlRule/list/1/10";
                                document.forms[0].submit();
                            } else {
                                alert(data.msg);
                            }
                        });
            } else {
                if(confirm("确定要删除？")){
                    $.post("/urlRule/del",
                            {"ids":ids},
                            function (data) {
                                if (data.code == 0) {
                                    alert("删除");
                                    document.forms[0].action = "/urlRule/list/1/10";
                                    document.forms[0].submit();
                                } else {
                                    alert(data.msg);
                                }
                            });
                }

            }



        });





    })

</script>
</body>
</html>
