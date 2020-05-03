package com.narryel.fitness.repository;

import com.narryel.fitness.domain.entity.FitUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FitUserRepository extends JpaRepository<FitUser, Long> {

    Optional<FitUser> findByChatId(Long chatId);

    Optional<FitUser> findByTelegramUserId(Integer telegramId);

    List<FitUser> findAllByNickName(String nickName);
}
