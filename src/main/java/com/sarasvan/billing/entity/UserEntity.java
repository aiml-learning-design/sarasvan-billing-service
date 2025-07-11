package com.sarasvan.billing.entity;

import com.sarasvan.billing.enums.AuthenticationProvider;
import com.sarasvan.billing.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private AuthenticationProvider provider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;

}

