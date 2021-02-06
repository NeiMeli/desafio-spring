package com.bootcamp.springchallenge.service.impl.purchase;

import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseClosureDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestArticleDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.builder.PurchaseResponseDTOBuilder;
import com.bootcamp.springchallenge.entity.Article;
import com.bootcamp.springchallenge.entity.Customer;
import com.bootcamp.springchallenge.entity.purchase.Purchase;
import com.bootcamp.springchallenge.repository.ArticleRepository;
import com.bootcamp.springchallenge.repository.CustomerRepository;
import com.bootcamp.springchallenge.repository.PurchaseRepository;
import com.bootcamp.springchallenge.service.PurchaseService;
import com.bootcamp.springchallenge.service.impl.purchase.util.ArticleUtil;
import com.bootcamp.springchallenge.service.impl.query.Query;
import com.bootcamp.springchallenge.service.impl.query.QueryParam;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
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

    @Override
    public PurchaseResponseDTO processPurchaseRequest(PurchaseRequestDTO purchaseRequest) {
        validate(purchaseRequest); // valido en memoria lo que pueda antes de ir a los repo
        final Purchase purchase = findOrCreatePurchase(purchaseRequest.getUserName());
        PurchaseResponseDTOBuilder responseBuilder = new PurchaseResponseDTOBuilder(purchase);
        try {
            processArticles(purchaseRequest.getArticles(), purchase);
        } catch (NotEnoughStockException e) {
            responseBuilder.withHttpStatus(HttpStatus.BAD_REQUEST);
            responseBuilder.withMessage(e.getMessage());
        }
        purchaseRepository.persist(purchase);
        return responseBuilder.build();
    }

    private void processArticles(List<PurchaseRequestArticleDTO> articles, Purchase purchase) throws NotEnoughStockException {
        final Map<Integer, Integer> quantitiesByArticleId = ArticleUtil.getQuantitiesByArticleDtoId(articles);
        final Map<Integer, Article> availableArticles = new HashMap<>();
        final List<Runnable> reserveAction = new ArrayList<>();
        final AtomicReference<List<String>> notEnoughStockSuggestions = new AtomicReference<>(Collections.emptyList());

        // Valido que haya stock de cada producto antes de reservar
        // Lo hago sincronico para acumular sugerencias ante falta de stock
        quantitiesByArticleId.forEach((articleId, quantity) -> {
            Article article = articleRepository.find(articleId).orElseThrow(() -> new PurchaseServiceException(ARTICLE_NOT_FOUND, articleId));
            if (article.hasStock(quantity)) {
                reserveAction.add(() -> article.reserveStock(quantity));
                availableArticles.put(articleId, article); // lo necesito para luego completar el purchase
            } else {
                if (notEnoughStockSuggestions.get().isEmpty()) notEnoughStockSuggestions.set(new ArrayList<>());
                notEnoughStockSuggestions.get().add(generateNotEnoughStockSuggestion(article, quantity));
            }
        });

        List<String> suggestions = notEnoughStockSuggestions.get();
        if (!suggestions.isEmpty()) throw new NotEnoughStockException(String.join(". ", suggestions));

        // Solo si todos los items tienen stock, hago la reserva forma asincrónica
        reserveAction.stream().parallel().forEach(Runnable::run);

        // Agrego los articulos a Purchase, tiene que ser sincrónico
        articles.forEach(dto -> {
            int articleId = dto.getArticleId();
            int quantity = dto.getQuantity();
            Article article = availableArticles.get(articleId);
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
                responseBuilder.withHttpStatus(HttpStatus.PARTIAL_CONTENT);
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
        Map<Integer, Integer> quantityByArticleId = ArticleUtil.getQuantitiesByArticleId(purchase.getArticles());
        quantityByArticleId.keySet().stream().parallel().forEach(id -> {
            Integer quantity = quantityByArticleId.get(id);
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

    private String generateNotEnoughStockSuggestion(Article article, int quantity) {
        Query query = new Query().withStockAvailable(quantity);
        List<QueryParam> compatibleParams = QueryParam.getCompatibleParams();
        String articleDescription = article.describe();
        final AtomicReference<String> suggestion = new AtomicReference<>("");
        final Supplier<String> finalMessage = () -> NOT_ENOUGH_STOCK_SUGGESTION.getMessage(articleDescription, suggestion.get());
        int index = 0;
        while (suggestion.get().isEmpty() && index < compatibleParams.size()) {
            QueryParam queryParam = compatibleParams.get(index);
            query.with(queryParam, article);
            suggestion.set(generateNotEnoughStockSuggestion(article, quantity, query));
            if (suggestion.get().isEmpty()) {
                query.without(queryParam);
                index ++;
            }
        }
        if (suggestion.get().isEmpty()) {
            suggestion.set(String.format("No hay productos similares a %s con stock %s", articleDescription, quantity));
        } else {
            suggestion.set(String.format("Otros productos disponibles con %s similar: %s", compatibleParams.get(index).getLabel(), suggestion));
        }
        return finalMessage.get();
    }

    private String generateNotEnoughStockSuggestion(Article article, int quantity, Query query) {
        List<String> suggestedArticles = articleRepository.listWhere(query.buildPredicate())
                .map(Article::describe)
                .collect(Collectors.toList());
        return String.join(", ", suggestedArticles);
    }
}
