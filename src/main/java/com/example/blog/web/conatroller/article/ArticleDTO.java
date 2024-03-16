package com.example.blog.web.conatroller.article;

import java.time.LocalDateTime;

public record ArticleDTO(
        long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
