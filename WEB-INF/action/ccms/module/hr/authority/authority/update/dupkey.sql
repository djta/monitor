select authority_name from hr_authority
where authority_name = ${fld:authority_name}
    and tuid <> ${fld:tuid}
