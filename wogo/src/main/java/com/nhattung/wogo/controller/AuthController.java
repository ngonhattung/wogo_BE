package com.nhattung.wogo.controller;


import com.nhattung.wogo.dto.request.LoginRequestDTO;
import com.nhattung.wogo.dto.request.LogoutRequestDTO;
import com.nhattung.wogo.dto.request.RefreshTokenRequestDTO;
import com.nhattung.wogo.dto.request.RegisterRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.JwtResponseDTO;
import com.nhattung.wogo.dto.response.LogoutResponseDTO;
import com.nhattung.wogo.dto.response.UserResponseDTO;
import com.nhattung.wogo.security.jwt.JwtUtils;
import com.nhattung.wogo.security.user.WogoUserDetailsService;
import com.nhattung.wogo.service.auth.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final WogoUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserService authService;

    @PostMapping("/signup")
    public ApiResponse<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ApiResponse.<UserResponseDTO>builder()
                .message("Create User Success!")
                .result(authService.createUser(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<JwtResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            request.getPhone(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = jwtUtils.createAccessToken(authentication);
            String refreshToken = jwtUtils.createRefreshToken(authentication);
            return ApiResponse.<JwtResponseDTO>builder()
                    .message("Login Success!")
                    .result(JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .expirationDate(jwtUtils.verifyToken(accessToken).getExpiration())
                            .build())
                    .build();
        }catch (AuthenticationException e) {
            //401
            return ApiResponse.<JwtResponseDTO>builder()
                    .message("Login Failed! " + "Phone or password is incorrect")
                    .build();
        }
    }

    @PostMapping("/refresh")
    public ApiResponse<JwtResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {

        try {
            if(jwtUtils.validateJwtToken(request.getAccessToken())){

                if(jwtUtils.isTokenBlacklisted(request.getAccessToken())){
                    return ApiResponse.<JwtResponseDTO>builder()
                            .message("Token has been blacklisted")
                            .build();
                }
                String phone = jwtUtils.getPhoneFromJwtToken(request.getAccessToken());
                UserDetails userDetails = userDetailsService.loadUserByUsername(phone);
                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                String accessToken = jwtUtils.createAccessToken(auth);

                return ApiResponse.<JwtResponseDTO>builder()
                        .message("Token refreshed successfully")
                        .result(JwtResponseDTO.builder()
                                .accessToken(accessToken)
                                .refreshToken(request.getAccessToken())
                                .expirationDate(jwtUtils.verifyToken(accessToken).getExpiration())
                                .build())
                        .build();
            }else{
                //401
                return ApiResponse.<JwtResponseDTO>builder()
                        .message("Invalid access token")
                        .build();
            }
        }catch (AuthenticationException e) {
            //401
            return ApiResponse.<JwtResponseDTO>builder()
                    .message("Token refresh failed! " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/logout")
    public ApiResponse<LogoutResponseDTO> logout(@Valid @RequestBody LogoutRequestDTO request) {
        try {
            // Revoke tokens
            jwtUtils.logout(request);
            return ApiResponse.<LogoutResponseDTO>builder()
                    .message("Logout successful")
                    .build();
        } catch (Exception e) {
            // Handle any exceptions that occur during logout
            return ApiResponse.<LogoutResponseDTO>builder()
                    .message("Logout failed! " + e.getMessage())
                    .build();
        }
    }
}
