//绉诲姩绔畼缃�-甯歌闂
function ApplyNumber(data){
    BasisLibrary.call(this,data);   //瀵硅薄鍐掑厖缁ф壙
    this.formDt = {}; //鎻愪氦鏁版嵁
    this.indexData = {};
}
//閫氳繃瀵勭敓缁勫悎缁ф壙 鏉ュ疄鐜扮户鎵�
create(BasisLibrary,ApplyNumber);



//椤甸潰鍒濆鍖栨柟娉�
ApplyNumber.prototype.initializeFun = function (data) {
    var self = this;
    var data = data;

    $(".input-style-webkit input").on('focus',function(){
        var viewTop = $(window).scrollTop(),            // 鍙鍖哄煙椤堕儴
            viewBottom = viewTop + window.innerHeight;  // 鍙鍖哄煙搴曢儴
        var elementTop = $(this).offset().top, // $element鏄繚瀛樼殑input
            elementBottom = elementTop + $(this).height();
        $(window).scrollTop(elementBottom); // 璋冩暣value
    });

    // var area2 = new LArea();
    // area2.init({
    //     'trigger': '#linkage',
    //     'valueTo': '#value',
    //     'keys': {
    //         id: 'value',
    //         name: 'text'
    //     },
    //     'type': 2,
    //     'data': [provs_data, citys_data, dists_data]
    // });
    // $(".yes-webkit .but").off("click").on("click",function(){
    //     $(".applyNumber-webkit").css("display","block");
    //     $(".yes-webkit").css("display","none");
    // });

};


jQuery.fn.ApplyNumberFun = function(object,data){
    var self = object;
    var data = data;



    setTimeout(function(){
        self.initializeFun(data);
    },100);



    $(window).scroll(function() {

    });
    $(window).resize(function(){

    });
};