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
	var uri = "/action/project/pension/pick/getdomain";
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
	ajaxCall("/action/ccms/xhome/loadmenubtn?uri=" + currenturi,{
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
function addMonth(_date, _month){
	var _tdate = new Date(_date);
	var year = _tdate.getFullYear();
	var month = _tdate.getMonth() + 1 + Number(_month);
	if( Number(month) > 12 ){
		var _month = month;
		month = Number(_month) % 12;
		var _year = (Number(_month) - month)/12;
		year += _year;
	}
	if( month == 0 ){
		month = 12;
		year--;
	}
	month = (month >= 10 ? "" : "0") + month;
	var tdate = year + "-" + month + _tdate.format("-dd");
	return tdate;
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

$(document).ready(function() {
	if( '' != '${def:user}'){
		checkMenuBtn();
	}
});
