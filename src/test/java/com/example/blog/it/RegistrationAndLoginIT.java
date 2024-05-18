package com.example.blog.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

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
        // ## Arrange ##

        // ## Act ##
        var responseSpec = webTestClient.get().uri("/").exchange();

        // ## Assert ##
        // webFluxが提供する検証処理（assertメソッドに相当）。
        responseSpec.expectStatus().isNoContent();

        var response = responseSpec.returnResult(String.class);

        // getFirstは@nullable付いてるのでnullを返却する可能性がある ⇒ こう言う時はoptionalを利用。
        Optional<ResponseCookie> xsrfTokenCookieOpt = Optional.ofNullable(response.getResponseCookies().getFirst("XSRF-TOKEN"));

        // xsrfTokenCookieOptは以下のいずれか。
        //   a) Optional<null>
        //   b) Optional<ResponseCookie>
        // isPresent・・・Optional<ResponseCookie>の時はテスト成功、Optional<null>の時にテスト失敗。
        //                ⇒ a)でないことを検証する。
        // hasValueSatisfying・・・Optionalの中身：ResponseCookieを検証できる（この中身がラムダ式の引数で受け取れる）。
        //                ⇒ 事前にisPresentすることで、ResponseCookieはオブジェクト自体は存在する。
        assertThat(xsrfTokenCookieOpt)
                .isPresent()
                .hasValueSatisfying(
                        xsrfTokenCookie-> assertThat(xsrfTokenCookie.getValue()).isNotBlank()
                );
    }

}
