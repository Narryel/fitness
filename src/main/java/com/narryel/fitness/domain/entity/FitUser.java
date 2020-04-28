package com.narryel.fitness.domain.entity;


import com.narryel.fitness.domain.enums.UserStatus;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@FieldDefaults(level = PRIVATE)
public class FitUser extends JpaEntity {

    @Column(name = "nickname")
    String nickName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    UserStatus status;

    String login;

    String password;


}
