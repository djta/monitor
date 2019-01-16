
//先清空当前流程设计器
clearData();
<nrows>
	var node = {
				name:"${fld:node_name@js}",
				top:${fld:node_y},
				left:${fld:node_x},
				width:${fld:node_width},
				height:${fld:node_height},
				type:"task",
				node_id:"${fld:node_id}"
	};
	addNode("n${fld:node_id}",node);
</nrows>

<arows>
	var action = {
				name:"",
				from:"n${fld:node_id}",
				to:"n${fld:next_node}",
				type:"sl",
				marked:false,
				action_id:"${fld:tuid}"
	};
	addLine("a${fld:tuid}",action);
</arows>