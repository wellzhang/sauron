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
    <script src="/resources/js/bootstrap.autocomplete.js" type="text/javascript" />
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
                <small>监控点配置</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i>监控点配置</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">
            <div class="box box-primary">
                <p>
                <form id="searchForm"  action="/mp/list/1/10" method="POST" class="form-inline" style="margin-left: 15px;">
                <div class="row" style="margin-left: -15px;">
                    <div class="col-xs-3">
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
                            <!--
                        <%--<input type="text" value="${appName}" name="appName"  placeholder="请输入应用名称(必填)" class="form-control">--%>
                        -->
                        </div>
                    </div>
                    <div class="col-xs-4" style="margin-bottom: 8px;">
                    <div class="input-group">
                        <div class="input-group-addon">
                            <i class="fa"> 查询或新增监控点</i>
                        </div>
                        <input  AUTOCOMPLETE="OFF" type="text" value="${methodName}" name="methodName" id="methodName" placeholder="如：（com.fengjr.trade.controller.v2.TopRankController:top(int,int)）" class="form-control">
                        <script>
                            $('#methodName').autocomplete({
                                source:function(query,process){
                                    //var matchCount = this.options.items;//返回结果集最大数量
                                    $.post("/mp/listJson/1/1000",{"methodName":query,"appName":$("#appNameId").val()},function(respData){
                                        return process(respData["attach"]);
                                    });
                                },
                                formatItem:function(item){
                                    return item["methodName"];
                                }
                                ,
                                setValue:function(item){
                                    return {'data-value':item["methodName"]};
                                }
                            });
                        </script>
                    </div>
                </div>
                    <div>
                        <div  class="input-group col-xs-2" style="margin-top: 8px;">
                            <button type="submit" class="fa fa-search-minus btn-sm queryButton">查询</button>
                            <button type="button" onclick="add();" class="fa fa-search-minus btn-sm fa-plus-square-o">新增</button>
                        </div>
                    </div>

                </div>
                </form>
                </p>
            </div>
            <div class="table-responsive">
                <div class="box-header">

                </div>
                <div class="box-body">
                    <table class="table table-bordered">
                        <tbody><tr>
                            <th>序号</th>
                            <th>方法名称</th>
                            <th>操作</th>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="iterm">
                        <tr>
                            <td>
                                ${iterm.sequence}
                            </td>
                            <td>${iterm.methodName}</td>
                            <td>
                                <button id="m_${iterm.sequence}" class="btn btn-flat btn-xs" onclick="modify(this,${iterm.sequence})">修改</button>
                                <button id="s_${iterm.sequence}" style="display: none;" class="btn btn-flat btn-xs" onclick="enterModify(this,${iterm.sequence})">提交</button>
                                <button onclick="deleteMethodName(this)" class="btn btn-flat btn-xs">删除</button>
                            </td>
                        </tr>
                        </c:forEach>
                        </tbody></table>
                </div>
                <div class="box-footer clearfix">
            </div>


            </div>

        </section>
    </aside>
</div>
<script>

    $(function () {
        $("body").attr("class","pace-done skin-black");
        $("#appNameId").val("${appName}");
    });

    add = function () {
        if($("#methodName").val()==""){
            alert("不能为空");
            return
        }else{
            $.post("/mp/cmd",{"methodName":$("#methodName").val(),"appName":$("#appNameId").val(),"option":"CREATE"},function (data) {
                if(data.code == "0"){
                    alert("success");
                    location.href = "/mp/list/1/10?appName="+$("#appNameId").val();
                }
            });
        }
    }

    modify = function (obj,index) {
        var lastVlue = $(obj).parent().siblings(":last").html();
        $(obj).parent().siblings(":last").html('<input class="form-control" id="c_'+index+'" type="text" value="'+lastVlue+'"/>' +
                '<input type="hidden" id="l_'+index+'" value="'+lastVlue+'" />');
        $(obj).hide();
        $("#s_"+index).show();
    }

    enterModify = function (obj,index) {
        var methodName = $("#c_"+index).val();
        var oldMethName = $("#l_"+index).val();
        $.post("/mp/cmd",{"methodName":methodName,"oldMethName":oldMethName,"appName":$("#appNameId").val(),"option":"UPDATE"},function (data) {
            if(data.code == "0"){
                alert("success");
                $(obj).parent().siblings(":last").html(methodName);
                $("#m_"+index).show();
                $(obj).hide();
                location.href = "/mp/list/1/10?appName="+$("#appNameId").val();
            }
        });
    }

    deleteMethodName = function (obj) {
        var methodName = $(obj).parent().siblings(":last").html();
        
        $.post("/mp/cmd",{"methodName":methodName,"appName":$("#appNameId").val()},function (data) {
            if(data.code == "0"){
                alert("success");
                location.href = "/mp/list/1/10?appName="+$("#appNameId").val();
            }
        });
    }

</script>
</body>
</html>
