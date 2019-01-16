ccms.dialog.notice("${req:dinamica.error.description@js}");
//校验失败后的回调函数
if(typeof(afterValidateError)!="undefined" && afterValidateError!=null){
	afterValidateError();
}