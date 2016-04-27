/**
 * Created by zakhar on 27.04.2016.
 */

$(document).ready(function () {

    var $registerForm = $('#register-form');
    var $registerSuccess = $('#register-success');
    var $emailFormGroup = $('#email-form-group');
    var $usernameFormGroup = $('#username-form-group');
    var $passwordFormGroup = $('#password-form-group');
    var $passwordRepeatFormGroup = $('#password-repeat-form-group');
    var $submitBtn = $('#submit-btn');

    $registerForm.submit(function(event) {
        event.preventDefault();

        $submitBtn.prop('disabled', true);
        $('.error-msg').hide();
        var username = $('#username-input').val();
        var clientValid = true;

        $registerForm.find('input').each(function() {
            var $formGroup = $(this).parents('.form-group');
            var $glyphicon = $formGroup.find('.form-control-feedback');
            if (this.checkValidity()) {
                $formGroup.addClass('has-success').removeClass('has-error');
                $glyphicon.addClass('glyphicon-ok').removeClass('glyphicon-remove');
            } else {
                $formGroup.addClass('has-error').removeClass('has-success');
                $glyphicon.addClass('glyphicon-remove').removeClass('glyphicon-ok');

                clientValid = false;
            }
        });
        if($passwordFormGroup.find('input').val() !== $passwordRepeatFormGroup.find('input').val()) {
            $passwordFormGroup.addClass('has-error').removeClass('has-success');
            $passwordRepeatFormGroup.addClass('has-error').removeClass('has-success');
            $passwordFormGroup.find('.form-control-feedback').addClass('glyphicon-remove').removeClass('glyphicon-ok');
            $passwordRepeatFormGroup.find('.form-control-feedback').addClass('glyphicon-remove').removeClass('glyphicon-ok');
            $('#password-error-msg').text('Введенные пароли не совпадают').show();

            clientValid = false;
        }
        if (clientValid) {

            $registerForm.find('.form-group').removeClass('has-error').addClass('has-success');
            $('.form-control-feedback').addClass('glyphicon-ok').removeClass('glyphicon-remove');
            $.ajax({
                type: "POST",
                url: "register",
                data: $registerForm.serialize(),
                success : function(result){
                    console.log(result);
                    var valid = result.valid;
                    if(valid) {
                        $('#registration-username').text(username);
                        $registerForm.hide();
                        $registerSuccess.show();
                    }
                    else {
                        var errors = result.errors;
                        for(var i = 0; i < errors.length; i++) {
                            if(errors[i] === 'EMAIL_VALIDATION_ERROR') {
                                $emailFormGroup.addClass('has-error').removeClass('has-success');
                                $emailFormGroup.find('.form-control-feedback').addClass('glyphicon-remove').removeClass('glyphicon-ok');
                                $('#email-error-msg').text('Введите корректный email').show();
                            }
                            if(errors[i] === 'USERNAME_VALIDATION_ERROR') {
                                $usernameFormGroup.addClass('has-error').removeClass('has-success');
                                $usernameFormGroup.find('.form-control-feedback').addClass('glyphicon-remove').removeClass('glyphicon-ok');
                                $('#username-error-msg').text('В имени должно быть от 3 до 15 символов (допускается кириллица)').show();
                            }
                            if(errors[i] === 'PASSWORD_VALIDATION_ERROR') {
                                $passwordFormGroup.addClass('has-error').removeClass('has-success');
                                $passwordRepeatFormGroup.addClass('has-error').removeClass('has-success');
                                $passwordFormGroup.find('.form-control-feedback').addClass('glyphicon-remove').removeClass('glyphicon-ok');
                                $passwordRepeatFormGroup.find('.form-control-feedback').addClass('glyphicon-remove').removeClass('glyphicon-ok');
                                $('#password-error-msg').text('В пароле должно быть от 3 до 15 символов (латинские буквы, цифры, символ подчеркивания)').show();
                            }
                            if(errors[i] === 'DUPLICATE_ERROR') {
                                $emailFormGroup.addClass('has-error').removeClass('has-success');
                                $usernameFormGroup.addClass('has-error').removeClass('has-success');
                                $emailFormGroup.find('.form-control-feedback').addClass('glyphicon-remove').removeClass('glyphicon-ok');
                                $usernameFormGroup.find('.form-control-feedback').addClass('glyphicon-remove').removeClass('glyphicon-ok');
                                $('#email-error-msg').text('Пользователь с таким именем/email уже существует').show();
                            }
                        }
                    }
                    $submitBtn.prop('disabled', false);
                }
            });
        }
        else {
            $submitBtn.prop('disabled', false);
        }
    });
});