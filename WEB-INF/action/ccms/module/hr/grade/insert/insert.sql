INSERT INTO
hr_org_grade
(
    tuid
    ,grade_code
    ,grade_name
    ,tenantry_id,
    createdby,
    created
)
VALUES
(
     ${seq:nextval@seq_hr_org_grade}
    ,${fld:grade_code}
    ,${fld:grade_name}
    ,${def:tenantry}
    ,'${def:user}'
    ,{ts '${def:timestamp}'}
)
