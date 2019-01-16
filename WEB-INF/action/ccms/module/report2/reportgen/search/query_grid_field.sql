SELECT
    case when ff.cal_type is not null
    		then ( 
    				case when ff.cal_type='sum' then concat(ff.cal_type , '(' ,lower(fi.field_code) ,') ') 
    							when ff.cal_type='avg' then concat(ff.cal_type , '(' ,lower(fi.field_code) ,') ') 
    							when ff.cal_type='max' then concat(ff.cal_type , '(' ,lower(fi.field_code) ,') ') 
    							when ff.cal_type='min' then concat(ff.cal_type , '(' ,lower(fi.field_code) ,') ') 
    							when ff.cal_type='count' then concat(ff.cal_type , '(' ,lower(fi.field_code) ,')  ') 
    				end 
    					)
         when nvl(fi.domain_namespace,'') <> '' 
         then concat('(select domain.domain_text_${def:locale} from t_domain domain where domain.namespace= ''',fi.domain_namespace,''' and domain.domain_value = ' 
			,(case when nvl(fi.is_virtual_type,'0')='0' then concat(t.table_code,'.') else '' end),fi.field_code,' limit 1)')

         when nvl(fi.fk_tab,'') <> ''
         then concat('(select max(',
			case when fi.fk_references is not null and '${def:locale}'='en' then fi.fk_references else fi.fk_fld_anchor end
			,') from ',fi.fk_schema,'.',fi.fk_tab,' a where a.' ,fi.fk_fld_id
			,' = ',(case when nvl(fi.is_virtual_type,'0')='0' then concat(t.table_code,'.') else '' end),fi.field_code,')')
		else lower(fi.field_code)
    end 	as	field_code
  ,	lower(fi.field_code_alias) as colname
    ,concat(lower('${FLD:'),lower(fi.field_code_alias),'}')	as  colname_mark
    ,fi.field_name_${def:locale}  as  field_name
FROM
	t_report f
	left join t_report_show_field ff
	on ff.report_id = f.tuid
	left join t_field fi
	on ff.field_id = fi.tuid
	inner join t_table t
	on fi.table_id = t.tuid
WHERE
    f.tuid = ${fld:report_id}
AND
	(ff.is_row_head = '1' or ff.is_col_head = '1'  or ff.is_cross_value = '1') --选中了行头或列头或值
order by 
    ff.show_order