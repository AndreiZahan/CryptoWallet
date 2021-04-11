package com.sci.finalproject.myProject.service;

import com.sci.finalproject.myProject.model.Coin;
import com.sci.finalproject.myProject.model.CoinName;
import com.sci.finalproject.myProject.repository.CoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CoinService {

    public CoinRepository coinRepository;

    @Autowired
    public CoinService (CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    public Coin findCoinByName (CoinName coinName) {
        return coinRepository.findCoinByCoinName(coinName);
    }

    public Coin saveCoin (Coin coin) {
        return coinRepository.save(coin);
    }

    public void deleteCoinByName(CoinName name) throws Exception
    {
        Coin coin = coinRepository.findCoinByCoinName(name);

        if(coin != null)
        {
            Long coinID = coin.getId();
            coinRepository.deleteById(coinID);
        } else {
            throw new Exception("No coin record exist for given name");
        }
    }

    public void deleteCoinById(Long id) throws Exception {
        Optional<Coin> coin = coinRepository.findById(id);

        if(coin.isPresent())
        {
            coinRepository.deleteById(id);
        } else {
            throw new Exception("No coin record exist for given id");
        }
    }

    public List<Coin> getAllCoins() {
        List<Coin> result = coinRepository.findAll();

        if (result.size() > 0) {
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    public Coin getCoinById(Long id) throws Exception {
        Optional<Coin> coin = coinRepository.findById(id);

        if(coin.isPresent()) {
            return coin.get();
        } else {
            throw new Exception("Coin not found!");
        }
    }

    public Coin createOrUpdateCoin(Coin coin)
    {
        if(coin.getId()  == null)
        {
            coin = coinRepository.save(coin);

            return coin;
        }
        else
        {
            Optional<Coin> coinExisting = coinRepository.findById(coin.getId());

            if(coinExisting.isPresent())
            {
                DecimalFormat df = new DecimalFormat("#.##");

                Coin newCoin = coinExisting.get();
                newCoin.setCoinName(coin.getCoinName());
                newCoin.setUsdPrice(coin.getUsdPrice());
                newCoin.setEurPrice(coin.getUsdPrice() * 0.8358);
                newCoin.setRonPrice(coin.getUsdPrice() * 4.0832);
                newCoin.setOneDayFluctuation(coin.getOneDayFluctuation());
                newCoin.setOneWeekFluctuation(coin.getOneWeekFluctuation());
                newCoin.setMarketCap(coin.getMarketCap());
                newCoin.setVolume(coin.getVolume());

                newCoin.setRonPrice(Double.valueOf(df.format(newCoin.getRonPrice())));
                newCoin.setEurPrice(Double.valueOf(df.format(newCoin.getEurPrice())));
                newCoin.setUsdPrice(Double.valueOf(df.format(newCoin.getUsdPrice())));
                newCoin.setOneDayFluctuation(Float.valueOf(df.format(newCoin.getOneDayFluctuation())));
                newCoin.setOneWeekFluctuation(Float.valueOf(df.format(newCoin.getOneWeekFluctuation())));

                newCoin = coinRepository.save(newCoin);

                return newCoin;
            } else {
                coin = coinRepository.save(coin);

                return coin;
            }
        }
    }
}
