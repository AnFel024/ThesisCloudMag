package com.antithesis.cloudmag.controller.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtResponse {
  private String token;
  private @Builder.Default String type = "Bearer";
  private String username;
  private String email;
  private List<String> roles;
}
