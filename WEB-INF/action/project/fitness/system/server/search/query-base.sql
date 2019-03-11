select 
 	concat('
	  <label class="am-checkbox">
	  <input type="checkbox"  name="datalist" 
	   value="',tuid,'','" >
	   </label>
	') as application_id
  ,tuid
  ,ip
  ,type
	,port
	,username
	,password
  ,created
  ,createdby
  ,updated
  ,updatedby
from cc_server m
where 
is_deleted is null
${filter}
