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
    <title></title>
    <link href="/resources/css/AdminLTE.css" rel="stylesheet" type="text/css">
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="/resources/css/bootstrap.min.css">

    <!-- 可选的Bootstrap主题文件（一般不用引入） -->
    <link rel="stylesheet" href="/resources/css/bootstrap-theme.min.css">

    <link href="/resources/css/ionicons.min.css" rel="stylesheet" />
    <link href="/resources/css/font-awesome.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="/resources/css/warm.css">

    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/resources/js/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>

    <script src="/resources/js/app.js"></script>


</head>

<body class="skin-blue">
<%@include file="jsp/common/head.html" %>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <%@include file="jsp/common/leftMenu.jsp" %>
    <aside class="right-side">
        <section class="content-header">

        </section>
        <section class="content">

            <!-- Map box -->
            <div class="box box-solid">
                <div class="box-header">

                    <i class="fa fa-flickr"></i>
                    <h3 class="box-title">
                        sauron 欢迎您
                    </h3>
                </div>
                <div class="box-body">
                    <div style="height: 80%; text-align: center; background-color: #f9f9f9;" id="world-map" >

                      <img src="/resources/images/e1.png" class="img-circle" style="width: 600px; height: 600px; margin-top: 2%;" />


                    </div>
                </div><!-- /.box-body-->

            </div>
            <!-- /.box -->
        </section>
    </aside>
</div>
</body>
</html>
