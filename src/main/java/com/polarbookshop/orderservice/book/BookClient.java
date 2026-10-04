package com.polarbookshop.orderservice.book;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BookClient {

	private static final String BOOKS_ROOT_API = "/books/";
	private final WebClient webClient;
	private final ObjectMapper mapper = new ObjectMapper();

	public BookClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<Book> getBookByIsbn(String isbn) {
		return webClient
				.get()
				.uri(BOOKS_ROOT_API + isbn)
				.exchangeToMono(response -> {
					if (response.statusCode().is2xxSuccessful()) {
						return response.bodyToMono(Book.class);
					}
					if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						return Mono.empty();
					}
					int status = response.statusCode().value();
					return response.bodyToMono(String.class)
							.flatMap(body -> {
								try {
									BookErrorResponse err = mapper.readValue(body, BookErrorResponse.class);
									return Mono.error(new BookClientException(status, body, err));
								} catch (Exception e) {
									return Mono.error(new BookClientException(status, body));
								}
							});
				})
				.timeout(Duration.ofSeconds(3), Mono.empty())
				.retryWhen(Retry.backoff(3, Duration.ofMillis(100)));
	}

}
