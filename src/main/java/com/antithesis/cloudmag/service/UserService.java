package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.configuration.security.jwt.JwtUtils;
import com.antithesis.cloudmag.constant.RoleTypes;
import com.antithesis.cloudmag.controller.payload.request.LoginRequest;
import com.antithesis.cloudmag.controller.payload.request.SignupRequest;
import com.antithesis.cloudmag.controller.payload.response.JwtResponse;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.Role;
import com.antithesis.cloudmag.entity.User;
import com.antithesis.cloudmag.repository.RoleRepository;
import com.antithesis.cloudmag.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public MessageResponse<?> createUser(SignupRequest signUpRequest) throws RuntimeException {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return MessageResponse.builder()
                    .message("Error: Email is already in use!")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        // Create new user's account
        User user = new User(signUpRequest.getEmail(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleTypes.ADMINISTRATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleTypes.ADMINISTRATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(RoleTypes.CONTRIBUTOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleTypes.VIEWER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return MessageResponse.builder()
                .message("User registered successfully!")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<JwtResponse> createSignInPetition(LoginRequest loginRequest) throws RuntimeException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return MessageResponse.<JwtResponse>builder()
                .data(JwtResponse.builder()
                        .token(jwt)
                        .id(userDetails.getId())
                        .username(userDetails.getEmail())
                        .email(userDetails.getEmail())
                        .roles(roles)
                        .build())
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<List<User>> getAllUsers() {
        return MessageResponse.<List<User>>builder()
                .data(userRepository.findAll())
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<?> deleteUser(Long user_id) {
        userRepository.deleteById(user_id);
        return MessageResponse.builder()
                .message("Usuario borrado correctamente")
                .status(HttpStatus.OK)
                .build();
    }
}
