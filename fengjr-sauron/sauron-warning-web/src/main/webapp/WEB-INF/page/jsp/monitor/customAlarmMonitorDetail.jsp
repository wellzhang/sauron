<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>


    <link rel="stylesheet" href="/resources/css/definedBootstrap.css">


    <!-- Ionicons -->
    <link href="/resources/css/ionicons.min.css" rel="stylesheet" />
    <link href="/resources/css/iCheck/all.css" rel="stylesheet"/>
    <link rel="stylesheet" href="/resources/css/warm.css">
    <script src="/resources/js/jquery.min.js"></script>




</head>


<body class="skin-blue">
        <section class="content">
            <div class="table-responsive">

                <div class="box-body" >
                    <textarea class="form-control" id="jsonView" style="width: 100%; height: 95%" readonly></textarea>
                </div>

            </div>

        </section>


</body>


<script>

    $(function () {
        $.getJSON("/monitor/getCustomAlarmMonitorDetailDto/${AppName}/${Traceid}",
                {},function (data) {
                    $("#jsonView").html(JSON.stringify(eval(data.treeData), null, 4));

                })
    })

</script>

</html>
