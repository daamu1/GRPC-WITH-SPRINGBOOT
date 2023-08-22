package com.saurabh.grpc.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;

    private Long bookId;
    private String firstName;
    private String lastName;
    private String gender;

    // Constructors, getters, setters, etc.

}
