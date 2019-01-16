$(function(){
    function remSize() {
        var doc = document.documentElement;
        var ww = doc.clientWidth;
        if (ww > 750) {
            ww = 750;
        }
        doc.style.fontSize = ww / 7.5 + "px";
    }
    window.onresize = function() {
        remSize();
    }
    remSize();
})