(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
			var obthis = this;
		  jQuery.validator.addMethod("isOnlyOne", function(value) { //验证编号
				return obthis.isExistCode(value); 
		  }, "您输入的编号已经存在");
		  
		  this.isExistCode = function(nowInputVal){    //新增货修改对编号进行验证
				var flag=true;
				if(nowInputVal==''){
					flag=true;
				}else{
					var tuid = '';
					if(document.getElementById("tuid") == null){
						 tuid = '';
					}else{
						 tuid = document.getElementById("tuid").value;
					}
					if(nowInputVal==tuid){
						flag=false;
					}else{
						ccms.util.postData("/qa/tag/searchExistCode.json?val="+nowInputVal+"&tuid="+tuid,{async:false,success:function(data){
							  var result=data.result;
							  if(parseInt(result)>0){
								  flag= false;
							  }else{
								  flag= true;
							  }
						}});
					}
				}
				return flag;
			};
		this.init = function() {
			var formObj = ccms.crud({
				formId:"formEditor",
				button:"save_btn",
				actionroot:"/qa/tag/tagGroup",
				insertBack:function(){
					obthis.searchData();
				},
				updateBack:function(data){
					if (data.resultcode == 1) {
						ccms.dialog.notice("修改成功！", 1500);
					}
					obthis.searchData();
				},
				deleteBack:function(){
					obthis.searchData();
				},
				addNewBack:function(){
					$("#formTitle").html("新增标签组");
					ccms.util.clearForm("formEditor");
				},
				editBack:function(data){
					$("#formTitle").html("修改标签组");
					obthis.initUpdate(data.result);
				},
				validate:function(){
					var flag=false;
					flag=$("#formEditor").validate({
						rules: {
							name:{
								required: true
							},
							code: {
								isNumber:true,
								required: true,
								isOnlyOne:true
							}
						},
						messages: {
							name:{
								required: "请输入名称",
								rangelength: "名称的长度为4到18位"
							},
							code: {
								required: "请输入编号",
								rangelength: "编号的长度为4到18位"
							}
						}
					});
					return flag;
				},
				check:function(){
					return true;
				}
			}).init();
			obthis.searchData();
			
			$("#datagrid").unbind().bind('click',function(e){
				var obj=$(e.target);
				var tuid=obj.data('id');
				if(obj.hasClass("editTagGroup")){
					formObj.edit({tuid:tuid});
				}if(obj.hasClass("delTagGroup")){
					ccms.dialog.confirm("确定删除吗?",function(){
					ccms.util.postData("/qa/tag/tagGroup/delete.json?tuid="+tuid,{success:function(data){
						 if(data){
							 ccms.dialog.notice("删除成功！",1500);
						 }
							obthis.searchData();
					}});
					});
				}if(obj.hasClass("open")){
					ccms.util.gotoPage ("/qa/tag/tagList?tuid="+tuid);
				}
			});
		};
		this.searchData=function(){
			search=ccms.search({
				"datagrid" : "datagrid",
				"formId" : "searchForm"
			}).initSearchBtn().searchData(1);
		};
		this.initUpdate=function(data){
			ccms.util.setFormData("formEditor", data);
		};
	};
	$Q_init.prototype = $Q.fn;
	window["kangfu"]["admin_tagGroupList"] = $Q;
})();

$(document).ready(function() {
	kangfu.admin_tagGroupList().init();
});




