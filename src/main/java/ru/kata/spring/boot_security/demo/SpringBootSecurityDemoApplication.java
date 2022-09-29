package ru.kata.spring.boot_security.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.LinkedHashSet;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {
	private static final Logger log = LoggerFactory.getLogger(SpringBootSecurityDemoApplication.class);

	@Autowired
	private UserService userService;

	@Autowired
	PasswordEncoder bCryptPasswordEncoder;

	@PostConstruct
	public void init() {
		//Role role1 = new Role("ROLE_USER");
		Role role1 = new Role("USER");
		Role role2 = new Role("ADMIN");
		User user = new User("admin@a.b", bCryptPasswordEncoder.encode("admin"));
		user.setFirstName("adminFirstName");
		user.setLastName("adminLastName");
		user.setAge(99);
		user.setRoles(new LinkedHashSet<Role>(Arrays.asList(role1, role2)));
		user = userService.create(user);

		user = new User("user@a.b", bCryptPasswordEncoder.encode("user"));
		user.getRoles().add(new Role("USER"));
		user.setFirstName("userFirstName");
		user.setLastName("userLastName");
		user.setAge(112);
		userService.create(user);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
	}

}
