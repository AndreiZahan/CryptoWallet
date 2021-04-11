package com.sci.finalproject.myProject.repository;

import com.sci.finalproject.myProject.model.CoinName;
import com.sci.finalproject.myProject.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findWalletByWalletCoin (CoinName walletCoin);
    Wallet findWalletByWalletName (String walletName);
}
