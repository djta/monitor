var obj = document.getElementById("campaign_id"); 

if(obj){
	obj.options.length = 0;
	var option = new Option("","");
	obj.options.add(option);
	<rows>	 
		option = new Option("${fld:campaign_id}","${fld:campaign_name}");
		obj.options.add(option);	 
	</rows>
 
}	
