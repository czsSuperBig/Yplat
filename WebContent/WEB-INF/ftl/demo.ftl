<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type=text/javascript src="<@url src="/jquery.min.js"> </@url>"></script><#-- js引入 -->
<link rel="stylesheet" type="text/css" href="<@url src="/style.css"> </@url>"><#-- css引入 -->
</head>
<body>
${goodsName}---------${goodsId}<br/>
<#list list as a>
----${a}------<br/>
</#list>
</body>
</html>