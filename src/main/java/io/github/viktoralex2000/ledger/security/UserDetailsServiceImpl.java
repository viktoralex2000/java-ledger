package io.github.viktoralex2000.ledger.security;

import io.github.viktoralex2000.ledger.model.UserEntity;
import io.github.viktoralex2000.ledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Optional<UserEntity> userOptional = userRepository.findByEmailIgnoreCase(username);
        //if(userOptional.isEmpty()) {
        //    throw new UsernameNotFoundException("user is not found");
        //} else return new UserPrincipal(userOptional.get());
        UserEntity user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(()-> new UsernameNotFoundException("User is not found"));
        return new UserPrincipal(user);
    }
}
