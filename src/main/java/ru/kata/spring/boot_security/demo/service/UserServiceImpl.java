package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserRepository;

@Service("userService")
@Repository
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(User user) {
        log.debug("create: <- " + user);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> list(int count) {
        log.debug("list: <- count = " + count);

        Iterator<User> it = userRepository.findAll().iterator();
        List<User> list = new ArrayList<>();
        for (int i = 0; it.hasNext() && i < count; i++) {
            User user = it.next();
            list.add(user);
        }
        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> listAll() {
        log.debug("listAll: <-");
        List<User> list = new ArrayList<>();
        Iterable<User> i = userRepository.findAll();
        for (User user : i) {
            list.add(user);
        }
        log.debug("listAll: -> " + Arrays.toString(list.toArray()));
        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public User find(Long id) {
        log.debug("find: <- id=" + id);
        Optional<User> u = userRepository.findById(id);
        if (u.isEmpty()) {
            log.warn("find: User with id=" + id + " not found");
        }
        return u.orElse(null);
    }

    @Override
    public void delete(User user) {
        log.debug("delete: <- " + user);
        userRepository.delete(user);
    }

    @Override
    public void delete(Long id) {
        log.debug("delete: <- id=" + id);
        User usr = find(id);
        if (usr != null) {
            delete(usr);
        } else {
            log.warn("delete: User with id=" + id + " not found");
        }
    }

    @Override
    public User update(long id, User user) {
        log.debug(String.format("update: <- id=%d, user=%s", id, user));

        // User u = userRepository.findById(id).get();
        User u = userRepository.findById(id).orElse(null);
        if (u == null) {
            log.warn("update: User with id=" + id + " not found");
            return null;
        }
        if (user != null) {
            u.setEmail(user.getEmail());
            if (user.getPassword() != null) {
                u.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            u.setAge(user.getAge());
            u.setFirstName(user.getFirstName());
            u.setLastName(user.getLastName());
            u.setRoles(user.getRoles());
            u = userRepository.save(u);
        }

        log.debug("update: -> " + u);
        return u;
    }

    @Override
    public User findUserByEmail(String username) {
        log.debug("findUserByEmail: <- " + username);
        User user = userRepository.findUserByEmail(username);
        log.debug("findUserByEmail: -> " + user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug(String.format("loadUserByUsername: <- username='%s'", username));
        User user = userRepository.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username='" + username + "' not found.");
        }
        log.debug("loadUserByUsername: -> " + user);
        return user;
    }
}
