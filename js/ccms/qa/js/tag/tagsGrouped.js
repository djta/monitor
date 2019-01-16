(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var totalTuid = '';
		var size;
		var obthis = this;
		var groupId =ccms.util.getUrlVar("groupId");
		if(null == groupId || groupId==	undefined){
			groupId="";
		}
		totalTuid = groupId;
		$("#groupId").val(groupId);
		var tuid = ccms.util.getUrlVar("tuid");
		$("#navUrl").html("");
		$("#navUrl").append('<li><a href="#/qa/tag/tagDetail?tuid='+tuid+'" id="tagDetail">标签详情</a></li> <li class="active"><a href="#/qa/tag/tagsGrouped?tuid='+tuid+'" id="tagsGrouped">标签分组</a></li> <li ><a href="#/qa/tag/resourceTransformation?tuid='+tuid+'" id="resourceTransformation">资源转换</a></li>');
		$(".btn_back").unbind().bind("click",function(){
			ccms.util.gotoBack();
		});
		this.searchData=function(){
			search=ccms.search({
				"datagrid" : "datagrid",
				"formId" : "searchForm",
				success:function() {
					size = 0;
					$("input[name=groupId]").each(function() {// 把totalTuid存储的所有id传过来与页面复选框的id匹配如果有就把该复选框选中
						var tuid = $(this).val(); // 先查
						var strs = new Array(); // 定义一数组
						strs = totalTuid.split(","); // 字符分割
						size += 1;
						for (var i = 0; i < strs.length; i++) {
							if (strs[i] == $(this).val()) {
								$("#ck" + tuid).attr("checked", true);
							}
						}
					});
					$('input[type=checkbox][name=taggroundIds]').on('click', function(event) {
						if ($(this).is(":checked")) {
							totalTuid += "," + $(this).val();
						} else {
							totalTuid = totalTuid.replace("," + $(this).val(), '');
						}
					}).iCheck({
						checkboxClass : 'icheckbox_square-blue',
						increaseArea : '20%'
					}); 
				},
				rowpackage:function(obj){
					var html = '<input id="ck' + obj.tuid
					+ '"  type="checkbox" name="taggroundIds" value=' + obj.tuid + '>';
					obj.checkbox = html;
 				}
			}).initSearchBtn().searchData(1);
		};
		this.searchData();
		this.totalTuid = $('input[type=hidden][name=groupList]').val();
		$(".selectAll").unbind().bind("click",function(){
			if ($('input[name=checkAll]').is(":checked")) {
				$('input[name=taggroundIds]').iCheck('check');
				var count = 0;
				$("input[name=groupId]").each(function() { // 全选加上当前页面之前没有选上的
					var strs = new Array(); // 定义一数组
					strs = totalTuid.split(","); // 字符分割
						if (strs.indexOf(',' + $(this).val()) < 0 && $(this).val() != '#tuid#') {
							totalTuid += "," + $(this).val();
					}
					count += 1;
					if (count > size - 1) {
						return false;
					}
				});
			}
			if ($('input[name=checkAll]').is(":unchecked")) {
				$('input[name=taggroundIds]').iCheck('uncheck');
				totalTuid= obthis.unique(totalTuid);
				$("input[name=groupId]").each(function() { // 如果该页面有之前选中的则取消选择
					if(totalTuid.indexOf(','+$(this).val())>-1){
			    	totalTuid= totalTuid.replace(","+$(this).val(),'');
			    }
				});
			}
		});
		$(".save_btn").unbind().bind("click",function(){
			var id = tuid;// 得到tuid
			if (totalTuid.length == 0) {
				ccms.dialog.notice("请先选择一个分租",1500);
			} 
			else {
				if (totalTuid.length > 0) {
					totalTuid= obthis.unique(totalTuid);
					if(totalTuid.indexOf(",")==0){
						totalTuid = totalTuid.substring(1,totalTuid.length);//去除第一个逗号
					}
				}
				ccms.util.postData("/qa/tag/insertTagsgrouped/" + totalTuid + "/" + id + ".json?",{
					success : function(data) {
						totalTuid = "," + totalTuid; 
						obthis.changUrl(tuid,totalTuid);
						if(data.resultcode==1){
							ccms.dialog.notice("保存成功！",1500);
						}else{
							ccms.dialog.notice("保存失敗！",1500);
						}
					}
				});
			}
		});
		this.unique = function(str)
		{
			var res = "";
			temp = str.split(",");
			temp.sort();
			for (var i = 0; i < temp.length; i++) {
				if (temp[i] == temp[i + 1]) {
					continue;
				}
				if (i == 0) {
					res += temp[i];
				} else {
					res += ',' + temp[i];
				}
			}
			return res;
		};
		this.changUrl = function(tuid,groupId){
			$("#tagDetail").attr("href","#/qa/tag/tagDetail?tuid="+tuid+"&groupId="+groupId);
			$("#tagsGrouped").attr("href","#/qa/tag/tagsGrouped?tuid="+tuid+"&groupId="+groupId);
			$("#resourceTransformation").attr("href","#/qa/tag/resourceTransformation?tuid="+tuid+"&groupId="+groupId);
		};
	};
	$Q_init.prototype = $Q.fn;
	window["kangfu"]["admin_tagGrouped"] = $Q;
})();

$(document).ready(function() {
	kangfu.admin_tagGrouped().init();
});


