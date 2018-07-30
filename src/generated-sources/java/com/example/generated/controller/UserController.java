package com.example.generated.controller;

import com.example.generated.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping
    public Mono<User> createUser(User userDto) {
        User user = new User();
        return Mono.just(user);
    }
}
