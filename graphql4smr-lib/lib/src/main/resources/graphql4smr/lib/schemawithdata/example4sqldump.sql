BEGIN TRANSACTION;
CREATE TABLE article (
id Text PRIMARY KEY,
author_id Text,
title Text,
FOREIGN KEY("author_id") REFERENCES "author"("id"));
CREATE TABLE author (
id Text PRIMARY KEY,
name Text
);


INSERT INTO article (id,author_id,title) VALUES
('book-1','author-1','Harry Potter and the Philosophers Stone'),
('book-2','author-2','Moby Dick'),
('book-3','author-3','Interview with the vampire');

INSERT INTO author (id,name) VALUES
('author-1','Joanne Rowling'),
('author-2','Herman Melville'),
('author-3','Anne Rice');


COMMIT;