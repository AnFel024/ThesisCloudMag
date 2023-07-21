package com.antithesis.cloudmag.controller.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MessageResponse<T> {
  private T data;
  private String message;
  private HttpStatus status;
}
