package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polarbookshop.orderservice.book.BookClientException;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

	private final BookClient bookClient;
	private final OrderRepository orderRepository;
	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	public OrderService(BookClient bookClient, OrderRepository orderRepository) {
		this.bookClient = bookClient;
		this.orderRepository = orderRepository;
	}

	public Flux<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Mono<Order> submitOrder(String isbn, int quantity) {
		return bookClient.getBookByIsbn(isbn)
				.onErrorResume(BookClientException.class, ex -> {
					logger.warn("Book service error (status {}): {}", ex.getStatus(), ex.getResponseBody());
					if (ex.getErrorResponse() != null) {
						logger.debug("Parsed error map: {}", ex.getErrorResponse().getErrors());
						String first = ex.getErrorResponse().getFirstErrorMessage();
						if (first != null) {
							logger.info("First error message from catalog: {}", first);
						}
					}
					return Mono.error(ex);
				})
				.map(book -> buildAcceptedOrder(book, quantity))
				.defaultIfEmpty(buildRejectedOrder(isbn, quantity))
				.flatMap(orderRepository::save);
	}

	public static Order buildAcceptedOrder(Book book, int quantity) {
		return Order.of(book.isbn(), book.title() + " - " + book.author(),
				book.price(), quantity, OrderStatus.ACCEPTED);
	}

	public static Order buildRejectedOrder(String bookIsbn, int quantity) {
		return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
	}

}
