<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>api文档</title>
<link rel="stylesheet" href="/Yplat/resource/css/bootstrap.min.css">
<script type="text/javascript" src="/Yplat/resource/js/jquery.min.js"></script>
<script type="text/javascript" src="/Yplat/resource/js/msgTool.js"></script>
<script type="text/javascript" src="/Yplat/resource/encrypt/aes.js"></script>
<script type="text/javascript" src="/Yplat/resource/encrypt/rsa.js"></script>
<style type="text/css">
h1 {text-align: center;margin-bottom: 50px;}
</style>
</head>
<body>
<h1>API接口详情</h1>
<h3>接口描述：${describe!};接口代号：<i id="intCode" style="color:red;"></i></h3>
<button class="btn btn-primary" onclick="login()">获取会话</button>
<h4>接口是否加密：<#if enCode>是<#else>否</#if></h4>
<h4>请求参数（reqBody）</h4>
<table class="table table-striped">
	<thead>
	    <tr>
	      <th>字段名</th>
	      <th>字段类型</th>
	      <th>字段中文描述</th>
	      <th>字段长度</th>
	      <th>字段是否必填</th>
	    </tr>
  	</thead>
	<tbody id="reqBody">
	    <#if reqBody??>
	    	<#list reqBody as req>
			<tr>
				<td>${req.fieldName!}</td>
				<td>${req.classType!}</td>
				<#if req.apiDescribe??>
				<td>${req.apiDescribe.describe!}</td>
				<td>${req.apiDescribe.length!}</td>
				<#if req.apiDescribe.required>
					<td>必填</td>
				<#else>
					<td>非必填</td>
				</#if>
				<#else>
				<td></td>
				<td></td>
				<td></td>
				</#if>
			</tr>
			<#if req.classType == 'java.util.List' && req.list??>
			
			<#list req.list as reqlist>
			<tr>
				<td>${reqlist.fieldName!}</td>
				<td>${reqlist.classType!}</td>
				<#if reqlist.apiDescribe??>
				<td>${reqlist.apiDescribe.describe!}</td>
				<td>${reqlist.apiDescribe.length!}</td>
				<#if reqlist.apiDescribe.required>
					<td>必填</td>
				<#else>
					<td>非必填</td>
				</#if>
				<#else>
				<td></td>
				<td></td>
				<td></td>
				</#if>
			</tr>
			
			<#if !reqlist_has_next>
			<tr>
			<td>${req.fieldName!}--end</td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			</tr>
			</#if>
			
			</#list>
			
			</#if>
		</#list>
	    <#else>
	    	<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
	    </#if>
		
		
	</tbody>
</table>

<h4>返回参数（rspBody）</h4>

<table class="table table-striped">
	<thead>
	    <tr>
	      <th>字段名</th>
	      <th>字段类型</th>
	      <th>字段中文描述</th>
	      <th>字段长度</th>
	      <th>字段是否必填</th>
	    </tr>
  	</thead>
	<tbody>
		<#if rspBody??>
			<#list rspBody as rsp>
			<tr>
				<td>${rsp.fieldName!}</td>
				<td>${rsp.classType!}</td>
				<#if rsp.apiDescribe??>
				<td>${rsp.apiDescribe.describe!}</td>
				<td>${rsp.apiDescribe.length!}</td>
				<#if rsp.apiDescribe.required>
					<td>必填</td>
				<#else>
					<td>非必填</td>
				</#if>
				<#else>
				<td></td>
				<td></td>
				<td></td>
				</#if>
			</tr>
			
			<#if rsp.classType == 'java.util.List' && rsp.list??>
			
			<#list rsp.list as reqlist>
			<tr>
				<td>${reqlist.fieldName!}</td>
				<td>${reqlist.classType!}</td>
				<#if reqlist.apiDescribe??>
				<td>${reqlist.apiDescribe.describe!}</td>
				<td>${reqlist.apiDescribe.length!}</td>
				<#if reqlist.apiDescribe.required>
					<td>必填</td>
				<#else>
					<td>非必填</td>
				</#if>
				<#else>
				<td></td>
				<td></td>
				<td></td>
				</#if>
			</tr>
			
			<#if !reqlist_has_next>
			<tr>
			<td>${rsp.fieldName!}--end</td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			</tr>
			</#if>
			
			</#list>
			
			</#if>
			
		</#list>
		<#else>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</#if>
		
	</tbody>
</table>

</br>

<textarea rows="20" cols="50" id="reqStr" style="margin-left: 90px;"></textarea>
<button class="btn btn-primary" onclick="jiazai()">加载报文</button>
<button class="btn btn-primary" onclick="msgReq(0)">明文请求</button>
<button class="btn btn-primary" onclick="msgReq(1)">密文请求</button>
<textarea rows="20" cols="50" id="rspStr"></textarea>

<script>

	var path = "<@url src=""> </@url>".replace(" ","");
    var msg = new msgTool();
    $("#intCode").text(getQueryString("doc"));
	function jiazai(){
		var reqJson = {};
		var trList = $("#reqBody").children("tr");
		for (var i=0;i<trList.length;i++) {
		    var tdArr = trList.eq(i).find("td");
		    if(tdArr.eq(1).text() != "java.util.List"){
		    	reqJson[tdArr.eq(0).text()] = "";
		    }
		}
		$("#reqStr").text(JSON.stringify(reqJson));	
	}
	
	function msgReq(s){
		var text = $("#reqStr").val();
		var json = JSON.parse(text);
		var code = getQueryString("doc");
		if(s == 0){
			msg.sendMsg('/api',json,code,function(data){
    			$("#rspStr").text(JSON.stringify(data));	
	    	},function(data){
	    		$("#rspStr").text(JSON.stringify(data));
	    	});
		}else{
			msg.sendEncryptMsg('/api',json,code,function(data){
    			$("#rspStr").text(JSON.stringify(data));
	    	},function(data){
	    		$("#rspStr").text(JSON.stringify(data));
	    	});
		}
		
	}
	
	function getQueryString(name) { 
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
        var r = window.location.search.substr(1).match(reg); 
        if (r != null) return unescape(r[2]); 
        return null; 
    }
    
    function login(){
    	msg.sendMsg('/api',{userName:'czs',password:'123456',phoneNo:'18892872738'},'userLogin',function(data){
    		sessionStorage.setItem("rsaKey",data.body.rsaKey);
    		alert("获取会话成功");
    	},function(data){
    		alert(data.head.retMsg);
    	});
    }
</script>
</body>
</html>