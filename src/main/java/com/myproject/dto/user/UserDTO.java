package com.myproject.dto.user;

import com.myproject.validate.anotations.Password;
import com.myproject.validate.anotations.Username;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;

    @Username
    private String username;

    @Password
    private String password;

    private String name;
    private String roleName;

}
