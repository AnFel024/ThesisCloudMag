package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.controller.payload.request.LoginDto;
import com.antithesis.cloudmag.controller.payload.request.SignupDto;
import com.antithesis.cloudmag.controller.payload.response.JwtResponse;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.service.UserService;
import com.antithesis.cloudmag.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user/auth")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserAuthController {

    private final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        MessageResponse<JwtResponse> messageResponse = userService.createSignInPetition(loginDto);
        return ResponseUtils.validateResponse(messageResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupDto signUpDto) throws RuntimeException {
        MessageResponse<?> messageResponse = userService.createUser(signUpDto);
        return ResponseUtils.validateResponse(messageResponse);
    }
}