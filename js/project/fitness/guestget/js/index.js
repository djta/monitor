(function(){
    $('.top-nav li').on('click', function(){
        var id = this.dataset.id, activeCls = 'active';

        this.classList.add(activeCls);
        $(this).siblings('li').removeClass(activeCls);
        console.log($(this).siblings('li'));
        $('.lists').hide();
        $('#'+id).show();
    })
})();