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
                    <c:if test="${empty urlRuleId}">新增URL配置</c:if>
                    <c:if test="${!empty urlRuleId}">修改URL配置</c:if>
                </small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> URL配置</a></li>
                <li class="active">
                    <c:if test="${empty urlRuleId}">新增</c:if>
                    <c:if test="${!empty urlRuleId}">修改</c:if>
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
                <form role="form" id="addUrlRuleForm" method="POST" action="/urlRule/addData" class="form-horizontal">
                    <div class="box-body">
                        <!--
                        <div style="font-size:16px;color:#f56954;border-bottom:2px solid #3c8dbc; margin-bottom: 5px; width: 130px;"><p>URL监控基础配置</p></div>
                        -->
                        <div class="row">
                            <div class="col-xs-6" id="findAppName">
                                <label>应用名称</label>

                                <form:select path="appName" id="appName" name="appId"
                                             cssClass="form-control selectpicker">
                                    <c:forEach items="${appName}" var="item">
                                        <form:option value="${item.id}">${item.name}</form:option>
                                    </c:forEach>
                                </form:select>
                                <input type="hidden" id="appName_id" name="appName">
                            </div>
                            <div class="form-group  col-xs-6">
                                <label>接入KEY</label><input type="text" id="monitorKey" name="monitorKey"
                                                                                       placeholder="请输入接入KEY"
                                                                                       class="form-control">
                            </div>
                        </div>

                        <div style="border-bottom:2px solid #3c8dbc; margin-bottom: 10px; margin-top: 5px; width: 100%"></div>

                        <div class="form-group row">
                            <div class="col-xs-12">
                                <label>监控地址</label>
                                <input type="text" name="monitorUrl" placeholder="请输入监控地址" id="monitorUrl"
                                       class="form-control">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-6">
                                <label>访问时间频率</label><select class="form-control" id="requestInterval"
                                                                                         name="requestInterval">
                                <option value="1">1分</option>
                                <option value="2">2分</option>
                                <option value="5">5分</option>
                                <option value="8">8分</option>
                                <option value="10">10分</option>
                            </select>
                            </div>
                            <div class="form-group col-xs-6">
                                <label>访问超时时间</label><input type="text" id="timeout"
                                                                                        placeholder="请输入访问超时时间，单位：秒"
                                                                                        class="form-control"
                                                                                        name="timeout">
                            </div>
                        </div>
                        <div class="form-group  row">
                            <div class="col-xs-6" style="margin: 20px 0px 0px 0px;">
                                <label style="width: 120px">请求方式</label>
                                <label style="width: 70px">
                                    <input type="radio" name="requestMode" value="0" class="minimal-red" <c:if test="${urlRules.requestMode == 0}">checked</c:if>/> HEAD
                                </label>
                                <label style="width: 70px">
                                    <input type="radio" name="requestMode" value="1" class="minimal-red" <c:if test="${urlRules.requestMode == 1}">checked</c:if>/> GET
                                </label>
                                <label style="width: 70px">
                                    <input type="radio" name="requestMode" value="2"  class="minimal-red" <c:if test="${urlRules.requestMode != 1 && urlRules.requestMode != 0}">checked</c:if>/> POST
                                </label>
                            </div>
                            <div class="isConfigHostwrap" style="margin: 20px 0px 0px 0px;">

                                <label style="width: 120px">是否匹配host</label>
                                <label style="width: 120px" class="isConfigHostLable">
                                    <input type="radio" value="0" name="isConfigHost" class="minimal-red isConfigHost" <c:if test="${urlRules.isConfigHost == 0}">checked</c:if>/> 匹配
                                </label>
                                <label style="width: 120px" class="isConfigHostLable">
                                    <input type="radio" value="1" name="isConfigHost" class="minimal-red isConfigHost" <c:if test="${urlRules.isConfigHost != 0}">checked</c:if> /> 不匹配
                                </label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-2">
                                <label>host IP</label><input type="text" disabled="" id="hostIp" placeholder="请输入IP"
                                                                                         class="form-control"
                                                                                         name="hostIp">
                            </div>
                            <div class="col-xs-10">
                                <label>请求内容</label><input type="text" id="param" placeholder="请输入请求内容"
                                                                                      class="form-control" name="param">
                            </div>
                        </div>

                        <div class="form-group" style="margin: 10px 0px 10px 0px;">
                            <label style="width: 120px">匹配响应内容</label>
                            <label style="width: 120px" class="isContainLabel">
                                <input type="radio" value="0" name="isContain" class="minimal-red"
                                        <c:if test="${urlRules.isContain == 0}">checked</c:if>/> 包含
                            </label>
                            <label style="width: 120px" class="isContainLabel">
                                <input type="radio" value="1" name="isContain" class="minimal-red" <c:if test="${urlRules.isContain != 0}">checked</c:if>/> 不包含
                            </label>
                        </div>
                        <div class="form-group row">
                            <div class="col-xs-12">
                                <label>匹配响应内容</label>
                                <textarea placeholder="Enter ..." rows="3" class="form-control" disabled=""
                                          name="matchContent" id="matchContent"></textarea>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-6">
                                <label>Cookies</label><input type="text" id="cookies" placeholder="请输入Cookies" class="form-control"
                                                             name="cookies">
                            </div>
                            <div class="col-xs-6">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <label>URL返回码</label>
                                        <div class="form-group" style="margin: 7px 0px 10px 0px;">
                                            <label class="isDefaultCodeLabel">
                                                <input value="0" name="isDefaultCode" type="checkbox" class="flat-red" <c:if test="${urlRules.isDefaultCode != 1}">checked</c:if>/> 默认
                                            </label>
                                        </div>
                                    </div>
                                    <div class="col-xs-3">
                                        <label> </label>
                                        <div class="form-group" style="margin: 17px 0px 10px 0px;">
                                            <label>
                                                <input disabled="" type="text" id="customCode" name="customCode" placeholder="返回码"
                                                       class="form-control">
                                            </label>
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </div>
                        <div style="border-bottom:2px solid #3c8dbc; margin-bottom: 10px; margin-top: 20px; width: 100%"></div>
                        <div class="form-group row">
                            <div class="col-xs-12">
                                <label>
                                    <input value="1"  <c:if test="${fn:contains(notifyMode, 1) || empty urlRuleId}">checked</c:if>
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
                            <div class="col-xs-8">
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
                            <div class="col-xs-4">
                                <label>接收策略</label><select class="form-control" name="template" id="template">
                                    <c:forEach items="${strategyList}" var="row">
                                        <option value="${row.id}">${row.stgyName}</option>
                                    </c:forEach>
                            </select>
                            </div>
                        </div>


                        <div class="box-footer">
                            <button class="btn btn-primary" id="submit_rule" type="submit">
                                <c:if test="${empty urlRuleId}">新增</c:if>
                                <c:if test="${!empty urlRuleId}">修改</c:if>
                            </button>
                        </div>
                </form>
            </div>
        </section><!-- /.content -->
    </aside>


</div>

<script>
    $(document).ready(function () {
        $('.isConfigHostLable').find('.iCheck-helper').on('click',function () {
            if($(this).prev().val()=="1"){
                $("#hostIp").attr("disabled",true);
            }else{
                $("#hostIp").attr("disabled",false);
            }
        })

        $(".isContainLabel").find('.iCheck-helper').on('click',function (){
            if($(this).prev().val()=="1"){
                $("#matchContent").attr("disabled",true);
            }else{
                $("#matchContent").attr("disabled",false);
            }
        })

        $(".isDefaultCodeLabel").find('.iCheck-helper').on('click',function (){
            if($("input[name=isDefaultCode]:checkbox:checked").val()=="0"){
                $("#customCode").attr("disabled",true);
            }else{
                $("#customCode").attr("disabled",false);
            }
        })


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

        $('#addUrlRuleForm').bootstrapValidator({
            message: 'This value is not valid',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
                monitorKey: {
                    validators: {
                        notEmpty: {
                            message: '不能为空'
                        },
                        stringLength: {
                            min: 20,
                            max: 20,
                            message: 'KEY长度为20'
                        },
                        regexp: {
                            regexp: /^[a-zA-Z0-9_\.]+$/,
                            message: 'KEY必须由数字和字母组成'
                        }
                    }
                },
                monitorUrl: {
                    validators: {
                        notEmpty: {
                            message: '不能为空'
                        },
                        regexp: {
                            regexp: /^(http|https):\/\/([\w-:]+\.)+[\w-:]+(\/[\w-./?%&=]*)?$/,
                            message: 'url格式不对'
                        }
                    }
                },
                timeout: {
                    validators: {
                        notEmpty: {
                            message: '不能为空'
                        },
                        regexp: {
                            regexp: /^[0-9]+$/,
                            message: '超时时间只能由数字组成'
                        }
                    }
                }
            }
        }).on('success.form.bv',function(e){
            e.preventDefault();
            var requestMode = $("input[name=requestMode]:checked").val();
            var isConfigHost = $("input[name=isConfigHost]:checked").val();
            var isContain = $("input[name=isContain]:checked").val();
            var isDefaultCode = $("input[name=isDefaultCode]:checkbox:checked").val();
            var contactType = '';
            $("input[name=contactType]:checkbox:checked").each(function(i){
                if(i>0)
                    contactType += ";";
                contactType += $(this).val();
            })
            $.post("${urlRuleId}"!=""?"/urlRule/update/${urlRuleId}":"/urlRule/addData",
                    {"appId":$("#appName").val(),"appName":$("#appName_id").val(),"monitorKey":$("#monitorKey").val(),"monitorUrl":$("#monitorUrl").val(),
                        "requestInterval":$("#requestInterval").val(),"timeout":$("#timeout").val(),"requestMode":requestMode,"isConfigHost":isConfigHost,
                        "hostIp":$("#hostIp").val(),"param":$("#param").val(),"isContain":isContain,"matchContent":$("#matchContent").val(),
                        "cookies":$("#cookies").val(),"isDefaultCode":isDefaultCode=="0"?"0":"1","customCode":isDefaultCode=="0"?"":$("#customCode").val(),"contactType":contactType,
                        "contact":$("#contact_ids").val(),"template":$("#template").val()},
                    function (data) {
                        if (data.code == 0) {
                            alert("<c:if test="${empty urlRuleId}">新增成功！</c:if><c:if test="${!empty urlRuleId}">修改成功！</c:if>");
                            document.forms[0].action = "/urlRule/list/1/10";
                            document.forms[0].submit();
                        } else {
                            alert(data.msg);
                        }
                    });

        });

        if("${urlRuleId}"!=""){
            $.getJSON("/urlRule/queryModify",{id:"${urlRuleId}"},function (data) {
                if (data.code == 0) {
                    $("#appName").val(data.attach.appId);
                    $("#monitorKey").val(data.attach.monitorKey);
                    $("#monitorUrl").val(data.attach.monitorUrl);
                    $("#requestInterval").val(data.attach.requestInterval);
                    $("#timeout").val(data.attach.timeout);
//                    $("input[name=requestMode]").attr("checked",false);
//                    $("input[name=requestMode][value="+data.attach.requestMode+"]").attr("checked",true);
//                    $("input[name=isConfigHost]").attr("checked",false);
//                    $("input[name=isConfigHost][value="+data.attach.isConfigHost+"]").attr("checked",true);
                    $("#hostIp").val(data.attach.hostIp)
                    $("#param").val(data.attach.param)
//                    $("input[name=isContain]").attr("checked",false);
//                    $("input[name=isContain][value="+data.attach.isContain+"]").attr("checked",true);
                    $("#matchContent").val(data.attach.matchContent)
                    $("#cookies").val(data.attach.cookies)
//                    $("input[name=isDefaultCode]:checkbox").attr("checked",false);
//                    $("input[name=isDefaultCode]:checkbox[value="+data.attach.isDefaultCode+"]").attr("checked",true);

                    $("#customCode").val(data.attach.customCode)
                    $("#contactType").val(data.attach.contactType)

//                    $("#contact").val(data.attach.contact)
                    $("#template").val(data.attach.template)


                    if(data.attach.isConfigHost=="1"){
                        $("#hostIp").attr("disabled",true);
                    }else{
                        $("#hostIp").attr("disabled",false);
                    }
                    if(data.attach.isContain=="1"){
                        $("#matchContent").attr("disabled",true);
                    }else{
                        $("#matchContent").attr("disabled",false);
                    }
                    if(data.attach.isDefaultCode=="0"){
                        $("#customCode").attr("disabled",true);
                    }else{
                        $("#customCode").attr("disabled",false);
                    }



                } else {
                    alert(data.msg);
                }
            })
        }

    });



</script>
</body>
</html>
