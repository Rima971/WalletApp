package com.bank.walletapp.entities;


import com.bank.walletapp.exceptions.WalletNotFound;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private Country country;

    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    public User(String username, String password, Country country){
        this.username = username;
        this.password = password;
        this.country = country;
        this.wallet = new Wallet(country.currency);
    }

    public Wallet getWallet() throws WalletNotFound {
        if (wallet == null) throw new WalletNotFound();
        return this.wallet;
    }
}