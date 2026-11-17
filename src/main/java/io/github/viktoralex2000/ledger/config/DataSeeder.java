package io.github.viktoralex2000.ledger.config;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.viktoralex2000.ledger.model.WalletEntity;
import io.github.viktoralex2000.ledger.model.UserEntity;
import io.github.viktoralex2000.ledger.repository.UserRepository;
import io.github.viktoralex2000.ledger.repository.WalletRepository;
import io.github.viktoralex2000.ledger.service.FxService;


@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final FxService fxService;
    private final PasswordEncoder passwordEncoder;
    private final LedgerProperties properties;

    public DataSeeder(UserRepository userRepository,
                      WalletRepository walletRepository,
                      FxService fxService,
                      PasswordEncoder passwordEncoder,
                      LedgerProperties properties) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.fxService = fxService;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        seedFeeWallets();
    }

    private void seedAdmin() {
        String email = properties.app().seedAdminEmail();
        if (!userRepository.existsByEmailIgnoreCase(email)) {
            UserEntity admin = new UserEntity();
            admin.setEmail(email);
            admin.setPasswordHash(passwordEncoder.encode(properties.app().seedAdminPassword()));
            admin.setFirstName("Admin");
            admin.setRole(UserEntity.UserRole.ADMIN);
            userRepository.save(admin);
            log.info("Seeded admin account: {}", email);
        }
    }

    private void seedFeeWallets() {
        String ownerEmail = properties.app().feeWalletOwnerEmail();
        UserEntity owner = userRepository.findByEmailIgnoreCase(ownerEmail).orElseGet(() -> {
            UserEntity fees = new UserEntity();
            fees.setEmail(ownerEmail);
            fees.setPasswordHash("!disabled!");
            fees.setFirstName("Ledger");
            fees.setLastName("Fees");
            fees.setRole(UserEntity.UserRole.SYSTEM);
            return userRepository.save(fees);
        });
        for (String currency : fxService.supportedCurrencies()) {
            if (!walletRepository.existsByUserIdAndCurrency(owner.getId(), currency)) {
                WalletEntity wallet = new WalletEntity();
                wallet.setUser(owner);
                wallet.setCurrency(currency);
                walletRepository.save(wallet);
            }
        }
        log.info("Seeded fee-account wallets for {}", ownerEmail);
    }
}
