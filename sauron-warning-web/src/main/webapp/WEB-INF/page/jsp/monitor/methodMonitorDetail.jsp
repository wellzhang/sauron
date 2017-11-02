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
    <!-- 可选的Bootstrap主题文件（一般不用引入） -->
    <link rel="stylesheet" href="/resources/css/bootstrap-theme.min.css">
    <link href="/resources/css/font-awesome.min.css" rel="stylesheet" />


    <link href="/resources/css/timepicker/bootstrap-timepicker.min.css" rel="stylesheet"/>
    <link href="/resources/css/timepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css" />


    <link href="/resources/css/tree/screen.css"  rel="stylesheet"/>
    <link href="/resources/css/tree/jquery.treetable.css"  rel="stylesheet"/>
    <link href="/resources/css/tree/jquery.treetable.theme.default.css"  rel="stylesheet"/>
    <link href="/resources/css/AdminLTE.css" rel="stylesheet" type="text/css">
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="/resources/js/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="/resources/js/bootstrap.min.js"></script>
    <%--<link href="/resources/css/dashboard.css" rel="stylesheet">--%>
    <script src="/resources/js/app.js"></script>
    <script src="/resources/js/timepicker/bootstrap-timepicker.min.js"></script>

    <script src="/resources/js/timepicker/daterangepicker.js"></script>


</head>


<body>













<body class="skin-blue">

        <section class="content">
            <div class="table-responsive">
                <div class="box-body">
                    <!-- 定位到具体节点-->
                    <!--
                    <form id="reveal">
                        <input type="text" name="nodeId" placeholder="nodeId" id="revealNodeId"/>
                        <input type="submit" value="Reveal"/><br/>
                        <small>Examples: <tt>2-1-1</tt>, <tt>3-1-1-2-2</tt> and <tt>3-2-1-2-3-1-2-2-1-1-1-1-2-5</tt></small>
                    </form>
                    -->

                    <table id="example-advanced" >
                        <!--
                        <caption>

                            <div class="btn-group">
                                <button onclick="jQuery('#example-advanced').treetable('expandAll'); return false;" class="btn btn-warning" type="button"><i class="fa fa-fw fa-plus-square"></i></button>
                                <button onclick="jQuery('#example-advanced').treetable('collapseAll'); return false;" class="btn btn-warning" type="button"><i class="fa fa-fw fa-minus-square"></i></button>
                            </div>
                        </caption>#52c2d5
                        -->
                        <thead>
                        <tr style=" background: #3c8dbc none repeat scroll 0 0; color:  #fff;">
                            <th>应用名</th>
                            <th>类型</th>
                            <th>状态</th>
                            <th>主机名</th>
                            <th>服务/方法</th>
                            <!--
                            <th>网络延时</th>
                            -->
                            <th style="text-align: center;border-right:0px;">时间轴(ms)</th>
                            <th style="border-left:0px;"></th>
                        </tr>
                        </thead>
                        <tbody id="traceTable" style="color: #999;border: none;">
                        </tbody>
                    </table>
                </div>

                <div class="box-body" id="dataDetail">

<!--
                    <textarea class="form-control" id="jsonView" style="width: 100%; height: 100%" readonly></textarea>
                    -->
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

<script src="/resources/js/tree/jquery-ui.js"></script>
<script src="/resources/js/tree/jquery.treetable.js"></script>
<script>



    var htmlStr = '';
    var totalDuration;
    var lastl;

    $(function () {




        $.getJSON("/monitor/getInfoByTraceid/${tid}/${time}",{},function (data) {


//            alert(JSON.stringify(data) );
            if (data.code == 0) {

//                alert(data.attach.document.InvokeResult);


//                alert(JSON.stringify(eval(data.treeData)));


                var resultData = eval(data.treeData);

                var treeTrHTML = "";

                var total  = 0;

                $(resultData).each(function (index,obj) {

                    var trHtml = "";

                    var trStr = '1'+obj.Spanid.substr(1,obj.Spanid.length);
                    var trStrArr = trStr.split(".");
                    var treeIndex = "";
                    var parentTreeIndex = "";
                    for(var loop=0;loop<trStrArr.length;loop++){
                        if(loop > 0) {
                            treeIndex += "-";
                        }
                        treeIndex += trStrArr[loop];
                        if(loop < trStrArr.length - 1){
                            if(loop > 0)  parentTreeIndex += "-";
                            parentTreeIndex += trStrArr[loop];
                        }
                    }

                    if(index == 0){
                        trHtml += "<tr data-tt-id='1' id='1' onclick='javascript:document.getElementById(\"here\").scrollIntoView()'>"
                        total = obj.duration;
                    }else{
                        trHtml += "<tr id='"+treeIndex+"'  data-tt-id='"+treeIndex+"' data-tt-parent-id='"+parentTreeIndex+"' ondblclick='javascript:document.getElementById(\"d_"+treeIndex+"\").scrollIntoView()'>";
                    }

                    trHtml += "<td>"+obj.AppName+"</td>";

                    var method = obj.methodName.split("#")[0];

                    if(method.indexOf('dubbo')>=0){
                        if(method.length<20){
                            trHtml += "<td style='width: 126px; color: #118850;'><b>"+method+"</b></td>";
                        }else{
                            trHtml += "<td style='width: 126px; color: #118850;'><b> </b></td>";
                        }
                    }else{
                        trHtml += "<td style='width: 126px'>native</td>";
                    }



                    if(obj.InvokeResult=='0'){
                        trHtml += "<td style='width: 50px;'>OK</td>";
                    }else{
                        trHtml += "<td style='color:  #c00;width: 50px;'><b>FAIL</b></td>";
                    }

                    trHtml += "<td style='width: 150px;'>"+obj.hostName+"</td>";

                    trHtml += "<td style='width: 370px;'>"+obj.methodName.substr(0,50)+"</td>";

//                    trHtml += "<td style='width: 70px;'>"+obj.elapse+"ms</td>";

                    trHtml += "<td style='padding: 0em 0em;text-align: right; width: 400px ;' class='durationLine'>";

                    trHtml += "<div style='height: 15px;line-height: 15px; width: 100%;margin:0 auto; background-color: #eff0f0;'>";

                    if((obj.duration/total)*80 <= 0.1 &&(obj.duration/total)*80 > 0 ){
                        trHtml += "<div style='background-color:#52c2d5;height: 15px;float:left;width: 0.5%; margin-left:"+(obj.offset/total)*80+"%;'>";
                    }else{
                        trHtml += "<div style='background-color:#52c2d5;height: 15px;float:left;width: "+(obj.duration/total)*80+"%; margin-left:"+(obj.offset/total)*80+"%;'>";
                    }


//                    trHtml += "<div style='width: "+(obj.elapse/total)*100+"%; float:left; width: 100%;'  class='progress-bar progress-bar-danger'>";
//                    trHtml += "<div style='width: "+(obj.elapse/total)*100+"%; margin-left:"+((obj.offset+obj.elapse)/total)*100+"%;' class='progress-bar progress-bar-danger'>";
//                    trHtml += "</div>";

                    trHtml += "</div>";

                    if(obj.elapse != undefined && obj.elapse != 0 ){
                        if((obj.elapse/total)*80 <= 0.1 && (obj.elapse/total)*80 > 0){
                            trHtml += "<div style='background-color:#f50;height: 15px;float:left;width: 0.5%;'></div>";
                        }else{
                            trHtml += "<div style='background-color:#f50;height: 15px;float:left;width: "+(obj.elapse/total)*80+"%;'></div>";
                        }
                        trHtml += "<div style='height: 15px;float:left;color:#118850;'>(网路延时"+obj.duration+"ms)</div>";

                    }



                    trHtml += "<div style='float:left; margin-left: 3px;'>"+obj.duration+"ms</div>";
                    trHtml += "</div>";
//                    trHtml += "<span style='color: #a5a5a5;font-size: 12px;padding: 0'>"+obj.duration+"</span>";
//                    trHtml += "<b style='color: #0f0f0f;'>"+obj.duration+"</b>ms";
                    trHtml += "</td>";

                    trHtml += "<td style='width: 50px; border-left: 1px solid #f50;'></td>";






                    trHtml += "<input type='hidden' value='"+obj.duration+"' /></tr>";


                    treeTrHTML += trHtml

                    $("#dataDetail").append("<pre id='d_"+treeIndex+"'>"+JSON.stringify(obj, null, 4)+"</pre>")


                });

//                alert(treeTrHTML);
                $("#traceTable").html(treeTrHTML);



//                totalDuration = data.attach.document.duration;
//
//
//                htmlStr = "<tr data-tt-id='1' id='1'><td><span class='file'>"+data.attach.document.AppName+"</span></td>" +
//                        "<td>"+((data.attach.document.methodName.split("#")[0].indexOf('dubbo')>=0)?'dubbo':'native')+"</td>" +
//                        "<td>"+(data.attach.document.InvokeResult=='0'?'OK':'<b style="color:#c00">FAIL</b>')+"</td>" +
//                        "<td>"+data.attach.document.hostName+"</td>" +
//                        "<td>"+data.attach.document.methodName.substr(0,110)+"</td>" +
//    /**  "<td>"+data.attach.document.methodName+"</td>" +**/
//                        "<td style='padding: 0em 0em;text-align: center;color: #eb9316;' class='durationLine'><b>" +
//
//
//
////                        "<div style='height: 22px;' class='progress xs progress-striped active'>" +
////                        "<div style='width: "+100+"%' class='progress-bar progress-bar-primary sliders'>"+
////                        "</div>"+
////                        "</div>"+
//
//
//                        data["attach"]["document"]["duration"]+"ms"
//
//
//
//
//
//
//                "</b></td><input type='hidden' value='"+totalDuration+"' /></tr>";
//
//
//                recursionJSON(data["attach"]["children"]);
//
//
//                $("#traceTable").html(htmlStr);

//                $("#jsonView").html(JSON.stringify(eval(data.treeData), null, 4));


                document.documentElement.style.overflow='hidden';

//                setIndex();

                var timeDiv = "";


                $("#traceTable tr").each(function (index) {
                    if(index%2){
                        $(this).attr("bgcolor","#f5f5f5");
                    }
                });

                $("#example-basic").treetable({ expandable: true });

                $("#example-basic-static").treetable();

                $("#example-basic-expandable").treetable({ expandable: true });

                $("#example-advanced").treetable({ expandable: true });

                // Highlight selected row
                $("#example-advanced tbody tr").mousedown(function() {
                    $("tr.selected").removeClass("selected");
//                    $(this).addClass("selected");
                });


                $("#example-advanced tbody tr").dblclick(function(){
                    $("[id*='d_']").css("color","#0f0f0f");
                    $("#d_"+$(this).attr("id")).css("color","red");
                });




                // Drag & Drop Example Code
                $("#example-advanced .file, #example-advanced .folder").draggable({
                    helper: "clone",
                    opacity: .75,
                    refreshPositions: true, // Performance?
                    revert: "invalid",
                    revertDuration: 300,
                    scroll: true
                });

                $("#example-advanced .folder").each(function() {
                    $(this).parents("tr").droppable({
                        accept: ".file, .folder",
                        drop: function(e, ui) {
                            var droppedEl = ui.draggable.parents("tr");
                            $("#example-advanced").treetable("move", droppedEl.data("ttId"), $(this).data("ttId"));
                        },
                        hoverClass: "accept",
                        over: function(e, ui) {
                            var droppedEl = ui.draggable.parents("tr");
                            if(this != droppedEl[0] && !$(this).is(".expanded")) {
                                $("#example-advanced").treetable("expandNode", $(this).data("ttId"));
                            }
                        }
                    });
                });

                $("form#reveal").submit(function() {
                    var nodeId = $("#revealNodeId").val()

                    try {
                        $("#example-advanced").treetable("reveal", nodeId);
                    }
                    catch(error) {
                        alert(error.message);
                    }

                    return false;
                });

                $('#example-advanced').treetable('expandAll');

                var h = $(window).height();
                var tree = $("#traceTable").height();



                $("#jsonView").height(h-(105 +tree));

                $(".no-print").hide();














            } else {
                alert(data.msg);
            }
        })
    })


//    function  recursionJSON(data) {
//        var duration = '';
//        var type = '';
//        var Spanid = '';
//        var hostName = '';
//        var AppName = '';
//        var methodName = '';
//        var InvokeResult = '';
//        var trHtml = '';
//        var trEnd = '';
//        var leftIndex = 0.1;
//        var tWidth = 0;
//        if(typeof(data) == "object") {
//            $.each(data, function(i, n) {
//                if(typeof(n) == "object") {
//                    recursionJSON(n)
////                    alert(JSON.stringify(n));
//                    if(typeof(n["document"]) == "object"){
////                        alert(n["document"]["Spanid"]+n["document"]["AppName"])
//                    }
//                } else {
//
////                    alert(n);
//
//                    if(i=="pid") {
////                        htmlStr = "";
//                        return;
//                    }
//
//                    if(i=="InvokeResult"){
//                        if(n=="0"){
//
//                        }else{
//
//                        }
//                    }
//                    if(i=="duration"){
//
//                        tWidth = lastl + 0.1;
//                        var l = (n/totalDuration)*100;
//                        lastl = l;
//
//                        //判断是否为什么方法
//                        //type += "<td>native</td>";
//
//
//
//
////                        duration += "<td style='padding: 0em 0em;' class='durationLine'>";
////                        duration += "<div style='height: 22px;' class='progress xs progress-striped active'>";
////                        duration += "<div style='width: "+l+"%;' class='progress-bar progress-bar-primary'><b style='color: #0f0f0f;'>"+n+"</b>ms</div></div>";
////                        duration += "</td>";
//
//
//                        duration += "<td style='padding: 0em 0em;text-align: center;' class='durationLine'>";
//                        duration += "<b style='color: #0f0f0f;'>"+n+"</b>ms";
//                        duration += "</td>";
//
//
//
//
//
//
//                        trEnd += "<input type='hidden' value='"+n+"' /></tr>";
//
//
//                    }
//                    if(i=="Spanid"){
//                        var trStr = '1'+n.substr(1,n.length);
//                        var trStrArr = trStr.split(".");
//                        var treeIndex = "";
//                        var parentTreeIndex = "";
//                        for(var loop=0;loop<trStrArr.length;loop++){
//                            if(loop > 0) {
//                                treeIndex += "-";
//                            }
//                            treeIndex += trStrArr[loop];
//                            if(loop < trStrArr.length - 1){
//                                if(loop > 0)  parentTreeIndex += "-";
//                                parentTreeIndex += trStrArr[loop];
//                            }
//                        }
//                        trHtml += "<tr id='"+treeIndex+"'  data-tt-id='"+treeIndex+"' data-tt-parent-id='"+parentTreeIndex+"'>";
//                    }
//                    if(i=="hostName"){
//                        hostName += "<td>"+n+"</td>";
//                    }
//                    if(i=="AppName"){
//                        AppName += "<td><span class='file'>"+n+"</span></td>";
//                    }
//                    if(i=="methodName"){
////                        methodName += "<td>"+n.substr(0,50)+"</td>";
//                        methodName += "<td>"+n+"</td>";
//                        type += "<td>"+((n.split("#")[0].indexOf('dubbo')>=0)?'dubbo':'native')+"</td>";
//                    }
//
//                    if(i=="InvokeResult"){
//                        if(n=='0'){
//                            InvokeResult += "<td>OK</td>";
//                        }else{
//                            InvokeResult += "<td style='color:  #c00'><b>FAIL</b></td>";
//                        }
////                        InvokeResult += "<td>"+(n=='0'?'OK':'FAIL')+"</td>";
//
//                    }
//
////                    htmlStr += n+"--";
////                    alert(i+": "+htmlStr)
//                }
//            })
//            htmlStr += (trHtml + AppName + type +  InvokeResult +  hostName + methodName + duration + trEnd);
//        }
//    }


    function referenceId(referenceStr,index) {
        var result = '';
        var referenceArr = referenceStr.split("-");
        for(var loop = 0; loop < referenceArr.length; loop++){
            if(loop > 0) result += '-';
            if(loop == (index-1)){
                result += (index-1);
            }else{
                result += referenceArr[loop];
            }
        }
        return result;
    }





</script>

</html>
