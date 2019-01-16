select group_name from hr_authority_group
where group_name = ${fld:group_name}
    and tuid <> ${fld:tuid}
