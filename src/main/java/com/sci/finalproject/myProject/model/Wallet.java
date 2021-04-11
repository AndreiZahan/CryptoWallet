package com.sci.finalproject.myProject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wallet_id")
    private Long id;

    @Column(name = "crypto_key")
    @NotEmpty(message = "*Please provide a crypto key")
    private String cryptoKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_coin")
    @NotEmpty(message = "*Please provide the coin you want to insert in your wallet")
    private CoinName walletCoin;

    @Column(name = "wallet_name")
    private String walletName;

    @Column(name = "number_of_coins")
    @NotEmpty(message = "*Please insert the number of your coins")
    private Integer numberOfCoins;

    @Column(name = "usd_price")
    private Double usdPrice;
}
