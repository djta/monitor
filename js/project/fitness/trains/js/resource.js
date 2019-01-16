/* (function(){ //改变视频控件尺寸与封面一样
	var w=$('#video_thumbnail_img').css('width');
	var h=$('#video_thumbnail_img').css('height');
	$('.panel.panel-default.col_mainInner').css('height',h).css('width','1000px');
})();
 */


$('#start_learning').click(function(){
	 var videourl;
	 var coverurl; 
	 var finished_status;
	 var courseid;
	 var classid;
	 var total_class_amount=parseInt($('.total_class_amount').val());
	 if($(".course_finished_status").attr('status')=='2' || CURRENT_COURSE_PROCESS >= total_class_amount) //已经全部学完，则从第一个开始播 
	 {
		 videourl=$('.class_info_bar').eq(0).find('.class_resource_url').val();
		 coverurl=$('.class_info_bar').eq(0).find('.class_cover_url').val(); 
		 finished_status=$('.class_info_bar').eq(0).find('.class_finished_status').val()
		 courseid=$('.course_id').attr('courseid');
		 classid=$('.class_info_bar').eq(0).attr('classid');//eq(CURRENT_COURSE_PROCESS).attr('classid');
	 }
	 else
	 {
		if($("#lastclassid").val()!=""&&$("#lastcoverurl").val()!=""&&$("#lastvideourl").val()!=""){
			courseid = $('.course_id').attr('courseid');
			classid = $("#lastclassid").val();
			coverurl = $("#lastcoverurl").val();
			videourl = $("#lastvideourl").val();
			finished_status = $('.course_finished_status').attr('status');
		}else{
			videourl=$('.class_info_bar').eq(0).find('.class_resource_url').val();
			coverurl=$('.class_info_bar').eq(0).find('.class_cover_url').val(); 
			finished_status=$('.class_info_bar').eq(0).find('.class_finished_status').val()
			courseid=$('.course_id').attr('courseid');
			classid=$('.class_info_bar').eq(0).attr('classid');
		}
	 }
	 if(type1=="1"){
		 finished_status=type2;
		 videourl = type3;
		 coverurl = type4;
		 courseid = type5;
		 classid=type6;
	 }
	 //alert(videourl+"----"+coverurl+"----"+courseid+"----"+classid);
	 loadResource(finished_status,videourl,coverurl,courseid,classid);
});


function determineProcess(finished_status,videourl,coverurl,courseid,classid)//判断课时学习进度来决定播放哪个视频
{
	if(finished_status=='2')
	{
		loadResource(finished_status,videourl,coverurl,courseid,classid);
	}
	else 
	{
	    var videourl=$('.class_info_bar').eq(CURRENT_COURSE_PROCESS).find('.class_resource_url').val();
	    var coverurl=$('.class_info_bar').eq(CURRENT_COURSE_PROCESS).find('.class_cover_url').val();
	    loadResource(finished_status,videourl,coverurl,courseid,classid);
	}
}

function loadResource(finished_status,videourl,coverurl,courseid,classid)
{
	$('#video_thumbnail_img').css('display','none');
	$('.panel.panel-default.col_mainInner').css('display','block');
	loadVideo(finished_status,videourl,coverurl,courseid,classid);
}


var JSQ_COURSEID;//计时器使用
var JSQ_CLASSID;
function loadVideo(finished_status,videourl,coverurl,courseid,classid){
	$("#videoshow").children().remove(); /*移除旧TcPlayer*/
	if(CURRENT_INTERVAL!=null)
	{
 	   clearInterval(CURRENT_INTERVAL); //清除计时器，重新设置
	}	
	JSQ_COURSEID=courseid;
	JSQ_CLASSID=classid
	CURRENT_INTERVAL=setInterval('sendCurrentSecond(JSQ_COURSEID,JSQ_CLASSID,RESOURCE_CURRENT_SECONT);',60000);
	var playtime = 1000, begints = 0, endts = 0;
	var player = new TcPlayer('videoshow', {
		mp4:videourl,
		coverpic: {
			style: "cover",
			src: coverurl
		},
		autoplay: false,	//iOS下safari浏览器，以及大部分移动端浏览器是不开放视频自动播放这个能力的
		width: '480',	//视频的显示宽度，请尽量使用视频分辨率宽度
		height: '320',	//视频的显示高度，请尽量使用视频分辨率高度
		/**live: true,	// 是否直播
		wording: {
			1: '网络错误，请检查网络配置或者播放链接是否正确',	// (H5提示的错误)
			2: "视频格式WEB播放器无法解码",	// (H5提示的错误)
			3: "视频解码错误 ",	// (H5提示的错误)
			4: "当前系统环境不支持播放该视频格式",	// (H5提示的错误)
			5: "当前浏览器不支持MSE或者Flash插件未启用",
			10: "请勿在file协议下使用播放器，可能会导致视频无法播放 ",
			11: "使用参数有误，请检查播放器调用代码",
			12: "请填写视频播放地址"
        },*/
		listener: function(res){
			console.log(res);
			if( res.type ){
				if( res.type == "error" ){
					window.setTimeout(function(){
						player.load();	//进行重连
					},5000);
                }else if( res.type == "play" ){	// 开始播放
					begints =res.ts;
					if(finished_status=='0')
					{
						insertCourseLog(courseid,classid);//增加一条状态记录
					}
					if(finished_status=='1') //如果为课时学习中，获取进度并跳转
					{
						var url=CURRENT_TIME_PROCESS_URL;
						url+="?courseid="+courseid;
						url+="&classid="+classid;
						ajaxCall(url,{
							method: "get",
							progress: false,
							dataType: "script",
							success: function() {	
								var time_process=parseInt($('.current_time_process').val());
								//alert(time_process);
								player.currentTime(time_process); //跳转到上次学习进度
								
							}
						});
					}
				}else if( res.type == "pause" ){	// 暂停
					begints = res.ts;
				}else if( res.type == "ended" ){	// 播放完成
					console.log("总播放 " + playtime/1000 + " 秒");
					if(finished_status!='2')
					{
						finishClass(courseid,classid); //改变课时学习状态
					}
				}else if( res.type == "timeupdate" ){	// 播放进度更新
					endts = res.ts;
					playtime += endts - begints;
					begints = endts;
					//console.log("已播放 " + playtime/1000 + " 秒");
					RESOURCE_CURRENT_SECONT=parseInt(playtime/1000); //传递给后台的currentsecond
					console.log("已播放 " +RESOURCE_CURRENT_SECONT + " 秒");
				}
			}
		}
	});    
};

/*CURRENT_INTERVAL=setInterval('AA();',1000);
function AA()
{
	RESOURCE_CURRENT_SECONT++;
	console.log(RESOURCE_CURRENT_SECONT,CURRENT_INTERVAL);
}*/

