var oXL, oWB, oSheet;
function printExcel(path){
	console.log("Print Begin");
	window.open(contextPath + path);
	/**
	try{
		// 获取Word过程	 请设置IE的可信任站点
		oXL = new ActiveXObject("Excel.Application");	// 创建操作EXCEL应用程序的实例 
	}catch(e){
		alert("无法调用Office对象，请确保您的机器已安装了Office并已将本系统的站点名加入到IE的信任站点列表中！");
		oXL = null;
		return;
	}
	path = path.replace(/\/\//g, "\/");
	path = path.replace(/\//g, "\\\\");
	oWB = oXL.Workbooks.open(path);	// 打开指定路径的excel文件
	oWB.worksheets(1).select();	//操作第一个sheet
	oSheet = oWB.ActiveSheet;
	//oSheet.Cells(row,col).Value = "内容";
	oSheet.Application.Visible = true;	// 使Excel通过 Application对象可见
	oWB.PrintOut;	// 打印
	oWB.Close(savechanges=false);	// 关闭
	// 结束进程
	oXL.Quit();
	oXL = null;
	oWB = null;
	oSheet = null;
	*/
	console.log("Print End");
}

// 小数
function isFloat(value){
	var patten = /^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/;
	return patten.test(value);
}

// 数字
function isNumber(value){
	return /^[0-9]*$/.test(value);
}

// 手机或固话
function isPhone(value){
	return /(^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$)|(^1[34578]\d{9}$)/.test(value);
}

// 格式化输出值
function formatStrValue(_value){
	_value = (undefined == _value ? "" : _value);
	return _value;
}

function getArrayValue(array, idx){
	var avalue = "";
	if( array.length >= idx ){
		avalue = array[idx];
		avalue = ( undefined != avalue && null != avalue ? avalue : "" );
	}
	return avalue;
}

// 日期加减天数
function addDate(fdate, day){
	var tdate = new Date(fdate);
	tdate = tdate.valueOf();
	tdate = tdate + day * 24 * 60 * 60 * 1000;
	tdate = new Date(tdate);
	return tdate;
}

// 获取域值
function getSelectDomain(objid, namespace, parent_namespace, parent_domain_value, callback){
	if( undefined == objid || "" == objid ){
		ccms.dialog.notice("Parameter id can not be null.");
		return false;
	}else if( undefined == namespace || "" == namespace ){
		ccms.dialog.notice("Parameter namespace can not be null.");
		return false;
	}
	parent_namespace = (undefined != parent_namespace ? parent_namespace : "");
	parent_domain_value = (undefined != parent_domain_value ? parent_domain_value : "");
	var uri = "/action/project/fitness/util/getdomain";
	uri += "?id=" + objid + "&namespace=" + namespace + "&parent_namespace=" + parent_namespace + "&parent_domain_value=" + parent_domain_value;
	return ajaxCall(uri,{
		method: "get",
		progress: true,
		dataType: "script",
		success: function() {
			if( undefined != callback ){
				callback();
			}
		}
	});
}

// 加载照片
function loadPic(objid, vc_code){
	var uri = contextPath+ "/action/project/fitness/attachment/getcustimage?objid=" + objid + "&vc_code=" + vc_code;
	ajaxCall(uri,{
		method: "get",
		progress: true,
		dataType: "script",
	    success: function() {
	    }
	});
}

function printdoc(path){
	if( path == undefined || "" == path ){
		ccms.dialog.notice("打印失败");
		return false;
	}
	window.open(contextPath + path, "_blank", "toolbar=no,menubar=no,location=no,status=no");
}

function createReportForm(objid){
	// 封装需要提交的数据
	if( undefined != $("#_reportForm") ){
		$("#_reportForm").empty();
	}
	$("#" + objid).after('<form id="_reportForm" name="_reportForm" style="display:none;"></form>');
	var str = '';
	var fieldstr = "";
	$("#" + objid).find("tr").each(function(idx,ele){
		$(this).find("td").each(function(idx2,ele2){
			var value = $(this).text();
			value = (undefined == value || null == value ? "" : value);
			str += '<input type="hidden" name="f' + (idx2+1) + '" value="' + value + '" />';
			if( idx == 1 ){
				fieldstr += "f" + (idx2+1) + ";";
			}
		});
	});
	$("#_reportForm").append(str);
	$("#_reportForm").append('<input type="hidden" name="paramlist" value="' + fieldstr + '">');
	$("#_reportForm").append('<input type="hidden" name="datatype" value="page" />');
}
function getRedirectPath(uri){
	var keyword = "/action/ccms/redirect?url=";
	if( uri.indexOf(keyword) >= 0 ){
		uri = uri.substring(uri.indexOf(keyword) + keyword.length);
	}
	if( uri.indexOf(keyword) >= 0 ){
		return getRedirectPath(uri);
	}
	return uri;
}
function checkMenuBtn(callback){
	var currentpath = window.location.href;
	var currenturi = "";
	if( currentpath.indexOf("/action/ccms/redirect?url=") >= 0 ){
		currenturi = getRedirectPath(currentpath);
	}else if( currentpath.indexOf("?mainuri=") >= 0 ){
		currenturi = $Base64.decode(currentpath.substring(currentpath.indexOf("?mainuri=") + "?mainuri=".length - 1));
	}else{
		currenturi = currentpath;
	}
	if( currenturi.indexOf(contextPath + "/") >= 0 ){
		currenturi = currenturi.substring(currenturi.indexOf(contextPath + "/") + (contextPath + "/").length - 1);
	}
	if( currenturi.indexOf("?") >= 0 ){
		currenturi = currenturi.substring(0, currenturi.indexOf("?"));
	}
	if( currenturi.indexOf("/action/") >= 0 ){
		var str = currenturi.split("/action/");
		currenturi = (str.length == 2 ? "/action/" + str[1] : currenturi);
	}
	ajaxCall("/action/project/fitness/home/loadmenubtn?uri=" + currenturi,{
		method:"get",
		progress:false,
		dataType:"script",
		success:function(){	
			if( undefined != callback ){
				callback();
			}
		}
	});
}
// 加载支付方式
function loadPayType(objid, callback){
	if( undefined == objid || "" == objid ){
		ccms.dialog.notice("参数objid不能为空");
	}else{
		var url = "/action/project/fitness/contract/util/loadpaytypelist?objid="+objid;
		ajaxCall(url,{
			method : "get",
			progress : true,
			dataType : "script",
			success : function() {
				if(callback){
					callback();
				}
				$('#paymethod').selectpicker("refresh");
				$('#paymethod').selectpicker("render");
			}
		});
	}
}
// 加载支付方式值
function setPayTypeValue(paydetail){
	if( undefined != paydetail && "" != paydetail ){
		var pobj = $("._paytype");
		pobj.each(function(idx,value){
			$(this).attr("readonly",true);
			$(this).addClass("inputStyle");
		});
		var payarr = paydetail.split(";");
		var len = payarr.length;
		for(var i = 0; i < len; i++){
			var payvalue = payarr[i];
			if( "" == payvalue || !isFloat(payvalue) ){
				continue;
			}
			pobj.eq(i).val(payvalue);
		}
	}
}
// 支付总金额
function getPayTotalmoney(){
	var f_allmoney = 0;
	var _paydetail = "";
	$("._paytype").each(function(i,ele){
		var _value = $(this).val();
		_value = ( "" == _value || !isFloat(_value) ? 0 : _value);
		$(this).val(_value);
		f_allmoney = parseFloat(f_allmoney) + parseFloat(_value);
		_paydetail += _value + ";";
	});
	$("#_paydetail").val(_paydetail);
	return f_allmoney.toFixed(2);
}

function initPayValue(){
	$("._paytype").each(function(idx,ele){
		$(this).val(0);
	});
}
function setPayTypeReadonly(){
	$("._paytype").each(function(idx,ele){
		$(this).attr('readonly','readonly');
	});
}
function removePayTypeReadonly(){
	$("._paytype").each(function(idx,ele){
		$(this).removeAttr('readonly');
		$(this).removeClass('inputStyle');
	});
}
function getIntegerValue(str, defaultValue){
	var out = null;
	if( null != str && "" != str && isNumber(str) ) {
		out = parseInt(str);
	}else if( undefined != defaultValue ){
		out = defaultValue;		
	}
	return out;
}
function getFloatValue(str, defaultValue){
	var out = null;
	if( null != str && "" != str && isFloat(str) ) {
		out = parseFloat(str);
	}else if( undefined != defaultValue ){
		out = defaultValue;		
	}
	return out;
}
// 根据map的value获取key值
function getMapKeyByValue(map, value){
	var _key = "";
	for (var key in map) {
		if( undefined != map[key] && "" != map[key] && value == map[key] ){
			_key = key;
		}
	}
	return _key;
}
// 根据身份证获取生日
function getBirthdayByIdcard(idcard) {
	if (idcard == null || idcard == "") {
		return '';
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
	}else if( !isDate(birthday) ){
		
		return "";
	}
	return birthday;
}
// 判断是否为日期
function isDate(date) {
    var result = date.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
    if (result == null){
        return false;
    }
    var d = new Date(result[1], result[3] - 1, result[4]);
    return (d.getFullYear() == result[1] && (d.getMonth() + 1) == result[3] && d.getDate() == result[4]);
}
// 根据身份证获取性别	0女，1男
function getSexByIdcard(idcard) {
	if (idcard == null || idcard == "") {
		return '';
	}
	var sex = "";
	var len = idcard.length;
	if( 15 == len ){	// 15位身份证号：最后一位奇数表示男，偶数表示女
		if ( Number(idcard.substr(14, 1)) % 2 == 1) {
			sex = "1";
		}else{
			sex = "0";
		}
	}else if( 18 == len ){	// 18位身份证号：倒数第二位奇数表示男，偶数表示女
		if ( Number(idcard.substr(16, 1)) % 2 == 1) {
			sex = "1";
		}else{
			sex = "0";
		}
	}
	return sex;
}
// 日期增加月份
function addMonth(_date, _month) {
	_month = parseInt(_month);
	var sDate = new Date(_date);
	var sYear = sDate.getFullYear();
	var sMonth = sDate.getMonth() + 1;
	var sDay = sDate.getDate();
 
	var eYear = sYear;
	var eMonth = sMonth + _month;
	var eDay = sDay;
	while (eMonth > 12) {
		eYear++;
		eMonth -= 12;
	}
	var eDate = new Date(eYear, eMonth - 1, eDay);
	while (eDate.getMonth() != eMonth - 1) {
		eDay--;
		eDate = new Date(eYear, eMonth - 1, eDay);
	}
	return eDate.format("yyyy-MM-dd");
}
//通过get提交JSON ajaxCall
function getJsonAjaxCall(uri, isprogress, callback){
	ajaxCall(uri, {
		method : "get",
		progress : isprogress == true ? true : false,
		dataType : "json",
		success : function(data) {
			if( undefined != callback ){
				callback(data);
			}
		}
	});
}
// 通过get提交ajaxCall
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
//通过post提交ajaxCall
function postAjaxCall(uri, formid, isprogress, callback){
	ajaxCall(uri, {
		method : "post",
		form : formid,
		progress : isprogress == true ? true : false,
		dataType : "script",
		success : function() {
			if( undefined != callback ){
				callback();
			}
		}
	});
}
// 计算两个时间的差额
function dateReduce(date1, date2){
	var str = "";
	var s1 = date1.getTime(), s2 = date2.getTime();
	var total = (s2 - s1)/1000;
	var day = parseInt(total / (24*60*60));	// 计算整数天数
	if( day > 0 ){
		str += day + "天";
	}
	var afterDay = total - day*24*60*60;	// 取得算出天数后剩余的秒数
	var hour = parseInt(afterDay/(60*60));	// 计算整数小时数
	if( hour > 0 ){
		str += hour + "小时";
	}
	var afterHour = total - day*24*60*60 - hour*60*60;	// 取得算出小时数后剩余的秒数
	var min = parseInt(afterHour/60);	// 计算整数分
	if( min > 0 ){
		str += min + "分钟";
	}
	//var afterMin = total - day*24*60*60 - hour*60*60 - min*60;	// 取得算出分
	return str;
}
function getMapValue(map, key){
	var value = "";
	if( undefined != map && undefined != key && "" != key ){
		value = map[key];
	}
	return value;
}
function lpad(str, n, joinstr) {
	var len = str.toString().length;
	if( len > n ){
		str = str.substring(len - n);
	}
	while(len < n) {
		str = joinstr + str;
		len++;
	}
	return str;
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


// 增加收款方式
function addShowPaymethod(paymethod, paymoney, paymethodName, isshow){
	var str = '<span  style="width:130px"  code1="'+paymethod+'" code2="'+paymoney+'">';
		str += '<span >'+paymethodName+'</span>：<span name="fee" >'+paymoney+'元</span>&nbsp;&nbsp;';
	if( !isshow ){
		str += '<img  style="margin-top:-2px" height="20" width="45"   src="'+contextPath+'/js/project/fitness/image/SVG/nav/shanchu.svg" title="删除" onclick="delPaymethod(this)">';
	//	str += '<a href="javascript:void(0)" title="删除" onclick="delPaymethod(this)">×</a>';
	}
	str += '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>';
	$("#paydiv").append(str);
	setSelectValue($("#paymethod"), "");
	$("#paymoney").val("");
}
// 合同付款
function pay(){
	// 判断合同状态
	getJsonAjaxCall("/action/project/fitness/contract/util/querycontractstatus?contractcode="+$("#contractcode").val(), "true", function(data){
		if( data && Number(data.status) >= 2 ){
			ccms.dialog.notice("已付款合同不允许重复付款！", 2000);
		}else if( data && Number(data.isaudit) == 1 ){
			ccms.dialog.notice("该合同需要审批后才允许付款！", 2000);
		}else if( data && Number(data.isaudit) == 3 ){
			ccms.dialog.notice("该合同审批未通过，无法继续操作！", 2000);
		}else{
			var paydetail = "";
			$("#paymethod").find("option").each(function(idx,ele){
				if( "" != $(ele).attr("code") ){
					paydetail += $(ele).attr("code") + ";";
				}
			});
			$("#paydetail").val(paydetail);
			$("#factmoney").val(paytotal);
			postAjaxCall($("#actionroot").val()+"/pay", "contractForm", true);	// 未付款合同可修改
		}
	});
}
// 删除支付
function delPaymethod(obj){
	var mobj = $(obj).parent();
	$("#paymethod").find("option[value="+mobj.attr("code1")+"]").attr("code", "0");
	paytotal = (parseFloat(paytotal) - parseFloat(mobj.attr("code2"))).toFixed(2);
	$("#factmoneyspan").text(paytotal);
	mobj.remove();
}

// 获取合同路径
function getContractUri(contracttype, type, isshow){
	var baseuri = "/action/project/fitness/contract";
	if( 1 == Number(contracttype) || 2 == Number(contracttype) ){	/** 升级合同 */
		baseuri += "/upgrade";
	}else if( 0 == Number(type) || 5 == Number(type) ){	/** 办卡合同、定金合同 */
		baseuri += "/newcard";
	}else if( 1 == Number(type) || 12 == Number(type) ){	/** 租柜合同、续租柜合同 */
		baseuri += "/cabinet";
	}else if( 2 == Number(type) ){	/** 私教合同 */
		baseuri += "/pt";
	}else if( 4 == Number(type) ){	/** 退卡合同 */
		baseuri += "/sendback";
	}else if( 7 == Number(type) || 9 == Number(type) || 11 == Number(type) ){	/** 续卡合同 */
		baseuri += "/continue";
	}else if( 10 == Number(type) ){	/** 转卡合同 */
		baseuri += "/turn";
	}
	if( 3 == Number(contracttype) ){	/** 还款合同 */
		baseuri += "/repay";
	}else{
		baseuri += "/create";
	}
	if( isshow ){
		baseuri += "/view";
	}else{
		baseuri += "/form";
	}
	return baseuri;
}

// 全选
function selectAll(objname){
	$("input[name="+objname+"]").iCheck('check');
}
// 取消全选
function unSelectAll(objname){
	$("input[name="+objname+"]").iCheck('uncheck');
}
// 反选
function reverseSelect(objname){
	$("input[name="+objname+"]").each(function(idx,ele){
		if( $(ele).prop("checked") ){
			$('input[name=datalist]').iCheck('uncheck');
		}else{
			$('input[name=datalist]').iCheck('check');
		}
	});
}


/**
* 通用的ajax调用（异常时不弹出错误提示）
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
function silentAjaxCall(url, map){
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
					console.log("系统错误.");
					if (error != null){//执行错误回调函数
						error();
					}
					break;  
				case(400):  
					console.log("系统错误.");
					break;  
				case(401):  
					console.log("要访问的服务需要 SSL.");
					break;  
				case(403):  
					console.log("拒绝访问.");
					break;  
				case(404):  
					console.log("要访问的服务不存在.");
					break; 
				case(408):  
					console.log("请求超时.");
					break;  
				default:  
					if(errThrow.toString().indexOf("JSON.parse") < 0){
						console.log("其他错误."+errThrow);
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
				return;
			}
			if (text.indexOf("_ajax_VE8374yz_")>0){//跳转到登录界面
				return;
			}
			if(XMLHttpRequest.status == 200){
				if(contentType.indexOf("text/validate") < 0){//验证失败的自定义错误
					if(contentType.indexOf("text/javascript") < 0){ //js代码会被jquery自动执行
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
								console.log("JSON格式不合法.");
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
							console.log("语法错误.");
						}
					}else{
						eval(text);
					}
				}
				
			}
		}
	});
}

function selectInit(obj, width){
	/**$(obj).wrap("<div></div>");*/
	var selectwidth = "150px";
	if( typeof(width) != "undefined" ){
		selectwidth = width;
	}
	$(obj).selectpicker({width: selectwidth});
}
function searchSelectInit(obj, width){
	/**$(obj).wrap("<div></div>");*/
	var selectwidth = "150px";
	if( typeof(width) != "undefined" ){
		selectwidth = width;
	}
	$(obj).attr("data-live-search","true");
	$(obj).selectpicker({width: selectwidth});
}
function setSelectValue(obj, value){
	$(obj).val(value);
	$(obj).selectpicker("refresh");
	$(obj).selectpicker("render");
}

function renderCheckboxObj(obj){
	$(obj).iCheck({
		checkboxClass : 'icheckbox_square-blue',
		increaseArea : '20%'
	});
}
function renderRadioObj(obj){
	$(obj).iCheck({
		radioClass : 'iradio_square-blue',
		increaseArea : '20%'
	});
}
function setCheckboxRadioValue(radioName,radioValue){
	$("input[name='"+radioName+"']").each(function(){
		if($(this).val() == radioValue){
			$(this).iCheck("check");
		}
	});
}

function getJsonValue(json, key, defaultvalue){
	var value = defaultvalue;
	if( undefined != json[key] && "" != json[key] ){
		value = json[key];
	}
	return value;
}
/** 根据日期获取周一 */
function getFirstDayOfWeek (date) {
	var day = date.getDay() || 7;
	return new Date(date.getFullYear(), date.getMonth(), date.getDate() + 1 - day);
}

function isInArray(arr,value){
	for(var i = 0; i < arr.length; i++){
		if(value === arr[i]){
			return true;
		}
	}
	return false;
}

function datedifference(sDate1, sDate2) {    //sDate1和sDate2是2006-12-18格式  
    var dateSpan,
        tempDate,
        iDays;
    sDate1 = Date.parse(sDate1);
    sDate2 = Date.parse(sDate2);
    dateSpan = sDate2 - sDate1;
    dateSpan = Math.abs(dateSpan);
    iDays = Math.floor(dateSpan / (24 * 3600 * 1000));
    return iDays
};

function validateNewCardcode(memberhead, code, objid){
	var newcode = "";
	if( null == memberhead || memberhead.length != 2 ){
		ccms.dialog.notice("编号头有且只能是2位", 2000);
	}else if( "undefined" == typeof(code) || null == code || "" == code || code.length <= 2 ){
		ccms.dialog.notice("会员卡号位数不足", 2000);
	}
	var iscardcodelimit = "0";	/** 是否限制卡号位数 : 0不限制  1限制 */
	var cardcodelimitlen = "0";	/** 卡号处理方式:0两位头字母+右六位  1两位头字母+右五位  不足位数补0*/
	ajaxCall("/action/project/fitness/util/querycardcodeconfig",{
		method: "get",
		async: false,
		progress: true,
		dataType: "json",
		success: function(data) {
			if( "undefined" != typeof(data) && undefined != data ){
				iscardcodelimit = data.iscardcodelimit;
				cardcodelimitlen = data.cardcodelimitlen;
			}
		}
	});
	if( "1" == iscardcodelimit ){	/** 限制卡号位数 */
		var extcode = code.substring(2);
		if( "1" == cardcodelimitlen ){	/** 1两位头字母+右五位 */
			newcode = memberhead + lpad(extcode, 5, "0");
		}else{	/** 两位头字母+右六位 */
			newcode = memberhead + lpad(extcode, 6, "0");
		}
	}else{
		newcode = code;
	}
	$("#"+objid).val(newcode);
}

$(document).ready(function() {
	if( '' != '${def:user}'){
		checkMenuBtn();
	}
	/**$("input[type=text],input[type=hidden]").wrap("<div></div>");*/
});
