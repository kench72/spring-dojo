package com.example.blog.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/// spring Bootアプリケーション起動時にテストでは利用できそうなportを見繕って起動してもらう。
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationAndLoginIT {

    // このテストを起動した際、
    // @SpringBootTestアノテーションを記述しているので、
    // Springアプリケーションが起動される ⇒ その時DIコンテナも起動される。
    // コンテナ起動後にWebTestClientがBean登録されて
    // @AutowiredにてDIコンテナにBean登録済のWebTestClientをフィールドに設定してくれる。
    // そして、JUnitのランナー（テストをたくさん実行してくれる奴）が@Testの付いたメソッドを見つけて実行してくれる。
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void integrationTest() {
        assertThat(webTestClient).isNotNull();
    }

}
