var showNotFound = function (){
	var s = '没有搜索到与 " '+$("#show_name").val()+ '" 相关的数据.';
	document.getElementById('show_search').innerHTML=s;
};
var sugges = '<rows>${fld:content}</rows>';
    sugges = sugges.length==0?sugges:sugges.split(',');
if(sugges.length>0){
	var su = '&nbsp;&nbsp;您要找的是不是：';
	for ( var int = 0 ; int < sugges.length; int++) {
		su += '<U class="hcursor" onclick="faqObj.searchFAQ(\''+sugges[int]+'\')">'+sugges[int]+'</U>&nbsp;';
		if(int>1)break;
	}	
	document.getElementById('showSugges').style.display = ''; 
	document.getElementById('showSugges').innerHTML = su;
	var notfound = document.getElementById('showSugges_notfound');
	if(notfound!=null && notfound!="undefined"){
		notfound.style.display = '';
		notfound.innerHTML = su;	
		showNotFound();
	}    	
}else{
	document.getElementById('showSugges').style.display = 'none';
	var notfound = document.getElementById('showSugges_notfound');
	if(notfound!=null && notfound!="undefined"){
		notfound.style.display = 'none';
		showNotFound();
	}
}


