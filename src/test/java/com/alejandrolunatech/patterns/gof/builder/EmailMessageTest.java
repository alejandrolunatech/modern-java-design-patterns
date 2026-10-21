package com.alejandrolunatech.patterns.gof.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EmailMessageTest {

    @Test
    void buildsAMessageWithOptionalParts() {
        EmailMessage message = EmailMessage.from("billing@example.com")
                .to("ana@example.com")
                .cc("finance@example.com")
                .replyTo("support@example.com")
                .subject("Your invoice")
                .body("Attached.")
                .build();

        assertThat(message.to()).containsExactly("ana@example.com");
        assertThat(message.cc()).containsExactly("finance@example.com");
        assertThat(message.replyTo()).contains("support@example.com");
    }

    @Test
    void optionalPartsCanBeLeftOut() {
        EmailMessage message = EmailMessage.from("a@example.com").to("b@example.com").subject("Hi").build();

        assertThat(message.cc()).isEmpty();
        assertThat(message.replyTo()).isEmpty();
    }

    @Test
    void validationLivesInTheRecordSoTheBuilderCannotSkipIt() {
        assertThatThrownBy(() -> EmailMessage.from("a@example.com").subject("Hi").build())
                .hasMessageContaining("recipient");
        assertThatThrownBy(() -> new EmailMessage("a@example.com", List.of("b@example.com"), List.of(),
                Optional.empty(), " ", "")).hasMessageContaining("subject");
    }

    @Test
    void theMessageIsImmutable() {
        List<String> recipients = new ArrayList<>(List.of("b@example.com"));
        EmailMessage message = new EmailMessage("a@example.com", recipients, List.of(), Optional.empty(), "Hi", "");

        recipients.add("intruder@example.com");

        assertThat(message.to()).containsExactly("b@example.com");
        assertThatThrownBy(() -> message.to().add("x@example.com")).isInstanceOf(UnsupportedOperationException.class);
    }
}
