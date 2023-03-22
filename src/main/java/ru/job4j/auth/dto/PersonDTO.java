package ru.job4j.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.auth.util.CapitalLetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    @NotBlank(message = "Login must be not empty")
    private String login;
    @Size(min = 6, message = "must be more than 6 characters")
    @CapitalLetter(message = "Password error. Must be capital letter")
    private String password;
}
