package com.kimi.easyget.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.kimi.easyget.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Rules {

    public static Rules rules = null;

    public static Rules getInstance() {
        if (rules == null) {
            rules = new Rules();
        }
        return rules;

    }

    public CustomError emailValidation(Context context, CharSequence email) {
        final boolean isValid = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return CustomError.builder()
                .error(!isValid)
                .message(context.getString(R.string.invalid_mail))
                .build();
    }

    public CustomError passwordValidation(final Context context, final CharSequence password) {
        final String PASSWORD_PATTERN = "^(?=\\S+$).{6,}$";
        final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        final Matcher matcher = pattern.matcher(password);
        final boolean isValid = !TextUtils.isEmpty(password) && matcher.matches();

        return CustomError.builder()
                .error(!isValid)
                .message(context.getString(R.string.invalid_password))
                .build();
    }

    public CustomError passwordValidationConfirm(final Context context,
                                                 final CharSequence password,
                                                 final CharSequence passwordConfirm) {

        final CustomError customErrorP1 = passwordValidation(context, password);
        final CustomError customErrorP2 = passwordValidation(context, passwordConfirm);

        CustomError customError = CustomError.builder().build();
        customError.setError(false);

        if (!passwordConfirm.equals(password)) {
            customError.setError(true);
            customError.setMessage(context.getString(R.string.passwords_no_equals));
        } else {
            if (customErrorP1.getError() || customErrorP2.getError()) {
                customError.setError(true);
                customError.setMessage(context.getString(R.string.invalid_password));
            }
        }

        return customError;
    }

    public CustomError fieldValidation(final Context context, final CharSequence charSequence) {
        final boolean isValid = !TextUtils.isEmpty(charSequence) && charSequence.length() < 128;
        return CustomError.builder()
                .error(!isValid)
                .message(context.getString(R.string.invalid_field))
                .build();
    }
}
