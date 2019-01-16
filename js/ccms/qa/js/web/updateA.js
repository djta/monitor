(function() {
	var $Q = function() {
		return new $Q.fn.init();
	}, $Q_init = null;
	$Q.fn = $Q.prototype;
	$Q_init = $Q.fn.init = function() {
		var obthis = this;
		CKEDITOR.replace('answerCk');
		CKEDITOR.instances["answerCk"].on("instanceReady", function () {  
			this.document.on("click", function(){
				if('<p>请写入您的答案</p>' == CKEDITOR.instances.answerCk.getData().trim() || CKEDITOR.instances.answerCk.getData().trim()==""){
					CKEDITOR.instances.answerCk.setData("");
					return;
				}
			});  
		});
		$('#submit_btn').click(function() {
			if('<p>请写入您的答案</p>' == CKEDITOR.instances.answerCk.getData().trim() || CKEDITOR.instances.answerCk.getData().trim()==""){
				$("#answerHelp").show();
				return;
			}
			$('#submit_btn').attr('disabled', true);
			$("#answerCk").val(CKEDITOR.instances.answerCk.getData().trim());
			$('#submit_btn').html("<i class='icon-spinner icon-spin'/> 保存中");
			obthis.submitForm();
		});
		
		 this.submitForm = function(){
			 $("#answerCk").val(CKEDITOR.instances.answerCk.getData().trim());
			 
				var url = $("#actionroot").val() + "/update_answer/update";
				ajaxCall(url,{
					method: "post",
					progress: true,
					form: "qaForm",
					button: "submit_btn",
					dataType: "script",
					success: function() {
						alert("保存成功");
						gotoPage("/action/ccms/qa/web/q_and_a?qid="+$('#qid').val()+"&random="+Math.random());
						//window.location.reload();
					}
				});
		 };
		
	};
	$Q_init.prototype = $Q.fn;
	window["ccms"]["updateA"] = $Q;
})();

$(document).ready(function() {
	ccms.updateA();
});

