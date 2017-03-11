
$(document).ready(function(){
    $('#menu').tendina({
        openCallback: function(clickedEl) {
          console.log(clickedEl);
        },
        closeCallback: function(clickedEl) {
          console.log(clickedEl);
        }
    });

});
$(function(){

    $("#ad_setting").click(function(){
        $("#ad_setting_ul").toggle();
    });
    $("#ad_setting_ul").mouseleave(function(){
        $(this).hide();
    });
    $("#ad_setting_ul li").mouseenter(function(){
        $(this).find("a").attr("class","ad_setting_ul_li_a");
    });
    $("#ad_setting_ul li").mouseleave(function(){
        $(this).find("a").attr("class","");
    });
});
