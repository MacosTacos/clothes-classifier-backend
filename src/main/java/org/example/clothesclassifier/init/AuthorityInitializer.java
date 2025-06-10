package org.example.clothesclassifier.init;

import org.example.clothesclassifier.entities.AuthorityEntity;
import org.example.clothesclassifier.repositories.AuthorityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AuthorityInitializer implements CommandLineRunner {
    private final AuthorityRepository authorityRepository;
    private final String[] authorities = {
            "user::read",
            "user::write",
            "user::delete",
            "staff::read",
            "staff::write",
            "staff::delete",
            "admin::read",
            "admin::write",
            "admin::delete"
    };
    public AuthorityInitializer(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        for (String authority : authorities) {
            if (!authorityRepository.existsByAuthority(authority)) {
                AuthorityEntity authorityEntity = new AuthorityEntity();
                authorityEntity.setAuthority(authority);
                authorityRepository.save(authorityEntity);
            }
        }
    }
}
