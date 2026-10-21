package com.alejandrolunatech.patterns.gof.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ShippingCostTest {

    @Test
    void flatRateIgnoresTheParcel() {
        assertThat(ShippingCost.flat(695).centsFor(new Parcel(12_000, 5_000))).isEqualTo(695);
    }

    @Test
    void weightIsChargedPerStartedKilogram() {
        ShippingCost perKilo = ShippingCost.perStartedKilogram(300);

        assertThat(perKilo.centsFor(new Parcel(1_000, 0))).isEqualTo(300);
        assertThat(perKilo.centsFor(new Parcel(1_001, 0))).isEqualTo(600);
    }

    @Test
    void strategiesCompose() {
        ShippingCost rule = ShippingCost.freeAbove(5_000, ShippingCost.flat(695));

        assertThat(rule.centsFor(new Parcel(500, 4_999))).isEqualTo(695);
        assertThat(rule.centsFor(new Parcel(500, 5_000))).isZero();
    }

    @Test
    void theContextUsesWhicheverStrategyItIsGiven() {
        Parcel parcel = new Parcel(2_500, 2_000);

        assertThat(new Checkout(ShippingCost.flat(695)).totalCents(parcel)).isEqualTo(2_695);
        assertThat(new Checkout(ShippingCost.perStartedKilogram(300)).totalCents(parcel)).isEqualTo(2_900);
        assertThat(new Checkout(parcelIgnored -> 0).totalCents(parcel)).isEqualTo(2_000);
    }

    @Test
    void aParcelMustWeighSomething() {
        assertThatThrownBy(() -> new Parcel(0, 100)).isInstanceOf(IllegalArgumentException.class);
    }
}
