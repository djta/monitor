SELECT
	t.tuid,
	CASE
	WHEN t.entity_type = '0'
	OR t.entity_type = '1'
	OR t.entity_type = '2'
	OR t.entity_type = '3'
	OR t.entity_type = '4'
	OR t.entity_type = '5'
	OR t.entity_type = '6'
	OR t.entity_type = '7' THEN
		concat (
			concat (
				concat (
					concat (
						'<button type="button" code="',
						t.tuid
					),
					'" code1="'
				),
				t.entity_type
			),
			'" class="btn btn-primary btn-md setMembers">设置成员</button>'
		)
	ELSE
		NULL
	END AS szcy,
	 ha.authority_name as authority_name,
	 t.entity_name,
	 t.entity_type,
	 t.remark,
	 t.created,
	 t.createdby,
	 t.updated,
	 t.updatedby,
	 case when t.entity_type = '0' then '个人' 
		when t.entity_type='1' then '岗位'
		when t.entity_type='2' then '岗位类型'
		when t.entity_type='3' then '部门'
		when t.entity_type='4' then '单位'
		when t.entity_type='5' then '组织层级'
		when t.entity_type='6' then '技能'
		when t.entity_type='7' then '团组'
		when t.entity_type='8' then '本人'
		when t.entity_type='9' then '本岗位'
		when t.entity_type='10' then '本部门'
		when t.entity_type='11' then '下级部门'
		when t.entity_type='12' then '上级部门'
		when t.entity_type='13' then '本单位'
		when t.entity_type='13' then '上级单位'
		else null end 
		as entity_type_alias
FROM
	hr_authority_entity t
	LEFT JOIN (
		SELECT
			tuid,
			authority_name
		FROM
			hr_authority
	) ha ON ha.tuid = t.authority_id
WHERE
	1 = 1
	
${filter}
${orderby}
