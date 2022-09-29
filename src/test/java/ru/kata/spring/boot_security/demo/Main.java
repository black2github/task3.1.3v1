package ru.kata.spring.boot_security.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.debug("createRole: <-");

        Role role1 = new Role("USER");
        Role role2 = new Role("ADMIN");

        User user = new User("firstName1", "secondName2", 1);
        user.setRoles(new LinkedHashSet<Role>(Arrays.asList(role1, role2)));

        log.debug("user2="+user);
        log.debug("user.getRoles().contains(role1) = " + user.getRoles().contains(role1));
        role1.setId(1l);
        log.debug("user.getRoles().contains(role1) = " + user.getRoles().contains(role1));
    }
}
