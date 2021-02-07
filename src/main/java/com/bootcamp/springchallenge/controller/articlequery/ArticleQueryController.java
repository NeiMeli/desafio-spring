package com.bootcamp.springchallenge.controller.articlequery;

import com.bootcamp.springchallenge.controller.ExceptionHandlerController;
import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.service.ArticleQueryService;
import com.bootcamp.springchallenge.service.impl.article.query.ArticleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleQueryController extends ExceptionHandlerController {

    @Autowired
    ArticleQueryService service;

    @GetMapping
    @ResponseBody
    public List<ArticleResponseDTO> query(@RequestParam (required = false) String name,
                                          @RequestParam (required = false) String[] category,
                                          @RequestParam (required = false) String brand,
                                          @RequestParam (required = false) Double maxPrice,
                                          @RequestParam (required = false) Boolean freeShipping,
                                          @RequestParam (required = false) Integer minPrestige,
                                          @RequestParam (required = false) Integer stock,
                                          @RequestParam (required = false) Integer order) {
        ArticleQuery articleQuery = new ArticleQuery()
                .withName(name)
                .withCategories(category)
                .withBrand(brand)
                .withMaxPrice(maxPrice)
                .withFreeShipping(freeShipping)
                .withMinPrestige(minPrestige)
                .withStockAvailable(stock)
                .withOrder(order);
        return service.query(articleQuery);
    }
}
