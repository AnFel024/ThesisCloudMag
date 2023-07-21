package com.antithesis.cloudmag.utils;

import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import org.springframework.http.ResponseEntity;

public final class ResponseUtils {
    private ResponseUtils() {
    }

    public static ResponseEntity<?> validateResponse(MessageResponse messageResponse) {
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }
}
