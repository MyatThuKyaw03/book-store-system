package com.example.bookstorewithsecurity.controller;


import com.example.bookstorewithsecurity.ds.CartItem;
import com.example.bookstorewithsecurity.entity.Author;
import com.example.bookstorewithsecurity.entity.Book;
import com.example.bookstorewithsecurity.entity.User;
import com.example.bookstorewithsecurity.service.BookService;
import com.example.bookstorewithsecurity.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class BooksController {

    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;



    @GetMapping({"/","/home"})
    public ModelAndView index(){

        return new ModelAndView("home","books",bookService.listBook());
    }
    @GetMapping("/author-form")
    public String authorForm(Model model){
        model.addAttribute("author",new Author());
        return "author-form";
    }
    @PostMapping("/author-form")
    public String saveAuthor(@Valid Author author, BindingResult result){
        if(result.hasErrors()){
            return "author-form";
        }
        bookService.saveAuthor(author);
        return "redirect:/authors";
    }
    @GetMapping("/authors")
    public String listAuthor(Model model){
        model.addAttribute("authors",bookService.listAuthor());

        return "authors";

    }
    @GetMapping("/book-form")
    public String bookForm(Model model){
        model.addAttribute("authors",bookService.listAuthor());
        model.addAttribute("book",new Book());
        return "bookform";
    }
    @PostMapping("/book-form")
    public String saveBook(@Valid Book book, BindingResult result,Model model
                            , RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            model.addAttribute("authors",bookService.listAuthor());
            return "bookform";
        }
        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("success",true);
        return "redirect:/list-books";
    }
    @GetMapping("/list-books")
    public String listAllBooks(Model model){
        model.addAttribute("update",model.containsAttribute("update"));
        model.addAttribute("delete",model.containsAttribute("delete"));
        model.addAttribute("success",model.containsAttribute("success"));
        model.addAttribute("updateAgain",model.containsAttribute("updateAgain"));
        model.addAttribute("books",bookService.listBook());
        return "books";
    }
    @GetMapping("/book/remove")
    public String removeBook(@RequestParam("id")int id,RedirectAttributes attributes){
        bookService.removeBook(id);
        attributes.addFlashAttribute("delete",true);
        return "redirect:/list-books";
    }
    @GetMapping("/book/update")
    public String updateForm(@RequestParam("id")int id,Model model){
        model.addAttribute("book",bookService.findBookById(id));
        this.bookId=id;
        model.addAttribute("authors",bookService.listAuthor());
        return "book-update";
    }
    int bookId;

    @PostMapping("/book/update")
    public String updateBook(@Valid Book book,RedirectAttributes attributes,BindingResult result,Model model){
        if(result.hasErrors()){
            model.addAttribute("book",bookService.findBookById(book.getId()));
            model.addAttribute("authors",bookService.listAuthor());
            return "book-update";
        }
        book.setId(bookId);
        bookService.update(book);
        attributes.addFlashAttribute("update", true);
        return "redirect:/list-books";
        }
        String imageUrl;
        Author  author;
        int updateId;
        @GetMapping("/book/ui-update")
        public String uiUpdate(@RequestParam ("id")int id,Model model){

            Book updateBook= bookService.findBookById(id);
            author=updateBook.getAuthor();
            imageUrl=updateBook.getImgUrl();
            updateId=updateBook.getId();
            List<Book> bookList=bookService.listBook().stream()//.filter(b -> b.equals(updateBook))
                    .map(a -> {
                        if(a.equals(updateBook)) {
                            a.setRender(true);
                        }
                        return a;})
                    .collect(Collectors.toList());
            model.addAttribute("books",bookList);
            model.addAttribute("updateBook",updateBook);
            return "books";
        }

        @PostMapping("/ui/update/book")
        public String updateBookAgain(Book updateBook,RedirectAttributes attributes,BindingResult result){
        if(result.hasErrors()){
            return "redirect:/book/ui-update";
        }
        updateBook.setImgUrl(imageUrl);
        updateBook.setAuthor(author);
        updateBook.setId(updateId);
        updateBook.setRender(false);
        attributes.addFlashAttribute("updateAgain",true);
        bookService.updateAgain(updateBook);
        return "redirect:/list-books";
        }
        @GetMapping("/book/details")
        public String detailsBook(@RequestParam ("id") int id,Model model){
            model.addAttribute("book",bookService.findBookById(id));
            return "details-book";
        }
        @GetMapping("/cart/add-cart")
        public String addToCart(@RequestParam("id")int id){
            bookService.addToCart(id);
            return "redirect:/book/details?id="+id;
        }
        @ModelAttribute("cartSize")
        public Integer cartSize(){
            return bookService.cartSize();
        }

        @GetMapping("/view-cart")
        public String viewCart(Model model){
            model.addAttribute("cartItem",new CartItem());
            model.addAttribute("changeButton",false);
            model.addAttribute("cartItems",bookService.getCartItems());
            return "cart-view";
        }
    @GetMapping("/book/remove-cart")
    public String removeCartBook(@RequestParam("id")int id,RedirectAttributes attributes){
        bookService.removeCArtBook(id);
        attributes.addFlashAttribute("delete",true);
        return "redirect:/view-cart";
    }
    @ModelAttribute("total")
    public String addPrice(Model model){
            model.addAttribute("total",bookService.totalPrice());
            return "redirect:/view-cart";
    }
    @GetMapping("/clear-cart")
    public String clearCart(){
            bookService.clearCart();
            return "redirect:/view-cart";
    }
    @GetMapping("/checkout-v1")
    public String checkOutV1(Model model){
        Set<CartItem> cartItems=bookService.getCartItems()
                .stream()
                .map(c ->
                        {c.setRender(true);
                        return c;}
                        ).collect(Collectors.toSet());
        model.addAttribute("cartItem",new CartItem());
        model.addAttribute("cartItems",cartItems);
        model.addAttribute("changeButton",true);
        return "cart-view";
    }
    @PostMapping("/checkout-v2")
    public String checkOutV2(CartItem cartItem,Model model){
            model.addAttribute("cartItems",bookService.getCartItems());
           int i=0;
            for (CartItem cartItem1:bookService.getCartItems()){
                cartItem1.setQuantity(cartItem.getQuantityLinkedList().get(i));
                cartItem1.setRender(false);
                i++;
            }

            return "redirect:/view-cart";
    }
    @GetMapping("/sign-up")
    public String signUp(Model model){
        model.addAttribute("user",new User());
        return "signup";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }


    @PostMapping("/register")
    public String register(User user, BindingResult result){

        if(result.hasErrors()){
            return "signup";
        }
        userService.signUp(user);
        return "redirect:/login";
    }
    @GetMapping("/login-error")
    public String loginError(Model model){
        model.addAttribute("loginError",true);
        return "login";
    }
}
