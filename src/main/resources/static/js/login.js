$(function() {
    var isPwd=0;
    $('#login_box').load('/jsp/login_pwd');
    $('#switch-href').text('手机号登录');
    $('#switch-href').click(function () {
        if(isPwd)
        {
            $('#login_box').load('/jsp/login_pwd');
            $('#switch-href').text('手机号登录');
            isPwd=0;
        }else {
            $('#login_box').load('/jsp/login_phone');
            $('#switch-href').text('密码登录');
            isPwd=1;
        }
    });
});
