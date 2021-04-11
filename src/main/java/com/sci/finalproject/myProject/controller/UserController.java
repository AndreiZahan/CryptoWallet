package com.sci.finalproject.myProject.controller;

import com.sci.finalproject.myProject.model.User;
import com.sci.finalproject.myProject.model.Wallet;
import com.sci.finalproject.myProject.service.UserService;
import com.sci.finalproject.myProject.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @RequestMapping(value = "/admin/seeUsers")
    public ModelAndView getAllUsers() {
        ModelAndView modelAndView = new ModelAndView();
        List<User> list = userService.getAllUsers();
        modelAndView.addObject("users", list);
        modelAndView.setViewName("admin/list-users");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/deleteUserById/{id}")
    public String deleteUserById(@PathVariable Long id) throws Exception {
        Optional<User> user = userService.findUserById(id);
        String username = user.get().getUserName();
        List<Wallet> userWalletList = walletService.getAllWallets();
        for (Wallet wallet : userWalletList) {
            if (wallet.getWalletName().contains(username)) {
                walletService.deleteWalletById(wallet.getId());
            }
        }
        userService.deleteUserById(id);

        return "redirect:/admin/seeUsers";
    }
}
