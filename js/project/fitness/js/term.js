//前提条件  题目code必须唯一  同一个题下面的list code 必须唯一    如果是矩阵题 矩阵题的 code 必须唯一

function Term(id,name,contentDiv){
	this.id = id;
	this.name = name;
	this.contentDiv = contentDiv;
	this.parseFloatExtend = function(val){//字符串转小数
		if(val == ""){
			return 0;
		}
	    return Math.round((parseFloat(val).toFixed(3))*100)/100;
	};

	this.types = new Array();//保存类型
	this.addTypes = function(types){//添加类型
		this.types[this.types.length] = types;
	};
	
	this.items = new Array();//保存题目
	this.addItem = function(item){//添加题目
		this.items[this.items.length] = item;
	};
	this.getItemById = function(id){//根据id查找item
		for(var i=0;i<this.items.length;i++){
			if(this.items[i].id == id){
				return this.items[i];	
			}
		}
		return null;
	};
	this.getItemByCode = function(code){//根据code来查找item，找不到返回null
		for(var i=0;i<this.items.length;i++){
			if(this.items[i].code == code){
				return this.items[i];	
			}
		}
		return null;
	};
	this.getNowItem = function(){//根据轨迹的最后一个code来查找当前显示的item
		return this.getItemByCode(this.guiji[this.guiji.length-1]);
	};
	this.getNowItemIndex = function(){//根据轨迹的最后一个code来查找当前显示的item在数组中的下标
		var code = this.guiji[this.guiji.length-1];
		return this.getItemIndexByCode(code);
	};
	this.getItemIndexByCode = function(code){//根据code来查找item在数组中的下标
		for(var i=0;i<this.items.length;i++){
			if(this.items[i].code == code){
				return i;
			}
		}
		return -1;
	};

	this.skipRules = new Array();//保存入口跳跃逻辑
	this.outRules = new Array();//保存出口逻辑
	this.addSkipRule = function(rule){//添加入口逻辑
		this.skipRules[this.skipRules.length] = rule;
	};
	this.addOutRule = function(rule){//添加出口逻辑
		this.outRules[this.outRules.length] = rule;
	};
	this.getSkipItemRules = function(id){//通过 item_id 来获取入口逻辑数组
		var rules = new Array();
		for(var i=0;i<this.skipRules.length;i++){
			var rule = this.skipRules[i];
			if(rule.item_id == id){
				rules[rules.length] = rule;
			}
		}
		return rules;
	};
	this.getOutItemRules = function(id){//通过 item_id 来获取出口逻辑数组
		var rules = new Array();
		for(var i=0;i<this.outRules.length;i++){
			var rule = this.outRules[i];
			if(rule.item_id == id){
				rules[rules.length] = rule;
			}
		}
		return rules;
	};

	this.guiji = new Array();//保存题目跳转轨迹（存题目的code）
	this.addGuiji = function(code){//添加轨迹
		/*for(var i=0;i<this.guiji.length;i++){
			if(this.guiji[i] == code){
				return false;//配置有问题，轨迹不行重复
			}
		}*/
		this.guiji[this.guiji.length] = code;
		this.showOneItemByCode(code);
		return true;
	};
	this.deleteGuiji = function(){//删除当前轨迹并把上一个题显示出来
		if(this.guiji.length > 1){
			this.guiji.pop();
			this.showOneItemByCode(this.guiji[this.guiji.length-1]);
			return true;
		}else{
			return false;
		}
	};

	this.printHTML = function(callback){//全部输出出来
		var str = "";
		var beginIndex = 0;
		for(var i=0;i<this.types.length;i++){
			str += this.types[i].toHTML();
		}
		
		//右侧答题卡
		str += '<div class="daTiKa" id="daTiKa">';
		str += '<div class="dtkTitle">';
		str += '<center style="padding-top: 5px; margin-bottom: 5px;">';
		str += '题卡 <span class="expandAndclose"	style="cursor: pointer; display: block;" id="expandAndclose">X</span>';
		str += '</center>';
		str += '<div class="main1">';
		
		for(var i=0;i<this.types.length;i++){
			str += this.types[i].toDatikaHTML();
		}
		
		str += '<div class="daTiKaBottom">';
		str += '<div class="tiJiao">';
		str += '<button type="button" id="save_btn" style="display:none">保&nbsp;存</button>';
		str += '</div>';
		str += '<div class="backToList">';
		str += '</div>';
		str += '</div>';
		
		str += '</div>';
		str += '</div>';
		str += '</div>';
		
		$("#"+contentDiv).html(str);//开始往div里面插入数据
		
		for(var i=0;i<this.items.length;i++){
			if(this.items[i].is_page_break == true){
				var page = new Array();
				page[0] = beginIndex;
				page[1] = i;
				this._pageArray[this._pageArray.length] = page;
				beginIndex = (i+1);
			}
		}
		
		if(beginIndex <= (this.items.length-1)){
			var page = new Array();
			page[0] = beginIndex;
			page[1] = (this.items.length-1);
			this._pageArray[this._pageArray.length] = page;
		}

		//判断是分页显示题目，还是一题一题的显示
		if(this.skipRules.length == 0 && this.outRules.length == 0){
			for(var i=this._pageArray[0][0];i<=this._pageArray[0][1];i++){//把第一页显示出来
				var item = this.items[i];
				$("#"+item.code).show();
			}

			//显示控制翻页的按钮并且隐藏上一题和下一题按钮
			if(this._pageArray.length > 1){
				this._page = 0;
				$("#prePageBtn").show();
				$("#nextPageBtn").show();
			}
		}else{//一题题显示先把第一题显示出来
			if(this.items.length > 0){
				this.addGuiji(this.items[0].code);
				$("#preBtn").show();
				$("#nextBtn").show();
			}
		}
		
		if (callback) {
			callback();
		}

	};
	this.hiddenAll = function(){//隐藏全部题
		for(var i=0;i<this.items.length;i++){
			$("#"+this.items[i].code).hide();
		}
	};
	this.showOnlyOneItem = function(index){//根据下标只显示某个题
		this.showOneItemByCode(this.items[index].code);
	};
	this.showOneItemByCode = function(code){//根据code只显示某个题
		this.hiddenAll();
		$("#"+code).show();
	};

	this._page = 0;//当前第几页（从0开始）
	this._pageArray = new Array();//保存每页的起始页和结束页
}

//保存分类对象
function TermTypes(id,name,code){
	this.id = id;
	this.name = name;
	this.code = code;

	this.items = new Array();//保存题目
	this.addItem = function(item){//添加题目
		this.items[this.items.length] = item;
	};

	this.toHTML = function(){

		var str = '<div class="tiao">';
		str += '<div id="rcsh" class="ceshiType" ceshitypeid="rcsh" >';
		str += '<img src="' + contextPath + '/js/project/fitness/image/SVG/common/gou-yse.svg " class="shiTiType" code="' + code + '"/>';
		str += '<span>' + name + '</span>';
		str += '</div>';
		str += '<div class="yinchang' + code + '">';
		str += '<div>';
		for(var i=0;i<this.items.length;i++){
			str += this.items[i].toHTML();
		}
		str += '</div>';
		str += '</div>';
		str += '</div>';

		return str;
	}

	this.toDatikaHTML = function(){

		var str = '<div id="rcsh" class="ceshiTypeRight">';
		str += this.name ;
		str += '</div>';
		str += '<ul id="rcsh" class="pgTitleRight" ceshitype_child="rcsh">';
		
		for(var i=0;i<this.items.length;i++){
			str += this.items[i].toDatikaHTML();
		}

		str += '</ul>';

		return str;
	}
	
}

//保存题目对象
function TermItem(id,name,code,is_matrix,show_type,is_page_break,show_order,remark,is_matrix_transpose,is_list_mline,type_id){
	this.id = id;
	this.name = name;
	this.code = code;
	this.show_order = show_order;
	this.show_type = show_type;
	this.remark = remark;
	this.type_id = type_id;
	this.is_matrix = is_matrix=='1'?true:false;//表示是否是矩阵题
	this.is_page_break = is_page_break=='1'?true:false;//表示是否分页
	this.is_matrix_transpose = is_matrix_transpose=='1'?true:false;//表示矩阵题目是否转置
	this.is_list_mline = is_list_mline=='1'?true:false;//表示普通题目答案是否换行

	this.lists = new Array();
	this.matrixs = new Array();
	this.addList = function(list){//题目添加选择项
		this.lists[this.lists.length] = list;
	};
	this.addMatrix = function(matrix){//题目添加矩阵题目
		this.matrixs[this.matrixs.length] = matrix;
	};
	this.toHTML = function(){

		var str = '';
		
		str += '<div id="'+this.code+'" name="'+this.code+'" style="display:none;">';
		str += '<div id="rcsh" class="pgTitle" ceshitype_child="rcsh">';
		str += '<div class="title-lv"><div class="titleNum"><a  name="a'+this.type_id + '' + this.show_order +'">'+this.show_order+'</a></div><span>'+this.name+(this.remark!=""?("&nbsp;&nbsp;&nbsp;【备注】"+this.remark):"")+'</span></div>';

		if(this.is_matrix == true){//矩阵题
			str += "<table class='table table-bordered'>";
			if(this.is_matrix_transpose == true){//如果是转置的话
				str += "<tr><td></td>";
				for(var i=0;i<this.matrixs.length;i++){
					str += "<td align='center'>"+this.matrixs[i].item_name+"</td>";
				}
				str+= "</tr>";
				for(var j=0;j<this.lists.length;j++){
					str += "<tr><td align='left'>"+this.lists[j].list_name+"</td>";

					for(var n=0;n<this.matrixs.length;n++){
						str += "<td align='center'>"+this.lists[j].toMatrixHTML(this.matrixs[n].item_code,this.matrixs[n].show_type)+"</td>";
					}
					str += "</tr>";
				}
			}else{
				str += "<tr><td></td>";
				for(var i=0;i<this.lists.length;i++){
					str += "<td align='center'>"+this.lists[i].list_name+"</td>";
				}
				str+= "</tr>";
				for(var j=0;j<this.matrixs.length;j++){
					str += "<tr><td align='left'>"+this.matrixs[j].item_name+"</td>";

					for(var n=0;n<this.lists.length;n++){
						str += "<td align='center'>"+this.lists[n].toMatrixHTML(this.matrixs[j].item_code,this.matrixs[j].show_type)+"</td>";
					}
					str += "</tr>";
				}
			}
			str += '</table>';
		}else{//普通题
			//生成选列内容列表
			str += '<div class="xuanxiang">';
			for (var i=0;i<this.lists.length;i++){
				str += '<div class="everyXuanXiang">';

				str += this.lists[i].list_name;
				if(this.lists[i].remark!="" && this.lists[i].remark!=null){
					str = str + "【"+this.remark+"】";
				}

				str += '</div>';
			}
			str += '</div>';
			str += '<div class="xuanXiangBottom">';
			str += '<ul>';
			str += '<li class="xz">[选择]</li>';
			/*if(this.is_list_mline == true){//加上换行符
				str += "<br/>";
			}*/
			
			for (var i=0;i<this.lists.length;i++){
				str += '<li class="xxdm">';
				str += this.lists[i].toSelectionHTML();	/*显示代码为选择项*/
				str += '</li>';
				/*if(this.is_list_mline == true){//加上换行符
					str += "<br/>";
				}*/
			}
			str += '</ul>';
			str += '</div>';
		}
		//加一个框
		str += "</div></div>";
		return str;
	};
	
	//生成答题卡题目
	this.toDatikaHTML = function(){

		var str = '';
		str += '<li class="titleNumRight" style="width: 40px;">';
		str += '<a href=\'javascript:document.getElementById("' + this.code + '").scrollIntoView();\'  name="' + this.code + '" >';
		str += '<label style="border: 1px solid rgb(198, 198, 198);" code="' + this.code + '" id="' + this.code + '_title">' + this.show_order + '</label>';
		str += '</a>';
		str += '</li>';

		return str;
	};

	this.getListByCode = function(code){//根据code查找出list对象
		for(var i=0;i<this.lists.length;i++){
			if(this.lists[i].list_code == code){
				return this.lists[i];
			}
		}
		return null;
	};
	this.getMatrixByCode = function(code){//根据code查找出矩阵题对象
		for(var i=0;i<this.matrixs.length;i++){
			if(this.matrixs[i].item_code == code){
				return this.matrixs[i];
			}
		}
		return null;
	};
	this.getValueByCode = function(code,matrix_code){//根据code查找出list是否被选中
		if(this.is_matrix == true){
			var matrix = this.getMatrixByCode(matrix_code);
			if(matrix != null){
				if(matrix.show_type != "0"){//无法根据文本输入判断跳转
					var listObj = document.getElementById(matrix_code+"_"+code);
					if(listObj){
						if(listObj.checked == true){
							return true;
						}
					}
				}
			}
		}else{
			if(this.show_type != "0"){//无法根据文本输入判断跳转
				var listObj = document.getElementById(this.code+"_"+code);
				if(listObj){
					if(listObj.checked == true){
						return true;
					}
				}
			}
		}
		return false;
	};
	this.isHaveValue = function(){//判断当前题目是否已经选择或者输入值了
		var flag = false;//用于判断当前题目是否有值了
		if(this.is_matrix == true){//矩阵题
			flag = true;
			for(var j=0;j<this.matrixs.length;j++){
				var flag2 = false;
				var listObjs = document.getElementsByName(this.matrixs[j].item_code);
				if(listObjs){
					if(listObjs.length > 0){
						for(var i=0;i<listObjs.length;i++){
							if(this.show_type == "0"){
								if(listObjs[i].value != ""){//输入框不为空情况
									flag2 = true;
									break;
								}
							}else{
								if(listObjs[i].checked == true){
									flag2 = true;
									break;
								}
							}
						}
					}else{
						flag2 = true;
					}
				}
				if(flag2 == false){
					flag = false;
					break;
				}
			}
		}else{
			var listObjs = document.getElementsByName(this.code);
			if(listObjs){
				if(listObjs.length > 0){
					for(var i=0;i<listObjs.length;i++){
						if(this.show_type == "0"){
							if(listObjs[i].value != ""){//输入框不为空情况
								flag = true;
								break;
							}
						}else{
							if(listObjs[i].checked == true){
								flag = true;
								break;
							}
						}
					}
				}else{
					flag = true;
				}
			}
		}
		return flag;
	};
}

//保存题目选择项对象
function TermList(id,item_id,list_name,list_code,list_score,show_order,show_type,is_unspeak,remark,parent_type,item_code,namespace_op){
	this.id = id;
	this.item_id = item_id;
	this.item_code = item_code;
	this.list_name = list_name;
	this.list_code = list_code;
	this.list_score = list_score;
	this.show_order = show_order;
	this.show_type = show_type;
	this.is_unspeak = is_unspeak=="1"?true:false;
	this.remark = remark;
	this.parent_type = parent_type;
	this.namespace_op = namespace_op;

	this.toSelectionHTML = function(){//非矩阵题目时的选择项输出成HTML格式
		var str = "";
		str += '<label class="selection">';
		//列出答案条目
		switch(this.parent_type){
			case "0"://文本输入
				str += "<input type='text' style='color:black;' code='selection' name='"+this.item_code+"' size='20' id='"+this.item_code+"_"+this.list_code+"' placeholder='请输入文本'>";
				break;
			case "1"://多选一
				switch(this.show_type){
					case "0"://标签
						str += "<input type='radio' onclick='showExtLabel(\""+this.item_code+"_\",this);' code='selection' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_code+"</label>";
						break;
					case "1"://文本
						str += "<input type='text' style='color:black;' onclick='showExtLabel(\""+this.item_code+"_\",this);' code='selection' name='"+this.item_code+"_text' size='20' id='"+this.item_code+"_"+this.list_code+"_text'>";
						break;
					case "2"://标签+文本
						str += "<input type='radio' onclick='showExtLabel(\""+this.item_code+"_\",this);' code='selection' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_code+"</label>"
						+"&nbsp;&nbsp;<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'><input type='text' style='color:black;' name='"+this.item_code+"_text' size='20' id='"+this.item_code+"_"+this.list_code+"_text'> </span>";
						break;
					case "3"://标签+下拉框
						str += "<input type='radio' onclick='showExtLabel(\""+this.item_code+"_\",this);' code='selection' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_code+"</label>";
						if(this.droplist.length > 0){
							if(this.namespace_op == "1"){  //多选
								str += "&nbsp;&nbsp;<input type='hidden' name='"+this.item_code+"_"+this.list_code+"_select' id='"+this.item_code+"_"+this.list_code+"_select' />";
								str += "<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'>";
								for(var i=0;i<this.droplist.length;i++){
									str += "<input type='checkbox' onclick='setNamespaceVal(this);' id='"+this.item_code+"_"+this.list_code+"'  name='"+this.item_code+"_"+this.list_code+"_check' value='"+this.droplist[i].drop_value+"'/>"+"<label for='"+this.item_code+"_"+this.list_code+"_check'>"+this.droplist[i].drop_text+"</label>"+"";
								}
								str += "</span>";
							}else{	//单选
								str += "&nbsp;&nbsp;<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'> <select name='"+this.item_code+"_"+this.list_code+"_select' id='"+this.item_code+"_"+this.list_code+"_select'>";
								str += "<option value=''>请选择</option>";
								for(var i=0;i<this.droplist.length;i++){
									str += "<option value='"+this.droplist[i].drop_value+"'>"+this.droplist[i].drop_text+"</option>";
								}
								str += "</select> </span>";
							}
						}
						
						break;
				}
				break;
			case "2"://复选
				switch(this.show_type){
					case "0"://标签
						str += "<input type='checkbox' onclick='showExtLabel(\""+this.item_code+"_\",this);' code='selection' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_code+"</label>";
						break;
					case "1"://文本
						str += "<input type='text' style='color:black;' onclick='showExtLabel(\""+this.item_code+"_\",this);' code='selection' name='"+this.item_code+"_text' size='20' id='"+this.item_code+"_"+this.list_code+"_text' placeholder='请输入文本'>";
						break;
					case "2"://标签+文本
						str += "<input type='checkbox'  onclick='showExtLabel(\""+this.item_code+"_\",this);' code='selection' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_code+"</label>"
						+"&nbsp;&nbsp;<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'> <input type='text' style='color:black;' name='"+this.item_code+"_text' size='20' id='"+this.item_code+"_"+this.list_code+"_text'> </span>";
						break;
					case "3"://标签+下拉框
						str += "<input type='checkbox' onclick='showExtLabel(\""+this.item_code+"_\",this);' code='selection' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_code+"</label>";
						if(this.droplist.length > 0){
							if(this.namespace_op == "1"){  //多选
								str += "&nbsp;&nbsp;<input type='hidden' name='"+this.item_code+"_"+this.list_code+"_select' id='"+this.item_code+"_"+this.list_code+"_select'/>";
								str += "<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'>";
								for(var i=0;i<this.droplist.length;i++){
									str += "<input type='checkbox' onclick='setNamespaceVal(this);' id='"+this.item_code+"_"+this.list_code+"'  name='"+this.item_code+"_"+this.list_code+"_check' value='"+this.droplist[i].drop_value+"'/>"+"<label for='"+this.item_code+"_"+this.list_code+"_check'>"+this.droplist[i].drop_text+"</label>"+"";
								}
								str += "</span>";
							}else{	//单选
								str += "&nbsp;&nbsp;<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'> <select name='"+this.item_code+"_"+this.list_code+"_select' id='"+this.item_code+"_"+this.list_code+"_select'>";
								str += "<option value=''>请选择</option>";
								for(var i=0;i<this.droplist.length;i++){
									str += "<option value='"+this.droplist[i].drop_value+"'>"+this.droplist[i].drop_text+"</option>";
								}
								str += "</select> </span>";
							}
						}
						
						break;
				}
				break;
		}
		str += '</label>';

		return str;
	};

	this.toHTML = function(){//非矩阵题目时的选择项输出成HTML格式
		var str = "";
		//列出答案条目
		switch(this.parent_type){
			case "0"://文本输入
				str = this.list_name+"<input type='text' style='color:black;' name='"+this.item_code+"' size='20' id='"+this.item_code+"_"+this.list_code+"'>";
				if(this.remark!="" && this.remark!=null){
					str = str + "【"+this.remark+"】";
				}
				break;
			case "1"://多选一
				switch(this.show_type){
					case "0"://标签
						str = "<input type='radio' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_name+"</label>";
						if(this.remark!="" && this.remark!=null){
							str = str + "【"+this.remark+"】";
						}
						break;
					case "1"://文本
						str = "<input type='text' style='color:black;' name='"+this.item_code+"_text' size='20' id='"+this.item_code+"_"+this.list_code+"_text'>";
						if(this.remark!="" && this.remark!=null){
							str = str + "【"+this.remark+"】";
						}
						break;
					case "2"://标签+文本
						str = "<input type='radio' onclick='showExtLabel(this);' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_name+"</label>"
						+"&nbsp;&nbsp;<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'><input type='text' style='color:black;' name='"+this.item_code+"_text' size='20' id='"+this.item_code+"_"+this.list_code+"_text'></span>";
						if(this.remark!="" && this.remark!=null){
							str = str + "【"+this.remark+"】";
						}
						break;
					case "3"://标签+下拉框
						str += "<input type='radio' onclick='showExtLabel(this);' code='selection' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_code+"</label>";
						if(this.droplist.length > 0){
							if(this.namespace_op == "1"){  //多选
								str += "&nbsp;&nbsp;<input type='hidden' name='"+this.item_code+"_"+this.list_code+"_select' id='"+this.item_code+"_"+this.list_code+"_select' />";
								str += "<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'>";
								for(var i=0;i<this.droplist.length;i++){
									str += "<input type='checkbox' onclick='setNamespaceVal(this);' id='"+this.item_code+"_"+this.list_code+"'  name='"+this.item_code+"_"+this.list_code+"_check' value='"+this.droplist[i].drop_value+"'/>"+"<label for='"+this.item_code+"_"+this.list_code+"_check'>"+this.droplist[i].drop_text+"</label>"+"";
								}
								str += "</span>";
							}else{	//单选
								str += "&nbsp;&nbsp;<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'><select name='"+this.item_code+"_"+this.list_code+"_select' id='"+this.item_code+"_"+this.list_code+"_select'>";
								str += "<option value=''>请选择</option>";
								for(var i=0;i<this.droplist.length;i++){
									str += "<option value='"+this.droplist[i].drop_value+"'>"+this.droplist[i].drop_text+"</option>";
								}
								str += "</select></span>";
							}
						}
						
						break;
				}
				break;
			case "2"://复选
				switch(this.show_type){
					case "0"://标签
						str = "<input type='checkbox' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_name+"</label>";
						if(this.remark!="" && this.remark!=null){
							str = str + "【"+this.remark+"】";
						}
						break;
					case "1"://文本
						str = "<input type='text' style='color:black;' name='"+this.item_code+"_text' size='20' id='"+this.item_code+"_"+this.list_code+"_text'>";
						if(this.remark!="" && this.remark!=null){
							str = str + "【"+this.remark+"】";
						}
						break;
					case "2"://标签+文本
						str = "<input type='checkbox' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_name+"</label>"
						+"&nbsp;&nbsp;<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'> <input type='text' style='color:black;' name='"+this.item_code+"_text' size='20' id='"+this.item_code+"_"+this.list_code+"_text'></span>";
						if(this.remark!="" && this.remark!=null){
							str = str + "【"+this.remark+"】";
						}
						break;
					case "3"://标签+下拉框
						str += "<input type='checkbox' onclick='showExtLabel(this);' code='selection' name='"+this.item_code+"' value='"+this.list_score+"' id='"+this.item_code+"_"+this.list_code+"'>"
						+"<label for='"+this.item_code+"_"+this.list_code+"'>"+this.list_code+"</label>";
						if(this.droplist.length > 0){
							if(this.namespace_op == "1"){  //多选
								str += "&nbsp;&nbsp;<input type='hidden' name='"+this.item_code+"_"+this.list_code+"_select' id='"+this.item_code+"_"+this.list_code+"_select' />";
								str += "<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'>";
								for(var i=0;i<this.droplist.length;i++){
									str += "<input type='checkbox' onclick='setNamespaceVal(this);' id='"+this.item_code+"_"+this.list_code+"'  name='"+this.item_code+"_"+this.list_code+"_check' value='"+this.droplist[i].drop_value+"'/>"+"<label for='"+this.item_code+"_"+this.list_code+"_check'>"+this.droplist[i].drop_text+"</label>"+"";
								}
								str += "</span>";
							}else{	//单选
								str += "&nbsp;&nbsp;<span name='"+this.item_code+"_span' id='"+this.item_code+"_"+this.list_code+"_span' style='display:none'><select name='"+this.item_code+"_"+this.list_code+"_select' id='"+this.item_code+"_"+this.list_code+"_select'>";
								str += "<option value=''>请选择</option>";
								for(var i=0;i<this.droplist.length;i++){
									str += "<option value='"+this.droplist[i].drop_value+"'>"+this.droplist[i].drop_text+"</option>";
								}
								str += "</select></span>";
							}
						}
						
						break;
				}
				break;
		}
		return str;
	};

	this.toMatrixHTML = function(item_code,show_type){//矩阵题目时的选择项输出成HTML格式
		var str = "";
		switch(show_type){
			case "0"://文本输入
				str = "<input type='text' style='color:black;' name='"+item_code+"' size='20' id='"+item_code+"_"+this.list_code+"'>";
				break;
			case "1"://多选一
				switch(this.show_type){
					case "0"://标签
						str = "<input type='radio' name='"+item_code+"' value='"+this.list_score+"' id='"+item_code+"_"+this.list_code+"'>";
						break;
					case "1"://文本
						str = "";
						break;
					case "2"://标签+文本
						str = "<input type='radio' name='"+item_code+"' value='"+this.list_score+"' id='"+item_code+"_"+this.list_code+"'>&nbsp;&nbsp;<input type='text' style='color:black;' name='"+item_code+"_text' size='20' id='"+item_code+"_"+this.list_code+"_text'>";
						break;
				}
				break;
			case "2"://复选
				switch(this.show_type){
					case "0"://标签
						str = "<input type='checkbox' name='"+item_code+"' value='"+this.list_score+"' id='"+item_code+"_"+this.list_code+"'>";
						break;
					case "1"://文本
						str = "";
						break;
					case "2"://标签+文本
						str = "<input type='checkbox' name='"+item_code+"' value='"+this.list_score+"' id='"+item_code+"_"+this.list_code+"'>&nbsp;&nbsp;<input type='text' style='color:black;' name='"+item_code+"_text' size='20' id='"+item_code+"_"+this.list_code+"_text'>";
						break;
				}
				break;
		}
		return str;
	};
	if(this.is_unspeak == false){
		this.list_name += "【不读出】";
	}

	this.droplist = new Array();
	this.addDroplist = function(list){//题目选择项增加下拉值项
		this.droplist[this.droplist.length] = list;
	};
}

//保存题目矩阵对象
function TermMatrixItem(id,item_id,item_name,item_code,show_order,show_type,remark){
	this.id = id;
	this.item_id = item_id;
	this.item_name = item_name;
	this.item_code = item_code;
	this.show_order = show_order;
	this.show_type = show_type;
	this.remark = remark;
}

//保存入口逻辑对象（其中rule_code的组成为 item_code + 冒号 + list_code + 逗号 + 分号 + ...）逗号表示and的关系 分号表示或关系
function TermSkipRule(id,item_id,rule_code,remark){
	this.id = id;
	this.item_id = item_id;
	this.rule_code = rule_code;
	this.remark = remark;
}
//保存出口逻辑对象（其中rule_code的组成为（item_code:matrix_code:list_code） + 逗号 或 分号 + ...）
//如果是矩阵题则考虑matrix_code。(如果是三个code则表示有矩阵code)逗号表示and的关系 分号表示或关系
function TermOutRule(id,item_id,rule_code,remark,to_code){
	this.id = id;
	this.item_id = item_id;
	this.rule_code = rule_code;
	this.remark = remark;
	this.to_code = to_code;
}

//保存题目选择项下拉值对象
function TermDroplist(drop_value,drop_text){
	this.drop_value = drop_value;
	this.drop_text = drop_text;
}