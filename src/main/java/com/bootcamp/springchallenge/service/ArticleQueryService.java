package com.bootcamp.springchallenge.service;

import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.service.impl.query.Query;

import java.util.List;

public interface ArticleQueryService {
    List<ArticleResponseDTO> query(Query query);
}
