package com.narryel.fitness.domain.entity;


import com.narryel.fitness.domain.enums.UserStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(columnList = "chatid"),
        @Index(columnList = "telegramuserid")
})
@FieldDefaults(level = PRIVATE)
@Accessors(chain = true)
public class FitUser extends JpaEntity {

    @Column(name = "nickname")
    String nickName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    UserStatus status;

    String login;

    String password;

    @Column(unique = true)
    Long chatId;

    @Column(unique = true)
    Integer telegramUserId;

}
