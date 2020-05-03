package com.narryel.fitness.repository;

import com.narryel.fitness.domain.entity.UserState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStateRepository extends JpaRepository<UserState, Long> {

    Optional<UserState> findByChatId(Long chatId);

    void deleteByChatId(Long chatId);
}
