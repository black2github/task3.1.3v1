package ru.kata.spring.boot_security.demo.configs.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            // поле, код ошибки, сообщение ошибки
            errors.rejectValue("email", "", "Email can't be null or empty");
        }

        // Проверяем, что у человека имя начинается с заглавной буквы
        // Если имя не начинается с заглавной буквы - выдаем ошибку
        if (userService.findUserByEmail(user.getEmail()) != null) {
            errors.rejectValue("Email", "", "Email already bound to someone.");
        }

        if (user.getPassword().isEmpty() || user.getPassword().isBlank()) {
            errors.rejectValue("password", "", "Password can't be null or empty");
        }
    }
}
