(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		var $tagListArray=[];
		var validateFlag=[false,false,false];
		
		this.show = function(id) {
			var obthis=this;
			var $ob = $("#" + id);
			$($ob).keyup(function(e){
				 e=e||event;
				var which=e.which;
				if(which==38){
					obthis.tagup();
					return;
				}else  if(which==40){
					obthis.tagdown();
					return;
				}
				if($.trim($($ob).val())==''){
					return;
				}
				obthis.init();
				obthis.reloadLoaction();
				if($.trim($(this).val())!=''  && $.trim($(this).val()).length>0){
					obthis.term=$.trim($(this).val());
					obthis.searchTagList($.trim($(this).val()));
				}
			});
			$($ob).keypress(function(e) {
				 e=e||event;
				 if(e.keyCode==13){
					 obthis.enterPress();
					 return false;
				 }
			});
			
			$("button.addtagbtn").click(function(){
				obthis.addTagListButton();
			});
			$(".show-pop-tag .tag-select").click(function(){
				obthis.showtagselect();
			});
			$(document).click(function(e){
				var target = e.target ;
				if(e.target.tagName!=undefined){
					  	var tag = e.target.tagName.toUpperCase();
					    if(tag!= 'INPUT' && $(target).attr("id")!='showTagDialog'  && $(target).parent().attr("id")!='showTagDialog' && $(target).parent().parent().attr("id")!='showTagDialog' 
					    	&& $(target).parent().parent().parent().attr("id")!='showTagDialog' && $(target).parent().parent().parent().parent().attr("id")!='showTagDialog' ){
					    	 $("#showTagDialog").remove();//删除键盘
					    }
				}
			});
			this.init=function(){
				var $div = $('<ul id="showTagDialog" class="showTagDialog list-unstyled" style="position:absolute;left:0px;top:0px;min-width:240px;z-index:2000;"><ul>');
				if ($("#showTagDialog").length == 0) {
					$(document.body).append($div);
					$(window).resize(function(){    //改变大小            
						var y = ccms.util.absPos($($ob)[0]).y;
						var x = ccms.util.absPos($($ob)[0]).x;
						$(obthis.$tob).css("top",y+bhei);
						$(obthis.$tob).css("left",x);
					 });
				}
				obthis.$tob=$("#showTagDialog");
			},
			this.reloadLoaction=function(){
				var y = ccms.util.absPos($($ob)[0]).y;
				var x = ccms.util.absPos($($ob)[0]).x;
				var bhei=$($ob).outerHeight();
				var wid=$($ob).outerWidth();
				$(obthis.$tob).css("top",y+bhei);
				$(obthis.$tob).css("left",x);
				$(obthis.$tob).css("width",wid);
			},
			this.searchTagList=function(value){
				if($tagListArray.length>0){
					obthis.callbackResult($tagListArray);
					return;
				}
				var pars='term='+value;
				var url=contextPath + "/action/ccms/qa/web/add_question/search/tags/page";
				$.ajax({
					type : "post",
					url : url,
					data : pars,
					dataType : "json",
					success : function(data) {
						$tagListArray=data.result;
						obthis.callbackResult(data.result);
					},
					error : function() {
						//alert('加载失败');
					}
				});
			},this.callbackResult=function(result){
				if(result==undefined){
					return;
				}
				var  term=$.trim($($ob).val());
				var array=[];
				var showArray=[];
				this.tagMap={};
				for(var i=0;i<result.length;i++){
					var obj=result[i];
					var name=obthis.formatSelection(obj);
					
					if(name==undefined) continue;
					
					var flag=obthis.matcher(term, name);
					if(flag){
						var html=obthis.formatResult(obj);
						showArray.push(name);
						this.tagMap[name]=obj;
						array.push(html);
					}
				}
				obthis.showTagList(array,showArray);
				obthis.listenResult();
			},this.matcher=function(term, text) {
				return text.toUpperCase().indexOf(term.toUpperCase()) == 0;
			},this.formatSelection=function(data){
				return data.name;
			},this.formatResult=function(data){
				var html = '';
				if (data.iconUrl != '' && data.iconUrl != null) {
					html = '<img src="' + contextPath + '/images/qa/' + data.iconUrl
							+ '" style="height:25px;width:25px;"/>';
				}
				html = html + data.name;
				return html;
			},this.showTagList=function(data,data2){
				$(obthis.$tob).empty();
				for(var i=0;i<data.length;i++){
					var $hob=$('<li class="tagli" data-name="'+data2[i]+'">'+data[i]+'</li>');
					if(i==0){
						$($hob).addClass("current");
					}
					$(obthis.$tob).append($hob);
				}
				var $dob=$('<li>添加标签&nbsp;&nbsp;<strong>'+obthis.term+'</strong></li>');
				if(data.length==0){
					$($dob).addClass("current");
				}
				$(obthis.$tob).append($dob);
				
			},this.listenResult=function(){
				$(".showTagDialog li.tagli").click(function(){
					var tagname=$(this).data("name");
					obthis.showAddedTagList(tagname);
					$($ob).val('');
					obthis.close();
				});
			},this.close=function(){
				 $("#showTagDialog").remove();//删除
			},this.showAddedTagList=function(name){
				var $listob=$(".showtagList");
				name=obthis.replaceSpecialChar(name);
				if(obthis.isExistTagInputValue(name)){
					return;
				}
				var html='<li data-id="'+name+'" >';
				html=html+'<div class=tag>';
				html=html+name;
				html=html+'</div>';
				html=html+'<i class="glyphicon glyphicon-remove close-tag-list" style="cursor: pointer;"></i>';
				html=html+'</li>';
				$($listob).append(html);
				obthis.validateTagList();
				obthis.initCloseTag();
				obthis.reloadLoaction();
			},this.initCloseTag=function(){
				$('.close-tag-list').click(function(){
					obthis.delTagList(this);
				});
			},this.isExistTagInputValue=function(term){//设置标签input的值
				var flag=false;
				if(term==''){
					flag=true;
				}
				$('.showtagList li').each(function(){
					var name=$(this).data('id');
					if(term==name){
						flag=true;
					}
				});
				return flag;
			},this.delTagList=function(ob){
				$(ob).parent().remove();
				obthis.validateTagList();
				obthis.reloadLoaction();
			},this.validateTagList=function(){//验证标签数
				if($('.showtagList li').length>=5){
					$($ob).attr("disabled", true); 
					$('.addtagbtn').attr('disabled', true);
					validateFlag[2]=false;
				}else if($('.showtagList li').length>0){
					$($ob).attr("disabled", false); 
					$('.addtagbtn').attr('disabled', false);
					validateFlag[2]=true;
				}else{
					$($ob).attr("disabled", false); 
					$('.addtagbtn').attr('disabled', false);
					validateFlag[2]=false;
				}
			},this.addTagListButton=function(){
				obthis.close();
				if($.trim($($ob).val())!=''  && $.trim($($ob).val()).length>0){
					obthis.showAddedTagList($.trim($($ob).val()));
					$($ob).val('');
				}
			},this.enterPress=function(){
					if($.trim($($ob).val())==''){
						return;
					}
					var tagname='';
					if($(".tagli.current").length>0){
						tagname=$(".tagli.current").data("name");
					}else{
						tagname=$.trim($($ob).val());
					}
					obthis.showAddedTagList(tagname);
					$($ob).val('');
					obthis.close();
			},this.tagup=function(){
				var ob=$(".tagli.current");
				if($(ob).prev(".tagli").length>0){
					$(ob).removeClass("current");
					$(ob).prev(".tagli").addClass("current");
				}
			},this.tagdown=function(){
				var ob=$(".tagli.current");
				if($(ob).next(".tagli").length>0){
					$(ob).removeClass("current");
					$(ob).next(".tagli").addClass("current");
				}
			},this.showtagselect=function(){
				var url=contextPath + "/action/ccms/qa/web/add_question/search/tags/hot";
				$.ajax({
					type : "get",
					url : url,
					dataType : "json",
					success : function(data) {
						obthis.hotTagsResult(data.result);
					},
					error : function() {
						ccms.dialog.alert("加载失败！");
					}
				});
			},this.hotTagsResult=function(data){
				if(data==undefined){
					return;
				}
				obthis.init();
				obthis.reloadLoaction();
				$(obthis.$tob).empty();
				for(var i=0;i<data.length;i++){
					var obj=data[i];
					var name=obj.name;
					var $hob=$('<li class="taglis" data-name='+name+'>'+name+'</li>');
					$(obthis.$tob).append($hob);
				}
				obthis.initTagSelect();
			},this.initTagSelect=function(){
				$(".showTagDialog li.taglis").click(function(){
					var tagname=$(this).data("name");
					obthis.showAddedTagList(tagname);
					$($ob).val('');
					obthis.close();
				});
			},this.replaceSpecialChar=function(str){//替换特殊字符
				str=str+'';
				str=str.replaceAll('/','-');
				str=str.replaceAll('&','-');
				str=str.replaceAll('*','-');
				return str;
			},this.close=function(){
				 $("#showTagDialog").remove();//删除
			}
		};
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["qaTag"] = $Q;
})();

$(document).ready(function() {
	ccms.qaTag();
});
