INSERT INTO
hr_team
(
    team_id
    ,team_name
    ,leader_id
    ,remark
    ,tenantry_id
)
VALUES
(
      ${seq:nextval@seq_hr_team}
    , ${fld:team_name}
    , ${fld:leader_id}
    , ${fld:remark}
    , ${def:tenantry}
)
