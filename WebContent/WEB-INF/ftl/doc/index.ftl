<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>api文档</title>
<link rel="stylesheet" href="<@url src="/resource/css/bootstrap.min.css"> </@url>">
<script type="text/javascript" src="<@url src="/resource/js/jquery.min.js"> </@url>"></script>
<style type="text/css">
h1 {text-align: center;margin-bottom: 50px;}
</style>
</head>
<body>
<h1>API文档列表</h1>
<table class="table table-striped">
	<thead>
		<tr>
			<th>api代码</th>
			<th>api描述</th>
			<th>操作</th>
		</tr>
	</thead>
	<tbody>
		<#list apiMap?keys as key>
			<tr>
				<td>${key}</td>
				<td>${apiMap[key].describe}</td>
				<td><button class="btn btn-primary" onclick="xiangqing('${key}')">详情</button></td>
			</tr>
		</#list>

		
	</tbody>
</table>

<script>

function xiangqing(code){
	var str = "<@url src="/api?doc="> </@url>";
	str = str.replace(" ",""); 
	window.location.href = str + code;
}

</script>