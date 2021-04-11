package com.sci.finalproject.myProject.controller;

import com.sci.finalproject.myProject.model.Coin;
import com.sci.finalproject.myProject.service.CoinService;
import com.sci.finalproject.myProject.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Controller
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private WalletService walletService;

    @RequestMapping(value="/admin/addCoin", method = RequestMethod.GET)
    public ModelAndView getCoinRegistration() {
        ModelAndView modelAndView = new ModelAndView();
        Coin coin = new Coin();
        modelAndView.addObject("coin", coin);
        modelAndView.setViewName("admin/addCoin");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/addCoin", method = RequestMethod.POST)
    public ModelAndView createNewCoin (@Valid Coin coin, BindingResult bindingResult) {
        DecimalFormat df = new DecimalFormat("#.##");
        ModelAndView modelAndView = new ModelAndView();
        Coin coinExists = coinService.findCoinByName(coin.getCoinName());
        if (coinExists != null) {
            bindingResult
                    .rejectValue("coinName", "error.coin",
                            "Coin already registered!");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/addCoin");
        } else {
            coin.setEurPrice(coin.getUsdPrice() * 0.8358);
            coin.setRonPrice(coin.getUsdPrice() * 4.0832);
            coin.setRonPrice(Double.valueOf(df.format(coin.getRonPrice())));
            coin.setEurPrice(Double.valueOf(df.format(coin.getEurPrice())));
            coin.setUsdPrice(Double.valueOf(df.format(coin.getUsdPrice())));
            coin.setOneDayFluctuation(Float.valueOf(df.format(coin.getOneDayFluctuation())));
            coin.setOneWeekFluctuation(Float.valueOf(df.format(coin.getOneWeekFluctuation())));
            coinService.saveCoin(coin);
            modelAndView.addObject("successMessage", "Coin has been registered successfully");
            modelAndView.addObject("coin", new Coin());
            modelAndView.setViewName("admin/addCoin");

        }
        return modelAndView;
    }

    @RequestMapping(value="/admin/deleteCoin", method = RequestMethod.GET)
    public ModelAndView getCoinDeleteForm() {
        ModelAndView modelAndView = new ModelAndView();
        Coin coin = new Coin();
        modelAndView.addObject("coin", coin);
        modelAndView.setViewName("admin/deleteCoin");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/deleteCoin")
    public ModelAndView deleteCoinByName (@Valid Coin coin, BindingResult bindingResult) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        Coin coinExists = coinService.findCoinByName(coin.getCoinName());
        if (coinExists == null) {
            bindingResult
                    .rejectValue("coinName", "error.coin",
                            "Coin doesn't exist!");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("admin/deleteCoin");
        }
        else {
            coinService.deleteCoinByName(coin.getCoinName());
            modelAndView.addObject("successMessage", "Coin has been deleted successfully");
            modelAndView.setViewName("admin/deleteCoin");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/delete/{id}")
    public String deleteCoinById(@PathVariable Long id) throws Exception {
        coinService.deleteCoinById(id);
        return "redirect:/admin/seeCoins";
    }

    @RequestMapping(value = "/admin/seeCoins")
    public ModelAndView getAllCoins() {
        ModelAndView modelAndView = new ModelAndView();
        List<Coin> list = coinService.getAllCoins();
        modelAndView.addObject("coins", list);
        modelAndView.setViewName("admin/list-coins");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/edit/{id}")
    public ModelAndView editCoinById(ModelAndView modelAndView, @PathVariable("id") Optional<Long> id) throws Exception {
        if (id.isPresent()) {
            Coin coin = coinService.getCoinById(id.get());
            modelAndView.addObject("coin", coin);
            modelAndView.setViewName("admin/add-edit-coin");
        } else {
            modelAndView.addObject("coin", new Coin());
            modelAndView.setViewName("admin/add-edit-coin");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/createCoin", method = RequestMethod.POST)
    public ModelAndView createOrUpdateCoin(Coin coin)
    {
        walletService.updateWalletWithCoinUpdate(coin);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin/adminHome");
        return modelAndView;
    }
}
