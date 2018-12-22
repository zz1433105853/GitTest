<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Container管理</title>
    <link href="http://cdn.bootcss.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet">
    <link href="http://cdn.bootcss.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="${ctxStatic}/pnotify/pnotify.custom.min.css" rel="stylesheet">
    <style>
        td,th {
            text-align: center;
            vertical-align: baseline;
        }
    </style>
</head>
<body class="container">
<h3><span class="label label-primary">直连</span></h3>
<table class="table ">
    <tr>
        <th>NickName</th>
        <th>TdName</th>
        <th>Connected</th>
        <th>Connector</th>
        <th colspan="2">Session</th>
        <th>Reload</th>
        <th>Remove</th>
    </tr>
    <c:forEach items="${straightMcGroup}" var="e">
        <c:forEach items="${e.value}" var="container" varStatus="s">
            <tr>    
                <c:if test="${s.index==0}">
                    <td rowspan="${fn:length(e.value)}"><span class="label label-primary">${e.key}</span></td>
                </c:if>
                <td>
                    <span class="label label-primary">${container.tdName}</span>
                </td>
                <td>
                    <c:if test="${container.connected==true}">
                        <span class="label label-success">正常</span>
                    </c:if>
                    <c:if test="${container.connected==false}">
                        <span class="label label-danger">断开</span>
                    </c:if>
                </td>
                <td>
                <span class="label label-<c:if test='${container.ioConnector.active==true}'>success</c:if><c:if test='${container.ioConnector.active==false}'>danger</c:if>">
                    Active
                </span>
                </td>
                <c:if test="${not empty container.ioSession}">
                    <td>
                        <span class="label label-<c:if test='${container.ioSession.connected==true}'>success</c:if><c:if test='${container.ioSession.connected==false}'>danger</c:if>">
                            Connected
                        </span>
                    </td>
                    <td>
                        <span class="label label-<c:if test='${container.ioSession.active==true}'>success</c:if><c:if test='${container.ioSession.active==false}'>danger</c:if>">
                                Active
                        </span>
                    </td>
                </c:if>
                <c:if test="${empty container.ioSession}">
                    <td colspan="2">
                        <span class="label label-danger">
                                Not Exist
                        </span>
                    </td>
                </c:if>

                <td><span type="button" onclick="reloadContainer('${container.tdName}', this)" class="btn btn-small" data-loading-text="loading..."><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span></span></td>
                <td><span type="button" onclick="removeContainer('${container.tdName}', this)" class="btn btn-small" data-loading-text="deleting..."><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></span></td>
            </tr>
        </c:forEach>
    </c:forEach>
</table>
<h3><span class="label label-primary">非直连</span></h3>
<table class="table">
    <tr>
        <th>NickName</th>
        <th>TdName</th>
        <th>操作</th>
    </tr>
    <c:forEach items="${third}" var="container">
        <tr>
            <td><span class="label label-primary">${container.tunnel.nickName}</span></td>
            <td><span class="label label-primary">${container.tdName}</span></td>
            <td>
                <span type="button" onclick="getBalance('${container.tdName}')" class="btn btn-small" data-loading-text="loading..."><span class="glyphicon glyphicon-search" aria-hidden="true"></span></span>
                <span type="button" onclick="reloadContainer('${container.tdName}', this)" class="btn btn-small" data-loading-text="loading..."><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span></span>
            </td>
        </tr>
    </c:forEach>
</table>
<h3><span class="label label-primary">彩信非直连</span></h3>
<table class="table">
    <tr>
        <th>NickName</th>
        <th>TdName</th>
        <th>操作</th>
    </tr>
    <c:forEach items="${cxthird}" var="container">
        <tr>
            <td><span class="label label-primary">${container.Tunnel.nickName}</span></td>
            <td><span class="label label-primary">${container.tdName}</span></td>
            <td>
                <span type="button" onclick="getBalance('${container.tdName}')" class="btn btn-small" data-loading-text="loading..."><span class="glyphicon glyphicon-search" aria-hidden="true"></span></span>
                <span type="button" onclick="reloadContainer('${container.tdName}', this)" class="btn btn-small" data-loading-text="loading..."><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span></span>
            </td>
        </tr>
    </c:forEach>
</table>


    <script src="http://cdn.bootcss.com/jquery/2.1.4/jquery.js"></script>
    <script src="http://cdn.bootcss.com/bootstrap/3.3.6/js/bootstrap.js"></script>
    <script src="${ctxStatic}/pnotify/pnotify.custom.min.js"></script>
    <script>
        function getBalance(tdId) {
            $.post(
                '${ctx}/container/loadTunnelBalance',
                {'tdId': tdId},
                function (data, stauts) {
                    if(data && data.count) {
                        alert(data.count);
                    }
                }
            );
        }
        PNotify.desktop.permission();
        function reloadContainer(tdId, obj) {

            (new PNotify({
                title: '重新加载通道',
                text: '确认重新加载？',
                icon: 'glyphicon glyphicon-question-sign',
                hide: false,
                confirm: {
                    confirm: true
                },
                buttons: {
                    closer: false,
                    sticker: false
                },
                history: {
                    history: false
                }
            })).get().on('pnotify.confirm', function() {
                $(obj).button('loading');
                $.post(
                    '${ctx}/container/reload',
                    {'tdId': tdId},
                    function (data, status) {
                        $(obj).button('reset');
                        if(data && data.status && data.status=='success') {
                            new PNotify({
                                title: '成功',
                                text: '重新加载通道成功',
                                type: 'success'
                            });
                            setTimeout(function () {
                                document.location = document.location;
                            }, 2000);
                        }else {
                            new PNotify({
                                title: '失败',
                                text: '重新加载通道失败',
                                type: 'error'
                            });
                        }
                    }
                );
            }).on('pnotify.cancel', function() {

            });


        }

        function removeContainer(tdId, obj) {

            (new PNotify({
                title: '删除通道',
                text: '确认删除通道？',
                icon: 'glyphicon glyphicon-question-sign',
                hide: false,
                confirm: {
                    confirm: true
                },
                buttons: {
                    closer: false,
                    sticker: false
                },
                history: {
                    history: false
                }
            })).get().on('pnotify.confirm', function() {
                $(obj).button('loading');
                $.post(
                    '${ctx}/container/remove',
                    {'tdId': tdId},
                    function (data, status) {
                        $(obj).button('reset');
                        if(data && data.status && data.status=='success') {
                            new PNotify({
                                title: '成功',
                                text: '删除通道成功',
                                type: 'success'
                            });
                            setTimeout(function () {
                                document.location = document.location;
                            }, 2000);
                        }else {
                            new PNotify({
                                title: '失败',
                                text: '删除通道失败',
                                type: 'error'
                            });
                        }
                    }
                );
            }).on('pnotify.cancel', function() {

            });
        }
    </script>
</body>
</html>
