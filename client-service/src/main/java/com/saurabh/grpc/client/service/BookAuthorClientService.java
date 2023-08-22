package com.saurabh.grpc.client.service;


import com.saurabh.Author;
import com.saurabh.Book;
import com.saurabh.BookAuthorServiceGrpc;
import com.google.protobuf.Descriptors;
import com.saurabh.model.GrpcAuthor;
import com.saurabh.repository.AuthorRepository;
import com.saurabh.repository.BookRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BookAuthorClientService {
    @Autowired(required = true)
    private AuthorRepository authorRepository;
    @Autowired
    private static BookRepository bookRepository;

    @GrpcClient("grpc-saurabh-service")
    BookAuthorServiceGrpc.BookAuthorServiceBlockingStub synchronousClient;

    @GrpcClient("grpc-saurabh-service")
    BookAuthorServiceGrpc.BookAuthorServiceStub asynchronousClient;

    public Map<Descriptors.FieldDescriptor, Object> getAuthor(int authorId) {
        Author authorRequest = Author.newBuilder().setAuthorId(authorId).build();
        Author authorResponse = synchronousClient.getAuthor(authorRequest);
        return authorResponse.getAllFields();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthor(int authorId) throws InterruptedException {
        final Author authorRequest = Author.newBuilder().setAuthorId(authorId).build();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        asynchronousClient.getBooksByAuthor(authorRequest, new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                response.add(book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        return await ? response : Collections.emptyList();
    }

    public Map<String, Map<Descriptors.FieldDescriptor, Object>> getExpensiveBook() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Map<String, Map<Descriptors.FieldDescriptor, Object>> response = new HashMap<>();
        StreamObserver<Book> responseObserver = asynchronousClient.getExpensiveBook(new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                response.put("Expensive Book", book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });
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
        books.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        return await ? response : Collections.emptyMap();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByGender(String gender) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        StreamObserver<Book> responseObserver = asynchronousClient.getBooksByGender(new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                response.add(book.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });
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
                .filter(author -> author.getGender().equalsIgnoreCase(gender))
                .forEach(author -> responseObserver.onNext(Book.newBuilder().setAuthorId(author.getAuthorId()).build()));
        responseObserver.onCompleted();
        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        return await ? response : Collections.emptyList();
    }
}
