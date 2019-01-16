and
tab_name like concat('%',case when ${fld:tab_name} is null then '' else ${fld:tab_name} end,'%')