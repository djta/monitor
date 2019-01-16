(function(){
    $('.gg-user-top').on('click', function(){
        location.href = 'profile';
    });

    $('.gg-user-bt').on('click', '#groupTask', function(){
    	var teamid = "";
    	if( $("#team_id").length > 0 && "undefined" != $("#team_id").val() ){
    		teamid = $("#team_id").val();
    	}
    	if( "" == teamid ){
    		$.toast("请设置员工组后再操作", "cancel");
    		return false;
    	}
        location.href = contextPath+"/action/project/fitness/guestget/grouptask/crud?team_id="+teamid+"&teamtype=2";
    }).on('click', '#personTask', function(){
    	// 暂无页面
        location.href = contextPath+"/action/project/fitness/guestget/persontask/crud";
    })
})();