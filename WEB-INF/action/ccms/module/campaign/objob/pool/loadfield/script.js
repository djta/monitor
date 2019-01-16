var sttr="";
var a=0;
<rows1>
a++;
sttr+="<th nowrap>${fld:field_name}</th>";
</rows1>
$(".rows1").html(sttr);
$("#table-title").attr('colspan',a);

var str="";
<rows2>
str+="<td class='text-center'>${fld:colname_mark}</td>";
</rows2>
$(".rows2").html(str);
