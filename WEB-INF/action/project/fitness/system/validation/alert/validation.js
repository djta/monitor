
ccms.dialog.notice("${fld:message@js}");

//校验失败后的回调函数
if(typeof(afterValidationSetting)!="undefined" && afterValidationSetting!=null){
	afterValidationSetting();
}