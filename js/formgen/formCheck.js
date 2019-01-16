//formNum  默认为null为forms【0】
//isNeeded 是否为必填项，默认null（必填项）
//errMsg 提示消息，默认值为“此项不可为空！”
//检测非空
function CheckLength(val,maxlen,msg,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

    var smsg=(msg==null || msg=="")? "此项不可为空！":msg;
    var str = Trim(document.forms[formTarget].elements[val].value);
    if (str == "")
    {
        alert (smsg);
        //2011-11-9 anthony add
        if (document.forms[formTarget].elements[val].type !="hidden")
        {
            document.forms[formTarget].elements[val].focus();
            document.forms[formTarget].elements[val].select();
        }
        return false;
    }
    else if(str!="" && maxlen!=null)
    {
        if (str.length>maxlen)
        {
            alert("此项信息超长，最多可输入" + maxlen +"个字符，请重新输入！");
            if (document.forms[formTarget].elements[val].type !="hidden")
            {
                document.forms[formTarget].elements[val].focus();
                document.forms[formTarget].elements[val].select();
            }
            return false;
        }
    }
    if (str.indexOf("\'")!= -1)
    {
        alert ("请不要使用英文单引号！");
            if (document.forms[formTarget].elements[val].type !="hidden")
            {
                document.forms[formTarget].elements[val].focus();
                document.forms[formTarget].elements[val].select();
            }
        return false;
    }
    document.forms[formTarget].elements[val].value = str;
    return true;
}
//检测纯数字
function CheckNum(val,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

   if(!CheckLength(val))
       return false;
   if(isNaN(document.forms[formTarget].elements[val].value)) {
       alert ("请输入数字！");
       document.forms[formTarget].elements[val].focus();
       document.forms[formTarget].elements[val].select();
       return false;
   }
   return true;
}
//检测纯数字(可以为空)
function CheckNullNum(val,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

   if(isNaN(document.forms[formTarget].elements[val].value))
   {
       alert ("请输入数字！");
       document.forms[formTarget].elements[val].focus();
       document.forms[formTarget].elements[val].select();
       return false;
   }
   return true;
}

function CheckFloatNum(val,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

    if(!CheckLength(val))
       return false;
    var charset = "1234567890.";
    var input = document.forms[formTarget].elements[val].value;
    if (!CheckChar(charset, input, true))
         {
            alert ("请输入合法数据！");
            document.forms[formTarget].elements[val].focus();
            document.forms[formTarget].elements[val].select();
            return false;
        }
       return true;
}
//日期校验
function CheckDate2(val,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

    if(!CheckDate(document.forms[formTarget].elements[val].value)){
        document.forms[formTarget].elements[val].focus();
        document.forms[formTarget].elements[val].select();
        return false;
    }
    else
    {
        document.forms[formTarget].elements[val].value = CheckDate(document.forms[formTarget].elements[val].value);
    }
    return true;
}
//日期校验（可以为空）
function CheckNullDate2(val,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

    var input = document.forms[formTarget].elements[val].value;
    if(input!="")
    {
        if(!CheckDate(input))
        {
            document.forms[formTarget].elements[val].focus();
            document.forms[formTarget].elements[val].select();
            return false;
        }
        else
        {
            document.forms[formTarget].elements[val].value = CheckDate(input);
        }
    }
    return true;
}
//检测日期
function  CheckDate(strDate, mini_year,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

    if(mini_year == null) mini_year = 1900;
    var i_countSeparater = 0;
    var charset = "1234567890";
    var the_date = strDate;
    var the_dateLength=the_date.length;
    var i_firstSepLoc = the_date.indexOf('-',0);
    var i_lastSepLoc = the_date.lastIndexOf('-');
    if (i_firstSepLoc < 0 || i_firstSepLoc == i_lastSepLoc){
        alert('请输入“年-月-日”格式的正确时间！');
        return false;
    }
    var the_year = the_date.substring(0,i_firstSepLoc);
    var the_month = the_date.substring(i_firstSepLoc+1,i_lastSepLoc);
    var the_day = the_date.substring(i_lastSepLoc+1,the_dateLength);
    if (! CheckChar(charset, the_year, true)){
        alert('年份应为数字！');
        return false;
    }
    if (! CheckChar(charset, the_month, true)){
        alert('月份应为数字！');
        return false;
    }
    if (! CheckChar(charset, the_day, true)){
        alert('日期应为数字！');
        return false;
    }
    if (the_year.length >4){
        alert('年份不能大于4位！');
        return false;
    }else if (the_year.length == 1){
        the_year = '200'+the_year;
    }else if (the_year.length == 2){
        the_year = '20'+the_year;
    }else if (the_year.length == 3){
        the_year = '2'+the_year;
    }else if (the_year.length == 0){
        alert('请输入“年-月-日”格式的正确时间！');
        return false;
    }
    if (the_month.length > 2){
        alert('月份不能大于2位！');
        return false;
    }else if (the_month.length == 1){
        the_month = '0'+the_month;
    }else if (the_month.length ==0){
        alert('请输入由“-”分隔的正确的时间！');
        return false;
    }
    if (the_day.length > 2){
        alert('日期不能大于2位！');
        return false;
    }else if (the_day.length == 1){
        the_day = '0'+the_day;
    }else if (the_day.length == 0){
        alert('请输入由“-”分隔的正确的时间！');
        return false;
    }

    if ( the_year < mini_year){
        alert("年份不得小于 " + mini_year +"！");
        return false;
    }
    if (the_month < 01 || the_month > 12){
        alert("请输入正确的月份！")
        return false;
    }
    if (the_day >31 || the_day < 01){
        alert("请输入正确的日期！")
        return false;

    }else{
        switch(eval(the_month)) {
            case 4:
            case 6:
            case 9:
            case 11:
                if (the_day < 31){
                    the_date=the_year+'-'+the_month+'-'+the_day;
                    return the_date;
                }
                break;
            case 2:
                var num = Math.floor(the_year/4) * 4;
                if(the_year == num) {
                    if (the_day < 30){
                        the_date=the_year+'-'+the_month+'-'+the_day;
                        return the_date;
                     }
                } else {
                    if (the_day < 29){
                        the_date=the_year+'-'+the_month+'-'+the_day;
                        return the_date;
                    }
                }
                break;
            default:
                if (the_day < 32){
                    the_date=the_year+'-'+the_month+'-'+the_day;
                    return the_date;
                }
                break;
        }
    }
    alert("请输入正确的日期！");
    return false;

}
//检查字符串中是否有规定字符以内/外的字符
function CheckChar(charset, val, should_in)
{
    var num = val.length;
    for (var i=0; i < num; i++) {
       var strchar = val.charAt(i);
       strchar = strchar.toUpperCase();
       if ((charset.indexOf(strchar) > -1) && (!should_in))
          return false;
       else if ((charset.indexOf(strchar) == -1) && (should_in))
          return false;
    }
    return true;
}
function CheckCheckbox(val,msg1,msg2,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

    var is_radio=document.forms[formTarget].elements[val];
    var s_msg1=(msg1==null || msg1=="")? "请选择CheckBox!":msg1;
    var s_msg2=(msg2==null || msg2=="")? "没有可选的CheckBox!":msg2;

    if(is_radio)
    {
         if (document.forms[formTarget].elements[val].value != null)
         {
            if (document.forms[formTarget].elements[val].checked)
            {
                return true;
            }
            else
            {
                alert(s_msg1);
                return false;
            }
         }
         else
         {
            var check_length = document.forms[formTarget].elements[val].length;
            var i_count=0
            for(var i=0;i<check_length;i++)
            {
                if (document.forms[formTarget].elements[val][i].checked)
                {
                    i_count=i_count+1;
                    return true;
                }
            }
            if(i_count==0)
            {
                alert(s_msg1);
                return false;
            }
         }
    }
    else
    {
        alert(s_msg2);
        return false;
    }

}
function CheckRadio(val,msg1,msg2,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

    var is_radio=document.forms[formTarget].elements[val];
    var s_msg1=(msg1==null || msg1=="")? "请选择 radio!":msg1;
    var s_msg2=(msg2==null || msg2=="")? "没有可选的 radio!":msg2;

    if(is_radio)
    {
         if (document.forms[formTarget].elements[val].value != null)
         {
            if (document.forms[formTarget].elements[val].checked)
            {
                return true;
            }
            else
            {
                alert(s_msg1);
                return false;
            }
         }
         else
         {
            var check_length = document.forms[formTarget].elements[val].length;
            var i_count=0
            for(var i=0;i<check_length;i++)
            {
                if (document.forms[formTarget].elements[val][i].checked)
                {
                    i_count=i_count+1;
                    return true;
                }
            }
            if(i_count==0)
            {
                alert(s_msg1);
                return false;
            }
         }
    }
    else
    {
        alert(s_msg2);
        return false;
    }

}
//判断radio对象是否被选中
function CheckRadioIsSelected(val,formObj)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

    var is_radio=document.forms[formTarget].elements[val];

    if(is_radio)
    {
         if (document.forms[formTarget].elements[val].value != null)
         {
            if (document.forms[formTarget].elements[val].checked)
            {
                return true;
            }
            else
            {
                return false;
            }
         }
         else
         {
            var check_length = document.forms[formTarget].elements[val].length;
            var i_count=0
            for(var i=0;i<check_length;i++)
            {
                if (document.forms[formTarget].elements[val][i].checked)
                {
                    i_count=i_count+1;
                    return true;
                }
            }
            if(i_count==0)
            {
                return false;
            }
         }
    }
    else
    {
        return false;
    }
}
//检测不能为空
function CheckNull(val,formObj,msg)
{
    //表单对象，如果为null，默认为“formEditor”
    var formTarget=(formObj==null || formObj=="" || typeof(formObj)=="undefined")? "formEditor":formObj;

    var input = document.forms[formTarget].elements[val].value;
    if(input!="")
    {
		return false;
    }
    else
    {   
		alert((msg==undefined?"此项不可为空!":msg));
        document.forms[formTarget].elements[val].focus();
        return true;
    }
    return false;
}