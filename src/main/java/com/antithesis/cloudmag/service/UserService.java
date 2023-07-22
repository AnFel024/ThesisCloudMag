package com.antithesis.cloudmag.service;

import com.antithesis.cloudmag.configuration.security.jwt.JwtUtils;
import com.antithesis.cloudmag.constant.RoleTypes;
import com.antithesis.cloudmag.controller.payload.request.LoginDto;
import com.antithesis.cloudmag.controller.payload.request.SignupDto;
import com.antithesis.cloudmag.controller.payload.response.JwtResponse;
import com.antithesis.cloudmag.controller.payload.response.MessageResponse;
import com.antithesis.cloudmag.entity.RoleEntity;
import com.antithesis.cloudmag.entity.UserEntity;
import com.antithesis.cloudmag.mapper.UserMapper;
import com.antithesis.cloudmag.model.User;
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
    private final UserMapper userMapper;

    public MessageResponse<?> createUser(SignupDto signUpDto) throws RuntimeException {
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return MessageResponse.builder()
                    .message("Error: Email is already in use!")
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        // Create new user's account
        UserEntity userEntity = new UserEntity(signUpDto.getEmail(), signUpDto.getEmail(),
                encoder.encode(signUpDto.getPassword()));

        Set<String> strRoles = signUpDto.getRole();
        Set<RoleEntity> roleEntities = new HashSet<>();

        if (strRoles == null) {
            RoleEntity userRoleEntity = roleRepository.findByName(RoleTypes.ADMINISTRATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roleEntities.add(userRoleEntity);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        RoleEntity adminRoleEntity = roleRepository.findByName(RoleTypes.ADMINISTRATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roleEntities.add(adminRoleEntity);
                        break;
                    case "mod":
                        RoleEntity modRoleEntity = roleRepository.findByName(RoleTypes.CONTRIBUTOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roleEntities.add(modRoleEntity);

                        break;
                    default:
                        RoleEntity userRoleEntity = roleRepository.findByName(RoleTypes.VIEWER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roleEntities.add(userRoleEntity);
                }
            });
        }

        userEntity.setRoleEntities(roleEntities);
        userRepository.save(userEntity);

        return MessageResponse.builder()
                .message("User registered successfully!")
                .status(HttpStatus.CREATED)
                .build();
    }

    public MessageResponse<JwtResponse> createSignInPetition(LoginDto loginDto) throws RuntimeException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return MessageResponse.<JwtResponse>builder()
                .data(JwtResponse.builder()
                        .token(jwt)
                        .username(userDetails.getEmail())
                        .email(userDetails.getEmail())
                        .roles(roles)
                        .build())
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll().stream()
                .map(userMapper::mapToUser)
                .collect(Collectors.toList());
        return MessageResponse.<List<User>>builder()
                .data(users)
                .status(HttpStatus.OK)
                .build();
    }

    public MessageResponse<?> deleteUser(String user_id) {
        userRepository.deleteById(user_id);
        return MessageResponse.builder()
                .message("Usuario borrado correctamente")
                .status(HttpStatus.OK)
                .build();
    }
}
