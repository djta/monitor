INSERT	INTO
HR_TEAM_STAFF
(
	tuid
  	,team_id
  	,staff_id
)
VALUES
(
	${seq:nextval@${schema}seq_default}
  	,${fld:team_id}
  	,${fld:userlogin_id}
)
