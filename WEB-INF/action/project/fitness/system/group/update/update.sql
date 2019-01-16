update hr_team set
  team_name= ${fld:team_name},
  skill_scope= ${fld:skill_scope},
  leader_id= ${fld:leader_id},
  data_limit= ${fld:data_limit},
  remark= ${fld:remark}
where
	team_id = ${fld:team_id}
