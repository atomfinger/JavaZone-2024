package contracts.bookstore.api.consumer1

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("given_that_a_single_book_exists_then_a_list_with_a_single_book_should_be_returned")
    description("""
Given that books exists, then they should be returned.
""")
    request {
        method 'GET'
        url "/api/books"
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 200
        body([
                [title      : "Test",
                 description: "More test",
                 authorName : "John"]
        ])
        headers {
            contentType(applicationJson())
        }
    }
}