
$(document).ready(function(){
    $('#menu').tendina({
        openCallback: function(clickedEl) {
          console.log(clickedEl);
        },
        closeCallback: function(clickedEl) {
          console.log(clickedEl);
        }
    });
    $('#username').text('测试用户');

});
