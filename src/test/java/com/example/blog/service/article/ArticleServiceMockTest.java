package com.example.blog.service.article;

import com.example.blog.repository.article.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceMockTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService cut;

    @Test
    public void cut() {
        assertThat(cut).isNotNull();
    }

    @Test
    public void mockPractice() {
        // Arrange
        when(articleRepository.selectById(999)).thenReturn(Optional.of(
                new ArticleEntity(999, null, null, null, null)
        ));

        // Act

        // Assert
        assertThat(articleRepository.selectById(999))
                .isPresent()
                .hasValueSatisfying(article -> {
                    assertThat(article.id()).isEqualTo(999);
                });

        assertThat(articleRepository.selectById(111)).isEmpty();
    }

    @Test
    @DisplayName("findById: 指定されたIDのArticleが存在するとき、ArticleEntityを返す")
    public void findById_returnArticleEntity() {
        // ## Arrange ##
        when(articleRepository.selectById(999)).thenReturn(Optional.of(
                new ArticleEntity(999
                        , "title_999"
                        , "body_999"
                        , LocalDateTime.of(2022, 1, 1, 10, 0, 0)
                        , LocalDateTime.of(2022, 2, 1, 11, 0, 0)
                )
        ));

        // ## Act ##
        var actual = cut.findById(999);

        // ## Assert ##
        Assertions.assertThat(actual).isPresent();

        Assertions.assertThat(actual)
                .isPresent()
                .hasValueSatisfying(article -> {
                    Assertions.assertThat(article.id()).isEqualTo(999);
                    Assertions.assertThat(article.title()).isEqualTo("title_999");
                    Assertions.assertThat(article.content()).isEqualTo("body_999");
                    Assertions.assertThat(article.createdAt()).isEqualTo("2022-01-01T10:00:00");
                    Assertions.assertThat(article.updatedAt()).isEqualTo("2022-02-01T11:00:00");
                });
    }

    @Test
    @DisplayName("findById: 指定されたIDのArticleが存在しないとき、Optional.Emptyを返す")
    public void findById_returnEmpty() {
        // ## Arrange ##
        when(articleRepository.selectById(999)).thenReturn(Optional.empty());

        // ## Act ##
        var actual = cut.findById(999);

        // ## Assert ##
        Assertions.assertThat(actual).isEmpty();
    }
}