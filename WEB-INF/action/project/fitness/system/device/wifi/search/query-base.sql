select 
 	concat('
	  <label class="am-checkbox">
	  <input type="checkbox"  name="datalist" 
	   value="',tuid,'','" >
	   </label>
	') as application_id,
	*

from cc_device m
where 
is_deleted is null
and
p_type='1' 
${filter}
