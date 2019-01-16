insert into hr_team
(
    team_id,
    team_name,--zuming
    
    leader_id,
    skill_scope,
    data_limit,
    remark,
    
    org_id
)
values 
(
	${seq:nextval@seq_hr_team},
    ${fld:team_name},
     ${fld:leader_id},
      ${fld:skill_scope},
       ${fld:data_limit},
         ${fld:remark},
    '${def:org}'
)
