package com.bootcamp.springchallenge.service.impl.article;

import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.entity.article.Article;
import com.bootcamp.springchallenge.repository.ArticleRepository;
import com.bootcamp.springchallenge.service.ArticleQueryService;
import com.bootcamp.springchallenge.service.impl.article.query.ArticleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ArticleQueryServiceImpl implements ArticleQueryService {
    @Autowired
    ArticleRepository repository;

    @Override
    public List<ArticleResponseDTO> query(ArticleQuery articleQuery) {
        Stream<Article> articleStream = repository.listWhere(articleQuery.buildPredicate()).sorted(articleQuery.getComparator());
        return articleStream.map(Article::toDTO).collect(Collectors.toList());
    }
}
