<%--
  Created by IntelliJ IDEA.
  User: Liuyb
  Date: 2015/12/18
  Time: 10:42
  To change this template use File | Settings | File Templates.
--%>
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


        <section class="content-header">
            <h1>
                <small>
                    <c:if test="${empty ruleId}">新增自定义配置</c:if>
                    <c:if test="${!empty ruleId}">修改自定义配置</c:if>
                </small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 自定义配置</a></li>
                <li class="active">
                    <c:if test="${empty ruleId}">新增</c:if>
                    <c:if test="${!empty ruleId}">修改</c:if>
                </li>
            </ol>
        </section>


        <!-- Main content -->



        <section class="content">
            <div class="box box-primary">
                <form role="form" id="addUrlRuleForm" method="POST" action="/urlRule/addData" class="form-horizontal">


                    <div class="form-group" style="margin-top: 20px;">
                        <label for="appName" class="col-sm-1 control-label">监控类型：</label>

                        <div class="col-sm-5">
                            <select id="type" class="form-control">
                                <option value="2">自定义KEY</option>
                                <option value="3">自定义块监控</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="appName" class="col-sm-1 control-label">应用名：</label>

                        <div class="col-sm-5">
                            <form:select path="appName" id="appName" name="appId"
                                         cssClass="form-control selectpicker">
                                <c:forEach items="${appName}" var="item">
                                    <form:option value="${item.id}">${item.name}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>



                    <div class="form-group">
                        <label for="monitorKey" class="col-sm-1 control-label">自定义KEY：</label>


                        <div class="col-sm-5">
                            <input type="text" class="form-control" id="monitorKey">
                        </div>

                    </div>



                    <div class="form-group">
                        <div class="col-sm-11">
                            <label for="metricOpt" class="col-sm-1 control-label">规则条款：</label>
                            <div class="col-sm-1">
                                <select id="metricOpt" class="form-control">
                                    <c:forEach items="${metricOptList}" var="row">
                                        <option value="${row.id}">${row.metricName}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-sm-1">
                                <select id="operator" class="form-control">
                                    <option value=">"> ></option>
                                    <option value="="> =</option>
                                    <option value="<"> <</option>
                                </select>
                            </div>

                            <div class="col-sm-1">
                                <input id="varible" class="form-control" type="text"/>
                            </div>

                            <div class="col-sm-1">
                                <input id="addClause" class="btn btn-default" type="button" value="添加规则条款"
                                       onclick="insertRowInTable();"/>
                            </div>

                        </div>
                    </div>

                    <div class="form-group">
                        <label for="metricOpt" class="col-sm-1 control-label"></label>
                        <div class="col-sm-7">
                            <table id="clauseList" class="table">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>指标ID</th>
                                    <th>指标名称</th>
                                    <th>条件</th>
                                    <th>数值</th>
                                    <th>操作</th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>





                    <div class="form-group">
                        <label class="col-sm-1 control-label">
                            发送方式：
                        </label>
                        <div class="col-sm-5">
                            <label class="col-sm-3 control-label">
                                <input value="1"  <c:if test="${fn:contains(notifyMode, 1) || empty ruleId}">checked</c:if>
                                       name="contactType" type="checkbox" class="flat-red"/> 邮件
                            </label>
                            <label class="col-sm-3 control-label">
                                <input value="2"
                                       <c:if test="${fn:contains(notifyMode, 2)}">checked</c:if>
                                       name="contactType" type="checkbox" class="flat-red"/> 短信
                            </label>
                        </div>
                        <!--
                        <label>
                            <input value="3" name="contactType" type="checkbox" class="flat-red"/> 邮件组
                        </label>
                        -->
                    </div>


                    <div class="form-group">
                        <label for="contacts" class="col-sm-1 control-label">接收人：</label>

                        <div class="col-sm-5">
                            <select id="contacts" multiple="multiple" class="multiselect form-control"
                            >
                                <c:forEach items="${contactsList}" var="row">
                                    <option value="${row.id}">${row.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="strategy" class="col-sm-1 control-label">接收策略：</label>

                        <div class="col-sm-5">
                            <select id="strategy" class="selectpicker form-control">
                                <c:forEach items="${strategyList}" var="row">
                                    <option value="${row.id}">${row.stgyName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="box-footer" style="margin-left: 40px;">
                        <input class="btn btn-primary " type="button" id="submit_rule" value="提交"/>
                    </div>

                </form>
            </div>
        </section>













    </aside>
</div>

<script>
    $(document).ready(function () {
        $("#addClause").hide();
        $("#type").change(function () {
            if($(this).val()=='2'){
                $("#addClause").hide();
            }else{
                $("#addClause").show();
            }
        });
        $('#contacts').multiselect({
            allSelectedText: '已全选所有联系人'
        });
        <c:if test="${operType == 'modify'}">
        $('#contacts').multiselect('select', ${receivers});
        </c:if>
        $('.selectpicker').selectpicker();
        if('${rule.type}' == 2){
            $("#addClause").hide();
        }

    });

    $('#metricOpt').change(function () {
        if ($("#metricOpt option:selected").text() == "exception") {
            $('#operator').selectpicker('val', '=');
            $('#operator').attr("disabled", true);
            $('#varible').val("0");
        } else {
            $("#operator option:contains('=')").removeAttr("selected");
            $('#operator').attr("disabled", false);
            $('#operator').selectpicker('deselectAll');
            $('#varible').val("");
        }
    });

//    function changeApp() {
//        var appName = $('#appName option:selected').val();
//        $.getJSON("/rule/updateSelect", {
//            appName: appName
//        }, function (data) {
//            var attach = data.attach;
//            if (data.code == 0) {
//                $("#hostName").empty();
//                $("#hostName").append($("<option></option>")
//                        .attr("value", 'ALL')
//                        .text('全部主机'));
//
//                $("#methodName").empty();
//                $.each(attach.hostName, function (k, v) {
////                   $('#hostName').append($("<option/>"), {
////                       value: k,
////                       text: v
////                   });
//                    $('#hostName')
//                            .append($("<option></option>")
//                                    .attr("value", v)
//                                    .text(v));
//                });
//                $.each(attach.methodName, function (k, v) {
//                    $('#methodName')
//                            .append($("<option></option>")
//                                    .attr("value", v)
//                                    .text(v));
//                });
//                $('.selectpicker').selectpicker('refresh');
//                ;
//
//            } else {
//                alert(data.msg + ':' + data.exceptionMsg);
//                return;
//            }
//        });
//    }
    <!-- 新增操作时-->
    <c:if test="${operType == 'add'}">
//    changeApp();
    //提交
    $("#submit_rule").click(function () {

        var appName = $("#appName option:selected").val();
        var appId = $("#appName").val();
        var strategyId = $("#strategy option:selected").val();
        var monitorKey = $("#monitorKey").val();
        var type = $("#type").val();

        var contactType = '';
        $("input[name=contactType]:checkbox:checked").each(function(i){
            if(i>0)
                contactType += ";";
            contactType += $(this).val();
        });

        var cellValues = [];
        $("#clauseList").find("td").each(function () {
            cellValues.push($(this).text());
        });

        var mp = "";

        for (var i = 0; i < cellValues.length; i++) {
            if (cellValues[i] == "") {
                cellValues[i] = "_";
            }
            mp += (cellValues[i] + ",");
        }

        var contact_string = "";
        contact_string = $("#contacts option:selected").map(function () {
            return $(this).val();
        }).get().join(",")

        $.getJSON('/rule/CreateRule', {
            appName: appName,
            metricsOpt: mp,
            contact: contact_string,
            strategyId: strategyId,
            contactType:contactType,
            appId:appId,
            type:type,
            monitorKey:monitorKey
        }, function (data) {
            if (data.code == 0) {
                alert(data.msg);
                location.href = "/rule/list/1/10/2";
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
        });

    });

    </c:if>
    <!-- 修改操作时-->
    <c:if test="${operType == 'modify'}">

    var appNameOptions = document.all.appName;
    for (var i = 0; i < appNameOptions.length; i++) {
        if (appNameOptions.options[i].value == '${rule.appName}') {
            appNameOptions.options[i].selected = true;
            break;
        }
    }


    $("#type").val('${rule.type}');
    $("#monitorKey").val('${rule.monitorKey}');

    if('${rule.type}' == 2){
        $("#addClause").hide();
    }

    var metricOptions = document.all.metricOpt;
    var operatorOptions = document.all.operator;
    <c:forEach items="${clauses}" var="row">
    for (var i = 0; i < metricOptions.length; i++) {
        if (metricOptions.options[i].value == '${row.metricOptId}') {
            metricOptions.options[i].selected = true;
            break;
        }
    }
    for (var i = 0; i < operatorOptions.length; i++) {
        if (operatorOptions.options[i].value == '${row.operator}') {
            operatorOptions.options[i].selected = true;
            break;
        }
    }
    var strategyOptions = document.all.strategy;

    for (var i = 0; i < strategyOptions.length; i++) {
        if (strategyOptions.options[i].value == '${rule.template}') {
            strategyOptions.options[i].selected = true;
            break;
        }
    }
    $("#appName").val('${rule.appId}');


    document.all.varible.value = ${row.varible};
    insertRowInTable();
    </c:forEach>

    //提交
    $("#submit_rule").click(function () {
        var ruleId = ${rule.id};
        var appName = $("#appName option:selected").val();
        var strategyId = $("#strategy option:selected").val();
        var appId = $("#appName").val();
        var monitorKey = $("#monitorKey").val();
        var type = $("#type").val();


        var contactType = '';
        $("input[name=contactType]:checkbox:checked").each(function(i){
            if(i>0)
                contactType += ";";
            contactType += $(this).val();
        });

        var cellValues = [];
        $("#clauseList").find("td").each(function () {
            cellValues.push($(this).text());
        });

        var mp = "";

        for (var i = 0; i < cellValues.length; i++) {
            if (cellValues[i] == "") {
                cellValues[i] = "_";
            }
            mp += (cellValues[i] + ",");
        }

        var contact_string = "";
        contact_string = $("#contacts option:selected").map(function () {
            return $(this).val();
        }).get().join(",")

        $.getJSON('/rule/modifyRule', {
            ruleId: ruleId,
            appName: appName,
            metricsOpt: mp,
            contact: contact_string,
            strategyId: strategyId,
            contactType:contactType,
            appId:appId,
            type:type,
            monitorKey:monitorKey
        }, function (data) {
            if (data.code == 0) {
                alert(data.msg);
                location.href = "/rule/list/1/10/2";
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
        });

    });

    </c:if>


    $(function () {
        $('#exportClause').click(function () {
            var html = '';
            $('#clauseList').find('tr').each(function () {
                $(this).find('td').each(function () {
                    html += $(this).text() + ',';
                })
                html += '|';
//        alert(html);
//        return false;
            })
            alert(html);
        })
    })
    function deleteRow(r) {
        var i = r.parentNode.parentNode.rowIndex
        document.getElementById('clauseList').deleteRow(i)
    }
    function insertRowInTable() {
        var table = document.getElementById("clauseList");
        var newRow = table.insertRow(-1);
        newRow.insertCell().innerHTML = newRow.rowIndex;
        newRow.insertCell().innerHTML = $("#metricOpt option:selected").val();
        newRow.insertCell().innerHTML = $("#metricOpt option:selected").text();
        newRow.insertCell().innerHTML = $("#operator option:selected").val();
        newRow.insertCell().innerHTML = $("#varible").val();
        newRow.insertCell(-1).innerHTML = "<input type='button' value='删除' onclick='deleteRow(this)'>";
    }
    //选择一项
    $("#addOneBtn").click(function () {
        $("#contacts option:selected").clone().appendTo("#receivers");
        $("#contacts option:selected").remove();
    });

    //选择全部
    $("#addAllBtn").click(function () {
        $("#contacts option").clone().appendTo("#receivers");
        $("#contacts option").remove();
    });

    //移除一项
    $("#removeOneBtn").click(function () {
        $("#receivers option:selected").clone().appendTo("#contacts");
        $("#receivers option:selected").remove();
    });

    //移除全部
    $("#removeAllBtn").click(function () {
        $("#receivers option").clone().appendTo("#contacts");
        $("#receivers option").remove();
    });

</script>
</body>
</html>
