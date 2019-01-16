(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		var tuid = ccms.util.getUrlVar("tuid");
		var groupId ='';
		$("#navUrl").html("");
		$("#navUrl").append('<li  class="active"><a href="#/qa/tag/tagDetail?tuid='+tuid+'" id="tagDetail">标签详情</a></li> <li><a href="#/qa/tag/tagsGrouped?tuid='+tuid+'" id="tagsGrouped">标签分组</a></li> <li><a href="#/qa/tag/resourceTransformation?tuid='+tuid+'" id="resourceTransformation">资源转换</a></li>');
		$(".btn_back").unbind().bind("click",function(){
			ccms.util.gotoBack();
		});
		var nameObj = $("#name");
		var typeObj = $("#type");
		var remarkObj = $("#remark");
		var listObj = $("#list");
			ccms.util.postData("/qa/tag/detail.json?tuid="+tuid,{success:function(data){
				nameObj.html("");
				typeObj.html("");
				remarkObj.html("");
				listObj.html("");
				nameObj.append(data.tags.name);
				if(data.tags.type=='U'){
					typeObj.append('<div><span title="转换为系统级"><a href="javascript:void(0);" class="changeTagsType" data-id="'+data.tags.tuid+'">用户级</a> </span></div>');
				}else{
					typeObj.append("<div>系统级</div>");
				}
				remarkObj.append(data.tags.remark);
			 	var html="";
				for(var i=0 ;i<data.list.length;i++){
					html+=data.list[i].name+"&nbsp;&nbsp;";
					groupId+=','+data.list[i].tuid;
				}
				$("#groupList").val(groupId);
				obthis.changUrl(tuid,groupId);
				listObj.append(html);
				$(".changeTagsType").unbind().bind("click",function(e){
					var obj=$(e.target);
					var tuid = obj.data('id');
					ccms.util.postData("/qa/tag/changeTagsType.json?tuid="+tuid,{success:function(data){
						if (data) {
							ccms.dialog.alert("转换成功！");
						}
						window.location.reload();
					}});
				});
			}});
			this.changUrl = function(tuid,groupId){
				$("#tagDetail").attr("href","#/qa/tag/tagDetail?tuid="+tuid+"&groupId="+groupId);
				$("#tagsGrouped").attr("href","#/qa/tag/tagsGrouped?tuid="+tuid+"&groupId="+groupId);
				$("#resourceTransformation").attr("href","#/qa/tag/resourceTransformation?tuid="+tuid+"&groupId="+groupId);
			};
	};
		$Q_init.prototype = $Q.fn;
		window["kangfu"]["admin_tagDetail"] = $Q;
	})();

	$(document).ready(function() {
		kangfu.admin_tagDetail().init();
	});
