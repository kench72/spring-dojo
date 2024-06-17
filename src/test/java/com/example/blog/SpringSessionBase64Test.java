package com.example.blog;

import org.junit.jupiter.api.Test;

import java.util.Base64;

public class SpringSessionBase64Test {
    @Test
    public void test() {
        var uuid = "3ed48e42-8ccb-4955-b631-9fd433b6a3be";
        var base64string = Base64.getEncoder().encodeToString(uuid.getBytes());
        System.out.println(base64string);
        //              => M2VkNDhlNDItOGNjYi00OTU1LWI2MzEtOWZkNDMzYjZhM2Jl
        // Cookie：SESSION=M2VkNDhlNDItOGNjYi00OTU1LWI2MzEtOWZkNDMzYjZhM2Jl
    }
}
