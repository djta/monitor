and
    (
        t.show_name like concat(concat('%',${fld:show_name}),'%')
        or
        t.lable like concat(concat('%',${fld:show_name}),'%')
    )
