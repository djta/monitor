function WinOpen(url, type, is_detail)
{
    var i_top=0;//上
    var i_left=0;//左
    var i_width=0;//宽
    var i_heigth=0;//高
    var now = new Date();
    var s_win_name="windowns"+now.getFullYear()+now.getMonth()+now.getDay()+now.getHours()+now.getMinutes()+now.getSeconds()+now.getMilliseconds();//窗口名称

    if(is_detail != "" && is_detail != undefined)
    	s_win_name=is_detail;

    if(type==1) { //小窗口
        i_top=234;
        i_left=287;
        i_width=450;
        i_heigth=300;
    } else if(type==2) { //中窗口
        i_top=159;
        i_left=187;
        i_width=650;
        i_heigth=450;
    }else if(type==3) { //宽窗口
        i_top=159;
        i_left=162;
        i_width=700;
        i_heigth=450;
    } else if(type==4) { //中窗口
        i_top=234;
        i_left=187;
        i_width=650;
        i_heigth=300;
    } else if(type==5) { //中窗口
        i_top=194;
        i_left=137;
        i_width=750;
        i_heigth=380;
    }
    else if(type==6) { //中窗口
        i_top=194;
        i_left=87;
        i_width=850;
        i_heigth=380;
    }
    else if(type==7) { //中窗口
        i_top=194;
        i_left=137;
        i_width=750;
        i_heigth=180;
    }
    else if(type==8) { //中窗口
        i_top=234;
        i_left=287;
        i_width=550;
        i_heigth=250;
    }
    else if(type==9) { //大窗口
        i_top=134;
        i_left=137;
        i_width=750;
        i_heigth=500;
    }
    else if(type==10) { //大窗口
        i_top=234;
        i_left=287;
        i_width=450;
        i_heigth=300;
    }
    else if(type==11) { //大窗口
        i_top=84;
        i_left=137;
        i_width=750;
        i_heigth=600;
    }
    else if(type==12) { //大窗口
        i_top=84;
        i_left=137;
        i_width=750;
        i_heigth=700;
    }
    else if(type==13) { //大窗口
        i_top=84;
        i_left=137;
        i_width=980;
        i_heigth=700;
    }
    else if(type==14) { //大窗口
        i_top=84;
        i_left=137;
        i_width=1024;
        i_heigth=768;
    }
    else { //全屏窗口
        i_top=0;
        i_left=0;
        i_width=window.screen.width;
        i_heigth=window.screen.height;
    }

    msg=open(url,s_win_name,"location=no,titlebar=no,toolbar=no,menubar=no,top="+i_top+",left="+i_left+",resizable=1,scrollbars=yes,width="+i_width+",height="+i_heigth);
    msg.focus();
}

function startNewWindow(url)
{
    if((url!=null) ||(url!="")){
        var titleString = "APP";
        var ScWidth = screen.availwidth - 10;
        var ScHeight = screen.availheight - 48;
        var WindowOption = "location=no,titlebar=no,location=no,menubar=no,toolbar=no,status=yes,resizable=yes,top=0,left=0";
        WindowOption = WindowOption + ",width=" + ScWidth;
        WindowOption = WindowOption + ",height=" + ScHeight;
        var newWin = window.open(url, titleString, WindowOption);
        newWin.focus();
    }
}