select
tuid
,tab_name
,imp_id
,(select imp_name from t_import where tuid=imp_id) as imp_name
,(select a.table_name from t_table a where a.tuid=t_import_table.table_id) as table_name
,(select a.tab_name from t_import_table a where a.tuid=t_import_table.parent_id) as parent_name
,bpk_field_alias
,case when if_new_flag='1' then '是'
			else '否' end as if_new_flag
,remark
,version
,created
,(select name from hr_staff where userlogin=t_import_table.createdby) as createdby
,updated
,(select name from hr_staff where userlogin=t_import_table.updatedby) as updatedby
from
t_import_table
where
imp_id=${fld:imp_id_filter}
${filter}
${orderby}