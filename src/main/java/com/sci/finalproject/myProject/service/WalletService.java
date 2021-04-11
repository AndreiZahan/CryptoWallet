package com.sci.finalproject.myProject.service;

import com.sci.finalproject.myProject.model.Coin;
import com.sci.finalproject.myProject.model.CoinName;
import com.sci.finalproject.myProject.model.Wallet;
import com.sci.finalproject.myProject.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    public WalletRepository walletRepository;

    @Autowired
    private CoinService coinService;

    @Autowired
    public WalletService (WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getWalletById(Long id) throws Exception {
        Optional<Wallet> wallet = walletRepository.findById(id);

        if(wallet.isPresent()) {
            return wallet.get();
        } else {
            throw new Exception("Wallet not found!");
        }
    }

    public List<Wallet> findWalletByCoin (CoinName walletCoin) {
        return walletRepository.findWalletByWalletCoin(walletCoin);
    }

    public Wallet findWalletByName (String walletName) {
        return walletRepository.findWalletByWalletName(walletName);
    }

    public Wallet saveWallet (Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public List<Wallet> getAllWallets() {
        List<Wallet> result = walletRepository.findAll();

        if (result.size() > 0) {
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    public Object updateWalletWithCoinUpdate (Coin coin) {
        List<Wallet> walletList = walletRepository.findWalletByWalletCoin(coin.getCoinName());
        coinService.createOrUpdateCoin(coin);
        if(walletList.isEmpty())
        {
            return coin;
        }
        else
        {
            for (Wallet wallet : walletList) {
                Optional<Wallet> walletExisting = walletRepository.findById(wallet.getId());

                if(walletExisting.isPresent())
                {
                    Wallet newWallet = walletExisting.get();
                    newWallet.setCryptoKey(wallet.getCryptoKey());
                    newWallet.setWalletCoin(wallet.getWalletCoin());
                    newWallet.setNumberOfCoins(wallet.getNumberOfCoins());
                    newWallet.setWalletName(wallet.getWalletName());
                    newWallet.setUsdPrice(wallet.getNumberOfCoins() * coinService.createOrUpdateCoin(coin).getUsdPrice());

                    newWallet = walletRepository.save(newWallet);

                } else {
                    wallet = walletRepository.save(wallet);
                }
            }
        }
        return null;
    }

    public Wallet createOrUpdateWallet(Wallet wallet)
    {
        if(wallet.getId()  == null)
        {
            wallet = walletRepository.save(wallet);

            return wallet;
        }
        else
        {
            Optional<Wallet> walletExisting = walletRepository.findById(wallet.getId());

            if(walletExisting.isPresent())
            {
                Wallet newWallet = walletExisting.get();
                newWallet.setNumberOfCoins(wallet.getNumberOfCoins());
                newWallet.setUsdPrice(wallet.getNumberOfCoins() * coinService.findCoinByName(newWallet.getWalletCoin()).getUsdPrice());

                newWallet = walletRepository.save(newWallet);

                return newWallet;
            } else {
                wallet = walletRepository.save(wallet);

                return wallet;
            }
        }
    }

    public void deleteWalletById(Long id) throws Exception {
        Optional<Wallet> wallet = walletRepository.findById(id);

        if(wallet.isPresent())
        {
            walletRepository.deleteById(id);
        } else {
            throw new Exception("No wallet record exist for given id");
        }
    }

    public void deleteWalletByWalletName(String name) throws Exception
    {
        Wallet wallet = walletRepository.findWalletByWalletName(name);

        if(wallet != null)
        {
            Long walletID = wallet.getId();
            walletRepository.deleteById(walletID);
        } else {
            throw new Exception("No wallet record exist for given name");
        }
    }
}
