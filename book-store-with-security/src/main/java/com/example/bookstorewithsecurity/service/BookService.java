package com.example.bookstorewithsecurity.service;


import com.example.bookstorewithsecurity.dao.AuthorDao;
import com.example.bookstorewithsecurity.dao.BookDao;
import com.example.bookstorewithsecurity.ds.Cart;
import com.example.bookstorewithsecurity.ds.CartItem;
import com.example.bookstorewithsecurity.entity.Author;
import com.example.bookstorewithsecurity.entity.Book;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final AuthorDao authorDao;
    private final BookDao bookDao;


    private final Cart cart;

    public BookService(AuthorDao authorDao, BookDao bookDao, Cart cart) {
        this.authorDao = authorDao;
        this.bookDao = bookDao;
        this.cart = cart;
    }

    public void saveAuthor(Author author){
        authorDao.save(author);
    }
    public List<Author> listAuthor(){
        return authorDao.findAll();
    }
    @Transactional
    public void saveBook(Book book){
        Author author=authorDao.findById(book.getAuthor().getId()).get();
        author.addBook(book);
        bookDao.save(book);
    }
    public List<Book> listBook(){
        return bookDao.findAll();
    }
    @Transactional
    public void removeBook(int bookId){
        Book book=bookDao.findById(bookId).get();
        Author author=book.getAuthor();
        author.removeBook(book);

//        if(bookDao.existsById(bookId)){
//            bookDao.deleteById(bookId);
//        }
//        else {
//            throw new EntityNotFoundException(bookId + " not found!");
//        }
    }
    public Book findBookById(int id){
        return bookDao.findById(id).orElseThrow(EntityNotFoundException::new);
    }
    @Transactional
    public void update(Book book) {
        Book existBook=findBookById(book.getId());
        existBook.setAuthor(book.getAuthor());
        existBook.setId(book.getId());
        existBook.setTitle(book.getTitle());
        existBook.setImgUrl(book.getImgUrl());
        existBook.setPublisher(book.getPublisher());
        existBook.setYearPublished(book.getYearPublished());
        existBook.setPrice(book.getPrice());
    }

    public void updateAgain(Book updateBook) {
       bookDao.saveAndFlush(updateBook);
    }

    public void addToCart(int id) {
        Book book=findBookById(id);
        cart.addToCart(new CartItem(
                book.getId(),
                book.getTitle(),
                book.getPrice(),
                1
        ));
    }
    public int cartSize(){
        return cart.cartSize();
    }
    public Set<CartItem> getCartItems(){
        return cart.getCartItems();
    }

    public Set<CartItem> removeCArtBook(int id) {
       Set<CartItem> cartItems=getCartItems().stream().filter(i -> i.getId()!=id).collect(Collectors.toSet());
       cart.setCartItems(cartItems);
       return cartItems;

    }

    public double totalPrice() {
        double total=getCartItems().stream()
                .map( a -> a.getPrice() *a.getQuantity())
                .mapToDouble(a -> a).sum();
        return total;
    }

    public void clearCart(){
        cart.clearCart();
    }
}
