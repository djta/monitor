/*ccms lib*/
(function() {
	/* ccms命名空间 */
	window["kangfu"] = {};
	window["ccms"] = {};
	
	var  $Constant={
			UserType:{//用户类型
				patient:0,
				doctor:1,
				seller:2,
				manager:3,
				admin:4
			}
	};
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
					var text = XMLHttpRequest.responseText;
					if (text!=undefined && text.indexOf("_ajax_VE8374yz_") > 0)/* 跳转到登录界面 */
					{
						 window.location = contextPath+"/"; 
					}
					if (XMLHttpRequest.status == 200) {
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
			window.history.back();
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
				$("#loading").hide();
				$(document.body).css("overflow",ccms.util.loadClass.state);
			},
			showLoad: function(){
				ccms.util.loadClass.state = $(document.body).css("overflow");
				var loadObj = $("#loading");
				$(document.body).css("overflow","hidden");
				if (loadObj.length == 0) {
					var loaddiv = '<div id="loading" class="loading"><div class="loading-wait">';
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
			var hash = url;// = flag?url:$Base64.encode(url);
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
	},
	/**
	 * 创建标准的crud操作 自动调用的全局函数 参数map 为一些参数和后处理函数 formId 对应表单ID button 对应提交按钮
	 * resetButton 对应重置按钮 addNewButton 对应新增按钮 actionroot 对应操作目录，默认当前目录 modalId
	 * 对应表单弹出框ID，默认modalAddnew isUseModal 是否使用弹出框，对于界面只有表单的情况使用，默认true bpkField
	 * 对应主键字段，默认 tuid validate 对应jquery validate 可以简单判断的验证情况 check
	 * 对应验证函数（逻辑比较复杂的情况） addNewBack 对应新增界面出来后函数 editBack 对应编辑后函数 insertBack
	 * 对应新增后函数 updateBack 对应修改后函数 deleteBack 对应删除后函数
	 */
	$Crud = function(map) {
		return new $Crud.fn.init(map);
	}, $Crud_init = null,
	/**
	 * 通用的分页查询类 参数map 为一些参数和后处理函数 formId 对应表单ID actionroot 对应操作目录，默认当前目录 button
	 * 查询按钮 resetButton 清空按钮 success 成功查询后回调函数 rowpackage 单条数据的遍历处理
	 */
	$Search = function(map) {
		return new $Search.fn.init(map);
	}, $Search_init = null,
	
	$FileUpload = function(map) {
		return new $FileUpload.fn.init(map);
	}, $FileUpload_init = null,
	
	$Extend = function(map) {
		return new $Extend.fn.init(map);
	}, $Extend_init = null,
	
	$Highcharts= function(map) {
		return new $Highcharts.fn.init(map);
	}, $Highcharts_init = null,
	
	/* 自定义弹出框类，以免更换弹出框架对整体改动 */
	$Dialog = {
		/* 弹出框 */
		open : function(map) {
			var url = map['url'], id = map['id'], width = map['width'], height = map['height'], isFull = map['isFull'], html = null;
			this.callback = map['success'];
			if (id == undefined) {
				var num = parseInt(Math.random() * 100);
				id = num + "dlg";
			}
			if (url == undefined || id == undefined) {
				alert("Dialog's url and id is requried,please check your configuration.");
			}
			if ($('#_dlg' + id).length > 0) {
				$('#_dlg' + id).remove();
			}
			if(isFull == true){
				width = $(document).width();
				height = $(document).height();
				html = '<div id="_dlg' + id
					+ '" class="modal fade" role="dialog" tabindex="-1" aria-hidden="true">'
					+ '<div class="modal-dialog" style="height:'+ height + 'px;width:' + width + 'px;margin:0;">'
					+ '<div class="modal-body" style="height:'+ height + 'px;width:' + width + 'px;padding:0;overflow:hidden;"><iframe name="' + id
					+ 'frame" width="' + width + 'px" height="'
					+ height + 'px" src="' + url
					+ '" frameBorder="0" ></iframe></div></div></div></div>';
			}else{
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
						+ 'px"><div class="modal-content"><div class="dialog-close" id="close1" data-dismiss="modal" title="关闭"></div>'
						+ '<div class="modal-body"><iframe name="' + id
						+ 'frame" width="' + (width - 30) + 'px" height="'
						+ (height - 40) + 'px" src="' + url
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
			$('.modal-content').on('click','#close1',function(){
				var divid;
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
			  });
			
		},
		/* 提示框 */
		alert : function(content, callback) {
			bootbox.alert(content, callback);
		},
		close : function() {// 关闭窗口
			var iframename = window.name;
			var divid = iframename.replace("frame", '');
			if (window.parent == null)
				return;
			var obj = $('#_dlg' + divid, window.parent.document);
			obj.next("div.modal-backdrop").remove();
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

	$Crud.fn = $Crud.prototype = {

	};
	$Crud_init = $Crud.fn.init = function(map) {
		this.formId = map.formId;
		if (this.formId == null || this.formId == undefined
				|| $("#" + this.formId).length == 0) {
			alert("The parameter 'formId' is not available, please check your configuration !");
		}
		this.map = map;
		this.actionroot = map.actionroot != null ? map.actionroot : $(
				"#actionroot").val();
		this.modalId = map.modalId != null ? map.modalId : "modalAddnew";
		this.isUseModal = map.isUseModal != null ? map.isUseModal : "true";
		this.button = map.button != null ? map.button : "save_btn";
		this.resetButton = map.resetButton != null ? map.resetButton
				: "reset_btn";
		this.addNewButton = map.addNewButton != null ? map.addNewButton
				: "addnew_btn";
		this.bpkField = map.bpkField != null ? map.bpkField : "tuid";
		this.bpkObj = $("#" + this.formId + " input[name='" + this.bpkField
				+ "']");
		this.cascade = null;
		this.file = map.file != null ? map.file :false;
		
		this.initValidate = function() {/* 初使化验证 */
			var obthis = this;
			$.validator.setDefaults({
				submitHandler : function(form) {
					if (obthis.map["check"] != null) {
						if (!obthis.map["check"]()) {
							return false;
						}
					}
					if (obthis.bpkObj.length > 0) {
						if (obthis.bpkObj.val() == "") {
							obthis.insert();
						} else {
							obthis.update();
						}
					}
				},
				errorPlacement : function(error, obj) {
					if (obj.attr("type") == "radio"
							|| obj.attr("type") == "checkbox") {/* iCheck样式需特殊处理 */
						obj.parent().parent().append(error);
					} else {
						obj.after(error);
					}
				}
			});
			/* 验证函数需要初始化 */
			if (this.map["validate"] != null) {
				this.map["validate"]();
			}
		},
		/* 初始化按钮事件 */
		this.init = function(callback) {

			this.initValidate();

			var obthis = this;
			$("#" + this.addNewButton).unbind().on("click", function(e) {
				obthis.addNew();
				e.preventDefault();
			});
			$("#" + this.button).unbind().on("click", function() {
				if (obthis.map["validate"] != null) {
					if (!obthis.map["validate"]()) {
						return false;
					}
				}
				$("#" + obthis.formId).submit();
			});
			$("#" + this.resetButton).unbind().on("click", function(e) {
				$Util.clearForm(obthis.formId);
				e.preventDefault();
			});

			if (callback != undefined) {
				callback();
			}

			return this;
		}, this.addNew = function() {
			/* 主键去除不能清空属性 */
			if (this.bpkObj.length > 0) {
				this.bpkObj.removeAttr("preserve");
			}
			$Util.clearForm(this.formId);
			if (this.isUseModal == "true") {
				$("#" + this.modalId).modal("show");
			}
			if (this.map["addNewBack"] != null) {
				this.map["addNewBack"]();
			}
		}, this.edit = function(maps) {
			$Util.clearForm(this.formId);
			var url = this.actionroot + "/edit.json?" + $Util.jsonToUrl(maps);
			var obthis = this;
			$Util.ajaxCall(url, {
				method : "post",
				progress : true,
				dataType : "json",
				success : function(data) {
					/* 主键增加不能清空属性 */
					if (obthis.bpkObj.length > 0) {
						obthis.bpkObj.attr("preserve", "true");
					}
					if (obthis.isUseModal == "true") {
						$("#" + obthis.modalId).modal("show");
					}
					if (obthis.map["editBack"] != null) {
						obthis.map["editBack"](data);
					}
				}
			});
		}, this.insert = function() {
			var obthis = this;
			if(obthis.file){//是文件
				var url = this.actionroot + "/insert";
				obthis.filesumbit(url,function(data){
					if (obthis.isUseModal == "true") {
						$("#" + obthis.modalId).modal("hide");
					}
					if (obthis.map["insertBack"] != null) {
						obthis.map["insertBack"](data);
					}
				});
				return;
			}
			var url = this.actionroot + "/insert.json";
			$Util.ajaxCall(url, {
				method : "post",
				progress : true,
				form : this.formId,
				button : this.button,
				dataType : "json",
				success : function(data) {
					if (obthis.isUseModal == "true") {
						$("#" + obthis.modalId).modal("hide");
					}
					if (obthis.map["insertBack"] != null) {
						obthis.map["insertBack"](data);
					}
				}
			});
			
		}, this.update = function() {
			var obthis = this;
			if(obthis.file){//是文件
				var url = this.actionroot + "/update";
				obthis.filesumbit(url,function(data){
					if (obthis.isUseModal == "true") {
						$("#" + obthis.modalId).modal("hide");
					}
					if (obthis.map["updateBack"] != null) {
						obthis.map["updateBack"](data);
					}
				});
				return;
			}
			var url = this.actionroot + "/update.json";
			$Util.ajaxCall(url, {
				method : "post",
				progress : true,
				form : this.formId,
				button : this.button,
				dataType : "json",
				success : function(data) {
					if (obthis.isUseModal == "true") {
						$("#" + obthis.modalId).modal("hide");
					}
					if (obthis.map["updateBack"] != null) {
						obthis.map["updateBack"](data);
					}
				}
			});
		},this.filesumbit=function(url,callback){
			var html=$('<div id="progressDiv" class=progressDiv><div class="uploadtext">已上传<span class=processtext>0%</span></div><div class="progress"><div class="progress-bar"  style="width:0%;"></div></div></div>');
			$("#progressDiv").remove();
			$(document.body).append(html);
			var options = {   
			        beforeSend: function() {
			        	$("#progressDiv").find("div.progress-bar").css("width","0%");
			        	$("#progressDiv").find(".processtext").html("0%");
			        },
			        uploadProgress: function(event, position, total, percentComplete) {
			        	$("#progressDiv").find(".progress-bar").css("width",percentComplete+"%");
			        	$("#progressDiv").find(".processtext").html(percentComplete+"%");
			        },
			        error:function(data){
			                ccms.dialog.alert("上传失败");
			                $("#progressDiv").remove();
			        },
			        success: function(html, status) {
			        	$("#progressDiv").remove();
			        	callback(html);
			        }
			};
			var sysurl=contextPath+"/"+url+'.json';
			sysurl=sysurl.replace('//','/');
			$('#' +this.formId).attr("action",sysurl);
			if($('#' + this.formId).ajaxSubmit!=undefined)
				$('#' +this.formId).ajaxSubmit(options);
		},
		this.del = function(maps) {
					var url = this.actionroot + "/delete.json?"
							+ $Util.jsonToUrl(maps);
					var obthis = this;
					$Util.ajaxCall(url, {
						method : "post",
						progress : true,
						dataType : "json",
						success : function() {
							if (obthis.map["deleteBack"] != null) {
								obthis.map["deleteBack"]();
							}
						}
					});
				};
	};
	$Crud_init.prototype = $Crud.fn;

	$Search.fn = $Search.prototype = {

	};
	$Search_init = $Search.fn.init = function(map) {
		this.datagrid = map.datagrid;
		this.formId = map.formId;
		if(!map.table){
			map.table=false;
		}
		if ((this.formId == null || this.formId == undefined
				|| $("#" + this.formId).length == 0) && map.table==false) {
			alert("The parameter 'formId' is not available, please check your configuration !");
		}
		this.formObj = $("#" + map.formId);
		this.actionroot = map.actionroot != null ? map.actionroot : $("#actionroot").val();
		this.button = map.button != null ? map.button : "search_btn";
		this.resetButton = map.resetButton != null ? map.resetButton : "search_reset_btn";
		var obthis = this;
		this.formObj.unbind().on("keypress", function(e) {/* 表单上面响应回车搜索事件  */
			e = e || event;
			if (e.keyCode == 13) {
				obthis.searchData();
				return false;
			}
		});
		this.initSearchBtn = function(callback) {
			var operthis = this;
			$("#" + this.button).unbind().on("click", function(e) {/* 搜索按钮 */
				operthis.searchData();
				e.preventDefault();
			});

			$("#" + this.resetButton).unbind().on("click", function(e) {
				$Util.clearForm(operthis.formId);
				e.preventDefault();
			});

			/* 给可排序字段添加事件 */
			this.formObj.next().find(".sortable").on("click", function() {
				var code = $(this).attr("code");
				if (code != undefined && code != "") {
					var caret = $(this).find(".caret");
					operthis.formObj.next().find(".caret,.caret-top").each(function() {
						$(this).remove();
					});
					if (caret.length > 0) {/* 倒序 */
						operthis.formObj.find("input[name='order']").val("desc");
						$(this).append('<span class="caret-top"></span>');
					} else {/* 顺序 */
						operthis.formObj.find("input[name='order']").val("asc");
						$(this).append('<span class="caret"></span>');
					}
					operthis.formObj.find("input[name='orderBy']").val(code);
					operthis.searchData(1);
					
				}
			});

			if (callback != undefined) {
				callback();
			}
			return this;
		},
		this.searchData = function(pNo) {
			if (this.formObj.length == 0 || $("#" + this.datagrid).length == 0) {/* 没有不执行 */
				return;
			}
			if (pNo == undefined) {
				pNo = 1;
			}
			if (typeof (this.formObj[0].pageNo) != "undefined") {
				this.formObj[0].pageNo.value = pNo;
			}
			$('#' + this.datagrid + 'Template').hide();/* 隐藏模版 */
			this.searchDataJson();
			return this;
		},
		this.searchDataJson = function() {
			var url = this.formObj.attr('action');
			if (url == "" || url == undefined) {
				url = this.actionroot + "/search.json";
			}
			$Util.ajaxCall(url, {
				method : "post",
				progress : true,
				form : this.formId,
				button : this.button,
				dataType : "json",
				success : function(data) {
					obthis._generalTable(data);
				}
			});
		},
		this._generalTable = function(data) {
			var list = data.result;
			/* 删除最后拼接的空对象 */
			var page = data.page;
			if (this.formObj[0].totalPages != undefined
					&& page != undefined) {/* 写入总页数 */
				this.formObj[0].totalPages.value = page.totalPages;
			}
			if (this.formObj[0].total != undefined && page != undefined) {/* 写入总条数 */
				this.formObj[0].total.value = page.totalCount;
			}
			var datagridObj = $("#" + this.datagrid).empty();
			var datagridTpl = $('#' + this.datagrid + 'Template');
			var datagridTplHtml = "";
			if (typeof(datagridTpl) != undefined) {
				datagridTplHtml = datagridTpl.html();
			}
			var tableHtml = "";
			for (var i = 0; i < list.length; i++) {
				var obj = list[i];
				obj.index = (i + 1);
				if (map['rowpackage'] != undefined) {/* 单条数据处理操作 */
					map['rowpackage'](obj);
				}
				tableHtml += this._generalHtml(datagridTplHtml, obj);
			}
			datagridObj.append(tableHtml);
			if (list.length == 0) {
				var thCount = datagridObj.parent().find("th").length;
				if (thCount > 0) {
					datagridObj.append("<td colspan='" + thCount + "' class='no-data'>没有符合条件的记录！</td>");
				} else {
					datagridObj.append("<div class='no-data'>没有符合条件的记录！</div>");
				}
			}
			if (map['success'] != undefined) {/* 渲染成功后的操作 */
				map['success'](data);
			}
			$(".pagination").each(
				function() {/* 分页条显示 */
					var targetpage = $(this).data('target');
					if ((targetpage == null || targetpage == 'datagrid')
							&& obthis.datagrid == 'datagrid') {
						if (page != undefined)
							obthis.setPage(page.pageNo,	page.totalPages, page.totalCount, $(this));
					} else if (targetpage == obthis.datagrid) {
						if (page != undefined)
							obthis.setPage(page.pageNo, page.totalPages, page.totalCount, $(this));
					}
			});
		},
		this._generalHtml = function(datagridTplHtml, obj) {
			var oldhtml = datagridTplHtml;
			for ( var key in obj) {
				if (typeof (obj[key]) == "object") {
					var obj2 = obj[key];
					for ( var key2 in obj2) {
						if (obj2[key2] != null) {
							var reg = new RegExp("#" + key + "." + key2 + "#", "g");
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
			if (totalPages <= 1) {
				elem.empty();
				return;
			}
			var options = {
				bootstrapMajorVersion : 3,
				useBootstrapTooltip : false,
				alignment : "center",
				currentPage : currentPage ? currentPage : 1,
				numberOfPages : 5,
				totalPages : totalPages,
				itemTexts : function(type, page, current) {
					switch (type) {
					case "first":
						return "首页";
					case "prev":
						return "上一页";
					case "next":
						return "下一页";
					case "last":
						return "尾页";
					case "page":
						return page;
					}
				},
				tooltipTitles : function(type, page, current) {
					switch (type) {
					case "first":
						return "第一页";
					case "prev":
						return "上一页";
					case "next":
						return "下一页";
					case "last":
						return "最后一页";
					case "page":
						return (page === current) ? "当前页是 " + page
								: " 去第 " + page + " 页";
					}
				},
				pageUrl : function(type, page, current) {
					return "javascript:void(0)";
				},
				onPageChanged : function(event, originalEvent, page) {
					elem.hide();
					obthis.searchData(page);
				}
			};
			elem.bootstrapPaginator(options);
			if (elem.first().html().indexOf("首页") < 0) {
				elem
						.prepend("<li class='disabled'><a>首页</a></li><li class='disabled'><a>上一页</a></li>");
			}
			if (elem.last().html().indexOf("尾页") < 0) {
				elem.append("<li class='disabled'><a>下一页</a></li><li class='disabled'><a>尾页</a></li>");
			}
			elem.append("<li><a><input style='width:40px;height:20px' id='page_input' />共"
							+ currentPage
							+ "/"
							+ totalPages
							+ "页"
							+ totalCount + "条</a></li>");
			$("#page_input").on("keypress", function(e) {
				e = e || event;
				$(this).val($(this).val().replace(/\D/g, ''));
				if ($.trim($(this).val()) == '') {
					return;
				}
				if(parseInt($(this).val())>totalPages){
					$(this).val(totalPages);
				}
				if (e.keyCode == 13) {// 回车
					obthis.searchData($(this).val());
					return false;
				}
			});
			elem.css("display", "inline-block");
		},this.makeTable=function(jsonarray,map){//生成表格 map中包含是否生序号
			if(map==undefined){
				map={};
			}
			var indexflag=map['index'];//是否生成序号
			if(indexflag==undefined){
				indexflag=false;
			}
			if(jsonarray==undefined){
				return;
			}
			var tabledid=this.datagrid;
			var $headob=$("#"+this.datagrid).children(":first");
			if($headob.length==0){
				return;
			}
			var $trobarr=$("#"+this.datagrid).find("tr");
			for(var i=1;i<$trobarr.length;i++){
				$trobarr[i].remove();
			}
			var $thobarray=$($headob).find("th");
			for(var i=0;i<jsonarray.length;i++){
				var html='<tr>';
				if(indexflag){
					html=html+'<td>'+(i+1)+'</td>';
				}
				var obj=jsonarray[i];
				for ( var key in obj) {
					$thobarray.each(function(){
						var colname=$(this).data("col");
						 if (colname != undefined){
							 if(key==colname){
								 html=html+'<td>'+obj[key]+'</td>';
							 }
						 }
					});
				}
				html=html+'<tr>';
				$("#"+tabledid).append(html);
			}
		};
	};
	$Search_init.prototype = $Search.fn;
	
	$FileUpload.fn = $FileUpload.prototype = {

	};
	
	$FileUpload_init = $FileUpload.fn.init = function(id) {
		var obthis = this;
		this.id=id;
		this.fileupload = function(map) {
			if(map==undefined){
				map={};
			}
			obthis.fileuploadfunc=map['success'];
			this.fileSize==map['fileSize'];
			this.accept==map['accept'];
			if(this.fileSize==undefined){
				this.fileSize=5*1024;//默认为5MB
			}
			obthis.formid=obthis.id;
			$('#' + this.id).find('.fileupload-buttons').find('input').each(function() {
				$(this).data("formid",obthis.id);
				$(this).change(function(evt) {
					obthis.opersubmit(this,evt);
				});
				if($(this).propertychange!='' && $(this).propertychange!=null )
				$(this).propertychange(function(evt) {
					obthis.opersubmit(this,evt);
				});
			});
			return this;
		}, this.opersubmit = function(ob,evt) {
			var format=$(ob).attr("accept");
			if(!format){
				format=obthis.accept;
			}
			var filepath=$(ob).val();
			var suffix=this.getfileSuffix(filepath);
			if(format!=undefined &&  format.indexOf(suffix)==-1){
				ccms.dialog.alert("文件格式不对");
				return;
			}
			var files = evt.target.files; // 获得文件对象   
			if(this.fileSize>0){
				 for (var i = 0, f; f = files[i]; i++)   
			        {   
						   if(f.size > this.fileSize*1024)   
				            {   
							   ccms.dialog.alert('文件最大 是' + this.fileSize + ' kb');
				                return;
				            }   
			        }
			}

			var html=$('<div id="progressDiv" class=progressDiv><div class="uploadtext">已上传<span class=processtext>0%</span></div><div class="progress"><div class="progress-bar"  style="width:0%;"></div></div></div>');
			$("#progressDiv").remove();
			$(document.body).append(html);
			var options = {   
			        beforeSend: function() {
			        	$("#progressDiv").find("div.progress-bar").css("width","0%");
			        	$("#progressDiv").find(".processtext").html("0%");
			        },
			        uploadProgress: function(event, position, total, percentComplete) {
			        	$("#progressDiv").find(".progress-bar").css("width",percentComplete+"%");
			        	$("#progressDiv").find(".processtext").html(percentComplete+"%");
			        },
			        error:function(data){
			                ccms.dialog.alert("上传失败");
			                $("#progressDiv").remove();
			        },
			        success: function(html, status) {
			        	$("#progressDiv").remove();
			        	obthis.callbackparent(html);
			        }
			};
			if($('#' + obthis.formid).ajaxSubmit!=undefined)
				$('#' + obthis.formid).ajaxSubmit(options);
		},this.callbackparent=function(data){
			var callback=obthis.fileuploadfunc;
			callback(data);
		},this.getfileSuffix=function(file_name){
			var result =/\.[^\.]+/.exec(file_name);
			return (result+'').toLowerCase().replace(".", '');
		},this.upload = function(map) {//文件上传
			var url=contextPath+'/file/upload';
			var btnobj=$("#"+this.id);
			var fid=parseInt(Math.random() * 1000);
			var formid="formid"+fid;
			var divid='dupload'+fid;
			if($("#"+divid).length>0){
				return;
			}
			if(map==undefined){
				map={};
			}
			obthis.fileuploadfunc=map['success'];
			this.fileSize=map['fileSize'];
			this.accept=map['accept'];
			var thumb=map['thumb'];
			if(this.fileSize==undefined){
				this.fileSize=5*1024;//默认为5MB
			}
			if(thumb){
				url=url+'?thumb='+thumb;
			}
			
			var formObj=$('<form action='+url+' id='+formid+' method=post enctype="multipart/form-data"></form>');
			btnobj.wrap(formObj);
			var fpdivobj=$('<div id='+divid+' class="ccms_upload"></div>');
			formObj=$("#"+formid);
			$("#"+formid).wrap(fpdivobj);
			var inputhidobj=$('<input type="file" name="files[]" />');
			formObj.append(inputhidobj);
			obthis.formid=formid;
			formObj.find('input[type=file]').each(function() {
				$(this).data("formid",formid);
				$(this).attr("accept",obthis.accept);
				$(this).change(function(evt) {
					obthis.opersubmit(this,evt);
				});
				if($(this).propertychange!='' && $(this).propertychange!=null )
				$(this).propertychange(function(evt) {
					obthis.opersubmit(this,evt);
				});
			});
			return this;
		},this.setFormat=function(format){//设置格式
			if(format){
				var btnobj=$("#"+this.id);
				if(btnobj.parent().find('input[type=file]').length>0)
				btnobj.parent().find('input[type=file]').attr("accept",format);
			}
		};
	};
	
	
	
	$Extend.fn = $Extend.prototype = {

	};
	
	$Extend_init = $Extend.fn.init = function(map) {
		if(map==undefined){
			map={};
		}
		var obthis = this;
		this.modalId=map.modalId;
		this.inputId = map.inputId != null ? map.inputId : "tagselectinput";
		this.addtagbtn = map.addtagbtn != null ? map.addtagbtn : "addtagbtn";
		this.addedtagList = map.addedtagList != null ? map.addedtagList : "addedtagList";
		this.tagoutput = map.tagoutput != null ? map.tagoutput : "tagoutput";//输出文本框
		this.limitSize = map.limitSize != null ? map.limitSize :5;
		var inputObj=$("#"+this.inputId);
		var tagselectObj=inputObj.next(".tag-select");
		var addtagbtnObj=$("#"+this.addtagbtn);
		var validateFlag=[false,false,false];
		this.init=function(){
			tagselectObj.unbind().click(function(){
				obthis.showSelectDlg();
			});
			
			inputObj.keypress(function(e) {
				 e=e||event;
				 if(e.keyCode==13){
					 obthis.enterPress();
					 return false;
				 }
			});
			
			addtagbtnObj.click(function(){
				obthis.addTagListButton();
			});
			
			$(document).click(function(e){
				var target = e.target ;
				if(e.target.tagName!=undefined){
					  	var tag = e.target.tagName.toUpperCase();
					    if(tag!= 'INPUT' && $(target).attr("id")!='showTagDialog'  && $(target).parent().attr("id")!='showTagDialog' && $(target).parent().parent().attr("id")!='showTagDialog' 
					    	&& $(target).parent().parent().parent().attr("id")!='showTagDialog' && $(target).parent().parent().parent().parent().attr("id")!='showTagDialog' ){
					    	 $("#showTagDialog").remove();//删除键盘
					    }
				}
			});
			return this;
		},this.addTagListButton=function(){
			obthis.closeDlg();
			if($.trim(inputObj.val())!=''  && $.trim(inputObj.val()).length>0){
				obthis.showAddedTagList($.trim(inputObj.val()));
				inputObj.val('');
			}
		},this.enterPress=function(){
			if($.trim(inputObj.val())==''){
				return;
			}
			var tagname='';
			if($(".tagli.current").length>0){
				tagname=$(".tagli.current").data("name");
			}else{
				tagname=$.trim(inputObj.val());
			}
			obthis.showAddedTagList(tagname);
			inputObj.val('');
			obthis.closeDlg();
		},this.showSelectDlg=function(){//显示选择框
			var url="/prescriptionTag/list.json";
			ccms.util.postData(url,{success:function(data){
				obthis.hotTagsResult(data.result);
			}});
		},this.hotTagsResult=function(data){
			if(data==undefined){
				return;
			}
			obthis.initDlg();
			obthis.reloadLoaction();
			$(obthis.$tob).empty();
			//debugger
			for(var i=0;i<data.length;i++){
				var obj=data[i];
				var name=obj.name;
				var $hob=$('<li class="taglis" data-name='+name+'>'+name+'</li>');
				$(obthis.$tob).append($hob);
			}
			obthis.initTagSelect();
		},this.initDlg=function(){
			var $div = $('<ul id="showTagDialog" class="showTagDialog list-unstyled" style="position:absolute;left:0px;top:0px;min-width:240px;z-index:2000;"><ul>');
			if ($("#showTagDialog").length == 0) {
				$(document.body).append($div);
				$(window).resize(function(){    //改变大小            
					var y = ccms.util.absPos($($ob)[0]).y;
					var x = ccms.util.absPos($($ob)[0]).x;
					$(obthis.$tob).css("top",y+bhei);
					$(obthis.$tob).css("left",x);
				 });
			}
			obthis.$tob=$("#showTagDialog");
		},this.reloadLoaction=function(){
			var y = ccms.util.absPos($(inputObj)[0]).y;
			var x = ccms.util.absPos($(inputObj)[0]).x;
			var scrollTop=$(document).scrollTop()
			var bhei=$(inputObj).outerHeight();
			var wid=$(inputObj).outerWidth();
			var xtop=y+bhei;
			if(obthis.modalId){
				var modalIdObj=$("#"+obthis.modalId);
				var scrolltop=modalIdObj.scrollTop();
				xtop=xtop-scrolltop;//($("#"+obthis.modalId).find(".modal-dialog").outerHeight()-$(document.body).outerHeight());
			} 
			$(obthis.$tob).css("top",xtop);
			$(obthis.$tob).css("left",x);
			$(obthis.$tob).css("width",wid);
		},this.initTagSelect=function(){
			$(".showTagDialog li.taglis").click(function(){
				var tagname=$(this).data("name");
				obthis.showAddedTagList(tagname);
				$(inputObj).val('');
				obthis.closeDlg();
			});
		},this.showAddedTagList=function(name){
			var $listob=$("#"+obthis.addedtagList);
			name=obthis.replaceSpecialChar(name);
			if(obthis.isExistTagInputValue(name)){
				return;
			}
			var html='<li data-id="'+name+'" >';
			html=html+'<div class=tag>';
			html=html+name;
			html=html+'</div>';
			html=html+'<i class="glyphicon glyphicon-remove close-tag-list" style="cursor: pointer;"></i>';
			html=html+'</li>';
			$($listob).append(html);
			obthis.validateTagList();
			obthis.initCloseTag();
			obthis.reloadLoaction();
		},this.isExistTagInputValue=function(term){//设置标签input的值
			var flag=false;
			if(term==''){
				flag=true;
			}
			$('.showtagList li').each(function(){
				var name=$(this).data('id');
				if(term==name){
					flag=true;
				}
			});
			return flag;
		},this.initCloseTag=function(){
			$('#'+obthis.addedtagList).find('.close-tag-list').click(function(){
				obthis.delTagList(this);
			});
		},this.delTagList=function(ob){
			$(ob).parent().remove();
			obthis.validateTagList();
			obthis.reloadLoaction();
		},this.validateTagList=function(){//验证标签数
			var liobj=$('#'+obthis.addedtagList+' li');
			if(liobj.length>=obthis.limitSize){
				$(inputObj).attr("disabled", true); 
				addtagbtnObj.attr('disabled', true);
				validateFlag[2]=false;
			}else if(liobj.length>0){
				$(inputObj).attr("disabled", false); 
				$('.addtagbtn').attr('disabled', false);
				validateFlag[2]=true;
			}else{
				$(inputObj).attr("disabled", false); 
				addtagbtnObj.attr('disabled', false);
				validateFlag[2]=false;
			}
			obthis.syncInputValue();//同时设置值
		},this.replaceSpecialChar=function(str){//替换特殊字符
			str=str+'';
			str=str.replaceAll('/','-');
			str=str.replaceAll('&','-');
			str=str.replace('*','-');
			str=$.trim(str);
			//str=str.replaceAll('*','-');
			return str;
		},this.closeDlg=function(){
			 $("#showTagDialog").remove();//删除
		},this.getValue=function(){//得到标签的值
			var strs='';
			$('#'+obthis.addedtagList+' li').each(function(){
				var name=$(this).data('id');
				strs=strs+name+',';
			});
			if($('#'+obthis.addedtagList+' li').length>0){
				strs=strs.substring(0,strs.length-1);
			}
			return strs;
		},this.syncInputValue=function(){
			var tagoutputObj=$("#"+obthis.tagoutput);
			if(tagoutputObj.length>0){
				tagoutputObj.val(obthis.getValue());
			}
		},this.initUpdate=function(entityId){//初使化标签修改列表
			obthis.clearTagList();
			if(entityId==undefined){
				return;
			}
			var url="/prescriptionTag/searchTags.json";
			var pars='entityType=2&entityId='+entityId;
			ccms.util.postData(url,{data:pars,success:function(data){
				obthis.showOutputResult(data.result);
			}});
		},this.showOutputResult=function(data){
			for(var i=0;i<data.length;i++){
				var obj=data[i];
				var name=obj.name;
				obthis.showAddedTagList(name);
			}
		},this.clearTagList=function(){
			$("#"+obthis.addedtagList).empty();
		}
	};
	
	
	$Highcharts.fn = $Highcharts.prototype = {

	};
	
	$Highcharts_init = $Highcharts.fn.init = function(map) {
		if(map==undefined){
			map={};
		}
		map.title = map.title != null ? map.title : "";
		map.type = map.type != null ? map.type : "";
		var obthis = this;
		this.init=function(){
			return this;
		},this.showChart=function(array){
			if(map.type=='pie'){
				obthis.showPieChart(array);
			}
		},this.loadScript=function(){
			/*require([contextPath+'/js/Highcharts/js/highcharts.js',contextPath+'/js/Highcharts/js/highcharts-3d.js',
			         contextPath+'/js/Highcharts/js/modules/exporting.js'],function(){
			});*/
		},this.showPieChart=function(array) {
			map.pietitle = map.pietitle != null ? map.pietitle : "";
			$('#'+map.chartId).highcharts({
		        chart: {
		            type: 'pie',
		            options3d: {
		                enabled: true,
		                alpha: 45,
		                beta: 0
		            }
		        },
		        title: {
		            text: map.title
		        },
		        tooltip: {
		            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		        },
		        plotOptions: {
		            pie: {
		                allowPointSelect: true,
		                cursor: 'pointer',
		                depth: 35,
		                dataLabels: {
		                    enabled: true,
		                    format: '{point.name}'
		                }
		                ,events:{
			            	click:function(e){
			            		if(map.click){
			            			map.click(e.point);
			            		}
			            	}
			            }
		            }
		        },
		        exporting:{ 
		            enabled:false //用来设置是否显示‘打印’,'导出'等功能按钮，不设置时默认为显示 
		        },
		        credits: {  
		            enabled: false // 去除链接和logo
		        },
		        series: [{
		            type: 'pie',
		            name: map.pietitle,
		            data: array
		        }]
		    });
		}

	};
	
	
	window["ccms"]["util"] = $Util;
	window["ccms"]["crud"] = $Crud;
	window["ccms"]["search"] = $Search;
	window["ccms"]["dialog"] = $Dialog;
	window["ccms"]["file"] = $FileUpload;
	window["ccms"]["constant"] = $Constant;
	window["ccms"]["customtag"] = $Extend;
	window["ccms"]["highcharts"] = $Highcharts;
	
	Date.prototype.format = function(format) {
		var o = {
			"M+" : this.getMonth() + 1,
			"d+" : this.getDate(),
			"h+" : this.getHours(),
			"m+" : this.getMinutes(),
			"s+" : this.getSeconds(),
			"q+" : Math.floor((this.getMonth() + 3) / 3),
			"S" : this.getMilliseconds()
		};
		if (/(y+)/.test(format))
			format = format.replace(RegExp.$1, (this.getFullYear() + "")
					.substr(4 - RegExp.$1.length));
		for ( var k in o)
			if (new RegExp("(" + k + ")").test(format))
				format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
						: ("00" + o[k]).substr(("" + o[k]).length));
		return format;
	};

	Date.prototype.addDate = function(days) {
		var d = this;
		d.setDate(d.getDate() + days);
		return d;
	};
	
	String.prototype.replaceAll = function(s1, s2) {
		return this.replace(new RegExp(s1, "gm"), s2);
	};
	String.prototype.endWith=function(str){  
		var reg = new RegExp(str+"$");  
		return reg.test(this);     
	};
	String.prototype.Trim = function() {
		return this.replace(/(^\s*)|(\s*$)/g, "");
	};
	String.prototype.LTrim = function() {
		return this.replace(/(^\s*)/g, "");
	};
	String.prototype.RTrim = function() {
		return this.replace(/(\s*$)/g, "");
	};
	String.prototype.getBytes = function() {
		var cArr = this.match(/[^\x00-\xff]/ig);
		return this.length + (cArr == null ? 0 : cArr.length);
	};

	/*
	 * 纵向合并table单元格 调用方法:$("#table1").rowspan(0);
	 * 传入的参数是对应的列数从0开始，哪一列有相同的内容就输入对应的列数值 封装的一个JQuery小插件
	 */
	jQuery.fn.rowspan = function(colIdx) {
		var tabObj = this.get(0);
		var colIndex = colIdx;
		if (tabObj != null) {
			var i, j;
			var intSpan;
			var strTemp;
			if (tabObj.rows == undefined) {
				return;
			}
			for (i = 0; i < tabObj.rows.length; i++) {
				intSpan = 1;
				if (typeof (tabObj.rows[i].cells[colIndex]) == "undefined") {
					continue;
				}
				strTemp = tabObj.rows[i].cells[colIndex].innerHTML;
				for (j = i + 1; j < tabObj.rows.length; j++) {
					if (strTemp == tabObj.rows[j].cells[colIndex].innerHTML) {
						intSpan++;
						tabObj.rows[i].cells[colIndex].rowSpan = intSpan;
						tabObj.rows[j].cells[colIndex].style.display = "none";
					} else {
						break;
					}
				}
				i = j - 1;
			}
		}
		return;
	};
	
	if(jQuery.validator==undefined){
		return;
	}

	jQuery.validator.addMethod("isChina", function(value, element) { // 验证中文
		return this.optional(element) || /^[\u4E00-\u9FA5]+$/.test(value);
	}, "必须是中文");

	jQuery.validator.addMethod("isEnglish", function(value, element) { // 验证昵称
		return this.optional(element)
				|| /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]+$/.test(value);
	}, "必须是字母与数字");

	jQuery.validator.addMethod("isPasswd", function(value, element) { // 验证密码
		return this.optional(element)
				|| /^(?=.*?[a-zA-Z])|(?=.*?\d)|(?=.*?[#@*&._~/$+|])+$/
						.test(value);
	}, "必须是字母、数字、特殊字符");

	jQuery.validator.addMethod("isIDCard", function(value, element) { // 身份证号
		return this.optional(element)
				|| /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(value);
	}, "身份证号格式不正确");

	jQuery.validator.addMethod("isMobile", function(value, element) { // 手机号或座机
		return this.optional(element)
				|| /(^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$)|(^1[358]\d{9}$)/
						.test(value);
	}, "联系方式格式不正确");

	jQuery.validator.addMethod("isNumber", function(value, element) { // 验证是数字
		return this.optional(element) || /^[0-9]*$/.test(value);
	}, "必须是数字");

	jQuery.validator.addMethod("isFloat", function(value, element) { // 验证是小数
		var patten = /^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/;
		return this.optional(element) || patten.test(value);
	}, "必须是小数");

	jQuery.validator.addMethod("isEmail", function(value, element) { // 邮箱地址
		return this.optional(element)
				|| /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value);
	}, "E-mail格式不正确");
	
})();