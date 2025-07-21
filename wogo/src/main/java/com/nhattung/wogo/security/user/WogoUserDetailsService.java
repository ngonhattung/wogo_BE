package com.nhattung.wogo.security.user;


import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WogoUserDetailsService implements UserDetailsService { // get information user from database

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = Optional.ofNullable(userRepository.findByPhone(phone))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return WogoUserDetails.buildUserDetail(user);
    }

}
