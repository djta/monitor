(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		var nowDate = new Date();
		$("#startDate").val(nowDate.format("yyyy-MM-dd"));
		$("#endDate").val(nowDate.format("yyyy-MM-dd"));
		$("#searchQuestionDate").val(
				$("#startDate").val() + "至" + $("#endDate").val());
		ccms.dialog.daterangepicker($("#searchQuestionDate"), function(start,
				end) {
			$("#startDate").val(start);
			$("#endDate").val(end);
		});
		
		this.searchData = function(){
			search=ccms.search({datagrid:"datagrid",formId:"searchForm",
				rowpackage:function(obj){
					obj.subject=obj.subject.substr(0,30);
					if (obj.deleteFlag == 0) {
						obj.btnenabled1 = '<a href="javascript:void(0)" class="disabled" data-id="' + obj.tuid + '">'
								+ '删除</a>';
					} else {
						obj.btnenabled1 = '<a href="javascript:void(0)" class="enabled" data-id="' + obj.tuid + '">'
								+ '恢复</a>';
					}
				}
			}).initSearchBtn().searchData(1);
		};
		this.searchData();
		
		
		$("#datagrid").unbind().bind('click',function(e){
			var obj=$(e.target);
			var tuid=obj.data('id');
			if(obj.hasClass("enabled")){
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
			}if(obj.hasClass("disabled")){
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
			}if(obj.hasClass("qaDetailInfo")){
				ccms.util.gotoPage("/qa/question/admin/qaDetailInfo?tuid="+tuid);
			}
		});
		
	};
		$Q_init.prototype = $Q.fn;
		window["ccms"]["admin_qaList"] = $Q;
	})();

	$(document).ready(function() {
		ccms.admin_qaList().init();
	});

