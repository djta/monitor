(function(){
    $('.foot-nav li').on('click', function(){
        var type = this.dataset.type, url = '';
        console.log(type)
        switch(type){
            case 'menu': url = '/action/project/fitness/guestget/menu/crud'; break;
            case 'location': url = '/action/project/fitness/guestget/location/crud'; break;
            case 'add': url = '/action/project/fitness/guestget/index/add'; break;
            case 'notice': url = '/action/project/fitness/guestget/notice/crud'; break;
            case 'myhome': url = '/action/project/fitness/guestget/myhome/crud'; break;
            case 'data': url = '/action/project/fitness/guestget/datacenter/crud'; break;
            default:;
        }
        location.href = contextPath+url
    })
})();