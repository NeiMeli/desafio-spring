package com.bootcamp.springchallenge.controller.articlequery;

import com.bootcamp.springchallenge.controller.BadRequestHandlerController;
import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.service.ArticleQueryService;
import com.bootcamp.springchallenge.service.impl.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleQueryController extends BadRequestHandlerController {

    @Autowired
    ArticleQueryService service;

    @GetMapping
    @ResponseBody
    public List<ArticleResponseDTO> query(@RequestParam (required = false) String name,
                                          @RequestParam (required = false) String[] category,
                                          @RequestParam (required = false) Double maxPrice,
                                          @RequestParam (required = false) Boolean freeShipping,
                                          @RequestParam (required = false) Integer minPrestige,
                                          @RequestParam (required = false) Integer order) {
        Query query = new Query()
                .withName(name)
                .withCategories(category)
                .withMaxPrice(maxPrice)
                .withFreeShipping(freeShipping)
                .withMinPrestige(minPrestige)
                .withOrder(order);
        return service.query(query);
    }
}
