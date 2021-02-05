package com.bootcamp.springchallenge.service.impl.purchase;

import com.bootcamp.springchallenge.controller.articlequery.dto.ArticleResponseDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseClosureDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestArticleDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.builder.PurchaseResponseDTOBuilder;
import com.bootcamp.springchallenge.entity.Article;
import com.bootcamp.springchallenge.entity.Category;
import com.bootcamp.springchallenge.entity.Customer;
import com.bootcamp.springchallenge.entity.purchase.Purchase;
import com.bootcamp.springchallenge.repository.ArticleRepository;
import com.bootcamp.springchallenge.repository.CustomerRepository;
import com.bootcamp.springchallenge.repository.PurchaseRepository;
import com.bootcamp.springchallenge.service.ArticleQueryService;
import com.bootcamp.springchallenge.service.PurchaseService;
import com.bootcamp.springchallenge.service.impl.query.Query;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bootcamp.springchallenge.controller.purchase.dto.response.builder.PurchaseResponseDTOExtra.*;
import static com.bootcamp.springchallenge.service.impl.purchase.PurchaseServiceErrorImpl.*;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ArticleQueryService articleQueryService;

    @Override
    public PurchaseResponseDTO processPurchaseRequest(PurchaseRequestDTO purchaseRequest) {
        validate(purchaseRequest); // valido en memoria lo que pueda antes de ir a los repo
        final Purchase purchase = findOrCreatePurchase(purchaseRequest.getUserName());
        processArticles(purchaseRequest.getArticles(), purchase);
        purchaseRepository.persist(purchase);
        return buildResponseDTO(purchase);
    }

    private void processArticles(List<PurchaseRequestArticleDTO> articles, Purchase purchase) {
        final Map<Integer, Integer> quantitiesByArticleId = new HashMap<>();
        articles.forEach(a -> quantitiesByArticleId.merge(a.getArticleId(), a.getQuantity(), Integer::sum));
        final Map<Integer, Article> availableArticles = new HashMap<>();
        quantitiesByArticleId.forEach((articleId, quantity) -> {
            Optional<Article> articleOpt = articleRepository.find(articleId);
            if (articleOpt.isEmpty()) {
                throw new PurchaseServiceException(ARTICLE_NOT_FOUND, articleId);
            }
            Article article = articleOpt.get();
            if (!article.hasStock(quantity)) {
                handleNotEnoughStockSuggestion(article, quantity);
            }
            availableArticles.put(articleId, article);
        });

        articles.forEach(dto -> {
            int articleId = dto.getArticleId();
            int quantity = dto.getQuantity();
            Article article = availableArticles.get(articleId);
            article.reserveStock(quantity);
            purchase.addArticle(articleId, article.getName(), quantity, dto.getDiscount(), article.getPrice());
        });
    }

    @Override
    public PurchaseResponseDTO confirmPurchase(PurchaseClosureDTO purchaseData) {
        validate(purchaseData);
        String userName = purchaseData.getUserName();
        int purchaseId = purchaseData.getReceipt();
        Purchase purchase = findPendingPurchaseOrFail(purchaseId, userName);
        PurchaseResponseDTOBuilder responseBuilder = new PurchaseResponseDTOBuilder(purchase);
        Customer customer = customerRepository.find(userName).orElseThrow();
        if (purchaseData.isUseBonus()) {
            if (customer.hasBonusAvailable()) {
                purchase.applyBonus();
                customer.consumeBonus();
                responseBuilder.withExtra(BONUS_CONSUMED.getMessage());
            } else {
                responseBuilder.withExtra(BONUS_UNAVAILABLE.getMessage());
            }
        }
        purchase.complete();
        customer.addPurchase();
        purchaseRepository.persist(purchase);
        if (customer.hasBonusAvailable()) {
            responseBuilder.withExtra(BONUS_AVAILABLE.getMessage());
        }
        return responseBuilder.build();
    }

    @Override
    public PurchaseResponseDTO cancelPurchase(PurchaseClosureDTO purchaseData) {
        validate(purchaseData);
        String userName = purchaseData.getUserName();
        int purchaseId = purchaseData.getReceipt();
        Purchase purchase = findPendingPurchaseOrFail(purchaseId, userName);
        releaseStock(purchase);
        purchase.cancel();
        purchaseRepository.persist(purchase);
        return buildResponseDTO(purchase);
    }

    private void releaseStock(Purchase purchase) {
        Map<Integer, Integer> quantityByArticleId = purchase.getQuantitiesByArticleIdMap();
        quantityByArticleId.forEach((id, quantity) -> {
            Article article = articleRepository.find(id).orElseThrow(); // no puede no existir el producto en este punto
            article.releaseStock(quantity);
            articleRepository.persist(article);
        });
    }

    private Purchase findPendingPurchaseOrFail(int purchaseId, String userName) {
        final Optional<Purchase> purchaseOpt = purchaseRepository.findPendingPurchase(purchaseId);
        if (purchaseOpt.isEmpty() || !purchaseOpt.get().getUserName().equals(userName)) {
            throw new PurchaseServiceException(PurchaseServiceErrorImpl.PURCHASE_NOT_FOUND);
        }
        return purchaseOpt.get();
    }

    private PurchaseResponseDTO buildResponseDTO(Purchase purchase) {
        return new PurchaseResponseDTOBuilder(purchase).build();
    }

    private void validate(PurchaseClosureDTO purchaseConfirmation) {
        validateUserName(purchaseConfirmation.getUserName());
        int purchaseId = purchaseConfirmation.getReceipt();
        if (purchaseId <= 0) {
            throw new PurchaseServiceException(INVALID_PURCHASE_ID, purchaseId);
        }
    }

    private void validate(PurchaseRequestDTO purchaseRequest) {
        validateUserName(purchaseRequest.getUserName());
        List<PurchaseRequestArticleDTO> articles = purchaseRequest.getArticles();
        if (articles == null || articles.isEmpty()) {
            throw new PurchaseServiceException(EMPTY_ARTICLE_LIST);
        }
        articles.forEach(article -> {
            int articleId = article.getArticleId();
            if (articleId <= 0) {
                throw new PurchaseServiceException(INVALID_ARTICLE_ID, articleId);
            }
            int quantity = article.getQuantity();
            if (quantity <= 0) {
                throw new PurchaseServiceException(INVALID_QUANTITY, quantity);
            }
            double discount = article.getDiscount();
            if (Double.compare(discount, 0) < 0 || Double.compare(discount, 100) >= 0) {
                throw new PurchaseServiceException(INVALID_DISCOUNT_VALUE, discount);
            }
        });
    }

    private void validateUserName(String userName) {
        if (Strings.isBlank(userName)) {
            throw new PurchaseServiceException(EMPTY_USERNAME);
        }
    }

    private Purchase findOrCreatePurchase(String userName) {
        Optional<Customer> customerOpt = customerRepository.find(userName);
        Optional<Purchase> purchaseOpt = Optional.empty();
        if (customerOpt.isEmpty()) {
            Customer customer = new Customer(userName);
            customerRepository.persist(customer);
        } else { // solo tiene sentido buscar compras en la base si el cliente ya existe
            purchaseOpt = purchaseRepository.findPendingPurchase(userName);
        }
        return purchaseOpt.orElseGet(() -> new Purchase(userName));
    }

    private void handleNotEnoughStockSuggestion(Article article, int quantity) {
        Category category = article.getCategory();
        Query query = new Query().withCategories(List.of(category)).withStockAvailable(quantity);
        List<ArticleResponseDTO> suggestedArticles = articleQueryService.query(query);
        final StringBuilder suggestionMessage = new StringBuilder();
        if (!suggestedArticles.isEmpty()) {
            suggestionMessage.append(String.format("Otros productos con stock %s en categoria ", quantity)).append(category.getValue()).append(": ");
            List<String> suggestions = suggestedArticles.stream()
                    .map(a -> String.format("%s %s %s", a.getId(), a.getName(), a.getPrice()))
                    .collect(Collectors.toList());
            suggestionMessage.append(String.join(", ", suggestions));
        } else {
            suggestionMessage.append(String.format("No hay otros productos con stock %s en categoria ", quantity)).append(category.getValue());
        }
        throw new PurchaseServiceException(NOT_ENOUGH_STOCK_SUGGESTION, article.describe(), suggestionMessage.toString());
    }
}
