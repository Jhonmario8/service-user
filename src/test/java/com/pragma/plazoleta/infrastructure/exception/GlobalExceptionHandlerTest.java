package com.pragma.plazoleta.infrastructure.exception;

import com.pragma.plazoleta.domain.constants.DomainConstants;
import com.pragma.plazoleta.domain.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("UnauthorizedException se traduce a 401 con el mensaje original")
    void mapsUnauthorizedTo401() {
        // when
        ResponseEntity<ErrorResponse> response =
                handler.handleUnauthorizedException(new UnauthorizedException(DomainConstants.MSG_INVALID_CREDENTIALS));

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo(DomainConstants.MSG_INVALID_CREDENTIALS);
    }
}
