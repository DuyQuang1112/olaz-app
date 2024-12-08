package com.myproject.service;

import com.myproject.dto.user.UserResponseDTO;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.User;
import com.myproject.repository.UserRepository;
import com.myproject.utils.UploadCloudinary;
import com.myproject.validate.UserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UploadCloudinary uploadCloudinary;
    private final UserValidation userValidation;

    public UserResponseDTO getUserById(Integer userId){
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            return new UserResponseDTO(userOptional.get());
        } else {
            throw new ResourceNotFoundException("User is not exist");
        }
    }

    public UserResponseDTO uploadAvatar(MultipartFile file, Integer userId) throws IOException {
        try {
            String imageUrl = uploadCloudinary.uploadImage(file);
            Optional<User> userOptional = userRepository.findById(userId);
            User user= new User();
            if(userOptional.isPresent()){
                System.out.println(imageUrl);
                user = userOptional.get();
                user.setAvatar(imageUrl);
                userRepository.save(user);
            }
            return new UserResponseDTO(user);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void updateName(Integer userId, String name){
        userValidation.validateName(name);
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setName(name);
            userRepository.save(user);
        } else {
            throw new ResourceNotFoundException("User is not exist");
        }
    }
}
