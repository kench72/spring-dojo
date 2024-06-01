package com.example.blog.it;

import com.example.blog.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/// spring Bootアプリケーション起動時にテストでは利用できそうなportを見繕って起動してもらう。
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationAndLoginIT {

    private static final String TEST_USERNAME = "user1";

    private static final String TEST_PASSWORD = "password1";

    private static final String DUMMY_SESSION_ID = "session_id_1";

    // このテストを起動した際、
    // @SpringBootTestアノテーションを記述しているので、
    // Springアプリケーションが起動される ⇒ その時DIコンテナも起動される。
    // コンテナ起動後にWebTestClientがBean登録されて
    // @AutowiredにてDIコンテナにBean登録済のWebTestClientをフィールドに設定してくれる。
    // そして、JUnitのランナー（テストをたくさん実行してくれる奴）が@Testの付いたメソッドを見つけて実行してくれる。
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        userService.delete(TEST_USERNAME);
    }

    @AfterEach
    public void afterEach() {
        userService.delete(TEST_USERNAME);
    }

    @Test
    public void integrationTest() {
        String xsrfToken = getRoot();
        register(xsrfToken);

        //ログイン成功
        loginSuccess(xsrfToken);

        // CookieにXSFT-TOKENがない
        loginFailure_NoXSRFTokenInCookie(xsrfToken);

        // ユーザー名がＤＢに存在する
        // パスワードがＤＢに保存されているパスワードと違う
        // CookieのXSRF-TOKENとヘッダーのX-XSRF-TOKENの値が一致する
        // → 200 OKが返却される
        // → レスポンスに Set-Cookie: JSESSIONIDが返却される
    }

    private String getRoot() {
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
                        xsrfTokenCookie -> assertThat(xsrfTokenCookie.getValue()).isNotBlank()
                );

        return xsrfTokenCookieOpt.get().getValue();
    }

    private void register(String xsrfToken) {

        // ## Arrange
        var bodyJson = String.format("""
                        {
                            "username": "%s",
                            "password": "%s"
                        }
                        """,
                TEST_USERNAME, TEST_PASSWORD);

        // ## Act
        var responseSpec = webTestClient
                .post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        // ## Assert
        responseSpec.expectStatus().isCreated();

    }

    private void loginSuccess(String xsrfToken) {

        // ## Arrange
        var bodyJson = String.format("""
                        {
                            "username": "%s",
                            "password": "%s"
                        }
                        """,
                TEST_USERNAME, TEST_PASSWORD);

        // ## Act
        var responseSpec = webTestClient
                .post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("XSRF-TOKEN", xsrfToken)
                .cookie("JSESSIONID", DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        // ## Assert
        responseSpec
                .expectStatus().isOk()
                .expectCookie().value("JSESSIONID", v -> assertThat(v)
                        .isNotBlank()
                        .isNotEqualTo(DUMMY_SESSION_ID)
                );
    }

    private void loginFailure_NoXSRFTokenInCookie(String xsrfToken) {

        // ## Arrange
        var bodyJson = String.format("""
                        {
                            "username": "%s",
                            "password": "%s"
                        }
                        """,
                TEST_USERNAME, TEST_PASSWORD);

        // ## Act
        var responseSpec = webTestClient
                .post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                /*
                .cookie("XSRF-TOKEN", xsrfToken)
                 */
                .cookie("JSESSIONID", DUMMY_SESSION_ID)
                .header("X-XSRF-TOKEN", xsrfToken)
                .bodyValue(bodyJson)
                .exchange();

        // ## Assert
        responseSpec.expectStatus().isForbidden();
    }
}
