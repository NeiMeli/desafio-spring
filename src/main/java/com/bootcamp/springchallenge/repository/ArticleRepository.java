package com.bootcamp.springchallenge.repository;

import com.bootcamp.springchallenge.entity.article.Article;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ArticleRepository {
    Stream<Article> listWhere(Predicate<Article> predicate);
    Optional<Article> find(int id);
    Article persist(Article article);
}
