var starttypeMap = {"0":"当天启用", "1":"两周内不限时启用", "2":"两月内不限时启用", "3":"初次训练启用", "4":"固定时间启用"};	/** 卡启用方式 */
var starttypeMap2 = {"0":"当天开卡", "1":"原卡过期后两周内不限时启用", "2":"原卡过期后两月内不限时启用", "3":"原卡过期后初次训练启用", "4":"固定时间启用", "5":"接原卡启用"};	/** 续卡启用方式 */

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

// 加载照片
function loadPic(objid, vc_code){
	var uri = contextPath+ "/action/project/erpClub/attachment/getcustimage?objid=" + objid + "&vc_code=" + vc_code;
	ajaxCall(uri,{
		method: "get",
		progress: true,
		dataType: "script",
	    success: function() {
	    }
	});
}

var wdapp,wddoc;
function printdoc(path){
	if( path == undefined || "" == path ){
		ccms.dialog.notice("打印失败");
		return false;
	}
	console.log("Print Begin");
	//window.open(contextPath + path);
	//window.open(contextPath + "/action/project/erpClub/print/pdfpreview?path=" + path, "_blank", "toolbar=no,menubar=no,location=no,status=no");
	window.open(contextPath + path, "_blank", "toolbar=no,menubar=no,location=no,status=no");
	/**
	try{
		// 获取Word过程	 请设置IE的可信任站点
		wdapp = new ActiveXObject("Word.Application");
	}catch(e){
		alert("无法调用Office对象，请确保您的机器已安装了Office并已将本系统的站点名加入到IE的信任站点列表中！");
		//wdapp.quit();
		wdapp = null;
		return;
	}
	//wdapp.visible = true;
	//wdapp.Application.Activate();
	path = path.replace(/\/\//g, "\/");
	path = path.replace(/\//g, "\\");
	wdapp.Documents.Open(path);	// 打开word模板url
	wddoc = wdapp.ActiveDocument;
	
	//var rang = wddoc.Bookmarks("manager").Range;	// word模板中标签为manager对象
	//range.Text = '';	// 给标签为manager对象赋值（追加）
	wdapp.ActiveDocument.ActiveWindow.View.Type = 3;
	//wdapp.visible = false;	// word模板是否可见
	//wddoc.saveAs("c:\\apply_temp.doc"); // 保存临时文件word
	
	wdapp.Application.Printout();	//	调用自动打印功能
	wdapp.quit();
	wdapp = null;
	//var idTmr = window.setInterval("cleanUp();",1);
	*/
	console.log("Print End");
}

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
	ajaxCall("/action/project/erpClub/home/loadmenubtn?uri=" + currenturi,{
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
function loadPayType(objid, templateid, callback){
	if( undefined == objid || "" == objid ){
		ccms.dialog.notice("参数objid不能为空");
	}else{
		var url = "/action/project/erpClub/msSellSys/loadpaytype?objid="+objid+"&templateid="+templateid;
		ajaxCall(url,{
			method : "get",
			progress : true,
			dataType : "script",
			success : function() {
				if(callback){
					callback();
				}
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

//四舍五入保留两位小数
function getMoney(money){
	return Number(money).toFixed(2);
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


$(document).ready(function() {
	checkMenuBtn();
});
