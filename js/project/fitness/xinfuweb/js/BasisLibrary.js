
//鍩虹鍑芥暟
//涓存椂涓浆鍑芥暟
function obj(o){        //灏嗚浼犻€掕繘鍏ョ殑瀵硅薄
    function F(){}      //涓存椂鏂板缓鐨勫璞＄敤鏉ュ瓨鍌ㄤ紶閫掕繃鏉ョ殑瀵硅薄
    F.prototype = o;    //灏唎瀵硅薄瀹炰緥璧嬪€肩粰F鏋勯€犵殑鍘熷瀷瀵硅薄
    return new F();     //杩斿洖杩欎釜寰楀埌浼犻€掕繃鏉ュ璞＄殑瀵硅薄瀹炰緥
}

//瀵勭敓鍑芥暟 鐖剁被 瑕佺户鎵跨殑瀛愮被
function create(parent,subclass){
    var f = obj(parent.prototype);
    f.constructor = subclass;   //璋冩暣鍘熷瀷鏋勯€犳寚閽�
    subclass.prototype = f;
}

function BasisLibrary(data){
    this.ua = window.navigator.userAgent.toLowerCase();
    this.data = data;
    this._events = {};
    //鏄惁鍦╝pp涓墦寮€
    if(typeof this.isApp != 'function'){
        BasisLibrary.prototype.isApp = function(){
            var isApp = null;
            var ua = this.ua;
            var user = this.data.comparePrice;
            var users = eval("/"+ user +"/i");
            if(ua.match(users) == user) {//鍒ゆ柇鏄惁鍦╝pp涓墦寮€
                isApp = true;
            }else{
                isApp = false;
            }
            return isApp;
        }
    };
    //鏄惁鍦ㄧЩ鍔ㄧ鎵撳紑
    if(typeof this.isMobile != 'function'){
        BasisLibrary.prototype.isMobile = function(){
            var isMobile = null;
            var ua = navigator.userAgent;
            if(!!ua.match(/AppleWebKit.*Mobile.*/)){
                isMobile = true;
            }else{
                isMobile = false;
            }
            return isMobile;
        }
    };
    //鏄惁鍦ㄥ井淇′腑鎵撳紑
    if(typeof this.isWeixin != 'function'){
        BasisLibrary.prototype.isWeixin = function(){
            var ua = this.ua;
            if(ua.match(/MicroMessenger/i) == "micromessenger") {
                return true;
            } else {
                return false;
            }
        }
    };
    //娴忚鍣ㄧ被鍨�
    if(typeof this.webType != 'function'){
        BasisLibrary.prototype.webType = function(){
            var webType = null;
            var ua = navigator.userAgent;

            //鏄惁鏄畨鍗�
            if(ua.indexOf('Android') > -1 || ua.indexOf('Linux') > -1){
                webType = 'android';
            }else if(!!ua.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)){ //鏄惁鏄痠os
                if(ua.indexOf('iPad') > -1){
                    webType = 'iPad';
                }else{
                    webType = 'ios';
                }
            }else{
                webType = 'pc';
            }
            return webType;
        }
    };
    //鐢熸垚涓嬭浇閾炬帴
    if(typeof this.download != 'function'){

        BasisLibrary.prototype.download = function(doms){
            var doms = $(doms);
            if(this.webType() == 'ios' || this.webType() == 'iPad'){
                if(this.isWeixin()){
                    doms.prop('href',this.data.downloadUrl.iosWeChat);
                }else{
                    doms.prop('href',this.data.downloadUrl.ios);
                }
            }else if(this.webType() == 'android'){
                if(this.isWeixin()){
                    doms.prop('href',this.data.downloadUrl.androidWeChat);
                }else{
                    doms.prop('href',this.data.downloadUrl.android);
                }
            }else{
                doms.prop('href',this.data.downloadUrl.android);
            }
        }
    };
    //鍥剧墖鍔犺浇瀹屾垚鍚庢墽琛�
    if(typeof this.imgLoading != 'function'){
        BasisLibrary.prototype.imgLoading = function(imgs,fun){
            var _function = fun;
            var t_img; // 瀹氭椂鍣�
            var isLoad = true; // 鎺у埗鍙橀噺
            // 鍒ゆ柇鍥剧墖鍔犺浇鐘跺喌锛屽姞杞藉畬鎴愬悗鍥炶皟
            isImgLoad(function(){
                // 鍥剧墖鍔犺浇瀹屾垚
                if(_function != undefined){
                    _function();
                }
            });
            // 鍒ゆ柇鍥剧墖鍔犺浇鐨勫嚱鏁�
            function isImgLoad(callback){
                // 娉ㄦ剰鎴戠殑鍥剧墖绫诲悕閮芥槸cover锛屽洜涓烘垜鍙渶瑕佸鐞哻over銆傚叾瀹冨浘鐗囧彲浠ヤ笉绠°€�
                // 鏌ユ壘鎵€鏈夊皝闈㈠浘锛岃凯浠ｅ鐞�
                $(imgs).each(function(){
                    // 鎵惧埌涓�0灏卞皢isLoad璁句负false锛屽苟閫€鍑篹ach
                    if(this.height === 0){
                        isLoad = false;
                        return false;
                    }
                });
                // 涓簍rue锛屾病鏈夊彂鐜颁负0鐨勩€傚姞杞藉畬姣�
                if(isLoad){
                    clearTimeout(t_img); // 娓呴櫎瀹氭椂鍣�
                    // 鍥炶皟鍑芥暟
                    callback();
                    // 涓篺alse锛屽洜涓烘壘鍒颁簡娌℃湁鍔犺浇瀹屾垚鐨勫浘锛屽皢璋冪敤瀹氭椂鍣ㄩ€掑綊
                }else{
                    isLoad = true;
                    t_img = setTimeout(function(){
                        isImgLoad(callback); // 閫掑綊鎵弿
                    },100); // 鎴戣繖閲岃缃殑鏄�500姣灏辨壂鎻忎竴娆★紝鍙互鑷繁璋冩暣
                };
            };
        }
    };
    //瑙ｅ喅杈撳叆娉曟尅浣弔extarea
    if(typeof this.textareaOr != 'function'){
        BasisLibrary.prototype.textareaOr = function(){
            $('.textareaText').each(function(){
                var offsetTop = $(this).offset().top;
                $(this).focus(function(){
                    $("html,body").animate({
                        scrollTop:$(this).offset().top
                    },500);
                }).blur(function(){
                    $("html,body").animate({
                        scrollTop:offsetTop
                    },500);
                });
            });
        }
    }
    //鏃堕棿杞崲
    if(typeof this.format != 'function'){
        BasisLibrary.prototype.format = function(format) {
            var date = {
                "M+": this.getMonth() + 1,
                "d+": this.getDate(),
                "h+": this.getHours(),
                "m+": this.getMinutes(),
                "s+": this.getSeconds(),
                "q+": Math.floor((this.getMonth() + 3) / 3),
                "S+": this.getMilliseconds()
            };
            if (/(y+)/i.test(format)) {
                format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
            }
            for (var k in date) {
                if (new RegExp("(" + k + ")").test(format)) {
                    format = format.replace(RegExp.$1, RegExp.$1.length == 1
                        ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
                }
            }
            return format;
        }
    }
    //绉掕浆灏忔椂鍒嗙殑鏂规硶
    if(typeof this.formatSeconds != 'function'){
        BasisLibrary.prototype.formatSeconds = function(value){
            var theTime = parseInt(value);// 绉�
            var theTime1 = 0;// 鍒�
            var theTime2 = 0;// 灏忔椂
            if(theTime > 60) {
                theTime1 = parseInt(theTime/60);
                theTime = parseInt(theTime%60);
                if(theTime1 > 60) {
                    theTime2 = parseInt(theTime1/60);
                    theTime1 = parseInt(theTime1%60);
                }
            }
            var result = parseInt(theTime2)+"灏忔椂"+parseInt(theTime1)+"鍒�"+parseInt(theTime)+"绉�";
            /*if(theTime1 > 0) {
                result = ""+parseInt(theTime1)+"鍒�"+result;
            }
            if(theTime2 > 0) {
                result = ""+parseInt(theTime2)+"灏忔椂"+result;
            }*/
            return result;
        }
    }
    //寮瑰嚭妗嗗眳涓�
    if(typeof this.centerLine != 'function'){
        BasisLibrary.prototype.centerLine = function(){
            var winWidth = $(window).outerWidth();
            var winHeight = $(window).outerHeight();
            var cenWidth = $('.center-line').outerWidth();
            var cenHeight = $('.center-line').outerHeight();
            this.imgLoading('img',function(){
                $('.center-line').css({'marginTop':-(cenHeight)/2+'px','marginLeft':-(cenWidth)/2+'px'});
            });
            $(window).resize(function(){
                $('.center-line').css({'marginTop':-(cenHeight)/2+'px','marginLeft':-(cenWidth)/2+'px'});
            });

        };
    }
    //鍒濆鍖栨椂闂村璞�
    if(typeof this.Timedata != 'function'){
        BasisLibrary.prototype.Timedata = function(){
            var myDate = new Date();
            return myDate;
        };
    }
    //绉诲姩绔痶ouch浜嬩欢
    if(typeof  this.bindtouch != 'function'){
        BasisLibrary.prototype.bindtouch = function(src, cb){
            $(src).unbind();
            var isTouchDevice = 'ontouchstart' in window || navigator.msMaxTouchPoints;
            if (isTouchDevice) {
                $(src).on("touchstart", function(event) {
                    $(this).data("touchon", true);
                    $(this).addClass("pressed");
                });
                $(src).on("touchend", function() {
                    $(this).removeClass("pressed");
                    if ($(this).data("touchon")) {
                        //cb.bind(this)();
                        cb();
                    }
                    $(this).data("touchon", false);
                });
                $(src).on("touchmove", function() {
                    $(this).data("touchon", false);
                    $(this).removeClass("pressed");
                });
            } else {
                $(src).on("mousedown", function() {
                    $(this).addClass("pressed");
                    $(this).data("touchon", true);
                });
                $(src).on("mouseup", function() {
                    $(this).removeClass("pressed");
                    $(this).data("touchon", false);
                    //cb.bind(this)();
                    cb();
                });
            }
        };
    }
    //娉ㄥ唽浜嬩欢鐩戝惉
    if(typeof  this.on != 'function'){
        BasisLibrary.prototype.on = function(eventName,callback){
            if(this._events[eventName]){ //宸茬粡璁㈤槄杩囦簡
                this._events[eventName].push(callback);
            }else{
                this._events[eventName] = [callback];
            }
        };
    }
    //鍙戝皠浜嬩欢
    if(typeof  this.emit != 'function'){
        BasisLibrary.prototype.emit = function(eventName){
            var args = Array.prototype.slice.call(arguments,1);
            var callbacks = this._events[eventName];
            var self = this;
            callbacks.forEach(function(callback){
                callback.apply(self,args);
            });
        };
    }
    //鍒ゆ柇浠婂ぉ鏄槦鏈熷嚑
    if(typeof  this.showTime != 'function'){
        BasisLibrary.prototype.showTime = function(time){
            var self = this;
            var time;
            var dateWeek;
            if(arguments[0] == "undefined"){
                time = new Date();
            }else{
                time = new Date(time);
            }
            var show_day=new Array('鏄熸湡涓€','鏄熸湡浜�','鏄熸湡涓�','鏄熸湡鍥�','鏄熸湡浜�','鏄熸湡鍏�','鏄熸湡鏃�');

            var year=time.getYear();
            var month=time.getMonth();
            var date=time.getDate();
            var day=time.getDay();
            var hour=time.getHours();
            var minutes=time.getMinutes();
            var second=time.getSeconds();
            month<10?month='0'+month:month;
            month=month+1;
            hour<10?hour='0'+hour:hour;
            minutes<10?minutes='0'+minutes:minutes;
            second<10?second='0'+second:second;
            var now_time='褰撳墠鏃堕棿锛�'+year+'骞�'+month+'鏈�'+date+'鏃�'+' '+show_day[day-1]+' '+hour+':'+minutes+':'+second;
            //document.getElementById('showtime').innerHTML=now_time;
            //setTimeout("showTime();",1000);
            if(show_day[day-1] == undefined){
                dateWeek = '鏄熸湡鏃�';
            }else{
                dateWeek = show_day[day-1];
            }

            return dateWeek;
        }

    }
}

//鎵╁睍Data鏂规硶 format('yyyy-MM-dd h:m:s')
Date.prototype.format = function(format) {
    var date = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S+": this.getMilliseconds()
    };
    if (/(y+)/i.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in date) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1
                ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
        }
    }
    return format;
}
//鎵╁睍Number鏂规硶灏嗙杞崲鎴愬垎绉�
Number.prototype.formatTime=function(){
    // 璁＄畻
    var h=0,i=0,s=parseInt(this);
    if(s>59){
        i=parseInt(s/59);
        s=parseInt(s%59);
        if(i > 59) {
            h=parseInt(i/59);
            i = parseInt(i%59);
        }
    }
    // 琛ラ浂
    var zero=function(v){
        return (v>>0)<10?"0"+v:v;
    };
    return [/*zero(h),*/zero(i),zero(s)].join(":");
};

Number.prototype.formatTime2 = function() {
    var theTime = parseInt(this);// 绉�
    var theTime1 = 0;// 鍒�
    var theTime2 = 0;// 灏忔椂
    if(theTime > 60) {
        theTime1 = parseInt(theTime/60);
        theTime = parseInt(theTime%60);
        if(theTime1 > 60) {
            theTime2 = parseInt(theTime1/60);
            theTime1 = parseInt(theTime1%60);
        }
    }
    var result = ""+parseInt(theTime)+"绉�";
    if(theTime1 > 0) {
        result = ""+parseInt(theTime1)+"鍒�"+result;
    }
    if(theTime2 > 0) {
        result = ""+parseInt(theTime2)+"灏忔椂"+result;
    }
    return result;
}

//鎵╁睍String鏂规硶娣诲姞鍘绘帀瀛楃涓插墠鍚庣┖鏍煎姛鑳�
String.prototype.trim=function() {
    return this.replace(/(^\s*)|(\s*$)/g,'');
}

//jquery缁戝畾touch浠ｆ浛click浜嬩欢

// $.fn.bindtouch = function(cb) {
//     function attachEvent(src, cb) {
//         $(src).unbind();
//         var isTouchDevice = 'ontouchstart' in window || navigator.msMaxTouchPoints;
//         if (isTouchDevice) {
//             $(src).on("touchstart", function(event) {
//                 $(this).data("touchon", true);
//                 $(this).addClass("pressed");
//             });
//             $(src).on("touchend", function() {
//                 $(this).removeClass("pressed");
//                 if ($(this).data("touchon")) {
//                     //cb.bind(this)();
//                     cb();
//                 }
//                 $(this).data("touchon", false);
//             });
//             $(src).on("touchmove", function() {
//                 $(this).data("touchon", false);
//                 $(this).removeClass("pressed");
//             });
//         } else {
//             $(src).on("mousedown", function() {
//                 $(this).addClass("pressed");
//                 $(this).data("touchon", true);
//             });
//             $(src).on("mouseup", function() {
//                 $(this).removeClass("pressed");
//                 $(this).data("touchon", false);
//                 //cb.bind(this)();
//                 cb();
//             });
//         };
//     }
//     attachEvent($(this), cb);
// };


$.fn.smartTime = function(options){
    var defaults = {
        to: "yyyy-MM-dd",
        attr: "smartTime"
    };
    var opts = $.extend(defaults, options);
    return this.each(function () {
        var $this = $(this);
        var now = new Date().getTime();
        var old = $this.attr(opts.attr);
        if (!old||old<1000){
            return;
        }
        var t = now - old*1000;
        var newTimeStr = "";
        if (t<1000*60*2){
            newTimeStr = "鍒氬垰";
        } else if (t < 1000*60*60){
            newTimeStr = parseInt(t/(1000*60))+"鍒嗛挓鍓�";
        } else if (t < 1000*60*60*24){
            newTimeStr = parseInt(t/(1000*60*60))+"灏忔椂鍓�";
        } else if (t < 1000*60*60*24*30){
            newTimeStr = parseInt(t/(1000*60*60*24))+"澶╁墠";
        } else {
            newTimeStr = new Date(old*1000).format(opts.to);
        }
        $this.html(newTimeStr);
    });
}
Date.prototype.format = function(fmt) {
    var o = {
        "M+" : this.getMonth()+1,                 //鏈堜唤
        "d+" : this.getDate(),                    //鏃�
        "h+" : this.getHours(),                   //灏忔椂
        "m+" : this.getMinutes(),                 //鍒�
        "s+" : this.getSeconds(),                 //绉�
        "q+" : Math.floor((this.getMonth()+3)/3), //瀛ｅ害
        "S"  : this.getMilliseconds()             //姣
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
}

//璁剧疆鍑芥暟鍙傛暟榛樿鍊�
var defaultFunParameter = function(Fun, Parameter) {
    if (!Fun) return null;
    if (!Parameter) return Fun;

    // 濡傛灉涔嬪墠淇濆瓨杩囬粯璁ゅ€硷紝灏嗗叾鍙栧嚭鍚堝苟鍒版柊鐨刣efaultValues涓�
    var _defaultValues = Fun._defaultValues;
    if (_defaultValues) {
        for (var k in _defaultValues) {
            if (!Parameter.hasOwnProperty(k)) {
                Parameter[k] = _defaultValues[k];
            }
        }
    }

    // 濡傛灉鏈変繚瀛樼殑func鍑芥暟灏卞彇鍑烘潵锛屼粠鑰岀渷鎺変竴灞傚wrapper鐨勮皟鐢�
    Fun = Fun._original ? Fun._original : Fun;
    var match = Fun.toString().match(/function[^\(]*\(([^\)]*)\)/);
    if (!match || match.length < 2) return Fun;

    var argNameStr = match[1].replace(/\/\/.*/gm, '') // remove single-line comments
        .replace(/\/\*.*?\*\//g, '') // remove multi-line comments
        .replace(/\s+/g, ''); // remove spaces
    if (!argNameStr) return Fun;
    var argNames = argNameStr.split(',');

    var wrapper = function() {
        var args = Array.prototype.slice.call(arguments);
        for (var i = arguments.length; i < argNames.length; i++) {
            args[i] = Parameter[argNames[i]];
        }
        return Fun.apply(null, args);
    };
    // 閲嶅啓wrapper鐨則oString鏂规硶锛岃繑鍥炲師濮媐unc鍑芥暟鐨則oString()缁撴灉
    wrapper.toString = function() {
        return Fun.toString();
    };
    // 鎶婂師濮嬬殑func鍑芥暟鍜屽綋鍓嶇殑榛樿鍊煎璞′繚瀛樺埌wrapper涓�
    wrapper._original = Fun;
    wrapper._defaultValues = Parameter;

    return wrapper;
};