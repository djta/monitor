var obj = document.formEditor.hc_id;
	if(typeof(obj) != "undefined"){
		obj.options.length = 0;
		obj.options.add(new Option("",""));
		<rows>
				var option = new Option("${fld:hc_name}","${fld:hc_id}");
				obj.options.add(option);
		</rows>
	}

