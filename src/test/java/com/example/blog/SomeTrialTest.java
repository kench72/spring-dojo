package com.example.blog;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Base64;

public class SomeTrialTest {
    @Test
    public void test() {
        var uuid = "3ed48e42-8ccb-4955-b631-9fd433b6a3be";
        var base64string = Base64.getEncoder().encodeToString(uuid.getBytes());
        System.out.println(base64string);
        //              => M2VkNDhlNDItOGNjYi00OTU1LWI2MzEtOWZkNDMzYjZhM2Jl
        // Cookie：SESSION=M2VkNDhlNDItOGNjYi00OTU1LWI2MzEtOWZkNDMzYjZhM2Jl
    }

    @Test
    public void bcrypt() {
        var encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("password"));
        System.out.println(encoder.encode("password"));
        System.out.println(encoder.encode("password"));
    }
}
