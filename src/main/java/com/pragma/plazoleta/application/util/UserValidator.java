package com.pragma.plazoleta.application.util;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserValidator {

    private static final String CELLPHONE_PATTERN = "^\\+?\\d{3,}$";
    private static final String DOCUMENT_PATTERN = "^\\d{3,}$";

    public static boolean validateCellphone(String cellphone) {
        if (cellphone == null) return false;
        String value = cellphone.trim();
        if (value.isEmpty()) return false;
        return value.matches(CELLPHONE_PATTERN);
    }


    public static boolean validateDocument(String document) {
        if (document == null) return false;
        String value = document.trim();
        if (value.isEmpty()) return false;
        return value.matches(DOCUMENT_PATTERN);
    }
}
