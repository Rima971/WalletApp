package com.bank.walletapp.services;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.entities.Country;
import com.bank.walletapp.exceptions.UserNotFound;
import com.bank.walletapp.exceptions.UsernameAlreadyExists;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletRepository walletRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UserNotFound {

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        return new CustomUserDetails(user);
    }

    public User register(String username, String password, Country country) throws UsernameAlreadyExists {
        if(userRepository.existsByUsername(username)){
            throw new UsernameAlreadyExists();
        }
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        User user = new User(username, encodedPassword, country);
        return this.userRepository.save(user);
    }

    public User fetchUserByUsername(String username) throws UserNotFound {
        return this.userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
    }

    public void deleteUserByUsername(String username) throws UserNotFound {
        User user = this.fetchUserByUsername(username);
        this.userRepository.deleteById(user.getId());
        this.walletRepository.deleteAllByUserUsername(username);
    }

}
