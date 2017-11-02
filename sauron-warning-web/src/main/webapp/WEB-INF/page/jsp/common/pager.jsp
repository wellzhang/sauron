<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<script type="text/javascript">

    function page(number) {
//        alert("../" + number + "/10")
        //document.forms[0].action = "../" + number + "/10";
        $("#searchForm").attr("action","../" + number + "/10")
//        alert($("#searchForm").html());
//        document.forms[0].submit();
        $("#searchForm").submit();
    }

    $("#paginations").twbsPagination({
        totalPages: ${pageData.totalPage},
        startPage:${pageData.pageNO},
        visiblePages: 5,
        href: "javascript:page({{number}});",
        first: '首页',
        prev: '<<',
        next: '>>',
        last: '末页'
    });
</script>