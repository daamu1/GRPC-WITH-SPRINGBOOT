package com.saurabh.grpc.server;

import com.saurabh.Author;
import com.saurabh.Book;
import com.saurabh.BookAuthorServiceGrpc;
import com.saurabh.model.GrpcAuthor;
import com.saurabh.repository.AuthorRepository;
import com.saurabh.repository.BookRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;



@GrpcService
@RequiredArgsConstructor
public class BookAuthorServerService extends BookAuthorServiceGrpc.BookAuthorServiceImplBase {
    @Autowired(required = true)
    private AuthorRepository authorRepository;
    @Autowired
    private static BookRepository bookRepository;

    @Override
    public void getAuthor(Author request, StreamObserver<Author> responseObserver) {
        List<GrpcAuthor> getAuthorsFromTemp = authorRepository.findAll();
        List<Author> authors=new ArrayList<>();
        for (GrpcAuthor dbAuthor : getAuthorsFromTemp) {
            // Map the fields from the database entity to the gRPC message
            Author grpcAuthor = Author.newBuilder()
                    .setAuthorId(dbAuthor.getAuthorId())
                    .setFirstName(dbAuthor.getFirstName())
                    .setLastName(dbAuthor.getLastName())
                    .setGender(dbAuthor.getGender())
                    .setBookId(dbAuthor.getBookId())
                    .build();
                     authors.add(grpcAuthor);
        }
                 authors
                .stream()
                .filter(author -> author.getAuthorId() == request.getAuthorId())
                .findFirst()
                .ifPresent(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getBooksByAuthor(Author request, StreamObserver<Book> responseObserver) {
        List<com.saurabh.model.GrpcBook> getBooksFromTempDb=bookRepository.findAll();
        List<Book>books=new ArrayList<>();
        for(com.saurabh.model.GrpcBook book:getBooksFromTempDb)
        {
            Book grpcbook=Book.newBuilder()
                    .setAuthorId(book.getAuthorId())
                    .setAuthorId(book.getAuthorId())
                    .setPages(book.getPages())
                    .setPrice(book.getPrice())
                    .setTitle(book.getTitle())
                    .build();
                    books.add(grpcbook);
        }
        books.stream()
                .filter(book -> book.getAuthorId() == request.getAuthorId())
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Book> getExpensiveBook(StreamObserver<Book> responseObserver) {
        return new StreamObserver<Book>() {
            Book expensiveBook = null;
            float priceTrack = 0;

            @Override
            public void onNext(Book book) {
                if (book.getPrice() > priceTrack) {
                    priceTrack = book.getPrice();
                    expensiveBook = book;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(expensiveBook);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Book> getBooksByGender(StreamObserver<Book> responseObserver) {
        return new StreamObserver<Book>() {
            List<Book> bookList = new ArrayList<>();

            @Override
            public void onNext(Book book) {
                List<com.saurabh.model.GrpcBook> getBooksFromTempDb=bookRepository.findAll();
                List<Book>books=new ArrayList<>();
                for(com.saurabh.model.GrpcBook abook:getBooksFromTempDb)
                {
                    Book grpcbook=Book.newBuilder()
                            .setAuthorId(abook.getAuthorId())
                            .setAuthorId(abook.getAuthorId())
                            .setPages(abook.getPages())
                            .setPrice(abook.getPrice())
                            .setTitle(abook.getTitle())
                            .build();
                    books.add(grpcbook);
                }
                books.stream()
                        .filter(bookFromDb -> bookFromDb.getAuthorId() == book.getAuthorId())
                        .forEach(bookList::add);
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                bookList.forEach(responseObserver::onNext);
                responseObserver.onCompleted();
            }
        };
    }
}
