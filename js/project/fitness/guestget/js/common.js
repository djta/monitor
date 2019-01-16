/**
* 公共字符串处理函数
*/
String.prototype.replaceAll = function(s1,s2){   
	return this.replace(new RegExp(s1,"gm"),s2);   
};
String.prototype.startsWith = function(str){
	if(str==null || str=="" || this.length==0 || str.length>this.length){
		return false;
	}else if(this.substr(0,str.length) == str){
		return true;
	}else{
		return false;
	}
	return true;
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
/**
* 公共日期处理函数
*/
Date.prototype.format = function(format) {
	var o = {
		"M+" : this.getMonth() + 1, // month
		"d+" : this.getDate(), // day
		"h+" : this.getHours(), // hour
		"m+" : this.getMinutes(), // minute
		"s+" : this.getSeconds(), // second
		"q+" : Math.floor((this.getMonth() + 3) / 3), // quarter
		"S" : this.getMilliseconds() // millisecond
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
Date.prototype.addDate=function(days){ //添加天
    var d=this;
    d.setDate(d.getDate()+days); 
    return d;
};

/**
* radio checkbox 赋值
*/

function setCheckboxValue(radioName,radioValue,formName)
{
	$("form[name='"+formName+"'] input[name='"+radioName+"']").each(function(){
		if($(this).val() == radioValue){
			$(this).iCheck("check");
		}
	});
}

/**
* 下拉框赋值
*/

function setComboValue(comboName,comboValue,formName)
{	   
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
}

//获取radiobutton值
function GetRadioValue(val,formObj){
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
}

//获取checkbox值，逗号拼接
function GetCheckboxValue(val,formObj){
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
}

//选择多个checkbox值,在界面生成中使用
function selectMultiCheckboxValue(ic,fc,fm){
	var str = "";
	$("form[name='"+fm+"'] input[name='"+ic+"']").each(function(){
		if($(this)[0].checked == true){
			str += $(this).val()+",";
		}
	});
	$("form[name='"+fm+"'] input[name='"+fc+"']").val(str);
}

function setMulitCheckboxValue(fc,fc_value,fm){	
	var s = fc_value.split(",");
	for(var b=0;b<s.length;b++){
		if(s[b]!=null && s[b]!="")
			setCheckboxValue(fc,s[b],fm);
	}
}

function selectAll(id,formName){
	$("form[name='"+formName+"'] input[name='"+id+"']").each(function(){
		$(this).iCheck("check");
	});
}

function unselectAll(id,formName){
	$("form[name='"+formName+"'] input[name='"+id+"']").each(function(){
		$(this).iCheck("uncheck");
	});
}

function reverseselectAll(id,formName){
	$("form[name='"+formName+"'] input[name='"+id+"']").each(function(){
		if($(this)[0].checked == true){
			$(this).iCheck("uncheck");
		}else{
			$(this).iCheck("check");
		}
	});
}

function followselectAll(id,formName,flag){
	if(flag == true){
		selectAll(id,formName);
	}else{
		unselectAll(id,formName);
	}
}
/*数字匹配算法（formgen）*/
function checkActionParameter(code,val){
	var intCode = parseInt(code);
	var intVal = parseInt(val);
	var i = 8;
	var k = 0;
	while(i > 0 ){
		k = Math.pow(2,i);
		if(intCode > k){
			intCode = intCode - k;
			if(k == intVal || intCode == intVal){
				 return true;
			}
		}
		i--;
	}
	return false;
}
/* 根据下标获取数据元素值 */
function getArrValue(arr, idx){
	var value = "";
	if( arr instanceof Array && arr.length >= idx + 1 ){
		value = arr[idx];
		value = ( undefined != value && null != value ? value : "" );
	}
	return value;
}
/*Base64加解密*/
var $Base64 = {
	encodeChars:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",
	decodeChars:new Array(
		     -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		     -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		     -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
		     52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
		     -1,   0,   1,   2,   3,   4,   5,   6,   7,   8,   9, 10, 11, 12, 13, 14,
		     15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
		     -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
		     41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1),
	encode:function(str){
		 var out, i, len;
	     var c1, c2, c3;
	     len = str.length;
	     i = 0;
	     out = "";
	     while(i < len) {
	         c1 = str.charCodeAt(i++) & 0xff;
	         if(i == len)
	         {
	             out += $Base64.encodeChars.charAt(c1 >> 2);
	             out += $Base64.encodeChars.charAt((c1 & 0x3) << 4);
	             out += "==";
	             break;
	         }
	         c2 = str.charCodeAt(i++);
	         if(i == len)
	         {
	             out += $Base64.encodeChars.charAt(c1 >> 2);
	             out += $Base64.encodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
	             out += $Base64.encodeChars.charAt((c2 & 0xF) << 2);
	             out += "=";
	             break;
	         }
	         c3 = str.charCodeAt(i++);
	         out += $Base64.encodeChars.charAt(c1 >> 2);
	         out += $Base64.encodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
	         out += $Base64.encodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >>6));
	         out += $Base64.encodeChars.charAt(c3 & 0x3F);
	     }
	     return out;
	},
	decode:function(str){
		 var c1, c2, c3, c4;
	     var i, len, out;
	     len = str.length;
	     i = 0;
	     out = "";
	     while(i < len) {
	         do {
	             c1 = $Base64.decodeChars[str.charCodeAt(i++) & 0xff];
	         } while(i < len && c1 == -1);
	         if(c1 == -1)
	             break;
	         do {
	             c2 = $Base64.decodeChars[str.charCodeAt(i++) & 0xff];
	         } while(i < len && c2 == -1);
	         if(c2 == -1)
	             break;
	         out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));
	         do {
	             c3 = str.charCodeAt(i++) & 0xff;
	             if(c3 == 61)
	                 return out;
	             c3 = $Base64.decodeChars[c3];
	         } while(i < len && c3 == -1);
	         if(c3 == -1)
	             break;
	         out += String.fromCharCode(((c2 & 0XF) << 4) | ((c3 & 0x3C) >> 2));
	         do {
	             c4 = str.charCodeAt(i++) & 0xff;
	             if(c4 == 61)
	                 return out;
	             c4 = $Base64.decodeChars[c4];
	         } while(i < len && c4 == -1);
	         if(c4 == -1)
	             break;
	         out += String.fromCharCode(((c3 & 0x03) << 6) | c4);
	     }
	     return out;
	}
};

//微信JS接口
var $Wechat = {
	//判断是否是微信端
	isWechat:function(){
		return typeof(WeixinJSBridge) != "undefined" ? true : false ;
	},
	onBridgeReady:function(val){
		if ($Wechat.isWechat()){
			WeixinJSBridge.call(val);
		}
	},
	//隐藏右上角按钮
	hideOptioMenu:function(){
		$Wechat.onBridgeReady('hideOptionMenu');
	},
	//显示右上角按钮
	showOptioMenu:function(){
		$Wechat.onBridgeReady('showOptionMenu');
	},
	//隐藏底部导航栏没有返回值
	hideToolbar:function(){
		$Wechat.onBridgeReady('hideToolbar');
	},
	//显示底部导航栏没有返回值
	showToolbar:function(){
		$Wechat.onBridgeReady('showToolbar');
	}
};
//判断浏览设备的类型（Android,BlackBerry,IOS,windows,移动设备）
var $Mobile = {
	    Android: function() {
	        return navigator.userAgent.match(/Android/i) ? true : false;
	    },
	    BlackBerry: function() {
	        return navigator.userAgent.match(/BlackBerry/i) ? true : false;
	    },
	    iOS: function() {
	        return navigator.userAgent.match(/iPhone|iPad|iPod/i) ? true : false;
	    },
	    Windows: function() {
	        return navigator.userAgent.match(/IEMobile/i) ? true : false;
	    },
	    isWeiXin: function(){
	    	return navigator.userAgent.match(/MicroMessenger/i) ? true : false;
	    },
	    any: function() {
	        return ($Mobile.Android() || $Mobile.BlackBerry() || $Mobile.iOS() || $Mobile.Windows());
	    }
	};  

//纵向合并table单元格
//调用方法:$("#table1").rowspan(0);//传入的参数是对应的列数从0开始，哪一列有相同的内容就输入对应的列数值
jQuery.fn.rowspan = function(colIdx) { // 封装的一个JQuery小插件
		var tabObj = this.get(0);
		var colIndex = colIdx;
		if (tabObj != null) {
			var i, j;
			var intSpan;
			var strTemp;
			for (i = 0; i < tabObj.rows.length; i++) {
				intSpan = 1;
				if(typeof(tabObj.rows[i].cells[colIndex])=="undefined"){
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

$(document).ready(function() {
	/**
	* 初始化常用验证函数
	*/
	jQuery.validator.addMethod("isChinaOrEng", function(value, element) { //验证中文
		return this.optional(element) || /^[\u4E00-\u9FA5-a-zA-Z0-9]+$/.test(value); 
	}, "不能输入特殊字符");

	jQuery.validator.addMethod("isChina", function(value, element) { //验证中文
		return this.optional(element) || /^[\u4E00-\u9FA5]+$/.test(value); 
	}, "必须是中文");
	
	jQuery.validator.addMethod("isEnglish", function(value, element) { //验证昵称
		return this.optional(element) || /^[a-zA-Z0-9]+$/.test(value); 
	}, "必须是字母与数字");
	
	jQuery.validator.addMethod("isIDCard", function(value, element) { //身份证号
		return this.optional(element) || /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(value); 
	}, "身份证号格式不正确");	
	
	jQuery.validator.addMethod("isMobile", function(value, element) { //手机号或座机
		return this.optional(element) || /(^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$)|(^1[3578]\d{9}$)/.test(value); 
	}, "联系方式格式不正确");
	
	jQuery.validator.addMethod("isNumber", function(value, element) { //验证是数字
		return this.optional(element) ||  /^[0-9]*$/.test(value); 
	}, "必须是数字");
	
	jQuery.validator.addMethod("isFloat", function(value, element) { //验证是小数
		var patten = /^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/;
		return this.optional(element) ||  patten.test(value);
	}, "必须是小数");
	
	jQuery.validator.addMethod("isEmail", function(value, element) { //邮箱地址
		return this.optional(element) ||   /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(value); 
	}, "E-mail格式不正确");
	
});