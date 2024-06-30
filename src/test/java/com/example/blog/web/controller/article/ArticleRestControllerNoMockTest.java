package com.example.blog.web.controller.article;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleRestControllerNoMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void mockMvc(){
        assertThat(mockMvc).isNotNull();
    }

}