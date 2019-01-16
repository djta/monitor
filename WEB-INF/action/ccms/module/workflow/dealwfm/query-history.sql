SELECT
	h.userlogin,
	h.name,
	finish_date,
	f.node_name,
	p.id as history_id,
	c.comments
FROM
	os_historystep p 
	join hr_staff h  on p.owner=h.userlogin
	left join os_wfm_node f on p.step_id=f.tuid
	left join os_entry_comment  c on c.entry_id=p.entry_id and c.history_id=p.id
where
	p.entry_id = ${fld:__wfentry_id__}
order by 
	finish_date