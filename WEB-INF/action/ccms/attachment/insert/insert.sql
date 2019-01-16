insert into t_attachment_files
(
    tuid,
    file_name,
    file_type,
    file_path,
    file_size,
    description,
    created,
    createdby,
    table_code,
    pk_value,
    is_system
)
values
(
    ${seq:nextval@seq_t_attachment_files},
    ${fld:file.filerealname},
    ${fld:file.content-type},
    ${fld:file.path},
    ${fld:file_size},
    ${fld:file.filename},
    {ts '${def:timestamp}'},
    '${def:user}',
    ${fld:table_code},
    ${fld:pk_value},
    '0'
)