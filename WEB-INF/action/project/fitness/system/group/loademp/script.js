var str='<option value="">请选择</option>'
var str2=""
var index=0;
<rows>
str+='<option value="${fld:user_id}">${fld:name}</option>';
	str2+='<div style="width:120px;float:left"><input  code="${fld:name}" type="checkbox"  class=" emplist" name="emplist"  value="${fld:user_id}" /><span>${fld:name}</span></div>'
</rows>
$('#leader_id').html(str);
$('#leader_id').selectpicker("refresh");
$('#leader_id').selectpicker("render");
$('#emplist').html(str2);

ccms.util.renderCheckbox("emplist");


$('.emplist').each(function(){
	$(this).on("ifChecked",function(){
		$('#num').text($('input[name=emplist]:checked').length)
	})
	$(this).on("ifUnchecked",function(){
		$('#num').text($('input[name=emplist]:checked').length)
	})
})