package com.narryel.fitness.dao;

import com.narryel.fitness.domain.entity.FitUser;
import com.narryel.fitness.repository.FitUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final FitUserRepository userRepository;


    public FitUser upsertUser(FitUser user){
        return userRepository.save(user);
    }
}
