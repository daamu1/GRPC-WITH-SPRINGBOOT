package com.saurabh.grpc.server.model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "book") // Specify the name of the database table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id") // Map book_id to the database column named book_id
    private Long bookId;

    @Column(name = "title") // Map title to the database column named title
    private String title;

    @Column(name = "price") // Map price to the database column named price
    private float price;

    @Column(name = "pages") // Map pages to the database column named pages
    private int pages;

    @Column(name = "author_id") // Map author_id to the database column named author_id
    private int authorId;

    // Other methods can be added as needed

}
