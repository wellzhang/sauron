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

    <script src="/resources/js/jquery.dataTables.js" type="text/javascript"></script>
    <script src="/resources/js/dataTables.bootstrap.js" type="text/javascript"></script>
    <script src="/resources/js/bootstrap-checkbox.js" type="text/javascript" />
    <%--<link href="/resources/css/dashboard.css" rel="stylesheet">--%>

</head>


<body class="skin-blue">
<%@include file="../common/head.jsp" %>
<header class="header">
    <a href="/" class="logo">
        <!-- Add the class icon to your logo image or logo icon to add the margining -->
        Sauron应用监控后台
    </a>
    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top" role="navigation">
        <!-- Sidebar toggle button-->
        <a href="#" class="navbar-btn sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </a>

        <div class="navbar-right">


        </div>
    </nav>
</header>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <%@include file="../common/leftMenu.jsp" %>
    <aside class="right-side">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                <small>开关配置</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i>开关配置</a></li>
                <li class="active">开关项</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="box box-primary">
                <p>
                <form id="searchForm"  action="/config/set" method="POST" class="form-inline" style="margin-left: 15px;">
                <div class="row" style="margin-left: -15px; margin-bottom: -10px;">
                    <div class="col-xs-4">
                        <div class="input-group">
                            <div class="input-group-addon">
                                <i class="fa"> 应用名：</i>
                            </div>
                            <form:select path="appNameList" name="appName" id="appNameId"
                                         cssClass="form-control">
                                <c:forEach items="${appNameList}" var="item">
                                    <form:option value="${item.name}">${item.name}</form:option>
                                </c:forEach>
                            </form:select>

            </div>
</div>
                    <div class="col-xs-3">
                        <div class="input-group">
                            <label>报警开关：</label>
                            <select class="form-control" name="option" id="option_id">
                                <option value="OFF">OFF</option>
                                <option value="ON">ON</option>
                            </select>


                        </div>
                    </div>

<div class="col-xs-3">
    <div class="input-group">
                <button type="button" id="setData">提交</button>

    </div>
</div>

                </div>
                </form>
                </p>
            </div>

        </section>
    </aside>
</div>
<script>

    $(function () {
        $("body").attr("class","pace-done skin-black");

        $("#appNameId").change(function(){
            $.getJSON("/config/getValue",{"appName":$(this).val()},function (data) {
                $("#option_id").val(data["option"]);
            })
        });

        $("#setData").click(function () {
            $.post("/config/set",{"appName":$("#appNameId").val(),"option":$("#option_id").val()},function(data){
                if(data["option"]!=""){
                    alert("success");
                }
                $("#option_id").val(data["option"]);
            });
        });

    });



</script>
</body>
</html>
