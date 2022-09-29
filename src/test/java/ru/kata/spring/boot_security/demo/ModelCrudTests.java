package ru.kata.spring.boot_security.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
class ModelCrudTests {
	private static final Logger log = LoggerFactory.getLogger(ModelCrudTests.class);

	private static final int RANGE = 10000;
	private static Random r = new Random();

	@Autowired
	//@Qualifier("userService")
	private UserService userService;

	@Autowired
	//@Qualifier("userDetailsServiceJpa")
	@Qualifier("userService")
	private UserDetailsService userDetailsService;

	// @Test
	//  void contextLoads() {
	// }

	@Test
	void createRole() {
		log.debug("createRole: <-");

		Role role1 = new Role("USER");
		Role role2 = new Role("ADMIN");

		User user = new User("user"+r.nextInt(RANGE), "password"+r.nextInt(RANGE));
		user.setRoles(new LinkedHashSet<Role>(Arrays.asList(role1, role2)));
		user = userService.create(user);
		User user2 = userService.find(user.getId());
		assertNotNull("User with roles not found", user2);
		log.debug("createRole: user2="+user2);
		assertTrue("Role " + role1.getName() + " not found", user2.getRoles().contains(role1));
		assertTrue("Role " + role2.getName() + " not found", user2.getRoles().contains(role2));

		user.getRoles().clear();
		userService.update(user.getId(), user);
		user = userService.find(user.getId());
		assertTrue("Role Set must be empty", user.getRoles().isEmpty());
		// clean
		userService.delete(user);
	}

	@Test
	void createUser() {
		log.debug("createUser: <-");

		User user = new User("user"+r.nextInt(RANGE), "password"+r.nextInt(RANGE));
		user.getRoles().add(new Role("ROLE_USER"));
		user = userService.create(user);
		assertNotNull("User must not be null.", user);

		User user2 = userService.find(user.getId());
		assertEquals("Users are not equal.", user, user2);

		userService.delete(user);
	}

	@Test
	void deleteUser() {
		User user = new User("user"+r.nextInt(RANGE), "user"+r.nextInt(RANGE));
		user.getRoles().add(new Role("ROLE_USER"));
		user = userService.create(user);
		user = userService.find(user.getId());
		assertNotNull("deleteUser: Just created user not found", user);

		userService.delete(user);
		user = userService.find(user.getId());
		assertNull("delete User: Found user after delete", user);
	}

	@Test
	void updateUser() {
		User user = new User("name"+r.nextInt(RANGE), "second1"+r.nextInt(RANGE));
		user.getRoles().add(new Role("ROLE_USER"));
		user = userService.create(user);

		User user2 = new User("name"+r.nextInt(RANGE), "second"+r.nextInt(RANGE), 10, "fistNameX", "lastNameX");
		user2.getRoles().add(new Role("ROLE_ADMIN"));
		log.debug("user2="+user2);

		user = userService.update(user.getId(), user2);
		log.debug("user after update = "+user);

		User user3 = userService.find(user.getId());
		log.debug("user3 = "+user3);
		log.debug("user3==user ? " + user3.equals(user));
		assertEquals("User not updated properly.", user3, user);

		userService.delete(user3.getId());
	}

	@Test
	void loadUser() {
		User user = new User("name"+r.nextInt(RANGE), "password"+r.nextInt(RANGE));
		user.getRoles().add(new Role("ROLE_USER"));
		user = userService.create(user);
		User u2 = userService.find(user.getId());
		log.debug("loadUser: user = " + u2);

		UserDetails user2 = userDetailsService.loadUserByUsername(user.getEmail());
		assertEquals("usernames are not equal.", user.getEmail(), user2.getUsername());
		assertEquals("passwords are not equal.", user.getPassword(), user2.getPassword());

		userService.delete(user.getId());
	}
}
