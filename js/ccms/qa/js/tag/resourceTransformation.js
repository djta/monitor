(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		var groupId =ccms.util.getUrlVar("groupId");
		if(null == groupId || groupId==	undefined){
			groupId="";
		}
		var tuid = ccms.util.getUrlVar("tuid");
		$("#navUrl").html("");
		$("#navUrl").append('<li><a href="#/qa/tag/tagDetail?tuid='+tuid+'" id="tagDetail">标签详情</a></li> <li><a href="#/qa/tag/tagsGrouped?tuid='+tuid+'" id="tagsGrouped">标签分组</a></li> <li class="active"><a href="#/qa/tag/resourceTransformation?tuid='+tuid+'" id="resourceTransformation">资源转换</a></li>');
			$(".btn_back").unbind().bind("click",function(){
			ccms.util.gotoBack();
		});
		var totalTuid2 = '';
		$("#tagid").val(tuid);
		this.searchData=function(){
			search=ccms.search({
				"datagrid" : "datagrid9",
				"formId" : "searchForm9",
				success:function(data){
					obthis.selectList(data.list2);
					$("input[name=groupId2]").each(function() {// 把totalTuid存储的所有id传过来与页面复选框的id匹配如果有就把该复选框选中
						var tuid = $(this).val(); // 先查
						if (totalTuid2.indexOf(',' + $(this).val()) >= 0) { // 匹配上了
							$("#ck" + tuid).attr("checked", true);
						}
					});
					$('input[type=checkbox][name=taggroundIds2]').on('click', function(event) {
						if ($(this).is(":checked")) {
							totalTuid2 += "," + $(this).val();
						} else {
							totalTuid2 = totalTuid2.replace("," + $(this).val(), '');
						}
					}).iCheck({
						checkboxClass : 'icheckbox_square-blue',
						increaseArea : '20%'
					});
				},
				rowpackage:function(obj){
					var html = '<input id="ck' + obj.tuid
							+ '"  type="checkbox" name="taggroundIds2" value=' + obj.tuid + '>';
					obj.checkbox2 = html;

					obj.createdAt = obthis.format(obj.createdAt);
 				}
			}).initSearchBtn().searchData(1);
		};
		this.searchData();
		$("#select_id").select2({
			   formatNoMatches:function(){return"没有检索到输入的标签"}
		   });
		$(".selectAll2").unbind().bind("click",function(){
			if ($('input[name=checkAll2]').is(":checked")) {
				$('input[name=taggroundIds2]').iCheck('check');
				$("input[name=groupId2]").each(
						function() {// 把totalTuid存储的所有id传过来与页面复选框的id匹配如果有就把该复选框选中
							if (totalTuid2.indexOf(',' + $(this).val()) == -1
									&& $(this).val() != '#tuid#') {
								totalTuid2 += "," + $(this).val();
							}
						});
			}
			if ($('input[name=checkAll2]').is(":unchecked")) {
				$('input[name=taggroundIds2]').iCheck('uncheck');
				$("input[name=groupId2]").each(function() {// 把totalTuid存储的所有id传过来与页面复选框的id匹配如果有就把该复选框选中
					if (totalTuid2.indexOf(',' + $(this).val()) >= 0) {
						totalTuid2 = totalTuid2.replace("," + $(this).val(), '');
					}
				});
			}
		});
		this.format = function(dateNum) {
			var date = new Date(dateNum);
			return date.getFullYear() + "-" + fixZero(date.getMonth() + 1, 2) + "-"
					+ fixZero(date.getDate(), 2) + " " + fixZero(date.getHours(), 2)
					+ ":" + fixZero(date.getMinutes(), 2) + ":"
					+ fixZero(date.getSeconds(), 2);
			function fixZero(num, length) {
				var str = "" + num;
				var len = str.length;
				var s = "";
				for (var i = length; i-- > len;) {
					s += "0";
				}
				return s + str;
			}
		};
		$(".btn_save2").unbind().bind("click",function(){
			var oldTagId = $("#tagid").val();
			var newTagId = $("#select_id").val();// 得到tuid 标签的id
			if ($('input[name=taggroundIds2]:checked').length == 0) {
				ccms.dialog.notice("请先选择一个资源",1500);
			} else {
				if (totalTuid2.length > 0) {
					if (totalTuid2.indexOf(",") == 0)
						totalTuid2 = totalTuid2.substring(1, totalTuid2.length); // 取里面的值
				}
				if (newTagId == 0) {
					ccms.dialog.notice("请先选择一个标签",1500);
				} else {
				ccms.util.postData(contextPath + "/qa/tag/tagsResource/tagAddResource/"
						+ totalTuid2 + "/" + newTagId + "/" +oldTagId+ ".json?",{
						success : function(data) {
							totalTuid2 = "," + totalTuid2;
							obthis.changUrl(tuid,groupId);
							if(data.resultcode==1){
								ccms.dialog.notice("保存成功！",1500);
							}else{
								ccms.dialog.notice("保存失敗！",1500);
							}
							$("#tagid").val(tuid);
							obthis.searchData();
						}
					});
				}
			}
		});
		this.changUrl = function(tuid,groupId){
			$("#tagDetail").attr("href","#/qa/tag/tagDetail?tuid="+tuid+"&groupId="+groupId);
			$("#tagsGrouped").attr("href","#/qa/tag/tagsGrouped?tuid="+tuid+"&groupId="+groupId);
			$("#resourceTransformation").attr("href","#/qa/tag/resourceTransformation?tuid="+tuid+"&groupId="+groupId);
		};
		this.selectList = function(array) {
			for (var i = 0; i < array.length; i++) {
				var data = array[i];
				var text = data.name;
				var value = data.tuid;
				$("#select_id").append(
						"<option value='" + value + "'>" + text + "</option>");
			}
			};
		};
		
		$Q_init.prototype = $Q.fn;
		window["kangfu"]["resourceTransformation"] = $Q;
	})();

	$(document).ready(function() {
		kangfu.resourceTransformation().init();
	});
