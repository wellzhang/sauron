<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>


    <link href="/resources/css/AdminLTE.css" rel="stylesheet" type="text/css">
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link href="/resources/css/bootstrap.min.css" rel="stylesheet">

    <link href="/resources/css/font-awesome.min.css" rel="stylesheet"/>
    <!-- Ionicons -->
    <link href="/resources/css/ionicons.min.css" rel="stylesheet"/>

    <link href="/resources/css/iCheck/all.css" rel="stylesheet"/>

    <link rel="stylesheet" href="/resources/css/bootstrapValidator.min.css"/>

    <link rel="stylesheet" href="/resources/css/bootstrap-dropselect.css">

    <link rel="stylesheet" href="/resources/css/warm.css">

    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/resources/js/jquery-2.2.0.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>

    <script src="/resources/js/app.js"></script>

    <!-- Include the plugin's CSS and JS: -->
    <script type="text/javascript" src="/resources/js/bootstrap-multiselect.js"></script>
    <link rel="stylesheet" href="/resources/css/bootstrap-multiselect.css" type="text/css"/>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/resources/css/bootstrap-select.min.css">

    <!-- Latest compiled and minified JavaScript -->
    <script src="/resources/js/bootstrap-select.min.js"></script>

    <script type="text/javascript" src="/resources/js/bootstrapValidator.min.js"></script>

    <script src="/resources/js/bootstrap-dropselect.js"></script>

</head>
<body class="skin-blue">
<%@include file="../common/head.jsp" %>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <%@include file="../common/leftMenu.jsp" %>
    <aside class="right-side">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                <small>
                    <c:if test="${empty dubboRuleId}">新增DUBBO配置</c:if>
                    <c:if test="${!empty dubboRuleId}">修改DUBBO配置</c:if>
                </small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> DUBBO配置</a></li>
                <li class="active">
                    <c:if test="${empty dubboRuleId}">新增</c:if>
                    <c:if test="${!empty dubboRuleId}">修改</c:if>
                </li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="box box-primary">
                <div class="box-header">
                    <!--
                    <h3 class="box-title"></h3>
                    -->
                </div><!-- /.box-header -->
                <!-- form start -->
                <form role="form" id="addDubboRuleForm" method="POST" action="/dubboRule/addData" class="form-horizontal">
                    <div class="box-body">
                        <!--
                        <div style="font-size:16px;color:#f56954;border-bottom:2px solid #3c8dbc; margin-bottom: 5px; width: 130px;"><p>URL监控基础配置</p></div>
                        -->
                        <div class="row">
                            <div class="col-xs-6">
                                <label>应用名称</label>

                                <form:select path="appName" id="appId" name="appId"
                                             cssClass="form-control selectpicker">
                                    <c:forEach items="${appName}" var="item">
                                        <form:option value="${item.id}">${item.name}</form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xs-6">
                                <label>ZK IP</label>
                                <form:select path="zookeeperIpses" id="zookeeperIpsId" name="zookeeperIpsId"
                                             cssClass="form-control selectpicker">
                                    <c:forEach items="${zookeeperIpses}" var="item">
                                        <form:option value="${item.id}">${item.name}</form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                        </div>

                        <div class="form-group row">
                            <div class="col-xs-6">
                                <label>application</label>
                                <input type="text" name="applicationName" id="applicationName" placeholder="请输入application名称"
                                       class="form-control">
                            </div>
                        </div>

                        <div style="border-bottom:2px solid #3c8dbc; margin-bottom: 10px; margin-top: 20px; width: 100%"></div>
                        <div class="form-group row">
                            <div class="col-xs-12">
                                <label>
                                    <input value="1"  <c:if test="${fn:contains(notifyMode, 1) || empty dubboRuleId}">checked</c:if>
                                           name="contactType" type="checkbox" class="flat-red"/> 邮件
                                </label>
                                <label>
                                    <input value="2"
                                           <c:if test="${fn:contains(notifyMode, 2)}">checked</c:if>
                                           name="contactType" type="checkbox" class="flat-red"/> 短信
                                </label>
                                <!--
                                <label>
                                    <input value="3" name="contactType" type="checkbox" class="flat-red"/> 邮件组
                                </label>
                                -->
                            </div>
                        </div>


                        <div class="row">
                            <div class="col-xs-6">
                                <label>接收人</label>
                                <div class="input-group input-group-sm">
                                    <input type="text" disabled="" value="${contactNames}" class="form-control" name="contact_name" id="contact_id">
                                    <input type="hidden" value="${contractIds}" name="contact" id="contact_ids">
                                        <span class="input-group-btn">
                                            <div class="navbar-header dropdown">

                                             <button data-toggle="dropdown" id="dropselect-label" type="button"
                                                     class="btn btn-info btn-flat">选择接收人</button>
                                              <ul class="dropdown-menu" id="dropselect" role="menu">
                                                <li class="dropdown-header">可选多个接收者</li>
                                                   <c:forEach items="${contactsList}" var="row">
                                                       <li><a href="#" tmpData="${row.id}">${row.name}</a></li>
                                                   </c:forEach>
                                              </ul>
                                            </div>

                                        </span>
                                </div>
                            </div>
                        </div>



                        <div class="row">
                            <div class="col-xs-6">
                                <label>接收策略</label><select class="form-control" name="template" id="template">
                                <c:forEach items="${strategyList}" var="row">
                                    <option value="${row.id}">${row.stgyName}</option>
                                </c:forEach>
                            </select>
                            </div>
                        </div>


                        <div class="box-footer">
                            <button class="btn btn-primary" id="submit_rule" type="submit">
                                <c:if test="${empty dubboRuleId}">新增</c:if>
                                <c:if test="${!empty dubboRuleId}">修改</c:if>
                            </button>
                        </div>
                </form>
            </div>
        </section><!-- /.content -->
    </aside>


</div>

<script>
    $(document).ready(function () {


        var contactTmp = $("#contact_id").val();
        var contactIds = $("#contact_ids").val();
        $contact = $('#dropselect').dropselect({
            onchange: function (e) {
                if (typeof e.selectedItem != 'undefined') {
                    var item = e.selectedItem;
                    if (item.value == 'destroy') {
                        e.destroy();
                    } else {
                        contactTmp == ''?contactTmp+=item.value:contactTmp+=";"+item.value;
                        contactIds == ''?contactIds+=$(item["$element"]).html().split("=")[2].split("\"")[1]:
                                contactIds+=";"+$(item["$element"]).html().split("=")[2].split("\"")[1];
                        $('#contact_id').val(contactTmp);
                        $('#contact_ids').val(contactIds);
                        //alert($('#contact_ids').val());
                    }
                }
                else {
                    $('#contact_id').val('');
                    $('#contact_ids').val('');
                    contactTmp = '';
                    contactIds = '';
                }
            },
            filter: true
        });

        $('#addDubboRuleForm').bootstrapValidator({
            message: 'This value is not valid',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
                applicationName: {
                    message: 'The applicationName is not valid',
                    validators: {
                        notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            min: 6,
                            max: 50,
                            message: 'applicationName长度为6至50个字符'
                        }
//                        ,
//                        regexp: {
//                            regexp: /^[a-zA-Z0-9_\.]+$/,
//                            message: 'applicationName只能由数字和字母组成'
//                        }
                    }
                }
            }
        }).on('success.form.bv',function(e){
            e.preventDefault();
            var contactType = '';
            $("input[name=contactType]:checkbox:checked").each(function(i){
                if(i>0)
                    contactType += ";";
                contactType += $(this).val();
            })
            $.post("${dubboRuleId}"!=""?"/dubboRule/update/${dubboRuleId}":"/dubboRule/addData",
                    {"appId":$("#appId").val(),"zookeeperIpsId":$("#zookeeperIpsId").val(),"applicationName":$("#applicationName").val(),
                        "contactType":contactType==""?"0":contactType,
                        "contact":$("#contact_ids").val(),"template":$("#template").val()},
                    function (data) {
                        if (data.code == 0) {
                            alert("<c:if test="${empty dubboRuleId}">新增成功！</c:if><c:if test="${!empty dubboRuleId}">修改成功！</c:if>");
                            document.forms[0].action = "/dubboRule/list/1/10";
                            document.forms[0].submit();
                        } else {
                            alert(data.msg);
                        }
                    });

        });

        if("${dubboRuleId}"!=""){
            $.getJSON("/dubboRule/queryModify",{id:"${dubboRuleId}"},function (data) {
                if (data.code == 0) {
                    $("#appId").val(data.attach.appId);
                    $("#zookeeperIpsId").val(data.attach.zookeeperIpsId);
                    $("#applicationName").val(data.attach.applicationName);
                    $("#template").val(data.attach.template);
                } else {
                    alert(data.msg);
                }
            })
        }

    });



</script>
</body>
</html>
