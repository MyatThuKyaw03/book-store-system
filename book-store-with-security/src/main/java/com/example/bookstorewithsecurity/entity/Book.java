package com.example.bookstorewithsecurity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter @Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotEmpty(message = "Title cannot be empty!")
    private String title;
    @Min(value = 1,message = "Price is too low")
    @Max(value = 100,message = "Price is too high")
    private double price;
    @Past(message = "Year published must be past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate yearPublished;
    @NotEmpty(message = "publisher cannot be null")
    private String publisher;
    @NotEmpty(message = "Image cannot be empty!")
    private String imgUrl;

    @ManyToOne
    private Author author;

    @Transient
    private boolean render;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
