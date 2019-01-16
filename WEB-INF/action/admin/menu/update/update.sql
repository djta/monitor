update ${schema}s_menu set 

	app_id = ${fld:app_id},
	title = ${fld:title_cn},
	title_cn = ${fld:title_cn},
	title_en = ${fld:title_en},
	position = ${fld:position},
	service_id = ${fld:service_id},
	parentmenu_id = ${fld:parentmenu_id},
	logo_path = ${fld:logo_path}

where
	menu_id = ${fld:tuid}
