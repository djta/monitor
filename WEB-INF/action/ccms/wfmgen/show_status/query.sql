select
	w.xml_release as xml_value
from 
	os_wfm w
	inner join os_wfentry e
	on w.tuid = e.wfm_id
where
	e.id = ${fld:entry_id}