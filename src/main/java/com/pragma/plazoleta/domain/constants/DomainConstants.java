package com.pragma.plazoleta.domain.constants;

public class DomainConstants {

    private DomainConstants() {}


    public static final String MSG_USER_NOT_FOUND = "User not found";
    public static final String MSG_INVALID_CELLPHONE = "Invalid cellphone number";
    public static final String MSG_INVALID_DOCUMENT = "Invalid document number";
    public static final String MSG_UNDERAGE_USER = "User must be at least 18 years old";
    public static final String MSG_EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String MSG_PHONE_ALREADY_EXISTS = "Phone number already exists";
    public static final String MSG_INVALID_CREDENTIALS = "Invalid credentials";


    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_ROLE_NAME = "role_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ERROR = "error";


    public static final String TOKEN_PREFIX = "Bearer ";
}
