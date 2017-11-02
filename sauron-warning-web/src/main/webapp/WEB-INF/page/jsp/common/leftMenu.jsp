<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<aside class="left-side sidebar-offcanvas">
    <section class="sidebar">
        <!-- Sidebar user panel -->
        <div class="user-panel">
            <div class="pull-left image">
                <%--<img src="img/avatar3.png" class="img-circle" alt="User Image" />--%>
            </div>
            <div class="pull-left info">

                <a href=""><img src="/resources/images/e1.png" class="img-circle" style="width: 30px; height: 30px;" /> 欢迎您</a>
            </div>
        </div>

        <!-- sidebar menu: : style can be found in sidebar.less -->
        <ul class="sidebar-menu">
            <c:if test="${anthor == 1}">
                <li class="treeview active">
                    <a href="#">
                        <i class="fa fa-fw fa-keyboard-o"></i>
                        <span>基础数据管理</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu">
                        <li><a href="/user/list/1/100"><i class="fa fa-angle-double-right"></i>用户管理</a></li>
                        <li><a href="/app/list/1/100"><i class="fa fa-angle-double-right"></i>项目管理</a></li>
                        <li><a href="/zk/list/1/100"><i class="fa fa-angle-double-right"></i>ZK组管理</a></li>
                        <li><a href="/mp/list/1/100"><i class="fa fa-angle-double-right"></i>监控点管理</a></li>
                        <li><a href="/config/list"><i class="fa fa-angle-double-right"></i>开关管理</a></li>
                    </ul>
                </li>
            </c:if>
            <c:if test="${anthor != 1}">
                <li class="treeview active">
                    <a href="#">
                        <i class="fa fa-fw fa-keyboard-o"></i>
                        <span>基础数据管理</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu">
                        <li><a href="/app/list/1/100"><i class="fa fa-angle-double-right"></i>项目管理</a></li>
                    </ul>
                </li>
            </c:if>
            <li class="treeview active">
                <a href="#">
                    <i class="fa fa-fw fa-wrench"></i>
                    <span>监控配置</span>
                    <i class="fa fa-angle-left pull-right"></i>
                </a>
                <ul class="treeview-menu">
                    <li><a href="/rule/list/1/10/1"><i class="fa fa-angle-double-right"></i>方法配置</a></li>
                    <li><a href="/urlRule/list/1/10"><i class="fa fa-angle-double-right"></i>ULR配置</a></li>
                    <li><a href="/dubboRule/list/1/10"><i class="fa fa-angle-double-right"></i>DUBBO配置</a></li>
                    <li><a href="/rule/list/1/10/2"><i class="fa fa-angle-double-right"></i>自定义配置</a></li>
                </ul>
            </li>
            <c:if test="${anthor == 1}">
                <li class="treeview active">
                    <a href="#">
                        <i class="fa fa-fw fa-volume-up"></i>
                        <span>报警配置</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu">
                        <li><a href="/contact/list/1/10"><i class="fa fa-angle-double-right"></i>联系人</a></li>
                        <li><a href="/strategy/list/1/10"><i class="fa fa-angle-double-right"></i>策略</a></li>
                    </ul>
                </li>
            </c:if>
            <c:if test="${anthor != 1}">
                <li class="treeview active">
                    <a href="#">
                        <i class="fa fa-fw fa-volume-up"></i>
                        <span>报警配置</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu">
                        <li><a href="/contact/list/1/10"><i class="fa fa-angle-double-right"></i>联系人</a></li>
                    </ul>
                </li>
            </c:if>
            <li class="treeview active">
            <a href="#">
                <i class="fa fa-fw fa-search-minus"></i>
                <span>调用追踪</span>
                <i class="fa fa-angle-left pull-right"></i>
            </a>
            <ul class="treeview-menu">
                <li><a href="/monitor/methodList"><i class="fa fa-angle-double-right"></i>方法监控</a></li>
                <li><a href="/monitor/urlList/1/10"><i class="fa fa-angle-double-right"></i>URL监控</a></li>
                <li><a href="/monitor/dubboList/1/10"><i class="fa fa-angle-double-right"></i>DUBBO监控</a></li>
                <li><a href="/monitor/getMetricsOriDataCodeBulk"><i class="fa fa-angle-double-right"></i>自定义块监控</a></li>
                <li><a href="/monitor/getMetricsOriDataCodebulkAlarm"><i class="fa fa-angle-double-right"></i>自定义KEY监控</a></li>
            </ul>
             </li>
            <%--<c:if test="${anthor == 1}">--%>
                <%--<li class="treeview active">--%>
                    <%--<a href="#">--%>
                        <%--<i class="fa fa-fw fa-envelope-o"></i>--%>
                        <%--<span>短信</span>--%>
                        <%--<i class="fa fa-angle-left pull-right"></i>--%>
                    <%--</a>--%>
                    <%--<ul class="treeview-menu">--%>
                        <%--<li><a href="/strategyTem/list/1/10"><i class="fa fa-angle-double-right"></i>模板管理</a></li>--%>
                    <%--</ul>--%>
                <%--</li>--%>
            <%--</c:if>--%>

           <!--
            <li class="treeview">
                <a href="#">
                    <i class="fa fa-bar-chart-o"></i>
                    <span>报警后台</span>
                    <i class="fa fa-angle-left pull-right"></i>
                </a>
                <ul class="treeview-menu">
                    <li><a href="/rule/list/1/10"><i class="fa fa-angle-double-right"></i> 报警规则</a></li>
                    <li><a href="/event/list/1/10"><i class="fa fa-angle-double-right"></i> 报警事件</a></li>
                    <li><a href="/contact/list/1/10"><i class="fa fa-angle-double-right"></i> 通讯联系人</a></li>
                    <li><a href="/strategy/list/1/10"><i class="fa fa-angle-double-right"></i> 报警策略</a></li>
                    <li><a href="/strategyTem/list/1/10"><i class="fa fa-angle-double-right"></i> 短信模板发送策略</a></li>
                </ul>
                <ul class="treeview-menu">
                    <li><a href="/trace"><i class="fa fa-angle-double-right"></i> 调用链跟踪</a></li>
                </ul>
            </li>
            -->
            <li>
                <a href="http://10.10.52.27:3000/">
                    <i class="fa fa-th"></i> <span>应用监控前台</span>
                    <small class="badge pull-right bg-green">new</small>
                </a>
            </li>

            <!--
            <li>
                <a href="/trace">
                    <i class="fa fa-th"></i><span>调用过程追踪</span>
                    <small class="badge pull-right bg-green">new</small>
                </a>
            </li>
            -->

        </ul>
    </section>
</aside>
<script type="text/javascript">
    function selectMenu(menuId) {
        $("#" + menuId).attr("class", "active");
    }
</script>