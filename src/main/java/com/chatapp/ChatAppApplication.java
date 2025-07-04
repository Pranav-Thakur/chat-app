package com.chatapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
public class ChatAppApplication {

    @Autowired
    private Environment env;

    @PostConstruct
    public void printActiveProfile() {
        System.out.println(">>>>> Runtime active profile: " + Arrays.toString(env.getActiveProfiles()));
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatAppApplication.class, args);
    }
}
