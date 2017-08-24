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

        <!-- Main content -->
        <section class="content">

       <div class="box box-primary">

           <div class="box-header">
               <div>
                   <p><b style="margin-left: 20px;">URL地址:</b> ${urlRules.monitorUrl}</p>
                   <p>
                   <b style="margin-left: 20px;">应用名称:</b> ${urlRules.appName} <b style="margin-left: 200px;">接入KEY:</b> ${urlRules.monitorKey}
                   <b style="margin-left: 200px;">总采样次数：</b> ${urlMonitor.totalTimes}
                   <b style="margin-left: 200px;">失败次数：</b> ${urlMonitor.failTimes}
                   </p>
               </div>
           </div>

            </div>

            <div class="table-responsive">
                <div class="box-header">
                </div>
                <div class="box-body">
                    <table class="table">
                        <tbody><tr>
                            <th>发生时间</th>
                            <th>采样结果</th>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="iterm">
                            <tr>
                                <td width="150px">
                                    <fmt:formatDate value="${iterm.createdTime}"  type="both" />
                            </td>
                                <td>${iterm.result}</td>
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


</body>
</html>
