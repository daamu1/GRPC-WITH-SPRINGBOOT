package com.saurabh;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "author") // Specify the name of the database table
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GrpcAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private int authorId;

    @Column(name = "book_id")
    private int bookId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    private String gender;
}
