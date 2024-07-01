package contracts.bookstore.orderservice

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("given_that_orders_exists_for_books_then_they_should_be_returned")
    description("description")
    request {
        method 'POST'
        url "/orders"
        body = body([isbns: [[code: "9780142424179"],
                             [code: "9780765326355"],
                             [code: "9780061120084"]]])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 200
        body = body([data: [[isbn: [code: "9780142424179"], order_count: 100],
                            [isbn: [code: "9780765326355"], order_count: 42],
                            [isbn: [code: "9780061120084"], order_count: 0]]])
        headers {
            contentType(applicationJson())
        }
    }
}