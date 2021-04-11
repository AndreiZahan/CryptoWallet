package com.sci.finalproject.myProject.controller;

import com.sci.finalproject.myProject.model.CoinName;
import com.sci.finalproject.myProject.model.Wallet;
import com.sci.finalproject.myProject.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
public class WalletController {

    @Autowired
    public WalletService walletService;

    @RequestMapping(value = "/user/createWallet", method = RequestMethod.GET)
    public ModelAndView createWallet() {
        ModelAndView modelAndView = new ModelAndView();
        Wallet wallet = new Wallet();
        modelAndView.addObject("wallet", wallet);
        modelAndView.setViewName("user/createWallet");
        return modelAndView;
    }

    @RequestMapping(value = "/user/createWallet", method = RequestMethod.POST)
    public ModelAndView createNewWallet (@Valid Wallet wallet, Principal principal, BindingResult bindingResult) {
        DecimalFormat df = new DecimalFormat("#.##");
        ModelAndView modelAndView = new ModelAndView();
        Wallet walletExists = walletService.findWalletByName(wallet.getWalletCoin().toString() + principal.getName());
        if (walletExists != null) {
            bindingResult
                    .rejectValue("walletName", "error.wallet",
                            "You already have a wallet for " + wallet.getWalletCoin() + ".");
        } if (bindingResult.hasErrors()) {
            modelAndView.setViewName("user/createWallet");
        } else {
            try {
                getCoinPriceSql();
                wallet.setUsdPrice(Double.valueOf(df.format(wallet.getNumberOfCoins() * getCoinPriceSql().get(wallet.getWalletCoin()))));
                wallet.setWalletName(wallet.getWalletCoin().toString() + principal.getName());
                walletService.saveWallet(wallet);
                modelAndView.addObject("successMessage", "Wallet has been created successfully");
                modelAndView.addObject("wallet", new Wallet());
                modelAndView.setViewName("user/createWallet");
            } catch (NullPointerException e) {
                bindingResult.rejectValue("walletName", "error.wallet", "The coin doesn't exist.");
            }
        }
        return modelAndView;
    }

    @RequestMapping(value = "/user/seeWallets")
    public ModelAndView getUserWallets(Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        List<Wallet> walletFullList = walletService.getAllWallets();
        List<Wallet> walletUserList = new ArrayList<>();
        for (Wallet wallet : walletFullList) {
            if (wallet.getWalletName().contains(principal.getName())) {
                walletUserList.add(wallet);
            }
        }
        modelAndView.addObject("wallets", walletUserList);
        modelAndView.setViewName("user/list-wallets");
        return modelAndView;
    }

    @RequestMapping(value = "/user/editWallet/{id}")
    public ModelAndView editWalletById(ModelAndView modelAndView, @PathVariable("id") Optional<Long> id) throws Exception {
        if (id.isPresent()) {
            Wallet wallet = walletService.getWalletById(id.get());
            modelAndView.addObject("wallet", wallet);
            modelAndView.setViewName("user/add-edit-wallet");
        } else {
            modelAndView.addObject("wallet", new Wallet());
            modelAndView.setViewName("user/add-edit-wallet");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/user/updateWallet", method = RequestMethod.POST)
    public ModelAndView updateWallet(Wallet wallet)
    {
        walletService.createOrUpdateWallet(wallet);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/user/seeWallets");
        return modelAndView;
    }

    @RequestMapping(value = "/user/deleteWalletById/{id}")
    public String deleteUserWalletById(@PathVariable Long id) throws Exception {
        walletService.deleteWalletById(id);
        return "redirect:/user/seeWallets";
    }

    @RequestMapping(value = "/admin/deleteWalletById/{id}")
    public String deleteWalletById(@PathVariable Long id) throws Exception {
        walletService.deleteWalletById(id);
        return "redirect:/admin/seeWallets";
    }

    @RequestMapping(value = "/admin/seeWallets")
    public ModelAndView getAllWallets() {
        ModelAndView modelAndView = new ModelAndView();
        List<Wallet> list = walletService.getAllWallets();
        modelAndView.addObject("wallets", list);
        modelAndView.setViewName("admin/list-wallets");
        return modelAndView;
    }

    private HashMap<CoinName, Double> getCoinPriceSql() {
        String sqlSelectCoins = "SELECT * FROM login.coins;";
        String connectionUrl = "jdbc:mysql://localhost:3306/login";
        HashMap<CoinName, Double> x = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "Ubuntu95!");
             PreparedStatement ps = conn.prepareStatement(sqlSelectCoins);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CoinName coinName = CoinName.valueOf(rs.getString("coin_name"));
                Double coinUsdPrice = rs.getDouble("usd_price");
                x.put(coinName, coinUsdPrice);
            }
        } catch (SQLException e) {
            System.out.println("nevertheless");
        }
        return x;
    }
}
