package com.myproject.service;

import com.myproject.constant.ErrorMessage;
import com.myproject.dto.JwtResponse;
import com.myproject.dto.user.UserDTO;
import com.myproject.dto.user.UserLogin;
import com.myproject.dto.user.UserResponseDTO;
import com.myproject.enums.RoleEnum;
import com.myproject.exception.CustomIllegalArgumentException;
import com.myproject.model.User;
import com.myproject.repository.RoleRepository;
import com.myproject.repository.UserRepository;
import com.myproject.security.CustomUserDetails;
import com.myproject.security.JwtTokenProvider;
import com.myproject.validate.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.myproject.constant.SecurityConstant.DEFAULT_AVATAR;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserValidation userValidation;

    public UserResponseDTO register(UserDTO userDTO){
        userValidation.checkUsernameExist(userDTO.getUsername());
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setRole(roleRepository.findByName(RoleEnum.USER.getValue()));
        user.setAvatar(DEFAULT_AVATAR);
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    public JwtResponse logIn(UserLogin userLogin) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLogin.getUsername(),
                            userLogin.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String jwt = jwtTokenProvider.generateToken(userDetails);
            return new JwtResponse(jwt);
        } catch (Exception exception) {
            throw new CustomIllegalArgumentException(ErrorMessage.PASSWORD_USERNAME_WRONG);
        }

    }

}
