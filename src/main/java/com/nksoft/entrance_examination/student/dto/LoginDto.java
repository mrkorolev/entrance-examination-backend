package com.nksoft.entrance_examination.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
public class LoginDto {
    @NotBlank(message = "Email can't be null/empty")
    @Size(max = 64, message = "Email must not exceed 64 characters")
    @Email
    private String email;
    @NotBlank(message = "Password can't be null/empty")
    @Size(min = 8, max = 16, message = "Password should be from 8 to 16 characters long")
    private String password;
}
