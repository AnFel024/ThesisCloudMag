package com.antithesis.cloudmag.controller;

import com.antithesis.cloudmag.service.UserService;
import com.antithesis.cloudmag.service.UserDetailsServiceImpl;
import com.antithesis.cloudmag.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UsersController {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getUsers(@PathVariable String user_id) {
        return ResponseUtils.validateResponse(userService.getAllUsers());
    }

    @DeleteMapping("/user/{user_id}/name/{user_name}")
    public ResponseEntity<?> deleteUser(@PathVariable String user_id) {
        return ResponseUtils.validateResponse(userService.deleteUser(user_id));
    }
}
