package com.bootcamp.springchallenge.common.articlequery;

import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryResultForTest {
    private ArticleQueryForTest query;
    private List<Integer> results;

    public QueryResultForTest(ArticleQueryForTest query) {
        this.query = query;
        results = new ArrayList<>();
    }

    public ArticleQueryForTest getQuery() {
        return query;
    }

    public void setQuery(ArticleQueryForTest query) {
        this.query = query;
    }

    public List<Integer> getResults() {
        return results;
    }

    public QueryResultForTest addResults(Integer ... articlesId) {
        results.addAll(Arrays.asList(articlesId));
        return this;
    }

    public QueryResultForTest addResult(Integer articleId, int index) {
        results.add(index, articleId);
        return this;
    }

    public QueryResultForTest addResult(Integer articleId) {
        results.add(articleId);
        return this;
    }


    public void clearResults() {
        this.results = new ArrayList<>();
    }

    public QueryResultForTest removeResult(Integer articleId) {
        results = results.stream().filter(r -> !r.equals(articleId)).collect(Collectors.toList());
        return this;
    }

    public Boolean hasResult(List<ArticleResponseDTO> other) {
        boolean result = other.size() == results.size();
        boolean ordered = query.isOrdered();
        for (int i = 0; i < results.size(); i ++) {
            Integer id = results.get(i);
            if (ordered) {
                ArticleResponseDTO otherDTO = other.get(i);
                result = result && id == otherDTO.getId();
            } else {
                result = result && other.stream().anyMatch(r -> r.getId() == id);
            }
        }
        return result;
    }
}
