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

    <link href="/resources/css/AdminLTE.css" rel="stylesheet" type="text/css">
    <!-- 新 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet" href="/resources/css/bootstrap.min.css">

    <!-- 可选的Bootstrap主题文件（一般不用引入） -->
    <link rel="stylesheet" href="/resources/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="/resources/css/bootstrap-clockpicker.min.css">
    <link href="/resources/css/ionicons.min.css" rel="stylesheet" />
    <link href="/resources/css/font-awesome.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="/resources/css/warm.css">

    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/resources/js/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>

    <script src="/resources/js/app.js"></script>

    <script src="/resources/js/jquery.twbsPagination.js"></script>
    <%--<link href="/resources/css/dashboard.css" rel="stylesheet">--%>

    <script src="/resources/js/bootstrap-checkbox.js"></script>
    <script src="/resources/js/bootstrap-clockpicker.min.js"></script>



</head>

<body class="skin-blue">
<%@include file="../common/head.jsp" %>
<div class="wrapper row-offcanvas row-offcanvas-left">
    <%@include file="../common/leftMenu.jsp" %>
    <aside class="right-side">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                <small>策略</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> 策略</a></li>
                <li class="active">列表</li>
            </ol>
        </section>
        <!-- Main content -->
        <section class="content">

            <div class="box box-primary">
                <div class="row" style="margin-top: 15px;">
                <form id="searchForm"  action="/strategy/list/1/10" method="POST" class="form-inline">
                    <div class="row">
                        <div class="col-xs-3">
                            <div class="input-group">
                                <div class="input-group-addon">
                                    ID
                                </div>
                                <input type="text" class="form-control" aria-describedby="basic-addon1" name="id"/>
                            </div>
                        </div>
                        <div class="col-xs-3">
                            <div class="input-group">
                                <div class="input-group-addon">
                                    名称
                                </div>
                                <input type="text" class="form-control" aria-describedby="basic-addon1" name="stgyName"/>
                            </div>
                        </div>
                        <div class="col-xs-1">
                            <input type="submit" class="btn btn-primary" value="查询">
                        </div>
                    </div>
                </form>
               </div>
            </div>

            <div class="row">
                <p>
                    <a class="btn btn-social-icon btn-linkedin" data-toggle="modal"
                       data-target="#maintainstrategyModal" data-title="新增报警策略" data-type='add'
                       data-button="新增"
                       onclick="add(this);"
                    ><i class="fa fa-fw fa-plus-square-o"></i></a>
                </p>
            </div>
            <div class="table-responsive">
                    <table id="resultTab" class="table">
                        <tr>
                            <td>序号</td>
                            <td>策略名称</td>
                            <td>状态</td>
                            <td>操作</td>
                        </tr>
                        <c:forEach items="${pageData.dataList}" var="row">
                            <tr>
                                <td>${row.id}</td>
                                <td>${row.stgyName}</td>
                                <td>
                                    <c:if test="${row.status == 0}">
                                        正常
                                    </c:if>
                                    <c:if test="${row.status != 0}">
                                        停用
                                    </c:if>
                                </td>
                                <td>
                                    <div class="btn-group" role="group" aria-label="...">
                                        <a class="btn btn-warning btn-flat btn-xs" id="modifyBtn" data-toggle="modal"
                                           data-target="#maintainstrategyModal" data-title="修改报警策略"
                                           onclick="modify(${row.id},this)" data-button="修改" data-type="mod">编辑</a>
                                        <a class="btn btn-warning btn-flat btn-xs" onclick="del(${row.id},this)">删除</a>
                                    </div>
                                </td>

                            </tr>
                        </c:forEach>
                    </table>
                    <div class="box-footer clearfix">
                        <nav>
                            <ul class="pagination pagination-sm no-margin pull-right" id="paginations">
                            </ul>
                        </nav>
                    </div>

            </div>

            <div class="modal fade" id="maintainstrategyModal" tabindex="-1" role="dialog"
                 aria-labelledby="exampleModalLabel">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                    aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="exampleModalLabel">报警策略</h4>
                        </div>
                        <div class="modal-body">
                            <form>
                                <input id="strategy-id" hidden="true"/>

                                <div class="input-group">
                                    <span class="input-group-addon">策略名称：</span>
                                    <input type="text" class="form-control" id="strategy-name">
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">发送间隔：</span>
                                    <input type="text" class="form-control" id="strategy-mininterval">
                                    <span class="input-group-addon">分钟</span>
                                </div>
                                <div class="input-group">

                                    <span class="input-group-addon">发送频率：在</span>
                                    <input type="text" class="form-control" id="strategy-warncountwithtime">
                                    <span class="input-group-addon">分钟内，累计达到</span>
                                    <input type="text" class="form-control" id="strategy-warnmaxcount">
                                    <span class="input-group-addon">次时发送</span>
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">发送次数：在</span>
                                    <input type="text" class="form-control" id="strategy-bakChar">
                                    <span class="input-group-addon">分钟内，最多发送</span>
                                    <input type="text" class="form-control" id="strategy-bakNum">
                                    <span class="input-group-addon">条</span>
                                </div>
                                <div class="input-group">
                                    &nbsp;
                                    <table>
                                        <tr>
                                            <td><span class="label label-default">周一</span></td>
                                            <td><span class="label label-default">周二</span></td>
                                            <td><span class="label label-default">周三</span></td>

                                        </tr>
                                        <tr>
                                            <td><input id="strategy-Monday" name="days" type="checkbox" data-value="1"
                                                       checked data-reverse data-style="btn-group-sm"/></td>
                                            <td><input id="strategy-Tuesday" name="days" type="checkbox" data-value="2"
                                                       checked data-reverse data-style="btn-group-sm"/></td>
                                            <td><input id="strategy-Wednesday" name="days" type="checkbox"
                                                       data-value="3" checked data-reverse data-style="btn-group-sm"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><span class="label label-default">周四</span></td>
                                            <td><span class="label label-default">周五</span></td>
                                            <td><span class="label label-default">周六</span></td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <input id="strategy-Thursday" name="days" type="checkbox" data-value="4"
                                                       checked data-reverse data-style="btn-group-sm"/>
                                            </td>
                                            <td>
                                                <input id="strategy-Friday" name="days" type="checkbox" data-value="5"
                                                       checked data-reverse data-style="btn-group-sm"/>
                                            </td>
                                            <td>
                                                <input id="strategy-Saturday" name="days" type="checkbox" data-value="6"
                                                       checked data-reverse data-style="btn-group-sm"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><span class="label label-default">周日</span></td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <input id="strategy-Sunday" name="days" type="checkbox" data-value="0"
                                                       checked data-reverse data-style="btn-group-sm"/>
                                            </td>
                                        </tr>

                                    </table>
                                </div>
                                <div class="input-group clockpicker" onclick="setIndex();">
                                    <span class="input-group-addon">报警开始时间：</span>
                                    <input type="text" class="form-control" id="strategy-startTimeInDay" value="00:00"
                                           data-default="00:00"/>
                                </div>
                                <div class="input-group clockpicker" onclick="setIndex();">
                                    <span class="input-group-addon">报警结束时间：</span>
                                    <input type="text" class="form-control" id="strategy-endTimeInDay"
                                           data-default="23:59"/>
                                </div>
                                <%--<div class="input-group clockpicker">--%>
                                <%--<input type="text" class="form-control" value="09:30">--%>
                                <%--<span class="input-group-addon">--%>
                                <%--<span class="glyphicon glyphicon-time"></span>--%>
                                <%--</span>--%>
                                <%--</div>--%>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button id="confirmBtn" type="button" class="btn btn-primary">更新</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </aside>
</div>
<%@include file="../common/pager.jsp" %>


<script>
    function setIndex() {
        $(".popover").css("zIndex",100000000);
    }
    $(':checkbox').checkboxpicker();

    $('.clockpicker').clockpicker({
        autoclose: true
    });
    $('#maintainstrategyModal').on('show.bs.modal', function (event) {
        $("input:checkbox").each(function () {
            $(this).prop('checked', true);
        });
        var button = $(event.relatedTarget) // Button that triggered the modal
        var title = button.data('title') // Extract info from data-* attributes
        var actType = button.data('type');
        var buttonTitle = button.data('button');
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this)
        modal.find('.modal-title').text(title)
        $("#confirmBtn").text(buttonTitle);
        if (actType == "add") {
            $('#confirmBtn').unbind("click"); //移除click
            $("#confirmBtn").click(function () {
//                var name = $('#strategy-id').val();
                        var name = $('#strategy-name').val();
                        var mininterval = $('#strategy-mininterval').val();
                        var warncountwithtime = $('#strategy-warncountwithtime').val();
                        var warnmaxcount = $('#strategy-warnmaxcount').val();
                        var baknum = $('#strategy-bakNum').val();
                        var bakchar = $('#strategy-bakChar').val();
                        var daysInWeek = "";

                        $("input:checkbox:checked").each(function () {
                            var day = $(this).data("value");
                            if (day != undefined) {
                                daysInWeek += day + ",";
                            }
                            <%--alert(${this}.text();--%>
                        });
                        var startTimeInDay = $('#strategy-startTimeInDay').val();
                        var endTimeInDay = $('#strategy-endTimeInDay').val();
                        if (name.length == 0 || mininterval.length == 0 || warncountwithtime.length == 0 ||
                                warnmaxcount.length == 0 || daysInWeek.length == 0 || startTimeInDay.length == 0 || endTimeInDay.length == 0) {
                            alert("页面所有字段均为必填项，请输入");
                            return;
                        }
                        if (isNaN(mininterval) || isNaN(warncountwithtime) || isNaN(warnmaxcount)) {
                            alert("请输入正确的数字");
                            return;
                        }

                        $.getJSON("/strategy/add", {
                            stgyName: name,
                            minInterval: mininterval,
                            warnCountWithTime: warncountwithtime,
                            warnMaxCount: warnmaxcount,
                            bakNum: baknum,
                            bakChar: bakchar,
                            warnDaysInWeek: daysInWeek,
                            startTimeInDay: startTimeInDay,
                            endTimeInDay: endTimeInDay,
                        }, function (data) {
                            if (data.code == 0) {
                                alert("新增成功！");
                            } else {
                                alert(data.msg + ':' + data.exceptionMsg);
                            }
                            document.forms[0].action = "/strategy/list/${pageData.pageNO}/${pageData.pageSize}";
                            document.forms[0].submit();
                        });
                    }
            );
        }
        if (actType == "mod") {
            $('#confirmBtn').unbind("click"); //移除click
            $("#confirmBtn").click(function () {
                        var id = $('#strategy-id').val();
                        var name = $('#strategy-name').val();
                        var mininterval = $('#strategy-mininterval').val();
                        var warncountwithtime = $('#strategy-warncountwithtime').val();
                        var warnmaxcount = $('#strategy-warnmaxcount').val();
                        var baknum = $('#strategy-bakNum').val();
                        var bakchar = $('#strategy-bakChar').val();
                        var daysInWeek = "";
                        $("input:checkbox:checked").each(function () {
                            var day = $(this).data("value");
                            if (day != undefined) {
                                daysInWeek += day + ",";
                            }
                        });
                        var startTimeInDay = $('#strategy-startTimeInDay').val();
                        var endTimeInDay = $('#strategy-endTimeInDay').val();

                        if (name.length == 0 || mininterval.length == 0 || warncountwithtime.length == 0 ||
                                warnmaxcount.length == 0 || daysInWeek.length == 0 || startTimeInDay.length == 0 || endTimeInDay.length == 0) {
                            alert("页面所有字段均为必填项，请输入");
                            return;
                        }
                        if (isNaN(mininterval) || isNaN(warncountwithtime) || isNaN(warnmaxcount)) {
                            alert("请输入正确的数字");
                            return;
                        }
                        $.getJSON("/strategy/update", {
                            id: id,
                            stgyName: name,
                            minInterval: mininterval,
                            warnCountWithTime: warncountwithtime,
                            warnMaxCount: warnmaxcount,
                            bakNum: baknum,
                            bakChar: bakchar,
                            warnDaysInWeek: daysInWeek,
                            startTimeInDay: startTimeInDay,
                            endTimeInDay: endTimeInDay,
                            status: 0
                        }, function (data) {
                            if (data.code == 0) {
                                alert("修改成功！");
                            } else {
                                alert(data.msg + ':' + data.exceptionMsg);
                            }
                            document.forms[0].action = "/strategy/list/${pageData.pageNO}/${pageData.pageSize}";
                            document.forms[0].submit();
                        });
                    }
            );
        }

    })
    $('#queryBtn').click(function () {
        var id = $('#queryId').val();
        var mobile = $('#queryMobile').val();
        var wechat = $('#queryWechat').val();

        document.forms[0].action = "/strategy/list/${pageData.pageNO}/${pageData.pageSize}/" + id + "/" + mobile + "/" + wechat;
        document.forms[0].submit();

    })

    function modify(id, obj) {
        $.getJSON("/strategy/modify/" + id, {}, function (data) {
            if (data.code == 0) {
                var attach = data.attach;
                $('#strategy-id').val(attach.id);
                $('#strategy-name').val(attach.stgyName);
                $('#strategy-mininterval').val(attach.minInterval);
                $('#strategy-warncountwithtime').val(attach.warnCountWithTime);
                $('#strategy-warnmaxcount').val(attach.warnMaxCount);
                $('#strategy-startTimeInDay').val(attach.startTimeInDay);
                $('#strategy-endTimeInDay').val(attach.endTimeInDay);
                $('#strategy-bakNum').val(attach.bakNum);
                $('#strategy-bakChar').val(attach.bakChar);


                var daysInWeek = attach.warnDaysInWeek;
                var days = daysInWeek.split(',');
                $("input:checkbox").each(function () {
                    $(this).prop('checked', false);
                });
                $("input:checkbox").each(function () {
                    var chkValue = $(this).data("value");
                    for (i = 0; i < days.length; i++) {
                        if (days[i] == '')
                            continue;
                        if (chkValue == days[i]) {
                            $(this).prop('checked', true);
                            break;
                        }
                    }
                });


            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
        });

    }
    function del(id, obj) {
        if (!confirm("删除报警策略后可能会造成报警信息无法发送，是否继续？")) {
            return;
        }
        $.ajaxSettings.async = false;
        $.getJSON("/strategy/del/" + id, {}, function (data) {
            if (data.code == 0) {
                alert("删除成功！");
            } else {
                alert(data.msg + ':' + data.exceptionMsg);
            }
            document.forms[0].action = "/strategy/list/${pageData.pageNO}/${pageData.pageSize}";
            document.forms[0].submit();
        });
    }
    function add(obj) {
        $("#maintainstrategyModal").find(".modal-body input").val("");
    }
</script>
</body>
</html>
