package com.polarbookshop.orderservice.order.web;

import com.polarbookshop.orderservice.book.BookClientException;
import com.polarbookshop.orderservice.order.domain.OrderService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
class OrderControllerWebFluxTests {

	@Autowired
	private WebTestClient webClient;

	@MockitoBean
	private OrderService orderService;

	@Test
	void whenBookServiceReturnsErrorThenBadRequest() {
		var orderRequest = new OrderRequest("1234567890", 3);
		var reason = "The book with ISBN 1234567890 was not found";
		given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
				.willThrow(new BookClientException(404, reason));

		webClient
				.post()
				.uri("/orders")
				.bodyValue(orderRequest)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(java.util.Map.class)
				.value(responseBody -> assertThat(responseBody.get("message")).isEqualTo(reason));
	}

}
