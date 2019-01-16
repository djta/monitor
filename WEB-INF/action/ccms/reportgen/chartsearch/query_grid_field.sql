SELECT
    case 
       when case when fi.domain_namespace is null then ' ' else fi.domain_namespace end  <> ' ' 
         then	concat(
			concat(
				concat(
					concat(
						concat('(select max(domain.domain_text_${def:locale}) from t_domain domain where domain.namespace= ''',fi.domain_namespace)
						,''' and domain.domain_value = ' 
						)
					,(case when (case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end) ='0' then concat(t.table_code,'.') else '' end)
				)
				,fi.field_code
			)
			,' )')

       when case when fi.fk_tab is null then ' ' else fi.fk_tab end <> ' '
         then	concat(	
			concat(
				concat(
					concat(
						concat(
							concat('(select max(', (case when fi.fk_references is not null and '${def:locale}'='en' then fi.fk_references else fi.fk_fld_anchor end))
								,') from '
						)
						,concat(concat( case when fk_sql is null then concat(concat(fi.fk_schema,'.'),fi.fk_tab) else fk_sql end ,' a where a.' ),fi.fk_fld_id)
					)
					,' = '
				),(case when (case when fi.is_virtual_type is null then '0' else fi.is_virtual_type end) ='0' then concat(t.table_code,'.') else '' end)
			),concat(fi.field_code,')'))
		
	when ff.cal_type is not null
    	  then ( 
    				case when ff.cal_type='sum' then concat(concat(concat(ff.cal_type , '(') ,lower(fi.field_code)) ,') ') 
    							when ff.cal_type='avg' then concat(concat(concat(ff.cal_type , '(') ,lower(fi.field_code)) ,') ') 
    							when ff.cal_type='max' then concat(concat(concat(ff.cal_type , '(' ),lower(fi.field_code)) ,') ') 
    							when ff.cal_type='min' then concat(concat(concat(ff.cal_type , '(') ,lower(fi.field_code)) ,') ') 
    							when ff.cal_type='count' then concat(concat(concat(ff.cal_type , '( distinct ') ,lower(fi.field_code)) ,')  ') 
    				end 
    	)
	else lower(fi.field_code)
        end as field_code
	,lower(fi.field_code) as field_code_rawdata
	,lower(fi.field_code_alias) as colname
	,concat(lower(fi.field_code_alias),'_rawdata') as colname_rawdata
	,concat(concat(lower('${FLD:'),lower(fi.field_code_alias)),'}')	as  colname_mark
	,fi.field_name_${def:locale}  as  field_name
	,ff.is_group_by
	,fi.field_type
	,ff.format
FROM
	t_report f
	inner join t_report_chart ff
	on ff.report_id = f.tuid
	inner join t_field fi
	on ff.field_id = fi.tuid
	inner join t_table t
	on fi.table_id = t.tuid
WHERE
    f.tuid = ${fld:report_id}

order by 
    ff.show_order