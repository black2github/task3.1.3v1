package ru.kata.spring.boot_security.demo.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_email", columnNames = {"email"})
})
public class User implements UserDetails {
    private static final Logger log = LoggerFactory.getLogger(User.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Version
    private int version;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "email")
    @NotEmpty(message = "Email should not be empty")
    private String email;

    @Column(name = "password")
    @NotEmpty(message = "Password should not be empty")
    private String password;

    private int age;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Set<Role> roles = new LinkedHashSet<>();

    public User() {
    }

    public User(String email, String password, int age) {
        this(email, password, age, null, null);
    }

    public User(String email, String password) {
        this(email, password, 0, null, null);
    }

    public User(String email, String password, int age, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.age = age;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPassword(String secondName) {
        this.password = secondName;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // UserDetails support
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public int getAge() {
        return age;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.debug("getAuthorities: ->" + mapRolesToAuthorities(roles));
        return mapRolesToAuthorities(roles);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        // ?????????????? ???????????????????????????? ???????? ???????????????? ?????????? ???????????????? ???????????????? "ROLE_"
        return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())).collect(Collectors.toList());
    }

    public List<String> getRoleNames() {
        return roles.stream().map(r -> r.getName()).collect(Collectors.toList());
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, email='%s', password='%s', age=%d, firstName='%s', lastName='%s', roles=%s}",
                id, email, password, age, firstName, lastName, Arrays.toString(roles.toArray()));
    }

    @Override
    public int hashCode() {
        return
                Objects.hash(id, email, password, age, firstName, lastName, roles);
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || (o.getClass() != User.class)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        return (email.equals(((User) o).getEmail())
                && password.equals(((User) o).getPassword())
                && age == ((User) o).getAge()
                && stringEquals(firstName, ((User) o).getFirstName())
                && stringEquals(lastName, ((User) o).getLastName())
                // compare two set
                && roles.containsAll(((User) o).getRoles())
                && ((User) o).getRoles().containsAll(roles)
        );
    }

    private boolean stringEquals(String s1, String s2) {
        if (s1 == s2) {
            return true;
        }
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 != null && s1.equals(s2)) {
            return true;
        }
        return false;
    }
}

