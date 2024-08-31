CREATE TABLE books (
    book_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    author_name VARCHAR(255) NOT NULL,
    page_numbers INT,
    published_date DATE,
    genre VARCHAR(100)
);
