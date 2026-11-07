package io.github.viktoralex2000.ledger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ledger")
public record LedgerProperties(

) {

}
