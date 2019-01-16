select
	'c' || t.tuid as tuid,
	t.rule_name as show_name,
	'0' as superior_id,
	t.tuid::varchar as rule_id,
	'parent' as operator,
	'1' as show_order
from 
	t_import_rule t
where
	t.tab_id=${fld:tab_id}

union

select
	'j' || t.tuid as tuid,
	case t.is_node when '0' then
		(case when t.logic_type='and' then '同时满足条件的结果'
			when t.logic_type='or' then '任意满足其中一个条件的结果' 
			else t.clause_code end )
		when '1'  then
		(substr((
select field_name_cn from t_field a,t_import_table b where a.table_id = b.table_id and  a.field_code = t.clause_code  and b.tuid = ${fld:tab_id} limit 1)  || '&nbsp;' || 
			case when t.clause_filter='=' then '等于'
			     when t.clause_filter='like' then '包含'
			     when t.clause_filter='>' then '大于'
			     when t.clause_filter='>=' then '大于等于'
			     when t.clause_filter='<' then '小于'
			     when t.clause_filter='<=' then '小于等于'
			     when t.clause_filter='<>' then '不等于'
			     when t.clause_filter='is not null' then '非空'
			     when t.clause_filter='is null' then '为空'
			     when t.clause_filter='in' then '任意满足'
			     when t.clause_filter='not in' then '排除'
			     else '' end
				 || '&nbsp;' ||  nvl((case when nvl(t.phrase_name,'')='' then (select col_name from t_import_field where tuid = t.field_id) else nvl(t.phrase_name,'') end),''),0,50) ) 
		end as show_name,
	'c' || t.rule_id as superior_id,
	t.rule_id::varchar as rule_id,
	case when is_node='0' then 'clause-node' else 'clause-item' end as operator,
	'2'::varchar as show_order
from 
t_import_rule_filter t 
where
t.rule_id in (select tuid from t_import_rule where tab_id=${fld:tab_id})
and
t.parent_id is null

union

select
	'j' || t.tuid as tuid,
	case when t.logic_type='and' then '同时满足条件的结果'
		when t.logic_type='or' then '任意满足其中一个条件的结果' 
		else '' end as show_name,
	'j'||t.parent_id::varchar as superior_id,
	t.rule_id::varchar as rule_id,
	'clause-node' as operator,
	'4' as show_order
from 
t_import_rule_filter t
where
t.rule_id in (select tuid from t_import_rule where tab_id=${fld:tab_id})
and
t.parent_id is not null
and
t.is_node = '0'

union

select
	'j' || t.tuid as tuid,
	substr((
select field_name_cn from t_field a,t_import_table b where a.table_id = b.table_id and  a.field_code = t.clause_code  and b.tuid = ${fld:tab_id} limit 1) 
	|| '&nbsp;' || 
	case when t.clause_filter='=' then '等于'
	     when t.clause_filter='like' then '包含'
	     when t.clause_filter='>' then '大于'
	     when t.clause_filter='>=' then '大于等于'
	     when t.clause_filter='<' then '小于'
	     when t.clause_filter='<=' then '小于等于'
	     when t.clause_filter='<>' then '不等于'
	     when t.clause_filter='is not null' then '非空'
	     when t.clause_filter='is null' then '为空'
	     when t.clause_filter='in' then '任意满足'
	     when t.clause_filter='not in' then '排除'
	     else '' end
	|| '&nbsp;' || 
	nvl((case when nvl(t.phrase_name,'')='' then (select col_name from t_import_field where tuid = t.field_id) else nvl(t.phrase_name,'') end),''),0,50) as show_name,
	'j'||t.parent_id::varchar as superior_id,
	t.rule_id::varchar as rule_id,
	'clause-item' as operator,
	'5' as show_order
from 
t_import_rule_filter t
where
t.rule_id in (select tuid from t_import_rule where tab_id=${fld:tab_id}) 
and
t.parent_id is not null
and
t.is_node = '1'


order by 
	show_order,show_name
