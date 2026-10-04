
package com.polarbookshop.orderservice.book;

public class BookClientException extends RuntimeException {

    private final int status;
    private final String responseBody;
    private final BookErrorResponse errorResponse;

    public BookClientException(int status, String responseBody) {
        super("Book service returned status " + status + " with body: " + responseBody);
        this.status = status;
        this.responseBody = responseBody;
        this.errorResponse = null;
    }

    public BookClientException(int status, String responseBody, BookErrorResponse errorResponse) {
        super("Book service returned status " + status + " with body: " + responseBody + " parsed: "
                + (errorResponse != null ? errorResponse.getErrors() : null));
        this.status = status;
        this.responseBody = responseBody;
        this.errorResponse = errorResponse;
    }

    public int getStatus() {
        return status;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public BookErrorResponse getErrorResponse() {
        return errorResponse;
    }

}
