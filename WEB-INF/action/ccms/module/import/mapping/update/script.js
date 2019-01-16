document.getElementById("divEditor").style.display='none';
document.forms["formEditor"].getElementsByTagName("span")[0].innerHTML = "新增数据映射";
document.forms["formEditor"].elements["deleteCommand"].style.display='none';
clearForm("formEditor");
search();
