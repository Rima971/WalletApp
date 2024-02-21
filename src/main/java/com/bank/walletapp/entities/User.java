package com.bank.walletapp.entities;


import com.bank.walletapp.exceptions.WalletNotFound;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
    }

    public Wallet getWallet() throws WalletNotFound {
        if (wallet == null) throw new WalletNotFound();
        return this.wallet;
    }
}