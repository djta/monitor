delete from hr_org_info where org_id = ${fld:id};
update hr_org set is_deleted = '1' where org_id = ${fld:id}
