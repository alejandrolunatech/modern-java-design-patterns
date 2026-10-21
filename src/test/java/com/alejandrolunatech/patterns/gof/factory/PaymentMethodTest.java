package com.alejandrolunatech.patterns.gof.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PaymentMethodTest {

    @Test
    void parsesEachKindIntoItsOwnType() {
        assertThat(PaymentMethod.parse("card:4111 1111 1111 1111")).isEqualTo(new PaymentMethod.Card("1111"));
        assertThat(PaymentMethod.parse("ideal:ingbnl2a")).isEqualTo(new PaymentMethod.Ideal("INGBNL2A"));
        assertThat(PaymentMethod.parse("transfer:NL91 ABNA 0417 1643 00"))
                .isEqualTo(new PaymentMethod.BankTransfer("NL91ABNA0417164300"));
    }

    @Test
    void keepsOnlyTheLastFourDigitsOfACard() {
        PaymentMethod card = PaymentMethod.parse("card:5555-5555-5555-4444");

        assertThat(card.toString()).doesNotContain("5555");
    }

    @Test
    void rejectsUnknownKindsAndBadInput() {
        assertThatThrownBy(() -> PaymentMethod.parse("crypto:abc")).hasMessageContaining("unknown");
        assertThatThrownBy(() -> PaymentMethod.parse("card:1234")).hasMessageContaining("digits");
        assertThatThrownBy(() -> PaymentMethod.parse("no-colon")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void feesFollowTheType() {
        assertThat(Fees.feeCents(new PaymentMethod.Card("1111"), 10_000)).isEqualTo(165);
        assertThat(Fees.feeCents(new PaymentMethod.Ideal("INGBNL2A"), 10_000)).isEqualTo(29);
        assertThat(Fees.feeCents(new PaymentMethod.BankTransfer("NL91ABNA0417164300"), 10_000)).isZero();
    }
}
