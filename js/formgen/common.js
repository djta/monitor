
String.prototype.replaceAll = function(s1,s2){   
	return this.replace(new RegExp(s1,"gm"),s2);   
	}

String.prototype.endWith=function(oString){  
	var   reg=new   RegExp(oString+"$");  
	return   reg.test(this);     
	}  
//千位分隔符
function fmoney(s)   
{   
	if(s==""){
		return "";
	}
   s = parseFloat((s + "").replace(/[^\d\.-]/g, "")) + "";  
   var l = s.split(".")[0].split("").reverse(),    
   t = "";   
   for(i = 0; i < l.length; i ++ )   
   {   
      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
   }
   return t.split("").reverse().join("") ;   
} 
//去左右两边空格
function Trim(str)
{
    var num = str.length;
    var i = 0;
    for(i = 0; i < num;i++) {
        if(str.charAt(i) != " ")
            break;
    }
    str = str.substring(i);
    num = str.length;
    for(i = num-1; i > -1;i--) {
        if(str.charAt(i) != " ")
            break;
    }
    str = str.substring(0, i+1);
    return str;
}
//必填项目加红色星号
function changeRequiredStyle() { 
	var cell=document.getElementsByTagName('td'); 
	var redstar = '<font color="#FF0000">(*)</font>';
	for(c=0;c<cell.length;c++) {
		if (cell[c].className =="th20 required")
		{
			if (cell[c].innerHTML.indexOf("*")<0)
			{
				cell[c].innerHTML = cell[c].innerHTML + redstar;
			}
			
		}
	} 

	//按键监听事件
	if(typeof(setKeyPress)=="function"){setKeyPress();}
	//页面所有text控件全角校验
	if(typeof(allElementsCheckDBC)=="function"){allElementsCheckDBC();}
		
}

//设置控件只读状态
function setReadOnly(objName,form){
    document.forms[form].elements[objName].setAttribute("readOnly","readonly");
    document.forms[form].elements[objName].setAttribute("class","readonly");
    return true;
}

function setReadOnly(me){
    me.setAttribute("readOnly","true");
    me.setAttribute("class","readonly");        /*FF*/
    me.setAttribute("className","readonly");    /*IE*/
    return true;
}

function setReadWrite(me){
    me.removeAttribute("readOnly");
    me.setAttribute("class","");
    me.setAttribute("className","");
    return true;
}

//选择标签改变样式风格
function changeLiStyle(obj){
    var obj1 = obj.parentNode.getElementsByTagName("li");
    for (var i=0; i<obj1.length; i++) {
        obj1[i].id="";
    }
    obj.id = "tabsl";
}


function selectAll(id,form){
	var obj = document.forms[form].elements;
	for (var i=0;i<obj.length;i++){
		if (obj[i].name == id){
			obj[i].checked = true;
		}
	}
}

function unselectAll(id,form){
	var obj = document.forms[form].elements;
	for (var i=0;i<obj.length;i++){
		if (obj[i].name == id){
			obj[i].checked = false;
		}
	}
}

function reverseselectAll(id,form){
	var obj = document.forms[form].elements;
	for (var i=0;i<obj.length;i++){
		if (obj[i].name == id){
			if (obj[i].checked==true) obj[i].checked = false;
			else obj[i].checked = true;
		}
	}
}

function followselectAll(id,form,flag){
	if(flag == true){
		selectAll(id,form);
	}else{
		unselectAll(id,form);
	}
}

//输入字符出错提示
 function KeyPressValidatorTip(tipmsg){
        var obj = document.activeElement;
        showInputValidatorTip(obj,tipmsg);
}

//输入字符出错提示
 function KeyPressValidatorLen(){
        var obj = document.activeElement;
        var maxvalue = obj.maxlength;

        if(maxvalue == undefined) {
            maxvalue = obj.maxlen;
         }
         
         //alert(obj.value.replace(/([^\x00-\xff])/g,'\x00$1').length);
        if(obj.value.replace(/([^\x00-\xff])/g,'\x00$1').length >= maxvalue)
        {
            showInputValidatorTip(obj,"超过限定长度!("+maxvalue+")");
            return false;
        }
        return true;
}

//输入字符长度检查函数
//obj: 文本域或textarea
 function InputValidator(obj){
        var showitstr = obj.value.replace(/([^\x00-\xff])/g,'\x00$1');      //每个汉字前补空字符占位,占成substring能理解的两个字符位.
        var maxvalue = obj.getAttribute("maxlength");
        var tip = obj.getAttribute("hlpmsg");    /*显示提示帮助信息*/

        var strShowTip = "";

        if(maxvalue == undefined) {
            maxvalue = obj.getAttribute("maxlen");
         }

        if(maxvalue == undefined || showitstr == undefined) {    /*所需的信息不足(值,最大长度),无法处理*/
            if (tip != undefined && tip !="")
                strShowTip = strShowTip + tip + "&nbsp;";
                
            strShowTip = '已经输入' +  showitstr.length + '个字符';
        }
        else {
            if(showitstr.length > maxvalue)
            {
                obj.value = showitstr.substring(0,maxvalue).replace(/\x00/g,'');    //按最长度截取后,将占位符去除.
            }
            showitstr = obj.value.replace(/([^\x00-\xff])/g,'\x00$1');      //每个汉字前补空字符占位,占成substring能理解的两个字符位.

            if (tip != undefined && tip !="")
                strShowTip = strShowTip + tip + "&nbsp;";

            strShowTip = strShowTip + '剩余' +  (maxvalue - showitstr.length) + '个字符';
        }

        if(obj.getAttribute("data-type") != null)      /*可以设置data_type属性值,以便于提示该数据为什么类型*/
            strShowTip = strShowTip + '(' + obj.getAttribute("data-type") +')';

        showInputValidatorTip(obj,strShowTip);
        return true;
 }

function showInputValidatorTip(obj,value){
        var mydiv = document.getElementById("InputValidatorTipDiv");
        if(mydiv == null )     /*所需的信息不足(值,最大长度),无法处理*/
            return;

        mydiv.innerHTML='<a style="border:1px solid blue;height:30px">&nbsp;'+value+'&nbsp;</a>';

        var dads  = mydiv.style;

        var ttop  = obj.offsetTop;     //TT控件的定位点高
        var thei  = obj.clientHeight;  //TT控件本身的高
        var tleft = obj.offsetLeft;    //TT控件的定位点宽
        var ttyp  = obj.type;          //TT控件的类型
        while (obj = obj.offsetParent){ttop+=obj.offsetTop; tleft+=obj.offsetLeft;}
        dads.top  = (ttyp=="image")? ttop : ttop + 40;
        dads.left = tleft;
        mydiv.style.visibility="visible";
}

function hidInputValidatorTip(TimeDelay){
        var mydiv = document.getElementById("InputValidatorTipDiv");
        if(mydiv == null)     /*所需的信息不足(值,最大长度),无法处理*/
            return;

        if(TimeDelay == undefined) { /*未指定停顿时间,默认为不等待*/
            mydiv.style.visibility="hidden";
        }else if(TimeDelay == 0){
            mydiv.style.visibility="hidden";
        }else{
            setTimeout("hidInputValidatorTip(0)", TimeDelay * 1000);
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
function selectMultiCheckboxValue(obj,fc){
	var vs = document.getElementsByName(obj.name);
	var str = "";
	var strV = ",";
	for(var a=0;a<vs.length;a++){
		if(vs[a].checked == true){
			strV += vs[a].value+",";
			str += vs[a].getAttribute("label")+",";
		}
	}
	document.getElementById(fc).value = strV;
}

function setMulitCheckboxValue(fc,fc_value,fm){	
	if(document.getElementById(fc)==null) return;
	
	var s = fc_value.split(",");
	for(var b=0;b<s.length;b++){
		if(s[b]!=null && s[b]!="")
			setCheckboxValue(fc,s[b],fm);
	}
}

//获取domain参数
function getDomain(namespace,select_id)
{
	var url = "/action/ccms/pub/pick/domain/getDomainByNamespace?namespace="+namespace+"&select_id="+select_id;
	return ajaxCall(url,	{method:"GET", 
						response:null, 
						progress:null, 
						form:null} 
						);
}

//获取下级参数 flag_show_all 表示如果上级值为空则下级全部显示 flag_show_all = true
function getChildDomain(parent_namespace,parent_domain_value,namespace,select_id,n_namespace,next_select_id,flag_show_all)
{
	var url = "/action/ccms/pub/pick/domain/getChildDomain?namespace="+namespace+"&parent_namespace="+parent_namespace+"&parent_domain_value="+parent_domain_value+"&select_id="+select_id;
	if(n_namespace!=undefined && n_namespace!=null && n_namespace != ""){
		url += "&next_namespace="+n_namespace+"&next_select_id="+next_select_id;
	}
	if(flag_show_all!=undefined && flag_show_all!=null && flag_show_all != ""){
		url += "&flag_show_all="+flag_show_all;
	}
	return ajaxCall(url,{method:"GET", 
						
						response:null, 
						progress:null, 
						form:null, 
						 
						
						});
}
//获取下级参数 flag_show_all ,下级默认为空，add-by-liuguofei
function getChildDomain2(parent_namespace,parent_domain_value,namespace,select_id,n_namespace,next_select_id,flag_show_all)
{
	var url = "/action/ccms/pub/pick/domain/getChildDomain2?namespace="+namespace+"&parent_namespace="+parent_namespace+"&parent_domain_value="+parent_domain_value+"&select_id="+select_id;
	if(n_namespace!=undefined && n_namespace!=null && n_namespace != ""){
		url += "&next_namespace="+n_namespace+"&next_select_id="+next_select_id;
	}
	if(flag_show_all!=undefined && flag_show_all!=null && flag_show_all != ""){
		url += "&flag_show_all="+flag_show_all;
	}
	return ajaxCall(url,	{method:"GET", 
						response:null, 
						progress:null, 
						dataType :"script",
						form:null} 
						);
}
//根据下级参数给父级和下级赋值并修改下级值列表
function getChildDomainByChild(parent_namespace,parent_select_id,namespace,select_id,domain_value)
{
	var url = "/action/ccms/pub/pick/domain/getChildDomainByChild?namespace="+namespace+"&parent_namespace="+parent_namespace+"&parent_select_id="+parent_select_id+"&domain_value="+domain_value+"&select_id="+select_id;
	return ajaxCall(url,	{method:"GET", 
						response:null, 
						progress:null, 
						dataType :"script",
						form:null}
						);
}
//根据下级选择上级
function getParentSelectDomain(parent_namespace,namespace,domain_value,select_id,p_namespace,p_select_id)
{
	var url = "/action/ccms/pub/pick/domain/getParentSelectDomain?namespace="+namespace+"&parent_namespace="+parent_namespace+"&domain_value="+domain_value+"&select_id="+select_id;
	if(p_namespace!=undefined && p_namespace!=null && p_namespace != ""){
		url += "&p_namespace="+p_namespace+"&p_select_id="+p_select_id;
	}
	return ajaxCall(url,{method:"GET", 
						response:null, 
						progress:null, 
						dataType :"script",
						form:null}
						);
}
//获取下级参数并赋值到文本框内
function getChildByTextDomain(namespace,parent_domain_value,text_id)
{
	var url = "/action/ccms/pub/pick/domain/getChildByTextDomain?namespace="+namespace+"&parent_domain_value="+parent_domain_value+"&text_id="+text_id;
	return ajaxCall(url,{method:"GET", 
						response:null, 
						progress:null, 
						dataType :"script",
						form:null}
						);
}
//根据大区选择出城市，再根据城市把省份和邮编显示出来
function getCityAndProvinceAndZipCode(area)
{
	var url = "/action/ccms/pub/pick/domain/getCityProvince?area="+area;
	return ajaxCall(url,{method:"GET", 
						response:null, 
						progress:null, 
						dataType :"script",
						form:null}
						);
}

//根据省份选择出城市，再根据城市把大区和邮编显示出来(如果是修改时调用此方法则是清除多余的城市)
function getCityAndAreaAndZipCode(province,city_old)
{
	var url = "/action/ccms/pub/pick/domain/getCityArea?province="+province;
	if(city_old!=undefined && city_old!=null && city_old != ""){
		url += "&city_old="+city_old;
	}
	return ajaxCall(url,{method:"GET", 
						response:null, 
						progress:null, 
						dataType :"script",
						form:null}
						);
}

//根据城市选择出省份大区，再根据城市把邮编显示出来
function getAreaAndProvinceAndZipCode(city)
{
	var url = "/action/ccms/pub/pick/domain/getProvinceArea?city="+city;
	return ajaxCall(url,	{method:"GET", 
						response:null, 
						progress:null, 
						form:null, 
						dataType :"script"}
						);
}

//页面表单修改时对有多级关联的数据删除非上级选项的子数据(上下级之间的值是 用 - 连接的情况)
function resetSelectOption(p_id,c_id)
{
	var p = document.getElementById(p_id);
	if(!p) return;
	var c = document.getElementById(c_id);
	if(!c) return;
	var pv = p.value;
	if(pv == "" || pv == "-1"){
		return;
	}
	var ca = c.options;
	for(var i=(ca.length-1);i>=0;i--){
		if(ca[i].value != "" && ca[i].value.indexOf(pv+"-") != 0){
			c.removeChild(ca[i]);
		}
	}
}

//全角转为半角
function DBC2SBC(e)
{
    var obj = e ? e.target : window.event.srcElement;
    if (typeof(obj)!="undefined")
    {   
        var objInputValue = obj.value;
        var formatedResult = '';

        //是否是全角输入
        var  isDBCinputType = false;
        for (i=0 ; i<objInputValue.length; i++)
        {
            singleCode = objInputValue.charCodeAt(i);//获取当前字符的unicode编码
            if (singleCode >= 65281 && singleCode <= 65373)//在这个unicode编码范围中的是所有的英文字母已经各种字符
            {
                formatedResult += String.fromCharCode(objInputValue.charCodeAt(i) - 65248);//把全角字符的unicode编码转换为对应半角字符的unicode码
                isDBCinputType = true;
            }else if (singleCode == 12288)//空格
            {
                formatedResult += String.fromCharCode(objInputValue.charCodeAt(i) - 12288 + 32);
                isDBCinputType = true;
            }else
            {
                formatedResult += objInputValue.charAt(i);
            }
        }
        if (isDBCinputType)
        {
            obj.value = formatedResult;
        }
     }
}
//页面所有元素全角校验
function allElementsCheckDBC(){
	/*
    var inputObject = document.getElementsByTagName("input");
    for(var i=0;i<inputObject.length;i++){
        if ((inputObject[i].type == "text"))
        {
            inputObject[i].onkeyup = DBC2SBC;
        }
    }

    var textareaObject = document.getElementsByTagName("textarea");
    for(var i=0;i<textareaObject.length;i++){
        textareaObject[i].onkeyup = DBC2SBC;
    }
	*/
}

//给表单元素添加事件
function addEvent(el,eventType,fn){
    if(el.addEventListener){
        el.addEventListener(eventType,fn,false);
    }else if(el.attachEvent){
        el.attachEvent("on" + eventType,fn);
    }else{
        el["on"+eventType]=fn;
    }
}


//控制iframe 高度，随内容自动改变
function getDocHeight(doc){ 
	//在IE中doc.body.scrollHeight的可信度最高 
	//在Firefox中，doc.height就可以了 
	var docHei = 0; 
	var scrollHei;//scrollHeight 
	var offsetHei;//offsetHeight，包含了边框的高度 

	if (doc.height){ 
		//Firefox支持此属性，IE不支持 
		docHei = doc.height; 
	} 
	else if (doc.body){ 
		//在IE中，只有body.scrollHeight是与当前页面的高度一致的， 
		//其他的跳转几次后就会变的混乱，不知道是依照什么取的值！ 
		//似乎跟包含它的窗口的大小变化有关 
		if(doc.body.offsetHeight) docHei = offsetHei = doc.body.offsetHeight; 
		if(doc.body.scrollHeight) docHei = scrollHei = doc.body.scrollHeight; 
	} 
	else if(doc.documentElement){ 
		if(doc.documentElement.offsetHeight) docHei = offsetHei = doc.documentElement.offsetHeight; 
		if(doc.documentElement.scrollHeight) docHei = scrollHei = doc.documentElement.scrollHeight; 
	} 
	/* 
	docHei = Math.max(scrollHei,offsetHei);//取最大的值，某些情况下可能与实际页面高度不符！ 
	*/ 
	return docHei; 
} 
//控制iframe 宽度，随内容自动改变
function getDocWidth(doc){ 
	var docHei = 0; 
	var scrollHei;
	var offsetHei;

	if (doc.width){ 
		//Firefox支持此属性，IE不支持 
		docHei = doc.width; 
	} 
	else if (doc.body){ 
		//在IE中，只有body.scrollHeight是与当前页面的高度一致的， 
		//其他的跳转几次后就会变的混乱，不知道是依照什么取的值！ 
		//似乎跟包含它的窗口的大小变化有关 
		if(doc.body.offsetWidth) docHei = offsetHei = doc.body.offsetWidth; 
		if(doc.body.scrollWidth) docHei = scrollHei = doc.body.scrollWidth; 
	} 
	else if(doc.documentElement){ 
		if(doc.documentElement.offsetWidth) docHei = offsetHei = doc.documentElement.offsetWidth; 
		if(doc.documentElement.scrollWidth) docHei = scrollHei = doc.documentElement.scrollWidth; 
	} 
	/* 
	docHei = Math.max(scrollHei,offsetHei);//取最大的值，某些情况下可能与实际页面高度不符！ 
	*/ 
	return docHei; 
} 
function doReSize(iframe){ 
	var iframeWin = window.frames[iframe]; 
	var iframeEl = window.document.getElementById? window.document.getElementById(iframe): document.all? document.all[iframe]: null; 
	if ( iframeEl && iframeWin ){ 
		var docHei = getDocHeight(iframeWin.document); 
		if (docHei != iframeEl.style.height) iframeEl.style.height = docHei + 'px'; 
		var docWid = getDocWidth(iframeWin.document); 
		if (docWid != iframeEl.style.width) iframeEl.style.width = docWid + 'px'; 
	} 
	else if(iframeEl){ 
		var docHei = getDocHeight(iframeEl.contentDocument); 
		if (docHei != iframeEl.style.height) iframeEl.style.height = docHei + 'px'; 
		var docWid = getDocWidth(iframeEl.contentDocument); 
		if (docWid != iframeEl.style.width) iframeEl.style.width = docWid + 'px'; 
	} 
}

//http://www.jb51.net/article/25985.htm
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
}
