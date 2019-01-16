(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		var tuid= ccms.util.getUrlVar("tuid");
		$("#tagGroupTuid").val(tuid);
		$("#groupId").val(tuid);
		this.init = function() {
			var formObj = ccms.crud({
				formId:"formEditor2",
				button:"save_btn2",
				actionroot:"/qa/tag/tags",
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
					$("#formTitle").html("新增标签");
					ccms.util.clearForm("formEditor2");
				},
				editBack:function(data){
					$("#formTitle").html("修改标签");
					obthis.initUpdate(data.tag);
				},
				validate:function(){
					var flag=false;
					flag=$("#formEditor2").validate({
						rules: {
							name:{
								required: true
							}
						},
						messages: {
							name:{
								required: "请输入名称",
								rangelength: "名称的长度为4到18位"
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
			$("#datagrid2").unbind().bind('click',function(e){
				var obj=$(e.target);
				var tuid=obj.data('id');
				if(obj.hasClass("editTag")){
					formObj.edit({tuid:tuid});
				}if(obj.hasClass("delTag")){
					ccms.dialog.confirm("确定删除吗?",function(){
					ccms.util.postData("/qa/tag/tags/delete.json?tuid="+tuid,{success:function(data){
						 if(data){
							 ccms.dialog.notice("删除成功！",1500);
						 }
							obthis.searchData();
					}});
					});
				}if(obj.hasClass("detail")){
					ccms.util.gotoPage("/qa/tag/tagDetail?tuid="+tuid);
				}
			});
			
			ccms.file("ImgmyUpload").upload({thumb:'avatar',success:function(imgId){
				$("#iconUrl").val(imgId);
				$("#imgshow").attr("src",ccms.util.getImageUrl(imgId));
			}});
		};
		this.searchData=function(){
			search=ccms.search({
				"datagrid" : "datagrid2",
				"formId" : "searchForm2",
				rowpackage:function(obj){
 						if(obj.type=='S')
 							{
 							obj.type="系统级";
 							}
 						if(obj.type=='U'){
 							obj.type="用户级";
 						}
 				}
			}).initSearchBtn().searchData(1);
		};
		this.initUpdate=function(data){
			ccms.util.setFormData("formEditor2", data);
			$("#imgshow").attr("src",ccms.util.getImageUrl(data.iconUrl));
		};
		
 
		/*
 		this.init = function() {
 			$Search('datagrid2').initSearchBtn($('#searchForm2'));
 			searchTagList();
 			this.searchTagList = function(){
 				searchData();
 				$Search('datagrid2').searchData($('#searchForm2'),1,{});
 				}
 			};
 			
 			this.addTag = function() {
 				var tuid = $("#showTagList").val();
 				if (!tuid){
 					tuid=-1;
 				}
 				ccms.dialog.open({
 					url : contextPath + "/tag/addTag/" +tuid+ "?ec",
 					width : 600,
 					height : 500
								 * , success:function(){ searchTagList(); }
								 
 				});
 			};
 			
 			this.editTag = function(tuid) {
 				ccms.dialog.open({
 					url : contextPath + "/tag/editTag/" +tuid+ "?ec",
 					width : 600,
 					height : 500
								 * , success:function(){ searchTagList(); }
								 
 				});
 			};
 			this.delTag = function(tuid){
 				var url='/tag/deleteTag.json';
 				var pars='tuid='+tuid;
 				ccms.dialog.confirm("确定删除吗?",function(){
 					$Q().postData({
 						url : url,
 						pars : pars,
 						success : function(){
 							$Search('datagrid2').searchData($('#searchForm2'));
 						}
 					});
 				});
 			};
 			
 			this.detail = fucntion(tuid) {
 				
 			};
 			
 			this.searchData = function (){
 				var id = $("#showTagList").val();
 				$("#tuid").val(id);
 			}*/
		
	};
 			$Q_init.prototype = $Q.fn;
 			window["kangfu"]["admin_tagList"] = $Q;
 		})();

 		$(document).ready(function() {
 			kangfu.admin_tagList().init();
 		});
