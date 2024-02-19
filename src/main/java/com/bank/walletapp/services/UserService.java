package com.bank.walletapp.services;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.exceptions.UserNotFound;
import com.bank.walletapp.exceptions.UsernameAlreadyExists;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    WalletService walletService;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UserNotFound {

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        return new CustomUserDetails(user);
    }

    public void register(String username, String password) throws UsernameAlreadyExists {
        if(userRepository.existsByUsername(username)){
            throw new UsernameAlreadyExists();
        }
        Wallet wallet = this.walletService.createWallet();
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        User user = new User(username, encodedPassword, wallet);
        this.userRepository.save(user);
    }

}
