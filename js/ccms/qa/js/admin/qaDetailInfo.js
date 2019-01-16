(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		var tuid =ccms.util.getUrlVar("tuid");
		$("#tuid").val(tuid);
		
		var questionTitle = $("#questionTitle");
		var tag = $("#tag");
		var createdby = $("#createdby");
		var cteated = $("#created");
		var content = $("#concent");
		ccms.util.postData("/qa/question/admin/detailInfo.json?tuid="+tuid,{
			success:function(data){
				questionTitle.html("");
				tag.html("");
				createdby.html("");
				cteated.html("");
				content.html("");
				questionTitle.append('<a href="javascript:void(0)" class="open" data-id="'+ tuid+'">"'+ data.question.subject+'"</a>');
				tag.append(data.question.tags);
				createdby.append(data.question.userProfiles.userName);
				cteated.append(data.question.createdAt);
				content.append(data.question.content);
				obthis.openEvent();
			}
		});
		$(".back").unbind().bind("click",function(){
			ccms.util.gotoBack();
		});
		this.searchData = function(){
			search=ccms.search({datagrid:"datagrid2",formId:"searchForm2",
				rowpackage:function(obj){
					obj.content=obj.content.substr(0,50);
					if (obj.deleteFlag == 0) {
						obj.btnenabled2 = '<a href="javascript:void(0)" class="disabled2" data-id="' + obj.tuid + '">'
								+ '删除</a>';
					} else {
						obj.btnenabled2 = '<a href="javascript:void(0)" class="enabled2" data-id="' + obj.tuid + '">'
								+ '恢复</a>';
					}
				},
				success:function(){
					obthis.openEvent();
				}
			}).initSearchBtn().searchData(1);
		};
		this.searchData();
		$("#datagrid2").unbind().bind('click',function(e){
			var obj=$(e.target);
			var tuid=obj.data('id');
			if(obj.hasClass("enabled2")){
				ccms.dialog.confirm("确定恢复吗?", function() {
					ccms.util.postData("/qa/question/admin/enabled.json?tuid=" + tuid,{
						success : function(data) {
							if (data) {
								ccms.dialog.notice("恢复成功！",1500);
							}
							obthis.searchData();
						}
					});
				});
			}if(obj.hasClass("disabled2")){
				ccms.dialog.confirm("确定删除吗?", function() {
					ccms.util.postData("/qa/question/admin/disabled.json?tuid=" + tuid,{
							success : function(data) {
								if (data) {
									ccms.dialog.notice("删除成功！",1500);
								}
								obthis.searchData();
							}
						});
					});
			}
		});
		this.openEvent= function(){
			$(".open").unbind().bind('click',function(){
				var tuid=$(this).data('id');
				ccms.dialog.open({ url : contextPath + "/qa/question/admin/commentsList?tuid=" +tuid, width : 800, height : 600 });
			});
		};
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["admin_qaDetailInfo"] = $Q;
})();

$(document).ready(function() {
	ccms.admin_qaDetailInfo().init();
});

	
