package com.foongdoll.portfolio.todoongs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TodoongsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoongsApplication.class, args);
    }

}
