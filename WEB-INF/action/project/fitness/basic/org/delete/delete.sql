delete from hr_org_info where org_id in(select org_id from hr_org where position('${fld:id}'  in org_path)>0);

delete from hr_org where position('${fld:id}'  in org_path)>0;