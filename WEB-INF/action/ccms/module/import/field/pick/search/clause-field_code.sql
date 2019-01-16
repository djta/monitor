and
t.field_code like concat('%',case when ${fld:field_code} is null then '' else ${fld:field_code} end,'%')