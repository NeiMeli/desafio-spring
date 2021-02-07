package com.bootcamp.springchallenge.service.impl.purchase;

import com.bootcamp.springchallenge.common.customer.CustomerTestConstants;
import com.bootcamp.springchallenge.common.purchase.PurchaseTestConstants;
import com.bootcamp.springchallenge.controller.common.util.DoubleProcessor;
import com.bootcamp.springchallenge.controller.purchase.dto.StatusCodeDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseClosureDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestArticleDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.request.PurchaseRequestDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.PurchaseResponseArticleDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.ReceiptDTO;
import com.bootcamp.springchallenge.controller.purchase.dto.response.builder.PurchaseResponseDTOBuilder;
import com.bootcamp.springchallenge.controller.purchase.dto.response.builder.PurchaseResponseDTOExtra;
import com.bootcamp.springchallenge.entity.article.Article;
import com.bootcamp.springchallenge.entity.customer.Customer;
import com.bootcamp.springchallenge.entity.purchase.Purchase;
import com.bootcamp.springchallenge.entity.purchase.PurchaseArticle;
import com.bootcamp.springchallenge.entity.purchase.PurchaseStatus;
import com.bootcamp.springchallenge.repository.ArticleRepository;
import com.bootcamp.springchallenge.repository.PurchaseRepository;
import com.bootcamp.springchallenge.service.impl.purchase.exception.PurchaseServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bootcamp.springchallenge.service.impl.purchase.exception.PurchaseServiceErrorImpl.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PurchaseServiceImplTest {
    @Autowired
    PurchaseServiceImpl service;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    PurchaseRepository purchaseRepository;

    @Test
    void testPurchaseRequestHappy() {
        PurchaseRequestDTO request1 = PurchaseTestConstants.PURCHASE_REQUEST_DTO1.get();
        // le cambio el nombre porque el otro user tiene una orden ya hecha que me complica el stock
        request1.setUserName(CustomerTestConstants.CUSTOMER_2.getUserName());
        PurchaseResponseDTO actualResponse = service.processPurchaseRequest(request1);
        List<PurchaseResponseArticleDTO> articles = actualResponse.getReceipt().getArticles();
        assertThat(articles).hasSize(3);
        assertThat(articles.stream().anyMatch(compareArticle(4, "Soldadora", 1, 5050.5d))).isTrue();
        assertThat(articles.stream().anyMatch(compareArticle(12, "Medias", 2, 600d))).isTrue();

        assertThat(articles.stream().anyMatch(compareArticle(13, "Short", 1, 3900d))).isTrue();

        // agrego un 4 con otro descuento, tiene que agregarme otro articulo
        PurchaseRequestDTO request2 = new PurchaseRequestDTO()
                .setUserName(request1.getUserName())
                .setArticles(List.of(PurchaseTestConstants.PR_ARTICLE_DTO_4_2.get()));
        PurchaseResponseDTO actualResponse2 = service.processPurchaseRequest(request2);
        List<PurchaseResponseArticleDTO> articles2 = actualResponse2.getReceipt().getArticles();
        assertThat(articles2).hasSize(4);
        assertThat(articles2.stream().anyMatch(compareArticle(4, "Soldadora", 1, 6493.5d))).isTrue();

        // agrego un 12 con el mismo descuento, me lo tiene que sumar
        PurchaseRequestDTO request3 = new PurchaseRequestDTO()
                .setUserName(request1.getUserName())
                .setArticles(List.of(PurchaseTestConstants.PR_ARTICLE_DTO_12.get().setQuantity(1)));
        PurchaseResponseDTO actualResponse3 = service.processPurchaseRequest(request3);
        List<PurchaseResponseArticleDTO> articles3 = actualResponse3.getReceipt().getArticles();
        assertThat(articles3).hasSize(4);
        assertThat(articles3.stream().filter(a -> a.getId() == 12).allMatch(compareArticle(12, "Medias", 3, 900d))).isTrue();

        // veamos como quedo la base de datos
        // Purchase
        Optional<Purchase> pendingPurchaseOpt = purchaseRepository.findPendingPurchase(request1.getUserName());
        assertThat(pendingPurchaseOpt).isPresent();
        List<PurchaseArticle> purchaseArticles = pendingPurchaseOpt.get().getArticles();
        articles3.forEach(a -> assertThat(purchaseArticles.stream().anyMatch(pa -> pa.getArticleId() == a.getId() && pa.getQuantity() == a.getQuantity())).isTrue());

        // Article (se tiene que haber movido el stock)
        final Map<Integer, Integer> initialStockByArticle = Map.of(4, 10, 12, 8, 13, 9);
        List<Integer> articlesId = articles3.stream().map(PurchaseResponseArticleDTO::getId).collect(Collectors.toList());
        Stream<Article> articleStream = articleRepository.listWhere(ar -> articlesId.contains(ar.getId()));
        assertThat(articleStream).allMatch(article -> {
            int initialStock = initialStockByArticle.get(article.getId());
            int reservedQuantity = purchaseArticles.stream().filter(a -> a.getArticleId() == article.getId()).map(PurchaseArticle::getQuantity).reduce(0, Integer::sum);
            return article.getStock() == (initialStock - reservedQuantity);
        });
    }

    @Test
    void testConfirmPurchaseCompleteTest() {
        PurchaseRequestDTO request1 = PurchaseTestConstants.PURCHASE_REQUEST_DTO1.get();
        PurchaseResponseDTO actualResponse = service.processPurchaseRequest(request1);

        // primero pruebo completar con datos incorrectos, deberia fallar
        String userName = request1.getUserName();
        int receipt = actualResponse.getReceipt().getId();
        assertPurchaseDataDTOBadRequestOcurr(userName, receipt, r -> service.confirmPurchase(r));

        PurchaseClosureDTO purchaseClosureDTO = new PurchaseClosureDTO().setUserName(userName).setReceipt(receipt);
        PurchaseResponseDTO response = service.confirmPurchase(purchaseClosureDTO);
        StatusCodeDTO statusCode = response.getStatusCode();
        assertThat(statusCode.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(statusCode.getMessage()).isEqualTo(PurchaseResponseDTOBuilder.getMessage(PurchaseStatus.COMPLETED));

        // ahora veamos como quedo la Purchase
        assertThat(purchaseRepository.findPendingPurchase(receipt)).isEmpty();
        assertThat(purchaseRepository.findPendingPurchase(userName)).isEmpty();
        Optional<Purchase> purchaseOpt = purchaseRepository.find(receipt);
        assertThat(purchaseOpt).isPresent();
        Purchase purchase = purchaseOpt.get();
        assertThat(purchase.isCompleted()).isTrue();

        // no tengo que poder finalizar de nuevo la compra
        assertThatExceptionOfType(PurchaseServiceException.class)
                .isThrownBy(() -> service.confirmPurchase(purchaseClosureDTO))
                .withMessageContaining(PURCHASE_NOT_FOUND.getMessage());
    }

    @Test
    void testBonus() {
        int defaultPurchasesUntilBonus = Customer.DEFAULT_PURCHASES_UNTIL_NEXT_BONUS;
        PurchaseRequestDTO purchaseRequestDTO = PurchaseTestConstants.PURCHASE_REQUEST_DTO2.get();
        String userName = purchaseRequestDTO.getUserName();
        for (int i = 1; i < defaultPurchasesUntilBonus; i ++) {
            PurchaseResponseDTO response = service.processPurchaseRequest(purchaseRequestDTO);
            int receipt1 = response.getReceipt().getId();
            PurchaseClosureDTO purchaseClosureDTO = new PurchaseClosureDTO().setUserName(userName).setReceipt(receipt1);
            PurchaseResponseDTO purchaseResponseDTO = service.confirmPurchase(purchaseClosureDTO);
            assertThat(purchaseResponseDTO.getStatusCode().getCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(purchaseResponseDTO.getStatusCode().getMessage()).doesNotContain(PurchaseResponseDTOExtra.BONUS_AVAILABLE.getMessage());
        }
        PurchaseResponseDTO responseBeforeAdquiringBonus = service.processPurchaseRequest(purchaseRequestDTO);
        int receipt1 = responseBeforeAdquiringBonus.getReceipt().getId();
        PurchaseClosureDTO purchaseClosureDTO = new PurchaseClosureDTO().setUserName(userName).setReceipt(receipt1);
        PurchaseResponseDTO responseWithBonusAdquired = service.confirmPurchase(purchaseClosureDTO);
        assertThat(responseWithBonusAdquired.getStatusCode().getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseWithBonusAdquired.getStatusCode().getMessage()).contains(PurchaseResponseDTOExtra.BONUS_AVAILABLE.getMessage(1));

        // en la compra siguiente puedo usar el bonus
        PurchaseResponseDTO responseBeforeConsumingBonus = service.processPurchaseRequest(purchaseRequestDTO);
        int receipt2 = responseBeforeConsumingBonus.getReceipt().getId();
        PurchaseClosureDTO purchaseClosureDTO2 = new PurchaseClosureDTO().setUserName(userName).setReceipt(receipt2).setUseBonus(true);
        PurchaseResponseDTO responseWithBonusConsumed = service.confirmPurchase(purchaseClosureDTO2);
        assertThat(responseWithBonusConsumed.getStatusCode().getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseWithBonusConsumed.getStatusCode().getMessage()).contains(PurchaseResponseDTOExtra.BONUS_CONSUMED.getMessage());

        // el bonus ya no esta disponible
        PurchaseResponseDTO responseAfterConsumingBonus = service.processPurchaseRequest(purchaseRequestDTO);
        int receipt3 = responseAfterConsumingBonus.getReceipt().getId();
        PurchaseClosureDTO purchaseClosureDTO3 = new PurchaseClosureDTO().setUserName(userName).setReceipt(receipt3).setUseBonus(true);
        PurchaseResponseDTO responseAfterBonusConsumed = service.confirmPurchase(purchaseClosureDTO3);
        assertThat(responseAfterBonusConsumed.getStatusCode().getCode()).isEqualTo(HttpStatus.PARTIAL_CONTENT.value());
        assertThat(responseAfterBonusConsumed.getStatusCode().getMessage()).contains(PurchaseResponseDTOExtra.BONUS_UNAVAILABLE.getMessage());
    }

    @Test
    void testCancelPurchaseCompleteTest() {
        PurchaseRequestDTO request1 = PurchaseTestConstants.PURCHASE_REQUEST_DTO1.get();
        // voy a armar un mapa con los stocks iniciales para usarlo al final
        final Map<Integer, Integer> initialQuantitiesByArticleIdMap = new HashMap<>();
        request1.getArticles().forEach(a -> {
            Article article = articleRepository.find(a.getArticleId()).orElseThrow();
            initialQuantitiesByArticleIdMap.put(a.getArticleId(), article.getStock());
        });
        // le cambio el nombre porque el otro user tiene una orden ya hecha que me complica el stock
        request1.setUserName(CustomerTestConstants.CUSTOMER_2.getUserName());
        PurchaseResponseDTO actualResponse = service.processPurchaseRequest(request1);

        // primero pruebo completar con datos incorrectos, deberia fallar
        String userName = request1.getUserName();
        int receipt1Id = actualResponse.getReceipt().getId();
        assertPurchaseDataDTOBadRequestOcurr(userName, receipt1Id, r -> service.cancelPurchase(r));

        PurchaseClosureDTO purchaseClosureDTO = new PurchaseClosureDTO().setUserName(userName).setReceipt(receipt1Id);
        PurchaseResponseDTO response = service.cancelPurchase(purchaseClosureDTO);
        StatusCodeDTO statusCode = response.getStatusCode();
        assertThat(statusCode.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(statusCode.getMessage()).isEqualTo(PurchaseResponseDTOBuilder.getMessage(PurchaseStatus.CANCELED));

        // ahora veamos como quedo la Purchase
        assertThat(purchaseRepository.findPendingPurchase(receipt1Id)).isEmpty();
        assertThat(purchaseRepository.findPendingPurchase(userName)).isEmpty();
        Optional<Purchase> purchaseOpt = purchaseRepository.find(receipt1Id);
        assertThat(purchaseOpt).isPresent();
        Purchase purchase = purchaseOpt.get();
        assertThat(purchase.isCanceled()).isTrue();

        // tiene que haber liberado stock
        initialQuantitiesByArticleIdMap.forEach((id, initialQuantity) -> {
            Article article = articleRepository.find(id).orElseThrow();
            assertThat(article.getStock()).isEqualTo(initialQuantity);
        });

        // el mismo usuario tiene que poder iniciar una compra sin relacion a la cancelada
        PurchaseRequestDTO request2 = PurchaseTestConstants.PURCHASE_REQUEST_DTO2.get();
        PurchaseResponseDTO response2 = service.processPurchaseRequest(request2);
        ReceiptDTO receipt2 = response2.getReceipt();
        assertThat(receipt2.getId()).isNotEqualTo(receipt1Id);
        List<PurchaseResponseArticleDTO> articlesResponse2 = receipt2.getArticles();
        assertThat(articlesResponse2).hasSize(1);
        PurchaseRequestArticleDTO articleRequest = request2.getArticles().get(0);
        PurchaseResponseArticleDTO articleResponse = articlesResponse2.get(0);
        assertThat(articleResponse.getId()).isEqualTo(articleRequest.getArticleId());
        assertThat(articleResponse.getQuantity()).isEqualTo(articleRequest.getQuantity());
    }

    @Test
    void testMultipleArticlesWithNotEnoughStock() {
        PurchaseRequestDTO request = PurchaseTestConstants.PURCHASE_REQUEST_DTO1.get();
        request.getArticles().forEach(a -> a.setQuantity(a.getQuantity() + 1000));
        PurchaseResponseDTO response = service.processPurchaseRequest(request);
        StatusCodeDTO statusCode = response.getStatusCode();
        assertThat(statusCode.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        String message = statusCode.getMessage();
        assertThat(message).contains(NOT_ENOUGH_STOCK.getMessage(12));
        assertThat(message).contains(NOT_ENOUGH_STOCK.getMessage(13));
        assertThat(message).contains(NOT_ENOUGH_STOCK.getMessage(4));
    }

    private void assertPurchaseDataDTOBadRequestOcurr(String userName, int receipt, Consumer<PurchaseClosureDTO> action) {
        BiConsumer<PurchaseClosureDTO, String> errorAsserter = (request, message) ->
                assertThatExceptionOfType(PurchaseServiceException.class)
                        .isThrownBy(() -> action.accept(request))
                        .withMessageContaining(message);
        PurchaseClosureDTO purchaseClosureDTO = new PurchaseClosureDTO().setUserName("userName").setReceipt(0);
        errorAsserter.accept(purchaseClosureDTO, INVALID_PURCHASE_ID.getMessage(0));
        purchaseClosureDTO.setReceipt(Integer.MAX_VALUE);
        errorAsserter.accept(purchaseClosureDTO, PURCHASE_NOT_FOUND.getMessage());
        purchaseClosureDTO.setUserName(userName);
        errorAsserter.accept(purchaseClosureDTO, PURCHASE_NOT_FOUND.getMessage());
        purchaseClosureDTO.setUserName("userName").setReceipt(receipt);
        errorAsserter.accept(purchaseClosureDTO, PURCHASE_NOT_FOUND.getMessage());
    }

    @Test
    void testBadRequests() {
        BiConsumer<PurchaseRequestDTO, String> errorAsserter = (request, message) ->
                assertThatExceptionOfType(PurchaseServiceException.class)
                .isThrownBy(() -> service.processPurchaseRequest(request))
                .withMessageContaining(message);

        // sin nada
        PurchaseRequestDTO invalidRequest = new PurchaseRequestDTO();
        errorAsserter.accept(invalidRequest, EMPTY_USERNAME.getMessage());

        // lista vacia
        invalidRequest.setUserName("some-user");
        errorAsserter.accept(invalidRequest, EMPTY_ARTICLE_LIST.getMessage());

        // articulo invalido
        invalidRequest.setArticles(List.of(PurchaseTestConstants.PR_ARTICLE_DTO_4.get().setArticleId(-5)));
        errorAsserter.accept(invalidRequest, INVALID_ARTICLE_ID.getMessage(-5));

        // articulo inexistente
        invalidRequest.setArticles(List.of(PurchaseTestConstants.PR_ARTICLE_DTO_4.get().setArticleId(Integer.MAX_VALUE)));
        errorAsserter.accept(invalidRequest, ARTICLE_NOT_FOUND.getMessage(Integer.MAX_VALUE));

        // decuento invalido
        invalidRequest.setArticles(List.of(PurchaseTestConstants.PR_ARTICLE_DTO_4.get().setDiscount(-500)));
        errorAsserter.accept(invalidRequest, INVALID_DISCOUNT_VALUE.getMessage(-500));
        invalidRequest.setArticles(List.of(PurchaseTestConstants.PR_ARTICLE_DTO_4.get().setDiscount(105)));
        errorAsserter.accept(invalidRequest, INVALID_DISCOUNT_VALUE.getMessage(105));

        // cantidad invalida
        invalidRequest.setArticles(List.of(PurchaseTestConstants.PR_ARTICLE_DTO_4.get().setQuantity(-500)));
        errorAsserter.accept(invalidRequest, INVALID_QUANTITY.getMessage(-500));

        // no hay stock
        invalidRequest.setArticles(List.of(PurchaseTestConstants.PR_ARTICLE_DTO_4.get().setQuantity(3000)));
        PurchaseResponseDTO responseWithStockError = service.processPurchaseRequest(invalidRequest);
        StatusCodeDTO statusCode = responseWithStockError.getStatusCode();
        assertThat(statusCode.getMessage()).contains(NOT_ENOUGH_STOCK.getMessage(4));
        assertThat(statusCode.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testDontReserveStockUntilAllArticlesAreValid() {
        PurchaseRequestDTO request1 = PurchaseTestConstants.PURCHASE_REQUEST_DTO1.get();
        // voy a armar un mapa con los stocks iniciales para usarlo al final
        final Map<Integer, Integer> initialQuantitiesByArticleIdMap = new HashMap<>();
        request1.getArticles().forEach(a -> {
            Article article = articleRepository.find(a.getArticleId()).orElseThrow();
            initialQuantitiesByArticleIdMap.put(a.getArticleId(), article.getStock());
        });
        // le cambio el nombre porque el otro user tiene una orden ya hecha que me complica el stock
        request1.setUserName(CustomerTestConstants.CUSTOMER_2.getUserName());
        // le pongo mucha cantidad a uno para que falle el stock
        PurchaseRequestArticleDTO purchaseRequestArticleDTO = request1.getArticles().stream().filter(a -> a.getArticleId() == 4).findFirst().orElseThrow();
        purchaseRequestArticleDTO.setQuantity(initialQuantitiesByArticleIdMap.get(purchaseRequestArticleDTO.getArticleId()) + 100);

        PurchaseResponseDTO responseWithStockError = service.processPurchaseRequest(request1);
        assertThat(responseWithStockError.getStatusCode().getMessage()).contains(NOT_ENOUGH_STOCK.getMessage(4));

        // al haber fallado la reserva, no tiene que haber reservado nada
        initialQuantitiesByArticleIdMap.forEach((id, initialQuantity) -> {
            Article article = articleRepository.find(id).orElseThrow();
            assertThat(article.getStock()).isEqualTo(initialQuantity);
        });
    }

    private Predicate<PurchaseResponseArticleDTO> compareArticle(int id, String name, int quantity, double cost) {
        return p -> p.getId() == id &&
                p.getName().equals(name) &&
                p.getQuantity() == quantity &&
                DoubleProcessor.equal(p.getCost(), cost);
    }
}