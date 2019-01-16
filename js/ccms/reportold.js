/*
	id		显示值
	idCtl	隐藏值
	url		地址
	width	显示宽度
	height	显示高度
	form	表单

*/
function pickListForReport(id, idCtl, url, width, height, form, field_id, namespace, checkname, parentNamespace, parent_value){
	
	if (pickControl!=null)
			pickClose();

	//verifica si existe el textbox
	var obj = document.getElementById(id);
	if (obj==null) {
		alert("ERROR (pickOpen): no existe un elemento con ID: " + id);
		return;			
	}

	if(namespace==null || namespace==""){
		alert("ERROR (pickOpen): namespace is requried ");
		return;
	}
			
	pickControl = id;
	idControl = idCtl;

	pickForm = form;

	for(var i=10;i<pickListForReport.arguments.length;i++){ /*可以多带控件参数,以便于在选择窗口中同时取值,并传回来*/
	    pickOtherControl[i-10] = pickListForReport.arguments[i];
	}
	
	url += "?field_id="+field_id+"&namespace="+namespace+"&checkname="+checkname+"&showid="+idCtl+"&parent_namespace="+parentNamespace+"&parent_value="+parent_value;

	showBox(pickControl, width, height, url, "true");//弹出窗口并在当前窗口居中显示

}

function pickClearForReport(id){
	var obj = document.getElementById(id);
	if(obj){
		if(obj.innerHTML){
			obj.innerHTML = "";
		}
	}
}

function pickSelectForReport(name, spanid, label, val){
	var checkObjs = document.getElementsByName(name);
	if(checkObjs){
		var flag = false;
		for(var i=0;i<checkObjs.length;i++){
			if(checkObjs[i].value == val){
				flag = true;
				break;
			}
		}
		if(flag == false){
			var spanObj = document.getElementById(spanid);
			if(spanObj){
				spanObj.innerHTML = spanObj.innerHTML + "<br/><input type='checkbox' checked name='"+name+"' value='"+val+"'>"+label;
			}
		}
	}
}

function setCheckBoxValues(name,hiddenId){
	
	var checkObjs = document.getElementsByName(name);
	if(checkObjs){
		var val = "";
		for(var i=0;i<checkObjs.length;i++){
			if(checkObjs[i].checked == true){
				var v = checkObjs[i].value;
				//将单引号替换为两个单引号，即数据库中的转义
				v = v.replace(/\'/ig,"''");
				val += "'"+v+"',";
			}
		}
		if(val != ""){
			val = val.substring(0,val.length-1);
		}
		var hidObj = document.getElementById(hiddenId);
		if(hidObj){
			hidObj.value = val;
		}
	}
}

function dragTrByOrder(table_id){
	//绑定事件   
       var addEvent = document.addEventListener ? function(el,type,callback){   
         el.addEventListener( type, callback, !1 );   
       } : function(el,type,callback){   
         el.attachEvent( "on" + type, callback );   
       };   
       //移除事件   
       var removeEvent = document.removeEventListener ? function(el,type,callback){   
         el.removeEventListener( type, callback );   
       } : function(el,type,callback){   
         el.detachEvent( "on" + type, callback);   
       };   
       //精确获取样式   
       var getStyle = document.defaultView ? function(el,style){   
         return document.defaultView.getComputedStyle(el, null).getPropertyValue(style);   
       } : function(el,style){   
         style = style.replace(/\-(\w)/g, function($, $1){   
           return $1.toUpperCase();   
         });   
         return el.currentStyle[style];   
       };   
       var dragManager = {   
         clientY:0,   
         draging:function(e){//mousemove时拖动行   
           var dragObj = dragManager.dragObj;   
           if(dragObj){   
             e = e || event;   
             if(window.getSelection){//w3c   
               window.getSelection().removeAllRanges();   
             }else  if(document.selection){   
               document.selection.empty();//IE   
             }   
             var y = e.clientY;   
             var down = y > dragManager.clientY;//是否向下移动   
             var tr = document.elementFromPoint(e.clientX,e.clientY);   
             if(tr && tr.nodeName == "TD"){   
               tr = tr.parentNode;   
               dragManager.clientY = y;   
               if( dragObj !== tr){   
                 tr.parentNode.insertBefore(dragObj, (down ? tr.nextSibling : tr));   
               }   
             };   
           }   
         },   
         dragStart:function(e){   
           e = e || event;   
           var target = e.target || e.srcElement;   
           if(target.nodeName === "TD"){   
             target = target.parentNode;   
             dragManager.dragObj = target;   
             if(!target.getAttribute("data-background")){   
               var background = getStyle(target,"background-color");   
               target.setAttribute("data-background",background);   
             }   
             //显示为可移动的状态   
             target.style.backgroundColor = "#ccc";   
             target.style.cursor = "move";   
             dragManager.clientY = e.clientY;   
             addEvent(document,"mousemove",dragManager.draging);   
             addEvent(document,"mouseup",dragManager.dragEnd);   
           }   
         },   
         dragEnd:function(){   
           var dragObj = dragManager.dragObj;   
           if (dragObj) {   
               dragObj.style.backgroundColor = dragObj.getAttribute("data-background");   
               dragObj.style.cursor = "default";   
               dragManager.dragObj = null;   
               removeEvent(document,"mousemove",dragManager.draging);   
               removeEvent(document,"mouseup",dragManager.dragEnd);   
			   //给行重新排序
			   if(typeof(changeOrder)=="function"){
				changeOrder();
			   }
           }   
         },   
         main:function(el){   
           addEvent(el,"mousedown",dragManager.dragStart);   
         }   
       };   
       var el = document.getElementById(table_id);
	   if(el){
		dragManager.main(el);
	   }
}

/*
parent_namespace 父命名空间
parent_domain_value 父值
namespace 要级联显示的下拉框字段命名空间
select_id 要级联显示的下拉框ID
show_value_type 从domain中取的值为哪个字段  0：domain_value 1:domain_text 2:domain_text_en
show_type 下拉框的显示情况 0：domain_value 1:domain_text 2:domain_text_en
*/
function getChildDomainReport(parent_namespace,parent_domain_value,namespace,select_id,show_value_type,show_type)
{
	var url = "/action/subject/report/reportgen/pick/getChildDomain?namespace="+namespace+"&parent_namespace="+parent_namespace+"&parent_domain_value="+parent_domain_value+"&select_id="+select_id+"&show_value_type="+show_value_type+"&show_type="+show_type;
	return ajaxCall(	httpMethod="GET", 
						uri=url, 
						divResponse=null, 
						divProgress=null, 
						formName=null, 
						afterResponseFn=null, 
						onErrorFn=null,
						ignoreShowDivStatus="true");
}

/*
parent_field_code 对应外键表字段
parent_value 父值
table_name 要级联显示的下拉框对应表名
value_field_code 从外键表中取的值为哪个字段
show_field_code 从外键表中取的显示值为哪个字段
select_id 要级联显示的下拉框ID
*/
function getChildFKReport(parent_field_code,parent_value,table_name,value_field_code,show_field_code,select_id)
{
	var url = "/action/subject/report/reportgen/pick/getChildFK?table_name="+table_name+"&value_field_code="+value_field_code+"&show_field_code="+show_field_code+"&parent_field_code="+parent_field_code+"&parent_value="+parent_value+"&select_id="+select_id;
	return ajaxCall(	httpMethod="GET", 
						uri=url, 
						divResponse=null, 
						divProgress=null, 
						formName=null, 
						afterResponseFn=null, 
						onErrorFn=null,
						ignoreShowDivStatus="true");
}

/* add by zl for cognos*/
var $currYear = (new Date()).getFullYear();
function realOffset(o){
	var x = y = 0; do{
	x += o.offsetLeft || 0; 
	y += o.offsetTop  || 0;
	o  = o.offsetParent;}while(o);
	return {"x" : x, "y" : y};
}

function cognosMonthShow(obj)
{
	var div = document.getElementById(obj.name + "_Area");
	div.style.display="";
	var xy = realOffset(obj);
	div.style.top = xy.y+obj.offsetHeight;
	div.style.left= xy.x;
	div.style.position = "absolute";
	div.style.border = "1px solid black";
	div.style.backgroundColor = "buttonface";
	cognosNewYear(0,obj.name);
}
function cognosNewYear(n,name)
{
	$currYear +=n;
	var str="";
	for(var i=1;i<13;i++)
	{
		str +=  "<span onclick='document.getElementById(\"" + name + "\").value=this.innerHTML;document.getElementById(\""+name+"_Area\").style.display=\"none\"' style='color:blue'>"+$currYear;
		if(i<10)
			str += "0"+i+"</span>";
		else
			str += i+"</span>";
		if(i%6==0)
			str+="<br>";
		else
			str+=",";
	}
	str +=  "<div style='text-overflow: ellipsis;white-space: nowrap;overflow: hidden;'><span onclick='cognosNewYear(-1,\"" + name + "\")' style='color:blue'>上一年</span>  <span onclick='cognosNewYear(1,\"" + name + "\")' style='color:blue'>下一年</span></div>";
	document.getElementById(name + "_Area").innerHTML=str;
}

var consts = function(){
    var prefix = "__v__";
    return {
        shade: prefix + "shade",
        box: prefix + "box",
        content: prefix + "content",
        index   : 10000
    };
}();

function findDimensions() {
    var winWidth = 0, winHeight = 0;
    //获取窗口宽度 
    if (window.innerWidth)
        winWidth = window.innerWidth;
    else if ((document.body) && (document.body.clientWidth))
        winWidth = document.body.clientWidth;
    //获取窗口高度 
    if (window.innerHeight)
        winHeight = window.innerHeight;
    else if ((document.body) && (document.body.clientHeight))
        winHeight = document.body.clientHeight;
    //通过深入Document内部对body进行检测，获取窗口大小 
    if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth) {
        winHeight = document.documentElement.clientHeight;
        winWidth = document.documentElement.clientWidth;
    }
    return {
        width: winWidth,
        height: winHeight
    };
}
function getRootPath(){
	var  webroot=document.location.href;
    webroot=webroot.substring(webroot.indexOf('//')+2,webroot.length);
    webroot=webroot.substring(webroot.indexOf('/')+1,webroot.length);
    webroot=webroot.substring(0,webroot.indexOf('/'));
    var rootpath="/"+webroot;
    return rootpath;
}
/**

*/
function showMultiBox(parentName, currId, title, width, height,cont,refName,userSql,isId,brandId) {
	
    var refTemp = "";
    if(isId){ // 取ID的属性
    	refTemp = '\'' + window.encodeURI(window.encodeURI(document.getElementById(parentName).value)) + '\'';
    } else {
    	var valsEl = document.getElementsByName(parentName) ;
        var vals = [];
        for (var i = 0 ; i < valsEl.length ; i++) {
            if (valsEl[i].checked) {
                vals.push("'" + valsEl[i].value + "'");
            }
        }
        refTemp = vals.join(",");
    }
    
    var path = getRootPath() + "/action/portal/cognosReport/getMultiDomain?ref_name=" + refName + "&ref_value=" + refTemp + "&field_id=" + currId;
    path += "&user_sql=" + (userSql ? userSql : "");
    path += "&brand_id=" + brandId;
    path += "&content=" + cont + "&__doc=" + new Date().getTime();
    crateMultiBox(cont,path,title,width,height);
}
function showBentleyCarModel(content){
	var url = getRootPath() + "/action/portal/cognosReport/getBentleyCarModel?content=" + content;
	crateMultiBox(document.getElementById("p_car_model_filter"),url,"",400,280);
	var tdEl = document.getElementById(content + "_filter_span").parentNode;
	var impEL = tdEl.getElementsByTagName("img")[0];
	changePosion(impEL,document.getElementById(consts.box));
}

// 弹出框的默认大小
var popupSize = {
	width	: 400,
	height	: 320
};

function crateMultiBox(id,url,title,width,height){
	popupSize.width = width;
	popupSize.height = height;
	var pos = findDimensions(),shade = document.getElementById(consts.shade);
    if (!shade) { 
        shade = document.createElement("div");
        shade.setAttribute("class", "overlay");
        shade.setAttribute("id", consts.shade);
        shade.style.display = "none";
        shade.style.zIndex = ++consts.index;
        document.body.appendChild(shade);
    }
    title = title.length == 0 ? "" : title;
    var box = document.getElementById(consts.box);
    if (!box) {
    	var offset = 3;
    	if (isIE())
    		offset = -13;
        box = document.createElement("div");
        box.setAttribute("id", consts.box);
        var boxStyle = box.style;
        boxStyle.position = "absolute";
        boxStyle.width = width ;
        boxStyle.height = height + 20 ;
        boxStyle.border = "#9d9d9d 1px solid";
        boxStyle.backgroundColor = "#F7F7F7";
        boxStyle.padding = "0px";
        var isFF = navigator.userAgent.toLowerCase().indexOf("firefox")!=-1;
        boxStyle.top = isFF ? (pos.height + offset) /2 : (pos.height - height + offset) / 2 ;
        boxStyle.left = (pos.width - width ) / 2;
        boxStyle.zIndex = ++consts.index;
        boxStyle.display = "none";
        document.body.appendChild(box);
    }

    var contents = box.getElementsByTagName(consts.content),content;
    if (contents.length == 0) {
        var els = ["<table class=\"pickListHeader\" style=\"width:100%;height:20px\" cellspacing=\"0\">"
	               + "<tbody><tr>"
		           + "<td style='text-align:left'>"
                   + " <img class=\"tool\" src=\"" + getRootPath() + "/images/clear.gif\" title=\"Close\" onclick=\"closeMultiBox()\" id=\"closeImg\">&nbsp;please select"
                   //+ "select parameters"
                   + "</td>"
                   + "</tr>"
                   + "</tbody></table><iframe id=\""];
        //var els = ["<div style=\"height:20px;border:#ffff 1px solid\"><span style='float:left'>" + title + "</span><span style='float:right'><a href='javascript:void(0)' onclick='closeMultiBox()'>关闭</a></span></div><iframe id=\""];
        els.push(consts.content);
        els.push("\" name=\"");
        els.push(consts.content);
        els.push("\" frameBorder=\"0\" style=\"width:");
        els.push(width);
        els.push("px;");
        els.push("height:");
        els.push(height);
        els.push("px;margin:0px;\"></iframe>");
        box.innerHTML = els.join("");
        content = document.getElementById(consts.content);
        content.src = url;
    }
    box.style.display = "block";
    shade.style.display = "block";
    if(id){
    	var tdEl = document.getElementById(id + "_filter_span").parentNode;
    	var impEL = tdEl.getElementsByTagName("img")[0];
    	changePosion(impEL,document.getElementById(consts.box));
    }
    
}

function closeMultiBox() {
    document.getElementById(consts.shade).style.display = "none";
    document.getElementById(consts.content).style.display = "none";
    document.getElementById(consts.box).style.display = "none";
}

function multiCallBack(content,valObj) {
	var els = document.getElementsByName(content + "_filter_check"),hasVal = false;
	for(var i = 0 ; i < els.length ; i ++){
		if(els[i].value == valObj.value){
			hasVal = true;
			break;
		}
	}
	if(!hasVal){
		var eles = ["<br />","<input type='checkbox' name='"];
		eles.push(content + "_filter_check' ");
		eles.push("value='" + valObj.value + "'");
		eles.push(" checked/>");
		eles.push(valObj.name);
		var cont = document.getElementById(content + "_filter_span");
		var htmls = cont.innerHTML;
		cont.innerHTML = htmls + eles.join("");
		/*var check = document.createElement("input");
		check.setAttribute("type", "checkbox");
		check.setAttribute("name", content + "_filter_check");
		check.setAttribute("checked",1);
		check.setAttribute("value",valObj.value);
		cont.appendChild(document.createElement("br"));
		cont.appendChild(check);
		cont.appendChild(document.createTextNode(valObj.name));*/
	}
}

function setCognosCheckValues(){
	var args = arguments;
	var suffixFilter = "_filter",checkFilter = "_filter_check",name,checkEls,vals;
	
	for(var i = 0 ; i < args.length ; i ++){
		
		name = args[i],vals = [];
		checkEls = document.getElementsByName(name + checkFilter);
		for(var j = 0 ;j < checkEls.length ; j ++){
			if(checkEls[j].checked){
				vals.push(checkEls[j].value);
			}
		}
		document.getElementById(name + suffixFilter).value = vals.join(",");
	}
}

function selectComplaintSub(fieldName,parentName,parentVal,node){
	var queryStr = ["/action/portal/cognosReport/getComplaintSubtype?"];
	queryStr.push("field_name=" + fieldName);
	queryStr.push("&parent_field_name=" + parentName);
	queryStr.push("&parent_field_value=" + window.encodeURI(window.encodeURI(parentVal)));
	queryStr.push("&curr_note=" + node);
	ajaxCall("GET",queryStr.join(""));
}

function checkIsCognos(){
	var cognosForm = document.forms["formFilter"];
	if(cognosForm){
		var reportFile = cognosForm.file.value;
		var paths = reportFile.split("/");
		if(paths[1] == "content")return true;
	}
	return false;
}

/**
 * 
 * @param source 触发弹出框的节点
 * @param popup  弹出框节点
 */
function changePosion(source,popup){
	var postion ,bodySize = getViewSize();
	if(source.getBoundingClientRect){
		postion = source.getBoundingClientRect();
	} else {
		postion  = getPostion(source);
	}
	if((postion.left + popupSize.width) > bodySize.width || postion.left + popupSize.width > 1000){ //右边的显示在左边
		popup.style.left = postion.left - popupSize.width - (isIE() ? 10 : 5); // 弹出框右边的位置在source的左边
	} else {
		popup.style.left = postion.left + 300;
	}
	
}

function getPostion(el){
	var actualLeft = el.offsetLeft,actualTop = element.offsetTop;
	var current = element.offsetParent;
	while (current !== null && current.tagName.toLowerCase() != 'body'){
		actualLeft += current.offsetLeft;
		actualTop += current.offsetTop;
		current = current.offsetParent;
	}
	return {
		x : actualLeft,
		y : actualTop
	};
}
function getViewSize(){
	if (document.compatMode == "BackCompat"){
		return {
			width: document.body.clientWidth,
			height: document.body.clientHeight
		};
	} else {
		return {
			width: document.documentElement.clientWidth,
			height: document.documentElement.clientHeight
		};
	}
	return {
		width	: 0,
		heigth	: 0
	};
}

function isZunxianghui(){
	var carModels = document.getElementsByName("p_car_model_filter_check"),ids = [];
	for(var i = 0 ,j = carModels.length ; i < j ; i ++){
		if(carModels[i].checked){
			ids.push("|" + carModels[i].value + "|");
		}
	}
	if(ids.length > 0){
		if(ids.indexOf("|21|") > -1){
			document.getElementsByName("p_is_zunxianghui_filter")[0].value = 1;
		} else {
			document.getElementsByName("p_is_zunxianghui_filter")[0].value = 0;
		}
	} else {
		document.getElementsByName("p_is_zunxianghui_filter")[0].value = 0;
	}
}

function isCarModel(){
	var carModels = document.getElementsByName("p_car_model_filter_check"),ids = [];
	for(var i = 0 ,j = carModels.length ; i < j ; i ++){
		if(carModels[i].checked){
			ids.push(carModels[i].value);
		}
	}
	if(ids.length > 0){
		document.getElementsByName("p_is_car_model_filter")[0].value = 1;
	} else {
		document.getElementsByName("p_is_car_model_filter")[0].value = 0;
	}
}

/**
 * 重写Array 的indexOf方法，兼容不同浏览器
 * @param obj
 * @returns {Number}
 */
Array.prototype.indexOf = function(obj){
	
	for(var i = 0 , j = this.length ; i < j ; i ++){
		if(this[i] == obj) return i;
	}
	return -1;
};

function setDefaultBeginDate(){
	var dt = new Date();
	var year = dt.getFullYear();
	document.getElementById("p_begin_date_filter").value = year + "-1-1";
}

function setAllRegion(){
	var region = document.getElementById("p_region_filter");
	if(region){
		region.value = "ALL";
	}
}

function setCognosSelectValue(fieldName,elId,refName,refVal,custSql,brandId){
	var field;
	if(document.getElementById(fieldName)){
		field = document.getElementById(fieldName).value;
	} else{
		var suffix = "_filed_hidden_value";
		field = document.getElementById(fieldName + suffix).value;
	}
	refVal = "'" + refVal + "'";
	var url = ["/action/portal/cognosReport/subdomain?field_id=",field,"&content=",elId,"&ref_name=",refName,"&ref_value=",refVal,"&user_sql=",custSql,"&brand_id=",brandId];
	ajaxCall("GET",url.join(""));
}

function compareDate(){
	
	var beginDate = document.getElementById("p_begin_date"),endDate = document.getElementById("p_end_date");
	if(beginDate && beginDate.value.length > 0 && endDate && endDate.value.length > 0){
		
	} else if(true){
		
	}
}

function submitRawdataForm(){
	var form = document.forms['formFilter'],cognosIp = form.cognos_ip.value,reportName = form.file.value;
	var rawdataForm = document.getElementById("rawdataForm");
	if(!rawdataForm){
		var params = {
			"ui.action" : "edit",
			"b_action" : "xts.run",
			"m" : "portal/launch.xts",
			"ui.tool": "QueryStudio",
			"ui.object" : reportName,
			"ui.header"	: "false"/*,
			"launch.openJSStudioInFrame":"true",
			"ui.gateway":'http://122.200.84.150/ibmcognos/cgi-bin/cognos.cgi'*/
		};
		rawdataForm = document.createElement("form");
		rawdataForm.setAttribute("name", "rawdataForm");
		rawdataForm.setAttribute("id", "rawdataForm");
		rawdataForm.setAttribute("method", "POST");
		rawdataForm.setAttribute("target", "reportspace");
		rawdataForm.setAttribute("action", cognosIp);
		var fields = createFormHiddenFields(params);
		for ( var int = 0; int < fields.length; int++) {
			rawdataForm.appendChild(fields[int]);
		}
		document.body.appendChild(rawdataForm);
	} else {
		rawdataForm.setAttribute("action", cognosIp);
		document.getElementById("ui.object").value = reportName;
	}
	setTimeout(function(){
		rawdataForm.submit();
	}, 50);
	
}
function createFormHiddenFields(fields){
	var fieldEls = [],el;
	for(var source in fields){
		el = document.createElement("input");
		el.setAttribute("type", "hidden");
		el.setAttribute("name", source);
		el.setAttribute("id", source);
		el.setAttribute("value", fields[source]);
		fieldEls.push(el);
	}
	return fieldEls;
}
