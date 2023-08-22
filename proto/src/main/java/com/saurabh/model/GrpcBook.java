package com.saurabh.model;
import lombok.*;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "books") // Specify the name of the database table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrpcBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private float price;

    @Column(name = "pages")
    private int pages;

    @Column(name = "author_id")
    private int authorId;
}
