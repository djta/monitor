select 
	(case 
		when ${fld:type}='1' then guest_target
		when ${fld:type}='2' then follow_target
		when ${fld:type}='3' then prepare_target
		when ${fld:type}='4' then visit_target
		when ${fld:type}='5' then ordernum_target
		when ${fld:type}='6' then orderfee_target
		when ${fld:type}='7' then call_target
		when ${fld:type}='8' then call_mc_target
		when ${fld:type}='9' then call_pt_target
		when ${fld:type}='10' then test_target
		when ${fld:type}='11' then unpayclass_target
		when ${fld:type}='12' then allclass_target
		when ${fld:type}='13' then site_target
	end) as targetval
from cc_target_group 
inner join (select team_id from hr_team where skill_scope = ${fld:skill_scope}
and org_id = ${def:org}) team on team.team_id = pk_value
where target_type=0 and org_id = ${def:org} and
concat(target_year||'-'||target_month)=(select to_char(('${def:date}'::date- ('1 month')::interval), 'yyyy-MM'))
