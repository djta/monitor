select 
 	concat('
	  <label class="am-checkbox">
	  <input type="checkbox"  data-am-ucheck name="datalist" 
	   value="',tuid,'','" >
	   </label>
	') as application_id,
	bannername,
	linkurl,
	attachid,
	showorder,
	created,
	createdby
from hr_org_banner 
where org_id = COALESCE(${fld:org_id}, ${def:org}) and status = 1 
and bannertype = 10 
	
	${filter}
	${orderby}