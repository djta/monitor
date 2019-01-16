function reportJS(){
	var thisObj = this;
	this.setCheckBoxValues = function(name,hiddenId){
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
	};
	//控制iframe 高度，随内容自动改变
	this.getDocHeight = function(doc){
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
	};
	//控制iframe 宽度，随内容自动改变
	this.getDocWidth = function(doc){
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
	};
	this.doReSize = function(iframe){
		var iframeWin = window.frames[iframe]; 
		var iframeEl = window.document.getElementById? window.document.getElementById(iframe): document.all? document.all[iframe]: null; 
		if ( iframeEl && iframeWin ){
			var docHei = thisObj.getDocHeight(iframeWin.document); 
			if (docHei != iframeEl.style.height) iframeEl.style.height = docHei + 'px'; 
			var docWid = thisObj.getDocWidth(iframeWin.document); 
			if (docWid != iframeEl.style.width) iframeEl.style.width = docWid + 'px'; 
		}
		else if(iframeEl){
			var docHei = thisObj.getDocHeight(iframeEl.contentDocument); 
			if (docHei != iframeEl.style.height) iframeEl.style.height = docHei + 'px'; 
			var docWid = thisObj.getDocWidth(iframeEl.contentDocument); 
			if (docWid != iframeEl.style.width) iframeEl.style.width = docWid + 'px'; 
		}
	};
	
	this.dragTrByOrder = function (table_id){
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
	};
	return thisObj;
}
