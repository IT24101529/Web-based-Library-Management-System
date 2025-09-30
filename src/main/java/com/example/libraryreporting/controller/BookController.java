package com.example.libraryreporting.controller;

import com.example.libraryreporting.dao.BookDao;
import com.example.libraryreporting.model.BookReport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BookController {

    private final BookDao bookDao;

    public BookController(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @GetMapping("/books/availability")
    public String getBookAvailability(Model model) {
        List<BookReport> reports = bookDao.getBookAvailability();
        model.addAttribute("books", reports);
        return "book-availability";
    }
}