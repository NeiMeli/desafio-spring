package com.bootcamp.springchallenge.repository.impl;

import com.bootcamp.springchallenge.entity.Article;
import com.bootcamp.springchallenge.repository.CacheDBTable;
import com.bootcamp.springchallenge.repository.ArticleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Repository
public class ArticleCacheRepository implements ArticleRepository {
    final CacheDBTable<Integer, Article> database;

    public ArticleCacheRepository() throws Exception {
        this.database = new CacheDBTable<>() {
            @Override
            protected @NotNull Integer generateNextId() {
                return size();
            }
        };
        JsonNode jsonNodes = JsonDBUtil.parseDatabase("src/main/resources/database/articles.json");
        for (JsonNode jsonNode : jsonNodes) {
            database.persist(Article.fromJson(jsonNode));
        }
    }

    @Override
    public Stream<Article> listWhere(Predicate<Article> predicate) {
        return database.listWhere(predicate);
    }

    @Override
    public Optional<Article> find(int id) {
        return database.find(id);
    }

    @Override
    public Article persist(Article article) {
        return database.persist(article);
    }
}
