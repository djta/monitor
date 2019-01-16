select
    tuid
    ,tab_id
    ,rule_name
    ,rule_order
    ,remark
    ,version
    ,case filter_type when '0' then '除重'  when '1' then 'EXCEL数据校验' when '2' then '入库数据校验' end as  filter_type
from
	 t_import_rule
where
	tab_id=${fld:tab_id1} 
	${filter}
	${orderby}
