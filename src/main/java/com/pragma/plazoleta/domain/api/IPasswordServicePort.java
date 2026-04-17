package com.pragma.plazoleta.domain.api;

public interface IPasswordServicePort {
    String encodePassword(String password);
    boolean matches(String password, String hashedPassword);
}
