package com.example.blog.web.controller.article;

import com.example.blog.service.article.ArticleService;
import com.example.blog.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleRestController {

    private final ArticleService articleService;

    @GetMapping("/articles/{id}")
    public ArticleDTO showArticle(@PathVariable("id") long id) {
        // memo：メソッド参照
        // 引数のないラムダ式はメソッド参照で定義可能。
        // ResourceNotFoundException::new
        // ::newだと引数のないデフォルトコンストラクタ
        return articleService.findById(id) // Optional<ArticleEntity>
                .map(ArticleDTO::from)
                .orElseThrow(ResourceNotFoundException::new); // Optional<ArticleDTO>
    }
}
