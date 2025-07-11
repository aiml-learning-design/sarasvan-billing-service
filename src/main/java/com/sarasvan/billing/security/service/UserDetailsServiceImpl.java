package com.sarasvan.billing.security.service;

import com.sarasvan.billing.entity.UserEntity;
import com.sarasvan.billing.mapper.UsersMapper;
import com.sarasvan.billing.model.User;
import com.sarasvan.billing.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository userRepository;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UsersMapper.INSTANCE.entityToDto(userEntity);
    }
}