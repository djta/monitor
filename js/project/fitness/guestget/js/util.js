function ajaxCall(url, map) {
	if (url.indexOf(contextPath) != 0) {
		url = contextPath + url;
	}
	var method = map["method"];
	if (method == null) {
		method = "get";
	}
	method = method.toLowerCase();
	var form = map["form"];
	var btn = map["button"];
	var error = map["error"];
	var callback = map["success"];
	var async = map["async"];
	var response = map["response"];
	var progress = map["progress"];
	var dataType = map["dataType"];
	if (dataType == null) {
		if (response != null) {
			dataType = "html";
		} else {
			dataType = "script";
		}
	}

	if (btn != null) {// 控制按钮不可用
		$("#" + btn).attr("disabled", true);
	}

	if (progress == true) {// 控制等待提示
		ccms.util.loadClass.showLoad();
	}

	var data = null;
	if (form != null) {
		var formObj = $("#" + form);
		if (formObj.length > 0) {
			data = formObj.serialize();
			// data = getFormValues(form);
		} else {
			data = $("form[name='" + form + "']").serialize();
		}
	} else if (method == "post") { /* 如果POST方法没指定form,则重构url及data数据 */
		data = url.substring(url.indexOf("?") + 1, url.length);
		url = url.substring(0, url.indexOf("?"));
	}

	$.ajax({
		type : method,
		url : url,
		data : data,
		dataType : dataType,
		async : async == false ? false : true,
		error : function(XMLHttpRequest, txtStatus, errThrow) {
			switch (XMLHttpRequest.status) {
			case (500):
				$.toptip('系统错误.', 2000, 'error');
				if (error != null) {// 执行错误回调函数
					error();
				}
				break;
			case (400):
				$.toptip('系统错误.', 2000, 'error');
				break;
			case (401):
				$.toptip('要访问的服务需要 SSL.', 2000, 'error');
				break;
			case (403):
				$.toptip('拒绝访问.', 2000, 'error');
				break;
			case (404):
				$.toptip('要访问的服务不存在.', 2000, 'error');
				break;
			case (408):
				$.toptip('请求超时.', 2000, 'error');
				break;
			default:
				if (errThrow.toString().indexOf("JSON.parse") < 0) {
					$.toptip('其他错误.' + errThrow, 20000, 'error');
				}
			}
			;
		},
		complete : function(XMLHttpRequest, textStatus) {
			if (btn != null) {// 控制按钮可用
				$("#" + btn).removeAttr("disabled");
			}
			if (progress == true) {// 控制等待提示消失
				ccms.util.loadClass.hideLoad();
			}

			var contentType = XMLHttpRequest.getResponseHeader("Content-type");
			var text = XMLHttpRequest.responseText;
			if (contentType.indexOf("text/login") >= 0) {// 后台返回需要登录才能操作
				if ($("#uri")) {
					var uri = url + (data == null ? "" : "?" + data)
							+ (url.indexOf("?") < 0 && data == null ? "?ajaxcall=true" : "&ajaxcall=true") + "&json="
							+ JSON.stringify(map).toLowerCase();
					$("#uri").val(uri);
				}

				if (typeof (loginpop) != undefined && typeof (loginpop) != "undefined") {
					loginpop();
				} else if ($("#modalLogin").length > 0) {// 存在弹出窗定义
					$("#modalLogin").modal();
				} else {
					window.location = contextPath + "/" + window.location.hash;
				}
				return;
			}
			if (text.indexOf("_ajax_VE8374yz_") > 0)// 跳转到登录界面
			{
				window.location = contextPath + "/" + window.location.hash;
			}
			if (XMLHttpRequest.status == 200) {
				if (contentType.indexOf("text/validate") < 0) {// 验证失败的自定义错误
					if (contentType.indexOf("text/javascript") < 0) // js代码会被jquery自动执行
					{
						if (response != null) {
							$("#" + response).html(text);
						}
					}
					if (callback != null) {
						if (dataType == "json") {
							var json = null;
							try {
								json = eval("(" + text + ")");
							} catch (e) {
								$.toptip('JSON格式不合法.', 2000, 'error');
							}
							if (json != null)
								callback(json);
						} else {
							callback();
						}
					}
				} else {// 查询时出现验证错误
					if (dataType == "json") {
						try {
							eval(text);
						} catch (e) {
							$.toptip('语法错误.', 2000, 'error');
						}
					} else {
						eval(text);
					}

				}
			}
		}
	});
}

//获取radio值
function getRadioValue(name){
	var obj;
	obj = document.getElementsByName(name);
	if(obj!=null){
		if(typeof(obj.length) == "undefined"){
			return obj.value;
		}
		var i;
		for(i=0;i<obj.length;i++){
			if(obj[i].checked){
				return obj[i].value;
			}
		}
	}
	return '';
}
//获取checkbox值，逗号拼接
function getCheckboxValue(name){
	var obj;
	obj = document.getElementsByName(name);
	if(obj!=null){
		var str = "";
		var i;
		if(obj.length){
			for(i=0;i<obj.length;i++){
				if(obj[i].checked){
					str += obj[i].value+",";
				}
			}
			if(str.length > 0){
				str = str.substring(0,str.length-1);
			}
		}else{
			if(obj.checked){
				str = obj.value;
			}
		}
		return str;
	}
	return '';
}

//通过get提交ajaxCall
function getAjaxCall(uri, isprogress, callback){
	ajaxCall(uri, {
		method : "get",
		progress : isprogress == true ? true : false,
		dataType : "script",
		success : function() {
			if( undefined != callback ){
				callback();
			}
		}
	});
}