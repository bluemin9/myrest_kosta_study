package com.blueming.myrest.accounts;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value= EnumType.STRING)
    private Set<AccountRole> roles;
}
