<%--
  Created by IntelliJ IDEA.
  User: zhenqin
  Date: 13-2-8
  Time: 下午3:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML>
<html lang="zh-CN">
<head>
    <title>探针实时数据</title>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery.min.js" ></script>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/sockjs-0.3.min.js" ></script>
</head>
<body>
<div class="page-header"></div>
    <h2>探针数据测试</h2>

    ClientMac:<input type="text" id="clientMac" name="clientMac" maxlength="32" value="<c:out value="${requestScope.clientMac}" />" /> &nbsp;&nbsp;
    WifiMac:<select type="text" id="wifiMac" name="wifiMac">
        <option value="">No Select</option>
        <option value="205CFA7E2957">205CFA7E2957</option>
        <option value="205CFA7DE28B">205CFA7DE28B</option>>
    </select> &nbsp;&nbsp;
    <input type="button" onclick="changeClientMac();" value="切換">
    <br><br>
    <hr size="1" color="aabbcc" width="100%">

    <textarea id="showArea" rows="40" cols="60" disabled></textarea>

</div><!-- #main -->

<script type="text/javascript">
    var clientMac = $("#clientMac").val();
    var wifiMac = $("#wifiMac").val();
    var sock;
    $(function(){
        websocket(clientMac, wifiMac);
    });

    function changeClientMac() {
        $("#showArea").val("");
        clientMac = $("#clientMac").val();
        wifiMac = $("#wifiMac").val();
        if(sock) {
            data = {"clientMac":clientMac, "wifiMac":wifiMac};
            sock.send(JSON.stringify(data));
        } else {
            window.alert("远程连接异常,请联系管理员.");
        }
    }

    function websocket(clientMac, wifiMac){
        var url = "${pageContext.servletContext.contextPath}/rssi";
        sock = new SockJS(url);
        sock.onopen = function () {
            //console.log('open');
            data = {"clientMac":clientMac, "wifiMac":wifiMac};
            sock.send(JSON.stringify(data));
        };
        sock.onmessage = function (e) {
            var data = e.data;

            var rs = JSON.parse(data);
            if(rs.success) {
                $("#showArea").append(rs.data + "\r");
            }
        };
        sock.onclose = function () {
            console.log('close');
        };
    };

</script>

</body>
</html>