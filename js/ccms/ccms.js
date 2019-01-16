/*ccms lib*/
(function() {
	/* ccms命名空间 */
	window["ccms"] = {};
	window["club"] = {};
	
var callback;
var
/* 工具类 */
$Util = {
	/**
	 * 通用的ajax调用 参数 url : 服务地址(不加项目名) 参数 map (json格式) method:提交方式 (GET POST)
	 * response:加载元素的div ID progress:是否开启等待提示(true,false) form:提交的表单ID
	 * button:按钮ID(防止重复提交) async:是否异步提交，默认异步(true,false) dataType:服务器返回的数据类型
	 * (html,script,json)默认情况如果该值为空，response有值则为html，无则为script  showerror是否显示错误消息
	 * success:成功回调函数 error:出错的回调函数
	 */
	ajaxCall : function(url, map) {
		if (url.indexOf(contextPath) != 0) {
			url = contextPath + url;
		}
		var method = map["method"].toLowerCase(), form = map["form"], btn = map["button"], error = map["error"], 
		callback = map["success"], async = map["async"], response = map["response"], progress = map["progress"], 
		dataType = map["dataType"], data = map["data"],showerror=map["showerror"];
		if(progress==true){
			ccms.util.loadClass.showLoad();
		}
		if (dataType == null) {
			if (response != null) {
				dataType = "html";
			} else {
				dataType = "script";
			}
		}
		var btnObj = null;
		if (btn != null) {/* 控制按钮不可用 */
			if (btn instanceof Object) {
				btnObj = btn;
			} else {
				btnObj = $("#" + btn);
			}
			btnObj.attr("disabled", true);
		}
		/* var data = null; */
		if (form != null) {
			var formObj = $("#" + form);
			if (formObj.length > 0) {
				data = formObj.serialize();
			} else {
				data = $("form[name='" + form + "']").serialize();
			}
		}
		map.islogin=map.islogin==null?true:map.islogin;
		var access_token=null;
		if(typeof($.cookie)!='undefined' && $.cookie('access_token')!="\"\"" && map.islogin){
			access_token = $.cookie('access_token');
		}
		$.ajax({
			type : method,
			beforeSend : function(request) {
				if (access_token) {
					request.setRequestHeader("Authorization", "Bearer "
							+ access_token);
				}
			},
			url : url,
			data : data,
			dataType : dataType,
			async : async == false ? false : true,
			error : function(XMLHttpRequest, txtStatus, errThrow) {
				if(progress==true){
					ccms.util.loadClass.hideLoad();
				}
				switch (XMLHttpRequest.status) {
				case (500):
					ccms.dialog.notice("系统错误.", 1500);
					if (error != null) {/* 执行错误回调函数 */
						error();
					}
					break;
				case (400):
					ccms.dialog.notice("系统错误.", 1500);
					break;
				case (401):
					ccms.dialog.notice("要访问的服务需要 SSL.", 1500);
					break;
				case (403):
					ccms.dialog.notice("拒绝访问.", 1500);
					break;
				case (404):
					ccms.dialog.notice("要访问的服务不存在.", 1500);
					break;
				case (408):
					ccms.dialog.notice("请求超时.", 1500);
					break;
				default:
					ccms.dialog.notice("其他错误.", 1500);
				}
			},
			complete : function(XMLHttpRequest, textStatus) {
				if(progress==true){
					ccms.util.loadClass.hideLoad();
				}
				if (btnObj) {/* 控制按钮可用 */
					btnObj.removeAttr("disabled");
				}
				var contentType = XMLHttpRequest.getResponseHeader("Content-type");
				var text = XMLHttpRequest.responseText;
				if(contentType.indexOf("text/login") >= 0){//后台返回需要登录才能操作
					if(typeof(loginpop)!=undefined){
						loginpop();
					}
					return;
				}
				
				if (text!=undefined && text.indexOf("_ajax_VE8374yz_") > 0)/* 跳转到登录界面 */
				{
					 window.location = contextPath+"/"; 
				}
				if (XMLHttpRequest.status == 200) {
					if(contentType.indexOf("text/validate") < 0){//验证失败的自定义错误
						if(contentType.indexOf("text/javascript") < 0) //js代码会被jquery自动执行
						{
							if(response != null){
								$("#"+response).html(text);
							}
						}
						
						if (callback != null) { /* js代码会被jquery自动执行 */
							if (dataType == "json") {
								var json = null;
								try {
									json = eval("(" + text + ")");
								} catch (e) {
									ccms.dialog.notice("JSON格式不合法.", 1500);
								}
								if (json != null){
									if(json.access_token){//是登录
										callback(json);
									}else{
										if(json.resultcode==0){
											if(showerror!=undefined && showerror==false){//是否显示错误消息提示
												callback(json);
											}else{
												var msg=json.message;
												if(msg==undefined || msg==null || msg==''){
													msg='操作失败';
												}
												ccms.dialog.alert(msg);
											}
										}else{
											callback(json,url);
										}
									}
								}
							} else {
								callback(text);
							}
						}
					} else {/* 查询时出现验证错误 */
						if (dataType == "json") {
							try {
								eval(text);
							} catch (e) {
								if (text.indexOf("invalid_token") > 0)/* 跳转到登录界面 */
								{
									 window.location = contextPath+"/"; 
								}else{
									ccms.dialog.notice("语法错误.", 1500);
								}
							}
						}
					}
				}
			}
		});
	},
	/* 清除错误提示信息 */
	clearErrorMessages : function() {
		$("label.error").each(function() {
			$(this).remove();
		});
		$(".form-control.error").each(function() {
			$(this).removeClass("error");
		});
	},
	/* 按照既定清除规则清理表单 */
	clearForm : function(formId) {
		var f = $("#" + formId);
		if (f.length == 0) {
			f = document.forms[formId];
		} else {
			f = f[0];
		}

		for (var i = 0; i < f.elements.length; i++) {
			var e = f.elements[i];
			if (e.type == "text" || e.type == "hidden"
					|| e.tagName == "TEXTAREA" || e.type == "password") {
				if (e.getAttribute("preserve") != "true")
					e.value = "";
			}
			if (e.tagName == "SELECT") {
				if (e.getAttribute("preserve") != "true")
					e.options.selectedIndex = 0;
			}
			if (e.type == "checkbox") {
				if (e.getAttribute("default") == "checked") {
					e.checked = true;
				} else {
					if (e.getAttribute("preserve") != "true") {
						$(e).iCheck("uncheck");
					}
				}
			}
			if (e.type == "radio") {
				if (e.getAttribute("default") == "checked") {
					e.checked = true;
				} else {
					if (e.getAttribute("preserve") != "true") {
						$(e).iCheck("uncheck");
					}
				}
			}
		}
		ccms.util.clearErrorMessages();
	},
	/* 错误信息展示 */
	setFormErrorMsg : function(formElementId, text) {
		ccms.util.clearErrorMessages();
		var obj = $("#" + formElementId);
		if (obj.length > 0) {
			if (obj.attr("type") == "radio"
					|| obj.attr("type") == "checkbox") {/* iCheck样式需特殊处理 */
				obj.parent().parent().append(
						$("<label/>").addClass("error").attr("for",
								formElementId).html(text));
			} else {
				obj.after($("<label/>").addClass("error").attr("for",
						formElementId).html(text));
			}
		}
	},
	/* json object to url */
	jsonToUrl : function(map) {
		var str = "";
		for (id in map) {
			str += "&" + id + "=" + map[id];
		}
		if (str.length > 0) {
			str = str.substring(1, str.length);
		}
		return str;
	},
	/* 页面返回 */
	gotoBack : function() {
		if(self.frameElement && self.frameElement.tagName.toLowerCase()=="iframe"){
			var uri = $(self.frameElement).attr("gotoBackUrl");
			if(uri!=""){
				window.location.href = uri;
			}else{
				window.history.back();
			}
		}else{
			window.history.back();
		}
	},
	/* 页面跳转公共方法 */
	gotoPage : function(url) {
		window.location = '#' + url;
	},
	decimal2: function(nm, n) {
		var ts = "1";
		var tn = 1;
		for (var i = 0; i < n; i++) {
			ts += "0";
		}
		tn = parseInt(ts);
		var num = Math.round(nm * tn) / tn;
		var numstr = num + "";
		var dotindex = numstr.indexOf(".");
		var newstr = '';
		if (dotindex > 0) {
			newstr = numstr.substring(dotindex, numstr.length);
			if (newstr > 2) {
				numstr = numstr.substring(0, numstr.length - 2);
				num = parseFloat(numstr);
			}
		}
		return num;
	},
	getAge : function(birthday) {/* 得到年龄 */
		if (birthday == null) {
			return '&nbsp;';
		}
		var now = new Date();
		var byear = birthday.substring(0, 4);
		var age = now.getFullYear() - parseInt(byear);
		if (age <= 0) {
			return '&nbsp;';
		}
		return age;
	},
	getAgeByIdcard : function(idcard) {/* 得到年龄 */
		if (idcard == null || idcard == "") {
			return '&nbsp;';
		}
		var birthday = null;
		var len = idcard.length;
		if( 15 == len ){
			var re_fifteen = /^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/;
			var arr = idcard.match(re_fifteen);
			birthday = "19" + arr[2] + "-" + arr[3] + "-" + arr[4];
		}else if( 18 == len ){
			var re_eighteen = /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X|x)$/;
			var arr = idcard.match(re_eighteen);
			birthday = arr[2] + "-" + arr[3] + "-" + arr[4];
		}
		if( null == birthday || "" == birthday ){
			return "";
		}
		return ccms.util.getAge(birthday);
	},
	getImageSize : function(sizestr) {
		var size = parseInt(sizestr);
		return (size / 1024).toFixed(2) + 'KB';
	},
	getImageUrl : function(id) {/* 得到图片url */
		return contextPath + "/image/" + id;
	},
	getVideoUrl : function(id) {/* 得到图片url */
		return contextPath + "/video/" + id;
	},
	getResource: function(id) {/* 得到资源图片url */
		id=id.replace(/\\/g,"-");
		id=id.replace("/","-");
		id=id.replace("/","-");
		id=id.replace("/","-");
		id=id.replace("/","-");
		var imageUrl=contextPath + "/resource/image/" + id;
		var videoUrl=contextPath + "/resource/video/" + id;
		var audioUrl=contextPath + "/resource/audio/" + id;
		return {'image':imageUrl,'video':videoUrl,'audio':audioUrl};
	},
	getCurrentDateTime : function() {/* 得到当前时间 */
		var date = new Date();
		return date.format("yyyy-MM-dd hh:mm:ss");
	},
	toDate : function(objDate) {
		var date = new Date(Date.parse(objDate.replace(/-/g, "/")));
		return date;
	},
	absPos : function(node) {
		var x = y = 0;
		do {
			x += node.offsetLeft;
			y += node.offsetTop;
		} while (node = node.offsetParent);
		return {
			'x' : x,
			'y' : y
		};
	},
	postData : function(url, map) {
		map.method = 'post';
		map.dataType = "json";
		ccms.util.ajaxCall(url, map);
	},
	setFormData : function(formName, dataMap) {/* 给表单赋值 */
		if (formName == null) {
			return;
		}
		if (dataMap == undefined) {
			return;
		}
		var f = document.forms[formName];
		if (f == undefined) {
			return;
		}
		var fLength = f.elements.length;
		for (var i = 0; i < fLength; i++) {
			var e = f.elements[i];
			var n = e.name;
			if(n==undefined || n=="" ){
				continue;
			}
			var v = dataMap[n];
			if (v == undefined || (v == '' && v != 0)) {
				continue;
			}
			if (e.type == "text" || e.type == "hidden"
					|| e.tagName == "TEXTAREA" || e.type == "password") {
				e.value = v;
			}else if (e.tagName == "SELECT") {
				ccms.util.setComboValue(n, v, formName);
			}else if (e.type == "checkbox") {
				var s = (v+"").split(",");
				for(var b=0;b<s.length;b++){
					if(s[b]!=null && s[b]!="" && e.value == s[b])
						$(e).iCheck("check");
				}
			}else if (e.type == "radio") {
				if(e.value == v){
					$(e).iCheck("check");
				}
			}
		}
	},
	loadClass: {
		state: "",
		hideLoad: function(){
			$("#ajax-loading").hide();
			$(document.body).css("overflow",ccms.util.loadClass.state);
		},
		showLoad: function(){
			ccms.util.loadClass.state = $(document.body).css("overflow");
			var loadObj = $("#ajax-loading");
			$(document.body).css("overflow","hidden");
			if (loadObj.length == 0) {
				var loaddiv = '<div id="ajax-loading" class="ajax-loading"><div class="ajax-loading-wait">';
				loaddiv+='<svg viewBox="0 0 150 150">';
				loaddiv+='<g>';
				loaddiv+='<circle r="10" cy="16.6987298" cx="35" transform="translate(35, 16.698730) rotate(-30) translate(-35, -16.698730) " id="12"/>';
				loaddiv+='<circle r="10" cy="35" cx="16.6987298" transform="translate(16.698730, 35) rotate(-60) translate(-16.698730, -35) " id="11"/>';
				loaddiv+='<circle r="10" cy="60" cx="10" transform="translate(10, 60) rotate(-90) translate(-10, -60) " id="10"/>';
				loaddiv+=' <circle r="10" cy="85" cx="16.6987298" transform="translate(16.698730, 85) rotate(-120) translate(-16.698730, -85) " id="9"/>';
				loaddiv+='<circle r="10" cy="103.30127" cx="35" transform="translate(35, 103.301270) rotate(-150) translate(-35, -103.301270) " id="8"/>';
				loaddiv+='<circle r="10" cy="110" cx="60" id="7"/>';
				loaddiv+='<circle r="10" cy="103.30127" cx="85" transform="translate(85, 103.301270) rotate(-30) translate(-85, -103.301270) " id="6"/>';
				loaddiv+='<circle r="10" cy="85" cx="103.30127" transform="translate(103.301270, 85) rotate(-60) translate(-103.301270, -85) " id="5"/>';
				loaddiv+='<circle r="10" cy="60" cx="110" transform="translate(110, 60) rotate(-90) translate(-110, -60) " id="4"/>';
				loaddiv+='<circle r="10" cy="35" cx="103.30127" transform="translate(103.301270, 35) rotate(-120) translate(-103.301270, -35) " id="3"/>';
				loaddiv+='<circle r="10" cy="16.6987298" cx="85" transform="translate(85, 16.698730) rotate(-150) translate(-85, -16.698730) " id="2"/>';
				loaddiv+='<circle r="10" cy="10" cx="60" id="1"/>';
				loaddiv+='</g>';
				loaddiv+='</svg>';
				loaddiv+='</div></div>';
				$(document.body).append(loaddiv);
			} else {
				loadObj.show();
			}
			$(document.body).css("overflow","auto");
		}
	},
	ajaxLoad : function(obj, url, callback) {
		ccms.util.loadClass.showLoad();
		obj.load(url, function(response, status, xhr) {
			ccms.util.loadClass.hideLoad();
			switch (status) {
			case ("error"):
				ccms.dialog.notice("系统错误.", 1500, function() {
					/* $Util.gotoBack(); */
				});
				break;
			case ("timeout"):
				ccms.dialog.notice("访问超时,稍候请刷新重试.", 1500);
				break;
			case ("parsererror"):
				ccms.dialog.notice("格式转换错误.", 1500);
				break;
			case ("notmodified"):
				break;
			default:
				if (response.indexOf("_ajax_VE8374yz_") > 0)// 跳转到登录界面
				{
					window.location="/";
				}
				if (callback != undefined) {
					callback(response);
				}
			};
		});
	},
	loadDivPage : function(id, url, flag) {
		if (url == undefined || url == "")
			return;
		if (flag == undefined) {
			flag = false;
		}
		var hash = $Base64.encode(url);
		var tabObj = $("#menuTab a[href=\"#" + hash + "\"]");
		if (tabObj.length > 0) {
			$("#menuTab").find(".active").removeClass("active");
			tabObj.parent().addClass("active");
		}
		ccms.util.ajaxLoad($("#" + id), contextPath + url, function() {
			/**
			 * 页面初始化时给必填字段初始化
			 */
			$("#" + id + " label.required").each(function() {
				if ($(this).find(".red").length == 0) {
					$(this).append("<span class='red'>*</span>");
				}
			});
			// radio样式
			$('#' + id + ' input[type=radio]').iCheck({
				radioClass : 'iradio_square-blue',
				increaseArea : '20%'
			});
			// checkbox样式
			$('#' + id + ' input[type=checkbox]').iCheck({
				checkboxClass : 'icheckbox_square-blue',
				increaseArea : '20%'
			});
		});
	},
	/*渲染radio or checkbox*/
	renderRadio: function(name){
		$("input[name="+name+"]").iCheck({
			radioClass : 'iradio_square-blue',
			increaseArea : '20%'
		});
	},
	renderCheckbox: function(name){
		$("input[name="+name+"]").iCheck({
			checkboxClass : 'icheckbox_square-blue',
			increaseArea : '20%'
		});
	},
	refreshMenu : function(url) {// 刷新选中菜单
		if (url) {
			var tabObj = $("#menuTab a[href=\"#" + url + "\"]");
			$("#menuTab").find(".active").removeClass("active");
			tabObj.parent().addClass("active");
		}
	},
	getUrlVars : function() {
		var vars = [], hash;
		var hashes = window.location.href.slice(
				window.location.href.indexOf('?') + 1).split('&');
		for (var i = 0; i < hashes.length; i++) {
			hash = hashes[i].split('=');
			vars.push(hash[0]);
			vars[hash[0]] = hash[1];
		}
		return vars;
	},
	getUrlVar : function(name) {
		var value=ccms.util.getUrlVars()[name];
		if(value==null){
			value='';
		}
		return value;
	},
	getUserlogin : function() {
		if ($("#system_userlogin").length > 0) {
			return $("#system_userlogin").val();
		}
		return "";
	},
	getCurrentDate : function() {//得到当前时间
		if(typeof(ccms_sys_date)!=undefined){
			return ccms_sys_date;
		}
		return '';
	},
	getUser : function() {//得到用户信息
		var userMap={};
		if ($("#system_userlogin").length > 0) {
			userMap.userlogin=$("#system_userlogin").val();
		}
		if ($("#system_userid").length > 0) {
			userMap.userId=$("#system_userid").val();
		}
		if ($("#currentUserType").length > 0) {
			userMap.userType=$("#currentUserType").val();
		}
		if ($("#system_userDegree").length > 0) {
			userMap.userDegree=$("#system_userDegree").val();
		}
		
		return userMap;
	},
	initSelect : function(obj, namespace, selectKey, callback) {// 初始化一级下拉框
		obj.empty();
		var data = "namespace=" + namespace;
		ccms.util.postData("/common/domain.json", {
			data : data,
			success : function(data) {
				var str = "<option value=''>请选择</option>";
				for ( var key in data.result) {
					var dobj = data.result[key];
					var dKey = dobj.domainValue;
					var dText = dobj.domainText;
					str += "<option value=" + dKey + ((selectKey!=null&&dKey==selectKey)?" selected":"")+">" + dText + "</option>";
				}
				obj.append(str);
				if(callback != null){
					callback();
				}
			}
		});
	},
	initRadio : function(divId, name, namespace, isBr, selectKey, callback) {// 初始化单选按钮
		var data = "namespace=" + namespace;
		ccms.util.postData("/common/domain.json", {
			data : data,
			success : function(data) {
				var str = "";
				for ( var key in data.result) {
					var dobj = data.result[key];
					var dKey = dobj.domainValue;
					var dText = dobj.domainText;
					str += "<input type='radio' value=" + dKey + ((selectKey!=null&&dKey==selectKey)?" checked":"")+" name='"+name+"' id='"+name+"_"+dKey+"' /><label for='"+name+"_"+dKey+"'>" + dText + "&nbsp;</label>";
					if(isBr == true){
						str += "<br/>";
					}
				}
				$("#"+divId).append(str);
				if(callback != null){
					callback();
				}
			}
		});
	},
	initCheckbox : function(divId, name, namespace, isBr, selectKey, callback) {// 初始化多选框
		var data = "namespace=" + namespace;
		if(selectKey == null || typeof(selectKey)=="undefined"){
			selectKey = "";
		}
		selectKey += ",";
		ccms.util.postData("/common/domain.json", {
			data : data,
			success : function(data) {
				var str = "";
				for ( var key in data.result) {
					var dobj = data.result[key];
					var dKey = dobj.domainValue;
					var dText = dobj.domainText;
					str += "<input type='checkbox' value=" + dKey + (selectKey.indexOf(dKey+",")>=0?" checked":"")+" name='"+name+"' id='"+name+"_"+dKey+"' /><label for='"+name+"_"+dKey+"'>" + dText + "&nbsp;</label>";
					if(isBr == true){
						str += "<br/>";
					}
				}
				$("#"+divId).append(str);
				if(callback != null){
					callback();
				}
			}
		});
	},
	showNextSelect : function(obj, namespace, tobj, selectKey, callback) {// 初使用二级下拉
		obj.empty();
		var id = "";
		if (typeof(tobj) == "object" && tobj) {
			id = $(tobj).val();
		}else{
			id = tobj;
		}
		var data = "namespace=" + namespace + "&id=" + id;
		ccms.util.postData("/common/domain.json", {
			data : data,
			success : function(data) {
				var str = "<option value=''>请选择</option>";
				for ( var key in data.result) {
					var dobj = data.result[key];
					var dKey = dobj.domainValue;
					var dText = dobj.domainText;
					str += "<option value=" + dKey + (dKey==selectKey?" selected":"")+">" + dText + "</option>";
				}
				obj.append(str);
				if(callback != null){
					callback();
				}
			}
		});
	},

	/**
	* radio checkbox 赋值
	*/

	setCheckboxValue:function(radioName,radioValue,formName){
		$("form[name='"+formName+"'] input[name='"+radioName+"']").each(function(){
			if($(this).val() == radioValue){
				$(this).iCheck("check");
			}
		});
	},

	/**
	* 下拉框赋值
	*/
	setComboValue:function(comboName,comboValue,formName){	   
		if( formName == "") { 
			return;
		}
	   var combo = document.forms[formName].elements[comboName];
	   var cantidad = combo.length;
	   for (var i = 0; i < cantidad; i++) {
	      if (combo[i].value == comboValue) {
	         combo[i].selected = true;
	      }
	    }   
	},
	//获取radiobutton值
	getRadioValue:function(val,formObj){
	    var obj;   
	    obj=document.forms[formObj].elements[val];
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
	},
	//获取checkbox值，逗号拼接
	getCheckboxValue:function(val,formObj){
	    var obj;   
	    obj=document.forms[formObj].elements[val];
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
	},
	//选择多个checkbox值,在界面生成中使用
	selectMultiCheckboxValue:function(ic,fc,fm){
		var str = "";
		$("form[name='"+fm+"'] input[name='"+ic+"']").each(function(){
			if($(this)[0].checked == true){
				str += $(this).val()+",";
			}
		});
		$("form[name='"+fm+"'] input[name='"+fc+"']").val(str);
	},
	setMulitCheckboxValue:function(fc,fc_value,fm){	
		var s = (fc_value+"").split(",");
		for(var b=0;b<s.length;b++){
			if(s[b]!=null && s[b]!="")
				ccms.util.setCheckboxValue(fc,s[b],fm);
		}
	},
	selectAll:function(id,formName){
		$("form[name='"+formName+"'] input[name='"+id+"']").each(function(){
			$(this).iCheck("check");
		});
	},
	unselectAll:function(id,formName){
		$("form[name='"+formName+"'] input[name='"+id+"']").each(function(){
			$(this).iCheck("uncheck");
		});
	},
	reverseselectAll:function(id,formName){
		$("form[name='"+formName+"'] input[name='"+id+"']").each(function(){
			if($(this)[0].checked == true){
				$(this).iCheck("uncheck");
			}else{
				$(this).iCheck("check");
			}
		});
	},
	followselectAll:function(id,formName,flag){
		if(flag == true){
			ccms.util.selectAll(id,formName);
		}else{
			ccms.util.unselectAll(id,formName);
		}
	},initUserNameTypeHead:function(){//用户名自动补全
		var userNameTypeHeadObj=$("input.userNameTypeHead");
		if(userNameTypeHeadObj.length==0){
			return;
		}
		userNameTypeHeadObj.attr("autocomplete","off");
		var url=contextPath+'/user/searchUser.json';
		$.getScript(contextPath+"/js/bootstrap/js/bootstrap-typeahead.js",function(){
			if(userNameTypeHeadObj.typeahead==undefined){
				return;
			}
			userNameTypeHeadObj.typeahead({
				ajax : {
					url : url,
					method : 'post',
					triggerLength : 1, // 输入几个字符之后，开始请求
					loadingClass : null, //
					preDispatch : function(query) {
						var para = {};
						para.userName = query;
						return para;
					},
					preProcess : function(data) {
						var list = data.result;
						var result = [];
						for (var i = 0; i < list.length; i++) {
							var map = list[i];
							map.showname = map.userlogin + '-'
									+ map.userName;
							result.push(map);
						}
						return list;
					}
				},
				display : "showname",
				val : "userName",
				items : 10,
				matcher : function(item) {
					return true;
				},
				itemSelected : function(item, value) {
					userNameTypeHeadObj.val(value);
				}
			});
		});
	}
};

/* 自定义弹出框类，以免更换弹出框架对整体改动 */
$dialog = {
	/* 弹出框 */
	open : function(map) {
		var url = map['url'], id = map['id'], width = map['width'], height = map['height'], isFull = map['isFull'], html = null;
		this.callback = map['success'];
		if (id == undefined) {
			var num = parseInt(Math.random() * 10);
			id = num + "dlg";
		}
		if (url == undefined || id == undefined) {
			alert("Dialog's url and id is requried,please check your configuration.");
		}
		if ($('#_dlg' + id).length > 0) {
			$('#_dlg' + id).remove();
		}
		if (isFull == true) {
			width = $(document).width();
			height = $(document).height();
			html = '<div id="_dlg'
					+ id
					+ '" class="modal fade" role="dialog" tabindex="-1" aria-hidden="true">'
					+ '<div class="modal-dialog" style="height:'
					+ height
					+ 'px;width:'
					+ width
					+ 'px;margin:0;">'
					+ '<div class="modal-body" style="height:'
					+ height
					+ 'px;width:'
					+ width
					+ 'px;padding:0;overflow:hidden;"><iframe name="'
					+ id
					+ 'frame" width="'
					+ width
					+ 'px" height="'
					+ height
					+ 'px" src="'
					+ url
					+ '" frameBorder="0" ></iframe></div></div></div></div>';
		} else {
			if (width == undefined || width > ($(document).width() - 40)) {
				width = $(document).width() - 40;
			}
			if (height == undefined || height > ($(document).height() - 60)) {
				height = $(document).height() - 60;
			}
			html = '<div id="_dlg'
					+ id
					+ '" class="modal fade" role="dialog" tabindex="-1" aria-hidden="true">'
					+ '<div class="modal-dialog" style="height:'
					+ height
					+ 'px;width:'
					+ width
					+ 'px"><div class="modal-content"><div class="dialog-close" data-dismiss="modal" title="关闭"></div>'
					+ '<div class="modal-body"><iframe name="'
					+ id
					+ 'frame" width="'
					+ (width - 30)
					+ 'px" height="'
					+ (height - 40)
					+ 'px" src="'
					+ url
					+ '" frameBorder="0" ></iframe></div></div></div></div>';
		}

		$("body").append(html);

		$('#_dlg' + id).on('hidden.bs.modal', function(e) {
			/* 说明父界面还有弹出层 */
			if ($(".modal-backdrop").length > 0) {
				$("body").addClass("modal-open");
			}
			$(this).remove();
		});
		$('#_dlg' + id).modal("show");
	},
	/* 提示框 */
	alert : function(content, callback) {
		bootbox.alert(content, callback);
	},
	close : function(dlgid) {// 关闭窗口
		var divid;
		if(dlgid==undefined){
			var iframename = window.name;
			divid = iframename.replace("frame", '');
		}else{
			divid=dlgid;
		}
		if (window.parent == null)
			return;
		var obj = $('#_dlg' + divid, window.parent.document);
		if( obj.length == 0 ){	/** 兼容在父窗口中关闭弹窗 */
			obj = $('#_dlg' + divid, window.document);
		}
		$("body").css("padding-right","0px");
		obj.next("div.modal-backdrop").remove();
		$(".modal-open").css("overflow","auto");
		obj.hide();
		obj.remove();
		if (this.callback) {
			this.callback();
		}
	},
	notice : function(content, time, callback) {
		if (typeof (bootbox) != 'undefined')
			bootbox.alert(content);
		else {
			alert(content);
		}
		if (time > 0) {
			window.setTimeout(function() {
				$("button[data-bb-handler=ok]").click();
				if (callback != undefined) {
					callback();
				}
			}, time);
		}
	},
	/* 确认框 */
	confirm : function(content, yesCallback, noCallback) {
		bootbox.confirm(content, function(result) {
			if (result == true) {
				yesCallback();
			} else {
				if (noCallback != undefined) {
					noCallback();
				}
			}
		});
	},
	date : function(ob, callback) {
		var format = 'yyyy-mm-dd';
		var nowDate = new Date();
		$(ob).datetimepicker({
			language : 'zh-CN',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			minView : 3,
			format : format,
			startDate : "1920-1-1",
			initialDate : nowDate.format("yyyy-MM-dd")
		});
		if (callback != undefined) {
			$(ob).datetimepicker().on('changeDate', function(ev) {
				callback(this);
			});
		}
	},
	time : function(ob) {
		var nowDate = new Date();
		$(ob).datetimepicker({
			language : 'zh-CN',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 1,
			format : 'hh:ii',
			startDate : nowDate.format("yyyy-MM-dd")
		});
	},
	datetime : function(ob, callback) {
		var format = 'yyyy-mm-dd hh:ii:ss';
		var nowDate = new Date();
		$(ob).datetimepicker({
			language : 'zh-CN',
			weekStart : 1,
			todayBtn : 1,
			autoclose : 1,
			todayHighlight : 1,
			startView : 2,
			minView : 1,
			format : format,
			startDate : "1920-1-1",
			initialDate : nowDate.format("yyyy-MM-dd")
		});
		if (callback != undefined) {
			$(ob).datetimepicker().on('changeDate', function(ev) {
				callback(this);
			});
		}
	},daterangepicker:function(ob,callback, dayLimit){//日期环围
		var nowDate=new Date();
		if(dayLimit == undefined){
			dayLimit = 60;
		}
		nowDate.setDate(nowDate.getDate()+1);
		var optionSet1 = {
		         startDate: moment(),//moment().subtract('days', 29),
		         endDate: moment(),
		        // minDate: '2012-01-01',
		          maxDate:nowDate.format("yyyy-MM-dd"),
		         dateLimit: { days: dayLimit },
		         showDropdowns: true,
		         //showWeekNumbers: true,
		         timePicker: false,
		         timePickerIncrement: 1,
		         timePicker12Hour: true,
		         ranges: {
		            '今天': [moment(), moment()],
		            '昨天': [moment().subtract('days', 1), moment().subtract('days', 1)],
		            '最近7天': [moment().subtract('days', 6), moment()],
		            //'最近30天': [moment().subtract('days', 29), moment()]//,
		            '一个月内': [moment().subtract('month', 1), moment()]//,
		            //'这个月': [moment().startOf('month'), moment().endOf('month')],
		            //'上个月': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
		         },
		         opens: 'left',
		         buttonClasses: ['btn btn-default'],
		         applyClass: 'btn-small btn-primary',
		         cancelClass: 'btn-small',
		         format: 'YYYY-MM-DD',
		         separator: ' 至 ',
		         locale: {
		             applyLabel: '确定',
		             cancelLabel: '清除',
		             fromLabel: '从',
		             toLabel: '到',
		             customRangeLabel: '自定义',
		             daysOfWeek: ['日', '一', '二', '三', '四', '五','六'],
		             monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
		             firstDay: 1
		         }
		       };
		if($(ob).daterangepicker)
		$(ob).daterangepicker(optionSet1,
				  function(start, end, label) {
					  if(callback!=undefined){
						  callback(start.format("YYYY-MM-DD"),end.format("YYYY-MM-DD"));
					  }
				  }
		);
	}
};

window["ccms"]["util"] = $Util;
})();

window["ccms"]["dialog"] = $dialog;

/**
* 通用的ajax调用
* 参数 url : 服务地址(不加项目名)
* 参数 map (json格式)
		method:提交方式 (GET POST)
		response:加载元素的div ID
		progress:是否开启等待提示(true,false)
		form:提交的表单ID
		button:按钮ID(防止重复提交)
		async:是否异步提交，默认异步(true,false)
		dataType:服务器返回的数据类型 (html,script,json)默认情况如果该值为空，response有值则为html，无则为script
		success:成功回调函数
		error:出错的回调函数
*/
function ajaxCall(url, map)
{
		if (url.indexOf(contextPath) != 0) {
			url = contextPath + url;
		}
		var method = map["method"];
		if(method == null){
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
		if(dataType == null){
			if(response != null){
				dataType = "html";
			}else{
				dataType = "script";
			}
		}

		if(btn != null){//控制按钮不可用
			$("#"+btn).attr("disabled", true);
		}

		if(progress == true){//控制等待提示
			ccms.util.loadClass.showLoad();
		}

		var data = null;
		if (form != null){
			var formObj = $("#"+form);
			if(formObj.length > 0){
				data = formObj.serialize();
				//data = getFormValues(form);
			}else{
				data = $("form[name='"+form+"']").serialize();
			}
		}else if(method == "post"){	/*如果POST方法没指定form,则重构url及data数据*/
			data = url.substring(url.indexOf("?")+1,url.length);
			url = url.substring(0,url.indexOf("?"));
		}
		console.log("URI: " + url);

		$.ajax({
			type : method,
			url : url,
			data : data,
			dataType : dataType,
			async: async==false?false:true,
			error: function(XMLHttpRequest,txtStatus,errThrow) {
				switch (XMLHttpRequest.status){  
					case(500):
						ccms.dialog.notice("系统错误.",1500);
						if (error != null){//执行错误回调函数
							error();
						}
						break;  
					case(400):  
						ccms.dialog.notice("系统错误.",1500);
						break;  
					case(401):  
						ccms.dialog.notice("要访问的服务需要 SSL.",1500);
						break;  
					case(403):  
						ccms.dialog.notice("拒绝访问.",1500);
						break;  
					case(404):  
						ccms.dialog.notice("要访问的服务不存在.",1500);
						break; 
					case(408):  
						ccms.dialog.notice("请求超时.",1500);
						break;  
					default:  
						if(errThrow.toString().indexOf("JSON.parse") < 0){
							ccms.dialog.alert("其他错误."+errThrow);
						}
				};
			},
			complete : function(XMLHttpRequest, textStatus) {
				if(btn != null){//控制按钮可用
					$("#"+btn).removeAttr("disabled");
				}
				if(progress == true){//控制等待提示消失
					ccms.util.loadClass.hideLoad();
				}

				var contentType = XMLHttpRequest.getResponseHeader("Content-type");
				var text = XMLHttpRequest.responseText;
				if(contentType.indexOf("text/login") >= 0){//后台返回需要登录才能操作
					if($("#uri")){
						var uri = url + (data==null?"":"?"+data) + (url.indexOf("?")<0 && data==null?"?ajaxcall=true":"&ajaxcall=true") + "&json="+JSON.stringify(map).toLowerCase();
						$("#uri").val(uri);
					}

					if(typeof(loginpop)!=undefined){
						loginpop();
					}else if($("#modalLogin").length>0){//存在弹出窗定义
							$("#modalLogin").modal();
					}else{
						window.location = contextPath + "/" + window.location.hash;
					}
					return;
				}
				if (text.indexOf("_ajax_VE8374yz_")>0)//跳转到登录界面
				{
					window.location = contextPath + "/" + window.location.hash;
				}
				if(XMLHttpRequest.status == 200){
					if(contentType.indexOf("text/validate") < 0){//验证失败的自定义错误
						if(contentType.indexOf("text/javascript") < 0) //js代码会被jquery自动执行
						{
							if(response != null){
								$("#"+response).html(text);
							}
						}
						if (callback != null){
							if(dataType == "json"){
								var json = null;
								try{
									json = eval("(" + text + ")");
								}catch(e){
									ccms.dialog.notice("JSON格式不合法.",1500);
								}
								if(json != null) callback(json);
							}else{
								callback();
							}
						}
					}else{//查询时出现验证错误
						if(dataType == "json"){
							try{
								eval(text);
							}catch(e){
								ccms.dialog.notice("语法错误.",1500);
							}
						}else{
							eval(text);
						}

					}
					
				}
			}
		});
}
/*
*加载页面
*/
function ajaxLoad(obj, url, callback){
	ajaxCall(url,{
		response: obj.attr("id"),
		progress: false,
		dataType: "html",
		success: callback
	});
}

/*
*页面跳转，flag表示url是否加密，默认为false
*/

function loadDivPage(id,url){
	if(url==undefined || url=="") return;
	var hash = $Base64.encode(url);
	var tabObj = $("#menuTab a[href=\"#"+hash+"\"]");
	if(tabObj.length > 0){
		$(".menu_on").removeClass("menu_on");
		tabObj.addClass("menu_on");
	}
	if(!url.startsWith("/")){
		url = unescape($Base64.decode(url));
	}
	ajaxLoad($("#"+id), contextPath+url,function(){
		/**
		* 页面初始化时给必填字段初始化
		*/
		$("#"+id+" label.required").each(function(){
			if($(this).find(".red").length == 0){
				$(this).append("<span class='red'>*</span>");
			}
		});
		//radio样式
		$('#'+id+' input[type=radio]').iCheck({
			   radioClass: 'iradio_square-blue',
			   increaseArea: '20%'
		});
		 //checkbox样式
		$('#'+id+' input[type=checkbox]').iCheck({
				checkboxClass: 'icheckbox_square-blue',
				increaseArea: '20%'
		});
		if($HomeHash != hash){
			$("#"+id+" .col_main_body_title").prepend($("<button>").addClass("btn btn-default pull-left btn-goto-back").html("<span class='glyphicon glyphicon-arrow-left'></span>"));
			$("#"+id+" .btn-goto-back").css("margin-top", "-5px").unbind().on("click",function(){
				var uri = "";
				if(self.frameElement && self.frameElement.tagName.toLowerCase()=="iframe"){
					uri = $(self.frameElement).attr("src");
				}
				if( undefined != uri && (uri.indexOf("&backflag=parent") >= 0 || uri.indexOf("?backflag=parent") >= 0) ){
					gotoBack("parent");
				}else{
					gotoBack();
				}
			});
		}
	});
}
/*
* 按照既定清除规则清理表单
*/
function clearForm(formId)
{
	var f = $("#"+formId);
	if(f.length == 0){
		f = document.forms[formId];
	}else{
		f = f[0];
	}
	for (var i=0;i<f.elements.length;i++)
	{
		var e = f.elements[i];
		if (e.type=="text" || e.type=="hidden" || e.tagName=="TEXTAREA" || e.type=="password")
		{
		    if(e.getAttribute("preserve")!="true")
    			e.value = "";
		}
		if (e.tagName=="SELECT")
		{
			if(e.getAttribute("preserve")!="true")
			    e.options.selectedIndex = 0;
		}
		if (e.type=="checkbox")
		{
		    if(e.getAttribute("default")=="checked"){
		    	e.checked = true;
    		}else{
    		    if(e.getAttribute("preserve")!="true"){
					$(e).iCheck("uncheck");
				}
			}
    	}
		if (e.type=="radio")
		{
		    if(e.getAttribute("default")=="checked"){
		    	e.checked = true;
    		}else{
    		    if(e.getAttribute("preserve")!="true"){
					$(e).iCheck("uncheck");
				}
			}
    	}
	}
	clearErrorMessages();
}

/*
	清除错误提示信息
*/
function clearErrorMessages() {
	$("label.error").each(function(){
		$(this).remove();
	});
	$(".form-control.error").each(function(){
		$(this).removeClass("error");
	});
}

/*
	错误信息展示
	/action/ccms/validate
*/
function setFormErrorMsg(formElementId,text) {
	//clearErrorMessages();
	var obj = $("#"+formElementId);
	if(obj.length > 0){
		if(obj.attr("type")=="radio" || obj.attr("type")=="checkbox"){//iCheck样式需特殊处理
			obj.parent().parent().append($("<label/>").addClass("error").attr("for",formElementId).html(text));
		}else{
			obj.after($("<label/>").addClass("error").attr("for",formElementId).html(text));
			obj.focus();
		}
	}
}

/**
* JSON对象转换成url拼接参数
*/
function jsonToUrl(map) {
	var str = "";
	$.each(map, function(id, value){
		str += "&"+id+"="+value;
	});
	if(str.length > 0){
		str = str.substring(1, str.length);
	}
	return str;
}
/*
* 页面跳转公共方法，当加载到首界面时会被hashchange替换
* open_url不为空时，弹出窗体，再hash调用url
*/
function gotoPage(url,open_url,is_popup){
	if(url==undefined || url=="" || window.location.href.indexOf(url)>=0 /*防卡循环调用*/) return;
	var hash = $Base64.encode(escape(url));
	if(open_url==undefined || open_url==""){
		if($("#body_content",window.document).length>0){//存在首页
			window.location.hash = "#"+hash;
		}else{
			window.location = contextPath+url;
		}
	}else{
		if(is_popup == 'true' || is_popup == '1'){
			window.open(open_url+"#"+hash);
		} else {
			window.location = contextPath+open_url+"#"+hash;
		}
	}
}
/*
* 页面返回
*/
function gotoBack(backflag){
	var frameobj = null;
	var winobj = window;
	if( undefined != backflag && "parent" == backflag ){
		frameobj = $(parent.self.frameElement);
		winobj = parent;
	}else if(self.frameElement && self.frameElement.tagName.toLowerCase()=="iframe"){
		frameobj = $(self.frameElement);
	}
	if( null != frameobj ){
		var uri = frameobj.attr("gotoBackUrl");
		if(undefined != uri &&  uri!=""){
			winobj.location.href = uri;
		}else{
			winobj.history.back();
		}
	}else{
		winobj.history.back();
	}
}
/**
* 创建标准的crud操作
	自动调用的全局函数
	参数map 为一些参数和后处理函数
	formId 对应表单ID
	button 对应提交按钮
	resetButton 对应重置按钮
	addNewButton 对应新增按钮
	actionroot 对应操作目录，默认当前目录
	modalId 对应表单弹出框ID，默认modalAddnew
	isUseModal 是否使用弹出框，对于界面只有表单的情况使用，默认true
	bpkField 对应主键字段，默认 tuid
	validate 对应jquery validate 可以简单判断的验证情况
	check 对应验证函数（逻辑比较复杂的情况）
	addNewBack 对应新增界面出来后函数
	editBack 对应编辑后函数
	insertBack 对应新增后函数
	updateBack 对应修改后函数
	deleteBack 对应删除后函数
*/
if(typeof top.globalCrud == "undefined"){/*同一页面加载多个文档表单时会冲突，统一存放到top中*/
	top.globalCrud={};
}

function SystemOperator(map) {
	this.formId = map.formId;
	if(this.formId == null || this.formId == undefined || $("#"+this.formId).length == 0){
		alert("CRUD The parameter 'formId' is not available, please check your configuration !");
	}
	this.map = map;
	this.actionroot = map.actionroot!=null?map.actionroot:$("#actionroot").val();
	this.modalId = map.modalId!=null?map.modalId:"modalAddnew";
	this.isUseModal = map.isUseModal!=null?map.isUseModal:"true";
	this.button = map.button!=null?map.button:"save_btn";
	this.resetButton = map.resetButton!=null?map.resetButton:"reset_btn";
	this.addNewButton = map.addNewButton!=null?map.addNewButton:"addnew_btn";
	this.bpkField = map.bpkField!=null?map.bpkField:"tuid";
	this.bpkObj = $("#"+this.formId+" input[name='"+this.bpkField+"']");
	this.cascade = null;
	
	this.initValidate = function() {// 初使化验证
		$.validator.setDefaults({
			submitHandler : function(form) {
				var obthis = top.globalCrud[form.id];
				if(obthis.map["check"] != null) {
					if (!obthis.map["check"]()) {
						return false;
					}
				}
				if(obthis.bpkObj.length > 0){
					if(obthis.bpkObj.val() == ""){
						obthis.insert();
					}else{
						obthis.update();
					}
				}
			},
			errorPlacement : function(error,obj) {
				if(obj.attr("type")=="radio" || obj.attr("type")=="checkbox"){//iCheck样式需特殊处理
					obj.parent().parent().append(error);
				}else{
					obj.after(error);
				}
			}
		});
		//验证函数需要初始化
		if(this.map["validate"] != null) {
			this.map["validate"]();
		}
	},
	//初始化按钮事件
	this.init = function(callback) {
		
		this.initValidate();

		var obthis = this;
		$("#"+this.addNewButton).unbind().on("click", function(e){
			obthis.addNew();
			e.preventDefault();
		});
		$("#"+this.button).unbind().click(function() {
			if(obthis.map["validate"] != null) {
				if (!obthis.map["validate"]()) {
					return false;
				}
			}
			$("#"+obthis.formId).submit();
		});
		$("#"+this.resetButton).unbind().on("click", function(e) {
			clearForm(obthis.formId);
			e.preventDefault();
		});

		if(callback != undefined){
			callback();
		}
		
		return this;
	}, this.addNew = function() {
		//主键去除不能清空属性
		if(this.bpkObj.length > 0){
			this.bpkObj.removeAttr("preserve");
		}
		clearForm(this.formId);
		if(this.isUseModal == "true"){
			$("#"+this.modalId).modal("show");
		}
		if(this.map["addNewBack"] != null) {
			this.map["addNewBack"]();
		}
	}, this.edit = function(maps) {
		clearForm(this.formId);
		var url = this.actionroot + "/edit?" + jsonToUrl(maps);
		var obthis = this;
		ajaxCall(url,{
				method: "post",
				progress: true,
				dataType: "script",
				success: function() {
					//主键增加不能清空属性
					if(obthis.bpkObj.length > 0){
						obthis.bpkObj.attr("preserve","true");
					}
					if(obthis.isUseModal == "true"){
						$("#"+obthis.modalId).modal("show");
					}
					if(obthis.map["editBack"] != null) {
						obthis.map["editBack"]();
					}
				}
		});
	}, this.insert = function() {
		var url = this.actionroot + "/insert";
		var obthis = this;
		ajaxCall(url,{
				method: "post",
				progress: true,
				form: this.formId,
				button: this.button,
				dataType: "script",
				success: function() {
					if(obthis.isUseModal == "true"){
						$("#"+obthis.modalId).modal("hide");
					}
					if(obthis.map["insertBack"] != null) {
						obthis.map["insertBack"]();
					}
				}
		});
	}, this.update = function() {
		var url = this.actionroot + "/update";
		var obthis = this;
		ajaxCall(url,{
				method: "post",
				progress: true,
				form: this.formId,
				button: this.button,
				dataType: "script",
				success: function() {
					if(obthis.isUseModal == "true"){
						$("#"+obthis.modalId).modal("hide");
					}
					if(obthis.map["updateBack"] != null) {
						obthis.map["updateBack"]();
					}
				}
		});
	}, this.del = function(maps) {
		var url = this.actionroot + "/delete?" + jsonToUrl(maps);
		var obthis = this;
		ajaxCall(url,{
				method: "post",
				progress: true,
				dataType: "script",
				success: function() {
					if(obthis.map["deleteBack"] != null) {
						obthis.map["deleteBack"]();
					}
				}
		});
	}, this.attachment = function(maps) {
		ccms.dialog.open({
			url : contextPath+"/action/ccms/attachment-my/crud?"+ jsonToUrl(maps),id:"pickid"});
	  };
	top.globalCrud[this.formId] = this;
}
/**
* 通用的分页查询类
	参数map 为一些参数和后处理函数
	formId 对应表单ID
	actionroot 对应操作目录，默认当前目录
	button 查询按钮
	resetButton 清空按钮
	success 成功查询后回调函数
	rowpackage 单条数据的遍历处理
*/
//全局的回调函数
function SystemSearchTool(map) {
	this.datagrid = map.datagrid;
	this.formId = map.formId;
	if(this.formId == null || this.formId == undefined || $("#"+this.formId).length == 0){
		alert("Search The parameter 'formId' is not available, please check your configuration !");
	}
	this.formObj = $("#"+map.formId);
	this.actionroot = map.actionroot!=null?map.actionroot:$("#actionroot").val();
	this.button = map.button!=null?map.button:"search_btn";
	this.resetButton = map.resetButton!=null?map.resetButton:"search_reset_btn";
	this.progress = map.progress!=null?map.progress:true;
	this.path = map.path!=null?map.path:"/search";
	this.validate = map.validate;

	this.initSearchBtn = function(callback) {
		var operthis = this;
		$("#"+this.button).unbind().on("click", function(e) {// 搜索按钮
			operthis.searchData();
			e.preventDefault();
		});

		$("#"+this.resetButton).unbind().on("click", function(e) {
			clearForm(operthis.formId);
			e.preventDefault();
		});

		$('#'+this.formId).unbind().on("keypress", function(e) {// 搜索按钮的search框
			e = e || event;
			if (e.keyCode == 13) {
				operthis.searchData();
				return false;
			}
		});
		
		//给可排序字段添加事件
		this.formObj.next().find(".sortable").unbind().on("click", function(){
			var code = $(this).attr("code");
			if(code != undefined && code != ""){
				var caret = $(this).find(".caret");
				operthis.formObj.next().find(".caret,.caret-top").each(function(){$(this).remove();});
				if(caret.length > 0){//倒序
					operthis.formObj.find("input[name='order']").val("desc");
					$(this).append('<span class="caret-top"></span>');
				}else{//顺序
					operthis.formObj.find("input[name='order']").val("asc");
					$(this).append('<span class="caret"></span>');
				}
				operthis.formObj.find("input[name='sort']").val(code);
				operthis.searchData(1);
			}
		});

		if(callback != undefined){
			callback();
		}
		return this;
	}, this.searchData = function(pNo) {
		if( undefined != this.validate && null != this.validate ){
			if( !this.validate() ){
				return;
			}
		}

		if(this.formObj.length==0 || $("#"+this.datagrid).length==0){//没有不执行
			return ;
		}
		if (pNo == undefined) {
			pNo = 1;
		}
		if(typeof(this.formObj[0].pageNo) != "undefined"){
			this.formObj[0].pageNo.value = pNo;
		}
		$('#' + this.datagrid + 'Template').hide();//隐藏模版
		this.searchDataJson();
		return this;
	}, this.searchDataJson = function() {
		var url = this.formObj.attr('action');
		if (url == "" || url == undefined) {
			url = this.actionroot + this.path;
		}
		var obthis = this;
		ajaxCall(url,{
				method: "post",
				progress: this.progress,
				form: this.formId,
				button: this.button,
				dataType: "json",
				success: function(data) {
					obthis._generalTable(data);
				}
		});
	}, this._generalTable = function(data) {
		var list = data.rows;
		//删除最后拼接的空对象
		list.pop();
		var page = data.page;
		if(this.formObj[0].totalPages!=undefined && page!=undefined){//写入总页数
			this.formObj[0].totalPages.value = page.totalPages;
		}
		if(this.formObj[0].total!=undefined && page!=undefined){//写入总条数
			this.formObj[0].total.value = page.total;
		}
		$("#" + this.datagrid).empty();
		for (var i = 0; i < list.length; i++) {
			var obj = list[i];
			obj.index=(i+1);
			if(map['rowpackage'] != undefined){//单条数据处理操作
				map['rowpackage'](obj);
			}
			var html = this._generalHtml(obj);
			$("#" + this.datagrid).append(html);
		}
		if (list.length == 0) {
			var thCount = $("#" + this.datagrid).parent().find("th").length;
			if(thCount > 0){
				$("#" + this.datagrid).append("<td colspan='"+thCount+"' class='no-data'>没有记录</td>");
			}else{
				$("#" + this.datagrid).append("<div class='no-data'>没有记录</div>");
			}
		}
		var obthis = this;
		$(".pagination").each(function() {// 分页条显示
			var targetpage=$(this).data('target');
			if((targetpage==null || targetpage=='datagrid') && obthis.datagrid=='datagrid'){
				if(page!=undefined)
					obthis.setPage(page.pageNo, page.totalPages, page.total, $(this));
			}else if(targetpage == obthis.datagrid){
				if(page != undefined)
					obthis.setPage(page.pageNo, page.totalPages, page.total, $(this));
			}
		});
		if(map['success'] != undefined){//渲染成功后的操作
			map['success'](data);
		}
	},
	this._generalHtml = function(obj) {
		if ($('#' + this.datagrid + 'Template') == undefined) {
			return '';
		}
		var oldhtml = $('#' + this.datagrid + 'Template').html();
		for ( var key in obj) {
			if (typeof (obj[key]) == "object") {
				var obj2 = obj[key];
				for ( var key2 in obj2) {
					if (obj2[key2] != null) {
						var reg = new RegExp("#" + key + "." + key2
								+ "#", "g");
						oldhtml = oldhtml.replace(reg, obj2[key2]);
					}
				}
			} else {
				if (obj[key] != null) {
					var reg = new RegExp("#" + key + "#", "g");
					oldhtml = oldhtml.replace(reg, obj[key]);
				}
			}
		}
		var reg = new RegExp("#[a-z]+#", "g");
		oldhtml = oldhtml.replace(reg, "&nbsp;");
		var reg = new RegExp("#.+#", "g");
		oldhtml = oldhtml.replace(reg, "&nbsp;");
		return oldhtml;
	},
	this.setPage = function(currentPage, totalPages, totalCount, elem) {
		var obthis = this;
		elem = $(elem);
		if (totalPages <= 1) {
			elem.empty();
			elem.parent().hide();
			return;
		}else{
			elem.parent().show();
		}
		var options = {
			bootstrapMajorVersion : 3,
			useBootstrapTooltip : false,
			alignment : "center",
			currentPage : currentPage ? currentPage : 1,
			numberOfPages :5,
			totalPages : totalPages,
			itemTexts : function(type, page, current) {
				switch (type) {
				case "first":
					return "<<";
				case "prev":
					return "<";
				case "next":
					return ">";
				case "last":
					return ">>";
				case "page":
					return page;
				}
			},
			tooltipTitles : function(type, page, current) {
				return "";
			},
			pageUrl : function(type, page, current) {
				return "javascript:void(0)";
			},onPageChanged:function(event, originalEvent,page){
                obthis.searchData(page);
            }
		};
		elem.bootstrapPaginator(options);
		if (elem.first().html().indexOf("&lt;&lt;") < 0) {
			elem.prepend("<li class='disabled'><a>&lt;&lt;</a></li>");
		}
		if (elem.last().html().indexOf("&gt;&gt;") < 0) {
			elem.append("<li class='disabled'><a>&gt;&gt;</a></li>");
		}
		if(typeof(obthis.formObj[0].pageSize) == "undefined"){
			obthis.formObj.append("<input type='hidden' name='pageSize' value='10'>");
		}
		var pageSize = 10;
		var pageSizeVal = obthis.formObj[0].pageSize.value;
		if(pageSizeVal != ""){
			try{
				pageSize = parseInt(pageSizeVal);
			}catch(e){}
		}
		var beginRecord = (currentPage-1)*pageSize + 1;
		var endRecord = currentPage*pageSize;
		if(endRecord > totalCount){
			endRecord = totalCount;
		}
		var str = "<select name='changePageSize'>";
		str += "<option value='10' "+(pageSize==10?"selected":"")+">10</option>";
		str += "<option value='25' "+(pageSize==25?"selected":"")+">25</option>";
		str += "<option value='50' "+(pageSize==50?"selected":"")+">50</option>";
		str += "<option value='100' "+(pageSize==100?"selected":"")+">100</option>";
		str += "<option value='500' "+(pageSize==500?"selected":"")+">500</option>";
		str += "</select>";
		elem.prev().remove();
		elem.before("<div class='pageInfo'>每页显示&nbsp;"+str+"&nbsp;行，共有"+totalCount+"条，当前显示"+beginRecord+"-"+endRecord+"条记录</div>");
		elem.css("display","inline");
		elem.prev().find("select").unbind().on("change", function(){
			obthis.formObj[0].pageSize.value = $(this).val();
			obthis.searchData();
		});
	};
}


/**
* 自定义弹出框类，以免更换弹出框架对整体改动
*/
function MyDialog() {
	this.open = function(map){
		var url = map['url'], id = map['id'], width = map['width'], height = map['height'], isFull = map['isFull'], html = null;
		this.id = map['id']==undefined?"":map['id'];
		this.callback = map['success'];
		if (id == undefined) {
			var num = parseInt(Math.random() * 10);
			id = num + "dlg";
		}
		if (url == undefined || id == undefined) {
			alert("Dialog's url and id is requried,please check your configuration.");
		}
		if ($('#_dlg' + id).length > 0) {
			$('#_dlg' + id).remove();
		}
		if (isFull == true) {
			width = $(document).width();
			height = $(document).height();
			html = '<div id="_dlg'
					+ id
					+ '" class="modal fade" role="dialog" tabindex="-1" aria-hidden="true">'
					+ '<div class="modal-dialog" style="height:'
					+ height
					+ 'px;width:'
					+ width
					+ 'px;margin:0;">'
					+ '<div class="modal-body" style="height:'
					+ height
					+ 'px;width:'
					+ width
					+ 'px;padding:0;overflow:hidden;"><iframe name="'
					+ id
					+ 'frame" width="'
					+ width
					+ 'px" height="'
					+ height
					+ 'px" src="'
					+ url
					+ '" frameBorder="0" ></iframe></div></div></div></div>';
		} else {
			if (width == undefined || width > ($(document).width() - 40)) {
				width = $(document).width() - 40;
			}
			if (height == undefined || height > ($(document).height() - 60)) {
				height = $(document).height() - 60;
			}
			html = '<div id="_dlg'
					+ id
					+ '" class="modal fade" role="dialog" tabindex="-1" aria-hidden="true">'
					+ '<div class="modal-dialog" style="height:'
					+ height
					+ 'px;width:'
					+ width
					+ 'px"><div class="modal-content"><div class="dialog-close" data-dismiss="modal" title="关闭"></div>'
					+ '<div class="modal-body"><iframe name="'
					+ id
					+ 'frame" width="'
					+ (width - 30)
					+ 'px" height="'
					+ (height - 40)
					+ 'px" src="'
					+ url
					+ '" frameBorder="0" ></iframe></div></div></div></div>';
		}
		$("body").append(html);

		$('#_dlg' + id).on('hidden.bs.modal', function(e) {
			/* 说明父界面还有弹出层 */
			if ($(".modal-backdrop").length > 0) {
				$("body").addClass("modal-open");
			}
			$(this).remove();
		});
		$('#_dlg' + id).modal("show");
	},
	this.close =function(dlgid) {// 关闭窗口
		var divid;
		if(dlgid==undefined){
			if(this.id!=""){
				divid=this.id;
			}else{
				var iframename = window.name;
				divid = iframename.replace("frame", '');
			}
		}else{
			divid=dlgid;
		}
		if (window.parent == null)
			return;
		var obj = $('#_dlg' + divid, window.parent.document);
		obj.next("div.modal-backdrop").remove();
		obj.hide();
		obj.remove();
		if (this.callback) {
			this.callback();
		}
	},
	this.alert=function(content,callback){//提示框
	    bootbox.alert(content,callback);
	},
	this.notice=function(content,time,callback){
		 bootbox.alert(content);
		if(time>0){
			window.setTimeout(function(){
				$("button[data-bb-handler=ok]").click();
				if(callback != undefined){
					callback();
				}
			}, time);
		}
	},
	this.confirm = function(content,yesCallback,noCallback){//确认框
		bootbox.confirm(content, function(result){
			if(result==true){
				yesCallback();
			}else{
				if(noCallback != undefined){
					noCallback();
				}
			}
		}); 
	},
	this.date=function(ob,callback){
		var format='yyyy-mm-dd';
		var nowDate=new Date();
		$(ob).datetimepicker({
	        language:  'zh-CN',
	        weekStart: 1,
	        todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 2,
			minView: 3,
			//forceParse: 0,
			format:format,
			startDate: "1920-01-01",
			initialDate:nowDate.format("yyyy-MM-dd")
	    });
		if(callback!=undefined){
			$(ob)
			.datetimepicker()
			.on('changeDate', function(ev){
				callback(this);
			});
		}
	},
	this.year = function(ob,callback){
		var format='yyyy';
		var nowDate=new Date();
		$(ob).datetimepicker({
	        language:  'zh-CN',
			autoclose: 1,
			startView: 4,
			minView: 4,
			//forceParse: 0,
			format:format,
			startDate: "1920",
			initialDate: nowDate.format("yyyy")
		});
		if(callback!=undefined){
			$(ob).datetimepicker().on('changeDate', function(ev){
				callback(this);
			});
		}
	},
	this.yearmonth = function(ob,callback){
		var format='yyyy-mm';
		var nowDate=new Date();
		$(ob).datetimepicker({
	        language:  'zh-CN',
	        weekStart: 1,
			autoclose: 1,
			startView: 3,
			minView: 3,
			//forceParse: 0,
			format:format,
			startDate: "1920-01",
			initialDate: nowDate.format("yyyy-MM")
		});
		if(callback!=undefined){
			$(ob).datetimepicker().on('changeDate', function(ev){
				callback(this);
			});
		}
	},
	this.monthdate=function(ob,callback){
		var format='mm-dd';
		var nowDate=new Date();
		$(ob).datetimepicker({
	        language:  'zh-CN',
	        weekStart: 1,
	        todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 2,
			minView: 2,
			//forceParse: 0,
			format:format,
			startDate: nowDate.format("yyyy")+"-01-01",
			initialDate:nowDate.format("MM-dd")
	  });
		if(callback!=undefined){
			$(ob)
			.datetimepicker()
			.on('changeDate', function(ev){
				callback(this);
			});
		}
	},this.time=function(ob, callback){
		var nowDate=new Date();
		$(ob).datetimepicker({
	        language:  'zh-CN',
			weekStart: 1,
	        todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 1,
			//forceParse: 0,
			format:'hh:ii',
			startDate:nowDate.format("yyyy-MM-dd")
	    });
		if(callback!=undefined){
			$(ob)
			.datetimepicker()
			.on('changeDate', function(ev){
				callback(this);
			});
		}
	},this.datetime=function(ob,callback){
		var format='yyyy-mm-dd hh:ii:ss';
		var nowDate=new Date();
		$(ob).datetimepicker({
	        language:  'zh-CN',
	        weekStart: 1,
	        todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 2,
			//forceParse: 0,
			format:format,
			startDate: "1920-01-01",
			initialDate:nowDate.format("yyyy-MM-dd")
	    });
		if(callback!=undefined){
			$(ob)
			.datetimepicker()
			.on('changeDate', function(ev){
				callback(this);
			});
		}
	};
}

function $Search(map) {
	var s = new SystemSearchTool(map);
	return s;
}
function $Crud(map) {
	var s = new SystemOperator(map);
	return s;
}
function $Dialog(){
	var s = new MyDialog();
	return s;
}


/**
* Retorna la trama requerida para hacer un POST del
* formulario indicado usando Ajax
* formName: Nombre del formulario 
*/
function getFormValues(formName)
{
 	
 	returnString ="";
 	
 	if( formName == "" ) return returnString;
 	
 	formElements=document.forms[formName].elements;
 	
 	for ( var i=formElements.length-1; i>=0; --i ) {
 		if (formElements[i].getAttribute("name") != null && formElements[i].getAttribute("name") != "" && formElements[i].getAttribute("type") != "button" && formElements[i].getAttribute("type") != "submit" && formElements[i].getAttribute("type") != "reset"){  /*add radio box elements analysis.*/
 		    if ((formElements[i].type != "radio" && formElements[i].type != "checkbox" && formElements[i].getAttribute("dependent") == null)){ //不是多选或单选,并且无前置关联.
 			    var isDependent = false;
     			for ( var j=formElements.length-1; j>=0; --j ){ /*检查哪些控件与本控件前置关联*/
     			    if(formElements[j].getAttribute("dependent") == formElements[i].id  && formElements[j].name != null && formElements[j].name != ""){
     			        isDependent = true; /*本控件被引用*/
             			if(formElements[i].value!="")
                 			returnString = returnString + formElements[j].name + "=" + encodeURIComponent(formElements[j].value) + "&";
     			    }
     			}
     			if(!isDependent)
         			returnString = returnString + formElements[i].name + "=" + encodeURIComponent(formElements[i].value) + "&";
         		else if(formElements[i].value!="")/*被引用,并且值不为空*/
         			returnString = returnString + formElements[i].name + "=" + encodeURIComponent(formElements[i].value) + "&";
     		    
 		    }else if(formElements[i].getAttribute("dependent") != null){/*存在前置关联*/
                continue;
            }else if(formElements[i].checked){/*单选或多选,并选中*/
                /*选把自己传上*/
     			returnString = returnString + formElements[i].name + "=" + encodeURIComponent(formElements[i].value) + "&";
     			for ( var j=formElements.length-1; j>=0; --j ){ /*检查哪些控件与本控件前置关联*/
     			    if(formElements[j].getAttribute("dependent") == formElements[i].id && formElements[j].name != null && formElements[j].name != ""){
             			returnString = returnString + formElements[j].name + "=" + encodeURIComponent(formElements[j].value) + "&";
     			    }
     			}
 		    }
 		}
 	}
	
	if(returnString != "")
	 	returnString = returnString.substring(0, returnString.length - 1);

 	return returnString;

}
