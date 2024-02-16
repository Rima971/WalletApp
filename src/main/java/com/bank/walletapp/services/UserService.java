package com.bank.walletapp.services;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.config.AppSecurityConfig;
import com.bank.walletapp.exceptions.UserNotFound;
import com.bank.walletapp.exceptions.UsernameAlreadyExists;
import com.bank.walletapp.models.User;
import com.bank.walletapp.models.Wallet;
import com.bank.walletapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    WalletService walletService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFound {

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        return new CustomUserDetails(user);
    }

    public void register(String username, String password) throws UsernameAlreadyExists {
        if(userRepository.existsByUsername(username)){
            throw new UsernameAlreadyExists();
        }
        Wallet wallet = this.walletService.createWallet();
        User user = new User(username, AppSecurityConfig.passwordEncoder().encode(password), wallet);
        this.userRepository.save(user);
    }

}
