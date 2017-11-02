<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
                <small>DUBBO监控</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i>DUBBO监控</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="box box-primary">
                <p>
                <form action="/monitor/dubboList/1/10" method="POST" class="form-inline" style="margin-left: 15px;">

                    <label>应用名称:
                        <form:select path="appNameList" name="appName" id="appNameId"
                                     cssClass="form-control selectpicker">
                            <option value="">全部</option>
                            <c:forEach items="${appNameList}" var="item">
                                <form:option value="${item.name}">${item.name}</form:option>
                            </c:forEach>
                        </form:select>
                    </label>
                    <label>application: <input value="${applicationName}" name="applicationName" type="text" aria-controls="example1"></label>
                    <label><button type="submit" class="btn btn-success btn-sm">查询</button></label>

                </form>
                </p>
            </div>
            <div class="table-responsive">
                <div class="box-header">
                </div>
                <div class="box-body">
                    <table class="table table-bordered">
                        <tbody><tr>
                            <th>应用名称</th>
                            <th>application</th>
                            <th>服务是否正常</th>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="iterm">
                            <tr>
                                <td>${iterm.appName}</td>
                                <td>${iterm.applicationName}</td>
                                <td>
                                        <c:if test="${iterm.isAlive == 0}">



                                            <span class="btn btn-info"><i class="fa fa-chain">  正常</i></span>
                                        </c:if>
                                        <c:if test="${iterm.isAlive == 1}">
                                            <a class="btn btn-danger"><i class="fa fa-unlink">  异常</i></a>
                                        </c:if>
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
<%@include file="../common/pager.jsp" %>

<script>

    $(function () {

        if('${appName}'!=''){
            $("#appNameId").val('${appName}');
        }else{
            $("#appNameId").val('')
        }

    })

</script>


</body>
</html>
