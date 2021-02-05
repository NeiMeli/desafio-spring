package com.bootcamp.springchallenge.controller.articlequery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleQueryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void test() {
        // mockMvc.perform(get("").params(new Query().getAsMap()));
    }

}