/**
 * 调用方式：html2canvasInit({container: document.body, objid: "contentDiv", removeOld: true});
 */
function html2canvasInit(opts){
	var isremoveOld = true;
	if( "undefined" != typeof(opts) && "undefined" != typeof(opts.removeOld) && false == opts.removeOld ){
		isremoveOld = false;
	}
	var container = opts.container;
	if( !("undefined" != typeof(opts) && "object" == typeof(opts.container)) ){
		alert("The options of container is null.");
		return false;
	}
	var objid = opts.objid;
	if( !("undefined" != typeof(opts) && "undefined" != typeof(opts.container) && "" != opts.container ) ){
		alert("The options of objid is null.");
		return false;
	}
	html2canvas(container).then(function(canvas) {
		container.appendChild(canvas);

		var can = document.getElementsByTagName("canvas");
		if( can.length > 0 ){
			can[0].style.display = "none";
            var image = new Image();
            image.width = $("#"+objid).width();
            image.height = $("#"+objid).height();
        	image.src = can[0].toDataURL("image/png");
        	container.appendChild(image);
        	if( isremoveOld ){
				$("#"+objid).remove();
			}
		}else{
			alert("生成图片失败");
		}
	});
	
	
}
