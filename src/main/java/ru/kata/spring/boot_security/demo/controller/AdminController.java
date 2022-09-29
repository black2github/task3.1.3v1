package ru.kata.spring.boot_security.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.configs.util.UserValidator;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private UserValidator userValidator;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    /*
    GET /admin/ - получение списка всех пользователей
     */
    @GetMapping()
    public String list(ModelMap model) {
        log.debug("list: <- ");
        List<User> users = userService.listAll();
        model.addAttribute("users", users);
        log.debug("list: -> " + users);
        // return "admin/index";
        return "admin/admin_panel";
    }

    /*
    GET /users/:id - заполнение данных данных о конкретном пользователе для просмотра
     */
    @GetMapping("/{id}")
    public String show(@PathVariable("id") long id, ModelMap model) {
        log.debug("show: <- id=" + id);

        // получение одного пользователя по id и передача на отображение
        User user = userService.find(id);
        model.addAttribute("user", user);
        log.debug("show: -> " + user);
        return "admin/show";
    }

    /*
    GET /users/new - создание пустого объекта для заполнения данными формы
    и ссылка на форму создания нового пользователя
     */
    // @GetMapping(value = "/new")
    // public String newUser(@ModelAttribute("user") User user) {
    //     log.debug("newUser: <- ");
    //     return "admin/new";
    // }

    /*
     POST /users/ - обработка данных с формы:
      - создание пользователя по объекту, заполненному на форме
      - перенаправление на начальну страницу вывода списка
     */
    // @PostMapping()
    // public String create(@ModelAttribute("user") User user) {
    //     log.debug("create: <- " + user);
    //     User u = userService.create(user);
    //     log.debug("create: -> " + u);
    //     return "redirect:/admin";
    // }

    /*
        POST /users/ - обработка данных с формы:
      - создание пользователя по объекту, заполненному на форме
      - перенаправление на начальну страницу вывода списка
     */
    @PostMapping("/user")
    public String createUser(@ModelAttribute("user") @Valid User user,
                             @RequestParam(value = "role", required = false) String[] roles,
                             BindingResult bindingResult) {
    //public String createUser(@ModelAttribute("user") User user) {
        log.debug("createUser: <- " + user);

        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("createUser: can't create. " + bindingResult.toString());
            return "redirect:/admin";
        }

        if (roles != null) {
            for (String role : roles) {
                user.getRoles().add(new Role(role));
            }
        }
        User u = userService.create(user);
        log.debug("createUser: -> " + u);
        return "redirect:/admin";
    }

    /*
     GET /users/:id/edit - заполнение объекта данными
     и отправка на отправка на форму редактирование данных пользователя
     */
    // @GetMapping("/{id}/edit")
    // public String edit(@PathVariable("id") long id, Model model) {
    //     log.debug("edit: <- id=" + id);
    //     User user = userService.find(id);
    //     model.addAttribute("user", user);
    //     log.debug("edit: -> " + user);
    //     return "admin/edit";
    // }

    /*
     PATCH /users/:id - обновление данных пользователя c конкретным id
     */
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("user") User user, @PathVariable("id") long id) {
        log.debug("update: <- user=" + user + ", id=" + id);
        userService.update(id, user);
        log.debug("update: ->");
        return "redirect:/admin";
    }

    /*
     PATCH /user - обновление данных пользователя c id из объекта
     */
    @PatchMapping("/user")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "id") Long id,
                             @RequestParam(value = "role", required = false) String[] roles) {
        log.debug(String.format("updateUser: <- user=%s, role=%s, id=%d", user, roles, id));
        if (roles != null) {
            for (String role : roles) {
                user.getRoles().add(new Role(role));
            }
        }
        log.trace("updateUser: user = " + user);
        userService.update(id, user);
        log.debug("updateUser: ->");
        return "redirect:/admin";
    }

    /*
     DELETE /user - обновление данных пользователя c id из объекта
     */
    @DeleteMapping("/user")
    public String deleteUser(@RequestParam(value = "id") Long id) {
        log.debug(String.format("deleteUser: <- id=%d", id));
        userService.delete(id);
        log.debug("deleteUser: ->");
        return "redirect:/admin";
    }

    /*
     DELETE /users/:id - удаление пользователя по id
     */
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id, Model model) {
        log.debug("delete: <- id=" + id);
        userService.delete(id);
        log.debug("delete: ->");
        return "redirect:/admin";
    }
}
