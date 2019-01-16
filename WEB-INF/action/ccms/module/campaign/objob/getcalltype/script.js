var obj = document.getElementById("call_type");
obj.options.length = 0;
var option = new Option("任意");
	option.value = "";
obj.options.add(option);
<rows>
	option = new Option("${fld:call_type_name@js}");
	option.value = "${fld:call_type@js}";
	obj.options.add(option);
</rows>
