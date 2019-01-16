select 1 from cc_target_group
where org_id = ${def:org} 
and concat(target_year||'-'||target_month)=concat(${fld:target_year}||'-'||${fld:target_month})
and pk_value = ${fld:pk_value} and target_type=0
and tuid !=${fld:vc_code}