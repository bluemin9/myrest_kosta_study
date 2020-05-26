package com.blueming.myrest.events;

import com.blueming.myrest.accounts.Account;
import com.blueming.myrest.accounts.AccountSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
@Entity
public class Event {

    // 커스텀한 값으로 mapping
    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager;

    // maria DB는 IDENTITY
    // oracle 은 SEQUENCE
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;

    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;

    private String location;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void update() {
        // free 값을 update
        if (basePrice == 0 && maxPrice == 0) {
            this.free = true;
        } else {
            this.free =false;
        }

        // offline update
        if (this.location == null || this.location.isBlank()) {
            // online
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}
