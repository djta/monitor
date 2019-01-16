var required_str = '不能为空';
var length_str = '位字符：数字、字母和中文均可';
var int_str = '必须是数字';



var inputfocusob;//获取焦点对象
/*function initvalidateForm(attrMap, validateRuleMap, formid) {// 初使化验证
	if (formid == undefined) {
		formid = 'collect_form';
	}
	if ($("#" + formid + " :input[type='text']").size() == 0)
		$("input[type='text']").each(function() {
			initvalidate(this);
		});
	$("#" + formid + " :input[type='text']").each(function() {
		initvalidate(this);
	});
	$("#" + formid).find('select').each(function() {
		initvalidate(this);
	});
	$("#" + formid).find('textarea').each(function() {
		initvalidate(this);
	});
}*/


function initvalidate(ob) {// 初使化验证
	var temp_val = '';
	$(ob).mousedown(function(event) {
		removemsgdiv(ob);
	});
	$(ob).focus(function(event) {
		temp_val = $(ob).val();
		//removemsgdiv(ob);
	});
	$(ob).keydown(function(event) {
		// temp_val = $(ob).val();
	});
	
}


// 验证
function initvalidatemsg(ob) {
	var attrCode = $(ob).attr('name');
	var input_val = $.trim($(ob).val());
	if (!validate.isEmpty(input_val)) {// 不为空才验证
		
		if (attrMap[attrCode] != undefined
				&& attrMap[attrCode].fieldType == 'STRING') {// 数据类型为字符串才验证长度
			var attrlen = parseInt(attrMap[attrCode].fieldLength);// 长度验证 //// 而且类型是text的
			if ($(ob).val().strLen() > attrlen) {
				//var tip = attrlen + '' + length_str;
				//var tip='字符已超过最大长度';
				var tip=attrlen+'位字符：数字、字母或'+parseInt(attrlen/3)+'位中文均可';
				initErrorMsg(ob, tip);// 生成错误消息
			}
		} else if (attrMap[attrCode] != undefined
				&& attrMap[attrCode].fieldType == 'INT') {// 数据类型为数字
			if (!validate.isInt(input_val)) {
				var tip = int_str;
				initErrorMsg(ob, tip);// 生成错误消息
			}
		} else if (attrMap[attrCode] != undefined
				&& attrMap[attrCode].fieldType == 'DOUBLE') {// 数据类型为数字
			if (!validate.isInt(input_val)) {
				var tip = int_str;
				initErrorMsg(ob, tip);// 生成错误消息
			}
		}
	}

}

function validateInput(ob, rule, input_val, desc) {
	rule = encodeURIComponent(rule);
	var pars = 'rule=' + rule + '&value=' + input_val;
	$.ajax({
		type : "POST",
		url : "../../collect/validate/validate!check.action",
		cache : "false",// cache: false,
		dataType : "json",
		data : pars,
		async : false,
		success : function(json) {//
			if (!json.result)
				initErrorMsg(ob, desc);
		}
	});
}

function iserrorinput(ob) {// 是否错误
	var size = $(ob).parent().parent().children('div.tip_red').size();
	if (size > 0)
		return true;
	return false;
}

function inputfocus(ob) {//获得焦点
	var attrText = $(ob).attr('type');
	 //  if(attrText!='text'){
	//	   return;
	//   }
	var size = $(ob).parent().parent().children('div.tip_red').size();
	if (size > 0)
	  $(ob).focus();//获得焦点
}

function isValidateInput(ob) {// 是否有错误信息
	var size = $(ob).parent().parent().children('div.tip_red').size();
	if (size > 0)
	   return  true;
	else 
	   return false;
}

function initErrorMsg(ob, tip) {// 生成错误消息
	var size = $(ob).parent().children('div.tip_red').size();
	if (size > 0)
		removemsgdiv(ob);
	addmsgdiv(ob, tip);
	//$(ob).focus();//获得焦点
}

function addmsgdiv(ob, tip) {// 添加消息
	$(ob).parent().append('<div class="tip_red">' + tip + '</div>');
}

function removemsgdiv(ob) {// 删除消息节点
	$(ob).parent().children('div.tip_red').remove();
}

var  editoritems=[
				'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
				'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
				'insertunorderedlist', '|', 'emoticons', 'image', 'link','code','removeformat'];//编辑器功能

function replaceHtml(str){
	if(str!=''){
		str=str.replace('<br />','');
		str=str.replace('<br','');
		str=str.replace('/>','');
		str=str.replace('/>','');
	}
	return str;
}

//js中的字符串正常显示在HTML页面中  
String.prototype.displayHtml= function(){  
    //将字符串转换成数组  
    var strArr = this.split('');  
    //HTML页面特殊字符显示，空格本质不是，但多个空格时浏览器默认只显示一个，所以替换  
    var htmlChar="&<>";  
    for(var i = 0; i< str.length;i++){  
        //查找是否含有特殊的HTML字符  
        if(htmlChar.indexOf(str.charAt(i)) !=-1){  
            //如果存在，则将它们转换成对应的HTML实体  
            switch (str.charAt(i)) {                          
                case '<':  
                    strArr.splice(i,1,'&#60;');  
                    break;  
                case '>':  
                    strArr.splice(i,1,'&#62;');  
                    break;  
                case '&':  
                    strArr.splice(i,1,'&#38;');  
            }  
        }  
    }  
    return strArr.join('');  
}  




String.prototype.gblen = function() {  
    var len = 0;  
    for (var i=0; i<this.length; i++) {  
        if (this.charCodeAt(i)>127 || this.charCodeAt(i)==94) {  
             len += 2;  
         } else {  
             len ++;  
         }  
     }  
    return len;  
}  
String.prototype.gbtrim = function(len, s) {  
    var str = '';  
    var sp   = s || '';  
    var len2 = 0;  
    for (var i=0; i<this.length; i++) {  
        if (this.charCodeAt(i)>127 || this.charCodeAt(i)==94) {  
             len2 += 2;  
         } else {  
             len2 ++;  
         }  
     }  
    if (len2 <= len) {  
        return this;  
     }  
     len2 = 0;  
     len   = (len > sp.length) ? len-sp.length: len;  
    for (var i=0; i<this.length; i++) {  
        if (this.charCodeAt(i)>127 || this.charCodeAt(i)==94) {  
             len2 += 2;  
         } else {  
             len2 ++;  
         }  
        if (len2 > len) {  
             str += sp;  
            break;  
         }  
         str += this.charAt(i);  
     }  
    return str;  
}  

String.prototype.replaceAll  = function(s1,s2){  
	var str=this;
	while( str.indexOf(s1) != -1 ) {
	     str=str.replace(s1,s2); 
	}
	return str;
}  

function getRegexStr(){//得到正则访问值
	var html=arguments[0];
	var numargs = arguments.length;
	for(var i=0;i<numargs-1;i++){
		var index=i+1;
		a:while(true){
			var rhtml=html.replace('${'+index+'}',arguments[index]);
			if(html==rhtml){
				break a;
			}
			html=rhtml;
		}
	}
	return  html;
}

function  replaceSpecialChar(str){//替换特殊字符
	str=str.replaceAll('/','-');
	str=str.replaceAll('&','-');
	str=str.replaceAll('*','-');
	return str;
}
