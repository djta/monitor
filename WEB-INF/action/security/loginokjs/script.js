if('${req:dinamica.security.uri}' !=""){
	var uri;
	if('${req:dinamica.security.uri}'.indexOf("&ajaxcall=true")>0){
		uri = '${req:dinamica.security.uri}'.substring(0,'${req:dinamica.security.uri}'.indexOf("&ajaxcall=true"));
	}else if('${req:dinamica.security.uri}'.indexOf("?ajaxcall=true")>0){
		uri = '${req:dinamica.security.uri}'.substring(0,'${req:dinamica.security.uri}'.indexOf("?ajaxcall=true"));
	}else{
		uri = '${req:dinamica.security.uri}';
	}

	var method = '${req:dinamica.security.uri}'.indexOf('"method":"post"')>0?"post":"get";
	var datatype = '${req:dinamica.security.uri}'.indexOf('"datatype":"json"')>0?"json":('${req:dinamica.security.uri}'.indexOf('"datatype":"html"')>0?"html":"script");
	
	if (uri.indexOf(contextPath) != 0) {
		uri = '${def:context}' + uri;
	}

	if(method=="post"){	//ajax方式提交
		ajaxCall(uri,{
			method: method,
			dataType:datatype
		});
	}else{
		var hash = window.location.hash;
		/**if(hash == "" || unescape($Base64.decode(hash)).indexOf('${req:dinamica.security.uri}') >=0 || unescape($Base64.decode(hash)).indexOf("/action/ccms/redirect") >=0 || unescape($Base64.decode(hash)).indexOf("/action/security/exit") >=0){
			window.location.href = uri;
		}else{
			window.location.href = uri + hash;
		}*/
		if(hash != ""){
			uri = '${def:context}' + $Base64.decode(hash.substr(1));
		}
		window.location = uri;
	}
}else{
	window.location.reload();
}
