package ru.kata.spring.boot_security.demo.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Role{id=%d, name='%s'}", id, name);
    }

    @Override
    public int hashCode() {
        //return Objects.hash(id, name);
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || (o.getClass() != Role.class)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        return (getName().equals(((Role) o).getName()));
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
