
//屏蔽鼠标右键、Ctrl+N、Shift+F10、F5刷新、退格键

var check = function(event){
	event=event||window.event;
	//屏蔽 F5 刷新键  Ctrl + R
	if(event.keyCode==116 || (event.ctrlKey && event.keyCode==82)){
		event.keyCode = 0;
		event.returnValue=false;
	}

	//屏蔽 Alt+ 方向键 ←  //屏蔽 Alt+ 方向键 →
	if ((event.altKey)&&	((event.keyCode==37) || (event.keyCode==39))){ 
		event.returnValue=false;
	} 
	
	//屏蔽退格键
	if ((event.keyCode == 8) && (event.srcElement.type != "text" && event.srcElement.type != "textarea" && event.srcElement.type != "password")){
		event.keyCode=0;
		event.returnValue=false;
	}

	if ((event.ctrlKey)&&(event.keyCode==78)){ //屏蔽 Ctrl+n
		event.returnValue=false;
	}

	if ((event.shiftKey)&&(event.keyCode==121)){ //屏蔽 shift+F10
		event.returnValue=false;
	}

	if (event.srcElement.tagName == "A" && event.shiftKey) {
		event.returnValue = false; //屏蔽 shift 加鼠标左键新开一网页
	}

	if ((event.altKey)&&(event.keyCode==115)){ //屏蔽Alt+F4
		window.showModelessDialog("about:blank","","dialogWidth:1px;dialogheight:1px");
		return false;
	}
}

var checkFirefox = function(e){
	e=e||window.event;
	if((e.which||e.keyCode)==116 || (e.ctrlKey && (e.which||e.keyCode)==82)){
		if(e.preventDefault){
			e.preventDefault();
		}
		else{
			event.keyCode = 0;
			e.returnValue=false;
		}
	}
}
if(document.addEventListener){
  document.addEventListener("keydown",checkFirefox,false);
}
else{
  document.attachEvent("onkeydown",check);
}