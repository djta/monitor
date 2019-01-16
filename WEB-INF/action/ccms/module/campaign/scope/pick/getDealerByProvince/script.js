alert(document.getElementById("dealerListSpan"))
var obj = document.getElementById("dealerListSpan");
if(obj){
	var innerHTML = "";
	<rows>
		innerHTML = innerHTML+'<input type="checkbox" name="dealer_code" value="${fld:dealer_code}">${fld:dealer_name}</input><br>';
	</rows>
	obj.innerHTML = innerHTML;
}	