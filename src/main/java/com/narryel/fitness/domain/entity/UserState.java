package com.narryel.fitness.domain.entity;

import com.narryel.fitness.domain.enums.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

import static javax.persistence.EnumType.STRING;

@Entity
@Accessors(chain = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserState extends JpaEntity{

    @Enumerated(STRING)
    State state;

    @Column(unique = true)
    Long chatId;
}
