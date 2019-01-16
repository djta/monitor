/*qcloud lib*/
(function() {
	var cosBox = [], isdebug = false;
	var $ugcobj = null, $resultObj = null, $fileidobj = null, $videonameobj = null, $videourlobj = null, $coverurlobj = null;
	
	/**var getSignatureDemo = function(callback){
		$.ajax({
			url: 'http://123.206.83.120:80/interface.php',
			data: JSON.stringify({"Action":"GetVodSignatureV2"}),
			type: 'POST',
			dataType: 'json',
			success: function(res){
				if(res.returnData && res.returnData.signature) {
					callback(res.returnData.signature);
				} else {
					return '获取签名失败';
				}
			}
		});
	};*/
	
	var $QCloud = {
		/*** 参数初始化 */
		initConfig : function(map){
			$ugcobj = undefined != map && map["uploadobj"] != null ? map["uploadobj"] : $("#ugcUpload");	// 上传区域
			$resultObj = undefined != map && map["resultobj"] != null ? map["resultobj"] : $("#uploadmsg");	// 信息回显区域
			isdebug = undefined != map && map["debug"] == true ? true : false;	// 是否debug，默认false
			$fileidobj = undefined != map && map["fileid"] != null ? map["fileid"] : $("#fileid");	// 回写fileId地址
			$videonameobj = undefined != map && map["videoname"] != null ? map["videoname"] : $("#videoname");	// 回写videoName地址
			$videourlobj = undefined != map && map["videourl"] != null ? map["videourl"] : $("#videourl");	// 回写videoUrl地址
			$coverurlobj = undefined != map && map["coverurl"] != null ? map["coverurl"] : $("#coverurl");	// 回写coverUrl地址
		},
		/*** 获取签名 */
		getSignature : function(callback){
			var signstr = "";
			$.ajax({
				url: contextPath + "/api/qcloud/getsignature",
				type: 'get',
				dataType: 'json',
				async: false,
				success: function(res){
					if(res.signature) {
						signstr =  res.signature;
					} 
					if( "" == signstr ){
						signstr = '获取签名失败';
					}else{
						callback(signstr);
					}
				}
			});
			return signstr;
		},
		/** 上传 */
		upload : function(videoFile, coverFile, finishFunc){
			if( undefined != coverFile && null != coverFile ){
				ccms.qcloud.uploadVideoWithCover(videoFile, coverFile, finishFunc);
			}else{
				ccms.qcloud.uploadVideo(videoFile, finishFunc);
			}
		},
		/*** 上传视频 */
		uploadVideo : function(videoFile, finishFunc){
			$resultObj.empty();
			ccms.qcloud.addUploaderMsgBox('hasVideo');
			var resultMsg = qcVideo.ugcUploader.start({
				videoFile: videoFile,
				getSignature: ccms.qcloud.getSignature,
				allowAudio: 1,
				success: function(result){
					if(isdebug){
						console.group("SUCCESS: ");
						console.log(result);
						console.groupEnd();
					}
					$resultObj.find('span[name=videoresult]').text('上传成功');
					$resultObj.find('a[name=cancel]').remove();
					cosBox[0] = null;
				},
				error: function(result){
					if(isdebug){
						console.group("ERROR: ");
						console.log(result);
						console.groupEnd();
					}
					$resultObj.find('span[name=videoresult]').text('上传失败>>'+result.msg);
				},
				progress: function(result){
					if(isdebug){
						console.group("PROGRESS: ");
						console.log(result);
						console.groupEnd();
					}
					$resultObj.find('span[name=videoname]').text(result.name);
					$resultObj.find('span[name=videocurr]').text(Math.floor(result.curr*100)+'%');
					$resultObj.find('a[name=cancel]').attr('taskId', result.taskId);
					cosBox[0] = result.cos;
				},
				finish: function(result){
					if(isdebug){
						console.group("FINISH: ");
						console.log(result);
						console.groupEnd();
					}
					if( $fileidobj ){
						$fileidobj.val(result.fileId);
					}
					if( $videourlobj ){
						$videourlobj.val(result.videoUrl);
					}
					if(result.message) {
						$resultObj.text(result.message);
					}
					if( typeof(finishFunc) == "function" ){
						finishFunc(result);
					}
				}
			});
			if(resultMsg){
				$resultObj.text(resultMsg);
			}
		},
		/*** 上传视频和封面 */
		uploadVideoWithCover : function(videoFile, coverFile, finishFunc){
			$resultObj.empty();
			ccms.qcloud.addUploaderMsgBox();
			var resultMsg = qcVideo.ugcUploader.start({
				videoFile: videoFile,
				coverFile: coverFile,
				getSignature: ccms.qcloud.getSignature,
				success: function(result){
					if(isdebug){
						console.group("SUCCESS: ");
						console.log(result);
						console.groupEnd();
					}
					if(result.type == 'video') {
						$resultObj.find('span[name=videoresult]').text('上传成功');
						$resultObj.find('a[name=cancel]').remove();
						cosBox[0] = null;
					} else if (result.type == 'cover') {
						$resultObj.find('span[name=coverresult]').text('上传成功');
					}
				},
				error: function(result){
					if(isdebug){
						console.group("ERROR: ");
						console.log(result);
						console.groupEnd();
					}
					if(result.type == 'video') {
						$resultObj.find('span[name=videoresult]').text('上传失败>>'+result.msg);
					} else if (result.type == 'cover') {
						$resultObj.find('span[name=coverresult]').text('上传失败>>'+result.msg);
					}
				},
				progress: function(result){
					if(isdebug){
						console.group("PROGRESS: ");
						console.log(result);
						console.groupEnd();
					}
					if(result.type == 'video') {
						$resultObj.find('span[name=videoname]').text(result.name);
						$resultObj.find('span[name=videocurr]').text(Math.floor(result.curr*100)+'%');
						$resultObj.find('a[name=cancel]').attr('taskId', result.taskId);
						cosBox[0] = result.cos;
					} else if (result.type == 'cover') {
						$resultObj.find('span[name=covername]').text(result.name);
						$resultObj.find('span[name=covercurr]').text(Math.floor(result.curr*100)+'%');
					}
				},
				finish: function(result){
					if(isdebug){
						console.group("FINISH: ");
						console.log(result);
						console.groupEnd();
					}
					if( $fileidobj ){
						$fileidobj.val(result.fileId);
					}
					if( $videourlobj ){
						$videourlobj.val(result.videoUrl);
					}
					if( $coverurlobj ){
						$coverurlobj.val(result.coverUrl);
					}
					if(result.message) {
						$resultObj.text(result.message);
					}
					if( typeof(finishFunc) == "function" ){
						finishFunc(result);
					}
				}
			});
			if(resultMsg){
				$resultObj.text(resultMsg);
			}
		},
		/** 添加上传信息模块 */
		addUploaderMsgBox : function(type){
			var html = '<div class="uploaderMsgBox" name="box">';
			if(!type || type == 'hasVideo') {
				html += '视频 <span name="videoname"></span> 已上传 <span name="videocurr">0%</span> <span name="videoresult"></span> ' + 
					'<a href="javascript:void(0);" name="cancel" cosnum="0" taskId="" act="cancel-upload">取消上传</a><br/>';
			}
			if(!type || type == 'hasCover') {
				html += '封面 <span name="covername"></span> 已上传<span name="covercurr">0%</span> <span name="coverresult"></span><br/>';
			}
			html += '</div>';
			$resultObj.append(html);

			$ugcobj.find("[name=cancel]").on('click', function() {
				ccms.qcloud.cancelUpload($(this));
			});
		},
		/*** 取消上传绑定事件 */
		cancelUpload : function(obj){
			var cancelresult = qcVideo.ugcUploader.cancel({
				cos: cosBox[$(obj).attr('cosnum')],
				taskId: $(obj).attr('taskId')
			});
			$resultObj.empty();
			if(cancelresult ){
				$resultObj.append(cancelresult);
			}
		},
		/** 修改封面 */
		updateCover : function(fileId, coverFile, finishFunc){
			$resultObj.empty();
			ccms.qcloud.addUploaderMsgBox("hasCover");
			var resultMsg = qcVideo.ugcUploader.start({
				fileId: fileId,
				coverFile: coverFile,
				getSignature: ccms.qcloud.getSignature,
				success: function(result){
					if(isdebug){
						console.group("SUCCESS: ");
						console.log(result);
						console.groupEnd();
					}
					if(result.type == 'video') {
						$resultObj.find('span[name=videoresult]').text('上传成功');
						$resultObj.find('a[name=cancel]').remove();
						cosBox[0] = null;
					} else if (result.type == 'cover') {
						$resultObj.find('span[name=coverresult]').text('上传成功');
					}
				},
				error: function(result){
					if(isdebug){
						console.group("ERROR: ");
						console.log(result);
						console.groupEnd();
					}
					if(result.type == 'video') {
						$resultObj.find('span[name=videoresult]').text('上传失败>>'+result.msg);
					} else if (result.type == 'cover') {
						$resultObj.find('span[name=coverresult]').text('上传失败>>'+result.msg);
					}
				},
				progress: function(result){
					if(isdebug){
						console.group("PROGRESS: ");
						console.log(result);
						console.groupEnd();
					}
					if(result.type == 'video') {
						$resultObj.find('span[name=videoname]').text(result.name);
						$resultObj.find('span[name=videocurr]').text(Math.floor(result.curr*100)+'%');
						$resultObj.find('a[name=cancel]').attr('taskId', result.taskId);
						cosBox[0] = result.cos;
					} else if (result.type == 'cover') {
						$resultObj.find('span[name=covername]').text(result.name);
						$resultObj.find('span[name=covercurr]').text(Math.floor(result.curr*100)+'%');
					}
				},
				finish: function(result){
					if(isdebug){
						console.group("FINISH: ");
						console.log(result);
						console.groupEnd();
					}
					if( $fileidobj ){
						$fileidobj.val(result.fileId);
					}
					if( $videourlobj ){
						$videourlobj.val(result.videoUrl);
					}
					if( $coverurlobj ){
						$coverurlobj.val(result.coverUrl);
					}
					if(result.message) {
						$resultObj.text(result.message);
					}
					if( typeof(finishFunc) == "function" ){
						finishFunc(result);
					}
				}
			});
			if(resultMsg){
				$resultObj.text(resultMsg);
			}
		},
		/** 删除已上传视频 */
		deleteVideo : function(fileid, callback){
			var uri = "/action/project/fitness/trains/util/video/delete?fileid="+fileid;
			ajaxCall(uri,{
				method: "get",
				progress: true,
				dataType: "script",
				success: function() {
					if( typeof(callback) == "function" ){
						callback();
					}
				}
			});
		}
	}

	window["ccms"]["qcloud"] = $QCloud;
})();