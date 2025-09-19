package com.training.ec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.training.ec.entity.Stock;
import com.training.ec.service.StockService;

/**
 * ユーザー向けの銘柄一覧ページを制御するコントローラーです。
 */
@Controller
@RequestMapping("/user")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * 銘柄一覧ページを表示します。検索機能にも対応しています。
     */
    @GetMapping("/stocks")
    @PreAuthorize("hasRole('USER')")
    public String showStockList(Model model,
                                @RequestParam(name = "searchTerm", required = false) String searchTerm) {
        List<Stock> stockList = stockService.getStocksBySearchTerm(searchTerm);
        model.addAttribute("stocks", stockList);
        model.addAttribute("searchTerm", searchTerm);
        return "user/stock/stockList"; // Thymeleafテンプレート名
    }
}
