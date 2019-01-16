insert
into
os_entry_comment
(
tuid,
entry_id,
node_id,
history_id,
comments,
created,
createdby
)
values
(
${seq:nextval@seq_os_entry_comment},
${fld:__wfentry_id__},
${fld:__current_step_id__},
(select id from os_currentstep where entry_id=${fld:__wfentry_id__} and step_id=${fld:__current_step_id__} and owner='${def:user}'),
${fld:_entry_comment_},
{ts '${def:timestamp}'},
'${def:user}'
)