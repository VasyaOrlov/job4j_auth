package ru.job4j.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.job4j.auth.util.CapitalLetter;
import ru.job4j.auth.util.ValidGroup;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "person")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @NotNull(message = "Id must be non null", groups = {ValidGroup.OnUpdate.class})
    private int id;
    @NotBlank(message = "Login must be not empty", groups = {ValidGroup.OnUpdate.class, ValidGroup.OnCreate.class})
    private String login;
    @Size(min = 6, message = "must be more than 6 characters",
            groups = {ValidGroup.OnUpdate.class, ValidGroup.OnCreate.class})
    @CapitalLetter(message = "Password error. Must be capital letter")
    private String password;
}