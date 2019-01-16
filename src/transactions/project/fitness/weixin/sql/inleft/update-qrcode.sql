UPDATE wx_qrcode 
SET 
	resultcode = ${resultcode},
	resultmsg = '${resultmsg}' 
WHERE tuid = ${fld:tuid}
