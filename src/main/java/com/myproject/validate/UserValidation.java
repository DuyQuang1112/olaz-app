package com.myproject.validate;

import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.User;
import com.myproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.myproject.constant.ErrorMessage.USERNAME_DUPLICATE;

@Component
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepository userRepository;

    public void validateName(String name){
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException("Name must be between 1 and 20 characters");
        }
    }

    public void checkUsernameExist(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            throw new ResourceNotFoundException(USERNAME_DUPLICATE);
        }
    }

}
