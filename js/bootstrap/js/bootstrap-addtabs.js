/**
 * Website: http://git.oschina.net/hbbcs/bootStrap-addTabs
 * 
 * Created by joe on 2015-12-19.
 */

/**
 * 
 * @param {type} options {
 * content string||html 直接指定内容
 * close bool 是否可以关闭
 * monitor 监视的区域
 * }
 * 
 * @returns
 */
$.fn.addtabs = function (options) {
	var is_do_closing = false;	//是否是关闭tab页
    var obj = window.parent==window?$(this):$(window.top.document);
    options = $.extend({
        content: '', //直接指定所有页面TABS内容
        close: true, //是否可以关闭
        monitor: 'body', //监视的区域
        iframeUse: true, //使用iframe还是ajax
        iframeHeight: $(document).height() - 105, //固定TAB中IFRAME高度,根据需要自己修改
        callback: function () { //关闭后回调函数
        },
        dbclick: false
    }, options || {});

	$(this).on('click','[role=presentation]',function(){
		if(!is_do_closing){
			obj.find('.speech-bubble-bottom').removeClass('speech-bubble-bottom').removeClass('speech-bubble');
		}
        $(this).addClass('speech-bubble-bottom').addClass('speech-bubble');
		is_do_closing = false;
	});
	
	var clickType = "click";
	if( options.dbclick == true ){
		clickType = "dblclick";
	}
    $(options.monitor).on(clickType, '[addtabs]', function () {
    	if($("#tab_content",obj).length > 0){
    		
    		var url = "";
    		if($(this).attr('url').startsWith("/")){
    			url = unescape($(this).attr('url'));
    		}else{
    			url = unescape($Base64.decode($(this).attr('url')));
    		}
			//判断是否为project_id为结尾，以项目视角看各模块数据
			if(url.endWith("project_id=") && top.$("#project_id")){
				var project_id = top.$("#project_id").val();
				if(project_id != undefined && project_id != ""){
					url = url +  project_id;
				}
			}
    			
    		_add_tab({
	            id: $(this).attr('addtabs'),
	            title: $(this).attr('title') ? $(this).attr('title') : $(this).html(),
   	            content: options.content ? options.content : $(this).attr('content'),
   	            close: $(this).attr('close') ? $(this).attr('close') : options.close ,
	            url: contextPath+"/action/ccms/redirect?url="+url,
	            ajax: $(this).attr('ajax') ? true : false
	        });
    	}else{
    		window.location.hash = $(this).attr('url');
    	}
    });

    obj.on('click', '.close-tab', function () {
    	is_do_closing = true;
        id = $(this).prev("a").attr("aria-controls");
        _close(id);
    });

    $(window).resize(function () {
        obj.find('iframe').attr('height', options.iframeHeight);
        _drop();
    });

    _add_tab = function (opts) {
        var id = 'tab_' + opts.id;
        obj.find('.active').removeClass('active');
        obj.find('.speech-bubble-bottom').removeClass('speech-bubble-bottom').removeClass('speech-bubble');
        //如果TAB不存在，创建一个新的TAB
        if (!$("#" + id,obj)[0]) {
            //创建新TAB的title
            var title = $('<li></li>')
                .attr('role', 'presentation')
                .attr('id', 'tab_' + id)
                .append(
                $('<a></a>')
                    .attr('href', '#'+id)
                    .attr('aria-controls', id)
                    .attr('role', 'tab')
                    .attr('close', opts.close)
                    .attr('data-toggle', 'tab')
                    .html(opts.title)
            );            
            //是否允许关闭
            if (opts.close!="false") {
                title.append(' <i class="close-tab glyphicon glyphicon-remove"></i>');
            }
            //创建新TAB的内容
            var content = $('<div></div>')
                .addClass('tab-pane')
                .attr('id', id)
                .attr('style', "overflow:auto")
                .attr('role', 'tabpanel');            
            //是否指定TAB内容
            if (opts.content) {
                content.append(opts.content);
            } else if (options.iframeUse && !opts.ajax) {//没有内容，使用IFRAME打开链接
                var iframe = $('<iframe></iframe>')
                    .addClass('iframeClass')
                    .attr('height', options.iframeHeight)
                    .attr('frameborder', "no")
                    .attr('border', "0")
                    .attr('width', "100%")
                    .attr('height', "900px")
                    .attr('marginwidth', "0")
                    .attr('scrolling', "yes")
                    .attr('allowtransparency', "yes")
                    .attr('src', opts.url)
                    .attr('gotoBackUrl',opts.url);
                content.append(iframe);                
            } else {
                $.get(opts.url, function (data) {
                    content.append(data);
                });
            }
            //加入TABS
           // obj.find('.nav-tabs').append(title);
            obj.find('#tabManage').before(title);
            
            obj.find(".tab-content").append(content);
        }else{
        	if (options.iframeUse && !opts.ajax) {
        		var tabiframe = $("#" + id,obj).find("iframe:first");
    			var oldurl = tabiframe.attr("src");
    			var newurl = opts.url;
    			if( oldurl != newurl ){	// 路径、参数变化时时
    				// 重新加载TAB内容
    				tabiframe.addClass('iframeClass')
    				.attr('height', options.iframeHeight)
    				.attr('frameborder', "no")
    				.attr('border', "0")
    				.attr('width', "100%")
    				.attr('height', "900px")
    				.attr('marginwidth', "0")
    				.attr('scrolling', "yes")
    				.attr('allowtransparency', "yes")
    				.attr('src', newurl);
    				// 更新title
    				var tabid = tabiframe.parent().attr("id");
    				obj.find("#tabs").find("#tab_" + tabid).find("a[href=#" + tabid + "]").text(opts.title);
    			}
        	}
		}

        //激活TAB
        $("#tab_" + id,obj).addClass('active').addClass('speech-bubble-bottom').addClass('speech-bubble');
        $("#" + id,obj).addClass("active");

        _drop();
    };
    
    _close = function (id) {
    	//debugger;
        //如果关闭的是当前激活的TAB，激活他的前一个TAB
        if (obj.find("li.active").not(".dropdown").attr('id') == "tab_" + id) {
        	var tabCurrent= $("#tab_" + id,obj);
        	if(tabCurrent.prev().size()>0){
        		tabCurrent.prev().addClass('active').addClass('speech-bubble-bottom').addClass('speech-bubble');
        		var code=tabCurrent.prev().find("a").attr("aria-controls");
        		$("#"+code).addClass("active").addClass('speech-bubble-bottom').addClass('speech-bubble');
        		tabCurrent.prev().click();	/*IE中iframe会变短，点击一下就恢复正常*/
        	}else{
        		var tabActive=$(".nav-tabs",obj).find(">li").not(".tabManage,.dropdown").last();
        		tabActive.addClass('active').addClass('speech-bubble-bottom').addClass('speech-bubble');
        		var code=tabActive.find("a").attr("aria-controls");
        		$("#"+code).addClass("active").addClass('speech-bubble-bottom').addClass('speech-bubble');
        		tabActive.click(); /*IE中iframe会变短，点击一下就恢复正常*/
        	}
        }
        //关闭TAB
        $("#tab_" + id).remove();
        $("#" + id).remove();
        _drop();
        options.callback();
    };

    _drop = function () {
        element=obj.find('.nav-tabs').not('#menuTab');/*排除下位式菜单内容*/
        //创建下拉标签
        var dropdown = $('<li class="dropdown pull-right hide tabdrop"><a class="dropdown-toggle" data-toggle="dropdown" href="#">' +
                '<i class="glyphicon glyphicon-align-justify"></i>' +
                ' <b class="caret"></b></a><ul class="dropdown-menu"></ul></li>');

        //检测是否已增加
        if (!$('.tabdrop',obj).html()) {
            dropdown.prependTo(element);
        } else {
            dropdown = element.find('.tabdrop',obj);
        }

        //检测是否有下拉样式
        if (element.parent().is('.tabs-below')) {
            dropdown.addClass('dropup');
        }
        var collection = 0;

        //检查超过一行的标签页
        $('#tabManage').before(dropdown.find('li'));
        element.find('>li')
                .not('.tabdrop,.tabManage')
                .each(function () {
                	var offsetRight=$(".nav-tabs",obj).width()-this.offsetLeft-this.offsetWidth;
                    if (this.offsetTop > 1 || offsetRight<90) {
                        dropdown.find('ul').append($(this));
                        collection++;
                    }
                }); 

        //如果有超出的，显示下拉标签
        if (collection > 0) {
            dropdown.removeClass('hide');
            if (dropdown.find('.active').length == 1) {
                dropdown.addClass('active');
            } else {
                dropdown.removeClass('active');
            }
        } else {
            dropdown.addClass('hide');
        }
    };  
    
	//关闭所有的tab页
    $("#closeAll").unbind().on("click", function () {
    	if($("#tab_content").length > 0){//如果tab打开方式，则清除每一个tab页
			$('a[role="tab"]').each(function(){
		        id = $(this).attr("aria-controls");
		        var close = $(this).attr('close');
		        if(close != "false"){
			        _close(id);
		        }
		    });
    	}else{//清除body_content中的内容，返回主页。
    		window.location.hash="";
    	}
    });
    //关闭当前的tab页
    $("#closeCurrent").unbind().on("click", function () { 
    	var obj=$('.nav-tabs').find("li.active").not(".dropdown");
    	obj=obj.find("a[role='tab']");
    	if(obj!=undefined){
    		if(obj.attr("close")!="false"){
    	        _close(obj.attr("aria-controls"));
        	}
    	}
    });
    //刷新当前的tab页
    $("#refreshCurrent").unbind().on("click", function () { 
    	var iframe=$("#tab_content").find(".active").find("iframe");
    	iframe.attr('src',iframe.attr('src'));
    });
    
};

