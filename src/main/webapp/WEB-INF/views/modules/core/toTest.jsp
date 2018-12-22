<%--
  Created by IntelliJ IDEA.
  User: tykjkf01
  Date: 2016/5/25
  Time: 10:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <title>短信测试</title>
    <link href="//cdn.bootcss.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet">
    <link href="//cdn.bootcss.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" rel="stylesheet">
</head>
<body class="container">

    <input type="text" class="form-control" placeholder="请输入手机号" id="mobile" value=""><br>
    <button onclick="sendMessage()" class="btn" id="btnSendCode">发送测试短信</button>
    <button onclick="reload()" class="btn" id="btnReload">刷新页面</button>
    <script src="//cdn.bootcss.com/jquery/2.1.4/jquery.js"></script>
    <script src="//cdn.bootcss.com/bootstrap/3.3.6/js/bootstrap.js"></script>
    <script>

        var InterValObj; //timer变量，控制时间
        var count = 60; //间隔函数，1秒执行
        var curCount;//当前剩余秒数

        function sendMessage() {

            var mobile = $('#mobile').val().trim();

            if(!mobile) {
                alert('请输入手机号');
                return;
            }
            curCount = count;
            //设置button效果，开始计时
            $("#btnSendCode").attr("disabled", "true");
            $("#btnSendCode").text("倒计时" + curCount);
            InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次
            //向后台发送处理数据
            $.post(
                    '${ctx}/msg/sendTestMsg',
                    {'mobile': mobile},
                    function (data, stauts) {
                        console.log('Response: ',data);
                        if(data && data.status && data.status.code==='0') {
                            //alert('短信发送成功');
                        }else {
                            alert(data.status.message);
                        }
                    }
            );
        }

        //timer处理函数
        function SetRemainTime() {
            if (curCount == 0) {
                window.clearInterval(InterValObj);//停止计时器
                $("#btnSendCode").removeAttr("disabled");//启用按钮
                $("#btnSendCode").text("发送测试短信");
            }
            else {
                curCount--;
                $("#btnSendCode").text("倒计时" + curCount );
            }
        }

        function reload(){
            location.reload()
        }

    </script>
</body>
</html>
