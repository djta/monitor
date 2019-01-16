/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var addTabs = function (obj) {
    id = "tab_" + obj.id;
    
    $(".active").removeClass("active");
     
    //如果TAB不存在，创建一个新的TAB
    if (!$("#" + id)[0]) {
        //固定TAB中IFRAME高度,根据需要自己修改
        mainHeight = $("#tab_content").height();
        //创建新TAB的title
        title = '<li role="presentation" id="tab_' + id + '"><a href="#' + id + '" aria-controls="' + id + '" role="tab" data-toggle="tab" class="tab">' + obj.title+'</a>';
        //是否允许关闭
        if (obj.close) {
            title += ' <i class="close-tab glyphicon glyphicon-remove"></i>';
        }
        title += '</li>';
        //是否指定TAB内容
        if (obj.content) {
            content = '<div role="tabpanel" class="tab-pane col_main" id="' + id + '">' + obj.content + '</div>';
        } else {//没有内容，使用IFRAME打开链接
            content = '<div role="tabpanel" class="tab-pane col_main" style="overflow:auto" id="' + id + '"><iframe src="' + obj.url + '" width="100%" height="900px' + "" +
                    '" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="yes" allowtransparency="yes"></iframe></div>';
        }
        //加入TABS
        $(".nav-tabs").not("#menuTab").append(title);
        $(".tab-content").append(content);
    }
     
    //激活TAB
    $("#tab_" + id).addClass('active');
    $("#" + id).addClass("active");
};

var closeTab = function (id) {
    //如果关闭的是当前激活的TAB，激活他的前一个TAB
    if ($("li.active").attr('id') == "tab_" + id) {
        $("#tab_" + id).prev().addClass('active');
        $("#" + id).prev().addClass('active');
    }
    //关闭TAB
    $("#tab_" + id).remove();
    $("#" + id).remove();
};

$(function () {

    $("[addtabs]").click(function () {
        addTabs({id: $(this).attr("addtabs"), title: $(this).attr('title'), url: $(this).attr('url'), close: true});
    });
    
    $(".nav-tabs").not("#menuTab").on("click", ".close-tab", function () {
        id = $(this).prev("a").attr("aria-controls");
        closeTab(id);
    });

		//关闭所有的tab页
    $("#close-all-tab").on("click", function () {
    	if($("#tab_content").length > 0){//如果tab打开方式，则清除每一个tab页
			$('a[class="tab"]').each(function(){
		        id = $(this).attr("aria-controls");
		        closeTab(id);
		    });
    	}else{//清除body_content中的内容，返回主页。
    		window.location.hash="";
    	}
    });    
});

//所有菜单调用接口
function openUrl(obj){
	if($("#tab_content").length > 0){
		addTabs({id: obj.id, title: obj.title, url:  contextPath+"/action/ccms/redirect?url="+unescape($Base64.decode(obj.url)), close: obj.close});
	}else{
		window.location.hash = obj.url;
	}
}
