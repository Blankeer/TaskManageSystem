$(document).ready(function() {
  $('.a_login').click(function () {
      $(".register-form").hide("fast");
      $(".login-form").show("fast");
      $(".forget-form").hide("fast");
      $(".forget-form-2").hide("fast");
  });
  $('.a_register').click(function () {
      $(".register-form").show("fast");
      $(".login-form").hide("fast");
      $(".forget-form").hide("fast");
      $(".forget-form-2").hide("fast");
  });
  $(".label_forget").click(function (){
      $(".register-form").hide("fast");
      $(".login-form").hide("fast");
      $(".forget-form").show("fast");
      $(".forget-form-2").hide("fast");
  });
  $('#bu_login').click(onClickLogin);
});
function onClickLogin(){
  var account=$('#account').val();
  var pwd=$('#password').val();
  //todo 邮箱 pwd校验
  $.ajax({
    type:"post",
    url:"/login",
    contentType:"application/json;charset=UTF-8",
    dataType:"json",
    data:JSON.stringify({
    "email":account,
    "pwd":pwd
    }),
    success:function(){

    },
    error:function (data){
      
    }});
}
