/**
 * 封装的请求类
 */
function msgTool(url,msgObj){ 
	this._url = url;
	if(msgObj!=null){
		this._msgObj = msgObj;
	}else{
		this._msgObj = new Object();	
	}
}

msgTool.prototype = {
		/** 发送明文请求 **/
		sendMsg:function(url,bodyObj,code,sucCallback,failCallback){
			var rocket = getCookie("rocket");
			var reqJson = {
					"head":{
						"mark":rocket,
						"bussCode":code
					},
					"body":bodyObj
			};
			this._msgObj = JSON.stringify(reqJson);
 			this._url = path+url;
			var ttkn2 = getCookie("ttl");
			try{
				$.ajax({
					type:"POST",//提交数据的类型 POST GET
					url:this._url,
					headers:{
						"ttl":ttkn2
					},
					data:this._msgObj,  //提交的数据
					timeout : 60000, //超时时间设置，单位毫秒
					datatype: "text",//"xml", "html", "script", "json", "jsonp", "text".
					//在请求之前调用的函数
					beforeSend:function(){
	 					 
					},
					success:function(data){ //成功返回之后调用的函数  
	 					var rsObj = null;
	 					var isjson = typeof(data) == "object" && Object.prototype.toString.call(data).toLowerCase() == "[object object]" && !data.length; 
						if(isjson){
							rsObj = data 
						}else{
							try{  
							rsObj = JSON.parse(data);
							}catch(e){
								rsObj = eval('['+data+']')
							}
						}
						console.log("resMsg:"+JSON.stringify(data));
						var retCode = rsObj.head.retCode ;
						setCookie("rocket",rsObj.head.retSign) ;
						if(retCode =='1110'){
							alert("会话超时");
							return ;
						}
						if(retCode =='1112'){
							console.log("交易已受理，请勿重复提交请求。");
							alert("交易已受理，请勿重复提交请求。");
							return ;
						}
						if(retCode !='0000'){
							if (failCallback!=undefined) {
								failCallback(rsObj);
								return;
							} else {
								alert(rsObj.head.retMsg);
								return;
							}
						}
						if(sucCallback!=undefined) 	sucCallback(rsObj);
						return rsObj;
					},
	 				error: function(){ //调用出错执行的函数   //请求出错处理
	  					console.log("请检查网络问题！");
	  					var rsp = {
  							head:{
  								retCode:"2000",
  								retMsg:"服务器开了个小差，请稍后再试！",
  							}
  						};
  						failCallback(rsp);
						return ;
					}
				});
			}catch(e){
				var rsp = {
					"head":{
						"retCode":"2000",
						"retMsg":"服务器开了个小差，请稍后再试！",
					}
				};
				failCallback(rsp);
				return;
			}
		},
		/** 发送密文请求 **/
		sendEncryptMsg:function(url,bodyObj,code,sucCallback,failCallback){
        	var key = getKey();
        	var encrypt = new JSEncrypt();
        	encrypt.setPublicKey(sessionStorage.getItem("rsaKey"));
        	var encryptKey = encrypt.encrypt(key);
	        var cipherText = AESEnc(key,JSON.stringify(bodyObj));
	        //var plainText = AESDec(key,cipherText);
	        var encryptObj = cipherText + "#" + encryptKey;
	        var rocket = getCookie("rocket");
	        var reqJson = {
					"head":{
						"mark":rocket,
						"bussCode":code
					},
					"body":encryptObj
			};
			this._msgObj = JSON.stringify(reqJson);
 			this._url = path+url;
			var ttkn2 = getCookie("ttl");
			try{
				$.ajax({
					type:"POST",//提交数据的类型 POST GET
					url:this._url,
					headers:{
						"ttl":ttkn2
					},
					data:this._msgObj,  //提交的数据
					timeout : 60000, //超时时间设置，单位毫秒
					datatype: "text",//"xml", "html", "script", "json", "jsonp", "text".
					//在请求之前调用的函数
					beforeSend:function(){
	 					 
					},
					success:function(data){ //成功返回之后调用的函数  
	 					var rsObj = null;
	 					var isjson = typeof(data) == "object" && Object.prototype.toString.call(data).toLowerCase() == "[object object]" && !data.length; 
	 					if(isjson){
							rsObj = data 
						}else{
							try{  
							rsObj = JSON.parse(data);
							}catch(e){
								rsObj = eval('['+data+']')
							}
						}
						console.log("resMsg:"+data);
						var retCode = rsObj.head.retCode ;
						setCookie("rocket",rsObj.head.retSign) ;
						if(retCode == "1110"){
							alert("会话超时");
							return ;
						}
						if(retCode =='1112'){
							console.log("交易已受理，请勿重复提交请求。");
							alert("交易已受理，请勿重复提交请求。");
							return ;
						}
						if(retCode !='0000'){
							if (failCallback!=undefined) {
								failCallback(rsObj);
								return;
							} else {
								alert(rsObj.head.retMsg);
								return;
							}
						}
						var bodyStr = AESDec(key,rsObj.body);
						rsObj.body = JSON.parse(bodyStr);
						if(sucCallback!=undefined) 	sucCallback(rsObj);
						return rsObj;
					},
	 				error: function(){ //调用出错执行的函数   //请求出错处理
	  					console.log("请检查网络问题！");
	  					var rsp = {
							head:{
								retCode:"2000",
								retMsg:"服务器开了个小差，请稍后再试！",
							}
						};
						failCallback(rsp);
						return ;
					}
				});
			}catch(e){
				var rsp = {
					"head":{
						"retCode":"2000",
						"retMsg":"服务器开了个小差，请稍后再试！",
					}
				};
				failCallback(rsp);
				return;
			}
		}
}

function setCookie(name,value) { 
	var Days = 30; 
	var exp = new Date(); 
	exp.setTime(exp.getTime() + Days*24*60*60*1000); 
	document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString(); 
} 

function getCookie(name) {
	var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
	if(arr=document.cookie.match(reg))
		return unescape(arr[2]);
	else
		return null;

}