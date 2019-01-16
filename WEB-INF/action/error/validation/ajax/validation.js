alert("${lbl:error_validation_ajax}");
<rows>
setFormErrorMsg("${fld:id}", "${fld:message_ajax@js}");
</rows>
//校验失败后的回调函数
if(typeof(afterValidationSetting)!="undefined" && afterValidationSetting!=null){
	afterValidationSetting();
}