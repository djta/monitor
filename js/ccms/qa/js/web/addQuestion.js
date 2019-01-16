(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		var validateFlag=[false,false,false];
		ccms.qaTag().show("selectList");
		 
		CKEDITOR.replace('content');
		
		this.ckfocus = function() {
			if(CKEDITOR.instances.content.getData().trim()==""){
				CKEDITOR.instances.content.setData("");
				return;
			}
		};
		/*CKEDITOR.instances["content"].on("instanceReady", function () {  
	        this.document.on("click", obthis.ckfocus());  
		});*/
		$('#swithtype').bootstrapSwitch();
		$('#swithtype').on({
		    'init.bootstrapSwitch': function() {
		    	
		    },
		    'switchChange.bootstrapSwitch': function(event, state) {
		      }
		  });
		
		 $('#submit_btn').unbind().on("click",function(){
				var titlelength = $('#qaForm input[name=title]').val().trim();
				if(titlelength == "请在此输入标题" || titlelength.length <1 || titlelength.length > 400){
						$("#titleHelp").show();
						return;
				}else if(CKEDITOR.instances.content.getData().trim()==""){
					$("#qHelp").show();
					return;
				}else if($('#tagList li').length < 1){
					$("#tagsHelp").show();
					return;
				}
				obthis.submitForm();
		 });
		
		 $('#qaForm input[name=title]').blur(function(){
				if($.trim($(this).val())=='' || $(this).val()=='请在此输入标题'){
					$("#titleHelp").show();
				}else{
					if($(this).val().gblen()>400){
						$("#titleHelp").show();
					}else{
						$("#titleHelp").hide();
					}
				}
		 });
		 this.submitForm = function(){
				obthis.setTagInputValue();
				if($("#swithtype").length>0){
					if($("#swithtype").is(':checked')){
						$("#swithtypehidden").val('Q');
					}else{
						$("#swithtypehidden").val('D');
					}
				}
				
				$("#content").val(CKEDITOR.instances.content.getData().trim());
				
				var url = $("#actionroot").val() + "/add_question/create";
				ajaxCall(url,{
					method: "post",
					progress: true,
					form: "qaForm",
					button: "submit_btn",
					dataType: "script",
					success: function() {
						//ccms.dialog.notice("保存成功",500);
						alert("保存成功");
						window.location.reload();
					}
				});
		 };
		 this.initTagConfig = function(){
			$("#selectList").on("select2-selecting", function(e) {
				var data=e.object;
				var name=data.name;
				name=replaceSpecialChar(name);
				if(obthis.isExistTagInputValue(name)){
					return;
				}
				var html='<li data-id="'+name+'" >';
				html=html+'<div class=tag>';
				if(data.iconUrl!='' && data.iconUrl!=null){
					var imageUrl=contextPath+'/images/qa/'+data.iconUrl;
					html=html+'<img src='+imageUrl+'  style="width:16px;height:16px;"  />';
				}
				html=html+name;
				html=html+'</div>';
				html=html+'<i class="glyphicon glyphicon-remove close-tag-list" style="cursor: pointer;"></i>';
				html=html+'</li>';
				$('#tagList').append(html);
				obthis.validateTagList();
				$('.close-tag-list').unbind().on("click",function(){
					obthis.delTagList(this);
					obthis.removeSelectElement('selectList');
				});
			});
			$('.close-tag-list').unbind().on("click",function(){
				obthis.delTagList(this);
			});
			$('#s2id_autogen1').keyup(function(e){
				if(e.keyCode==13){
					var name=$(this).val();
					name=replaceSpecialChar(name);
					if(obthis.isExistTagInputValue(name)){
						return;
					}
					var html='<li data-id="'+name+'" >';
					html=html+'<div class=tag>';
					html=html+name;
					html=html+'</div>';
					html=html+'<i class="glyphicon glyphicon-remove close-tag-list" style="cursor: pointer;"></i>';
					html=html+'</li>';
					$('#tagList').append(html);
					$('.select2-results').empty();
					obthis.validateTagList();
					$('.close-tag-list').unbind().on("click",function(){
						obthis.delTagList(this);
						obthis.removeSelectElement('selectList');
					});
					$(this).val('');
					$('#select2-drop').hide();
				}
			});
		};
			
		this.validateTagList = function(){//验证标签数
			if($('#tagList li').length>=5){
				$("#selectList").select2("enable", false); 
				$('#addtag_btn').attr('disabled', true);
				validateFlag[2]=false;
			}else if($('#tagList li').length>0){
				$("#selectList").select2("enable", true); 
				$('#addtag_btn').attr('disabled', false);
				validateFlag[2]=true;
			}else{
				$("#selectList").select2("enable", true); 
				$('#addtag_btn').attr('disabled', false);
				validateFlag[2]=false;
			}
		};
			
		this.isExistTagInputValue = function(term){//设置标签input的值
			var flag=false;
			if(term==''){
				flag=true;
			}
			$('#tagList li').each(function(){
				var name=$(this).data('id');
				if(term==name){
					flag=true;
				}
			});
			return flag;
		};
		this.setTagInputValue = function(){//设置标签input的值
			var strs='';
			$('#tagList li').each(function(){
				var name=$(this).data('id');
				strs=strs+name+',';
			});
			if($('#tagList li').length>0){
				strs=strs.substring(0,strs.length-1);
			}
			$('#tags_input').val(strs);
		};
		this.delTagList = function(ob){//删除标签
			$(ob).parent().remove();
			obthis.validateTagList();
		};
		this.clearselect = function(){
			$("#selectList").select2("container").find('.select2-search-choice-close').hide();
		};
	

		/**
		 * 删除下拉元素
		 */
		this.removeSelectElement = function(ob){
			var id='s2id_'+ob;
			var obc=$('#'+id).find('.select2-choices');
			$(obc).find('.select2-search-choice').find('.select2-search-choice-close').each(function(){
				$(this).click();
			});
		};
		
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["add_question"] = $Q;
})();

$(document).ready(function() {
	ccms.add_question();
});
