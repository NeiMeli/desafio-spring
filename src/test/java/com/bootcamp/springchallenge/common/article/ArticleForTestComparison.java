package com.bootcamp.springchallenge.common.article;

import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.controller.common.util.DoubleProcessor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArticleForTestComparison {
    private final ArticleResponseDTO dto;

    public ArticleForTestComparison(ArticleResponseDTO dto) {
        this.dto = dto;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArticleForTestComparison)) return false;
        ArticleForTestComparison other = (ArticleForTestComparison) obj;
        return dto.getId() == other.dto.getId() &&
                dto.getName().equals(other.dto.getName()) &&
                dto.getCategory().equals(other.dto.getCategory()) &&
                DoubleProcessor.equal(dto.getPrice(), other.dto.getPrice()) &&
                dto.getPrestige().equals(other.dto.getPrestige());
    }

    public static Stream<ArticleForTestComparison> toComparableStream(List<ArticleResponseDTO> dtos) {
        return dtos.stream().map(ArticleForTestComparison::new);
    }

    public static List<ArticleForTestComparison> toComparableList(List<ArticleResponseDTO> dtos) {
        return dtos.stream().map(ArticleForTestComparison::new).collect(Collectors.toList());
    }

    public static ArticleForTestComparison[] toComparableArray(List<ArticleResponseDTO> dtos) {
        return dtos.stream().map(ArticleForTestComparison::new).toArray(ArticleForTestComparison[]::new);
    }
}
