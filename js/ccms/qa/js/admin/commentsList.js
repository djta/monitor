(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		$("#parentid").val(ccms.util.getUrlVar("tuid"));
		var obthis = this;
		$(".btn_close").unbind().bind("click",function(){
			ccms.dialog.close();
		});
		this.searchData = function(){
			search=ccms.search({datagrid:"datagrid3",formId:"searchForm3",
				rowpackage:function(obj){
					obj.content=obj.content.substr(0,30);
					if (obj.deleteFlag == 0) {
						obj.btnenabled3 = '<a href="javascript:void(0)" class="disabled3" data-id="' + obj.tuid + '">'
								+ '删除</a>';
					} else {
						obj.btnenabled3 = '<a href="javascript:void(0)" class="enabled3" data-id="' + obj.tuid + '">'
								+ '恢复</a>';
					}
				}
			}).initSearchBtn().searchData(1);
		};
		this.searchData();
		$("#datagrid3").unbind().bind('click',function(e){
			var obj=$(e.target);
			var tuid=obj.data('id');
			if(obj.hasClass("enabled3")){
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
			}if(obj.hasClass("disabled3")){
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
		};
		$Q_init.prototype = $Q.fn;
		window["ccms"]["admin_commentList"] = $Q;
	})();
	$(document).ready(function() {
		ccms.admin_commentList().init();
	});

