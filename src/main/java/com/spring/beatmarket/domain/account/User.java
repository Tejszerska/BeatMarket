package com.spring.beatmarket.domain.account;

import com.spring.beatmarket.domain.catalog.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
@Builder
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue( generator = "users_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name="users_id_seq",
            sequenceName = "users_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, unique = true)

    private String email;

    private String password;

    private boolean enabled = false;

    private Collection<String> authorities = new HashSet<>();

    private String confirmationToken;

    public boolean confirm(){
        this.setEnabled(true);
        this.setConfirmationToken(null);
        return true;
    }
}
