INSERT INTO
hr_skill
(
    skill_id
    ,skill_name
    ,tenantry_id
    ,skill_scope
    ,remark
    ,is_default
)
VALUES
(
      ${seq:nextval@seq_hr_skill}
    , ${fld:skill_name}
    , ${def:tenantry}
    , ${fld:skill_scope}
    , ${fld:remark}
    , ${fld:is_default}
)
