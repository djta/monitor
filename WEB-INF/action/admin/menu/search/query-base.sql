select
	m1.menu_id
	, m1.app_id
	, m1.title
	, m1.position
	, case when m1.parentmenu_id is null then 0 else m1.parentmenu_id end as parentmenu_id
	, m2.title as parenttitle,
	'${fld:app_id}' as m_app_id
from 
	${schema}s_menu m1
	left join ${schema}s_menu m2
	on m2.app_id = m1.app_id
	and m2.menu_id = m1.parentmenu_id
 where 
	m1.app_id = ${fld:app_id}

      ${filter}
      ${orderby}