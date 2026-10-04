package com.shop.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.shop.payment.DemoCardProcessor.ApprovedPayment;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class DemoCardProcessorTest {

  /** Octobre 2026 : une carte expirant 09/26 est périmée, 10/26 encore valable. */
  private final DemoCardProcessor processor =
      new DemoCardProcessor(Clock.fixed(Instant.parse("2026-10-15T12:00:00Z"), ZoneOffset.UTC));

  private static CardDetails card(String number, int month, int year, String cvc) {
    return new CardDetails(number, month, year, cvc, "Jane Doe");
  }

  @Test
  void validVisa_isApproved_withBrandAndLast4Only() {
    ApprovedPayment payment = processor.charge(card("4242 4242 4242 4242", 12, 30, "123"));

    assertThat(payment).isEqualTo(new ApprovedPayment("Visa", "4242"));
  }

  @Test
  void mastercardAndAmex_areDetected() {
    assertThat(processor.charge(card("5555555555554444", 1, 2030, "123")).cardBrand())
        .isEqualTo("Mastercard");
    assertThat(processor.charge(card("378282246310005", 1, 2030, "1234")).cardBrand())
        .isEqualTo("American Express");
  }

  @Test
  void numberFailingLuhnCheck_isRejected() {
    assertThatThrownBy(() -> processor.charge(card("4242 4242 4242 4241", 12, 30, "123")))
        .isInstanceOf(PaymentDeclinedException.class)
        .hasMessage("Your card number is invalid.");
  }

  @Test
  void expiryDate_lastMonthIsExpired_currentMonthIsValid() {
    assertThatThrownBy(() -> processor.charge(card("4242424242424242", 9, 26, "123")))
        .isInstanceOf(PaymentDeclinedException.class)
        .hasMessage("Your card has expired.");
    assertThat(processor.charge(card("4242424242424242", 10, 26, "123"))).isNotNull();
  }

  @Test
  void cvc_mustHave3Digits_or4ForAmex() {
    assertThatThrownBy(() -> processor.charge(card("4242424242424242", 12, 30, "12")))
        .hasMessage("Your card's security code is invalid.");
    assertThatThrownBy(() -> processor.charge(card("378282246310005", 12, 30, "123")))
        .hasMessage("Your card's security code is invalid.");
  }

  @Test
  void stripeDeclineTestCards_areDeclinedWithTheirReason() {
    assertThatThrownBy(() -> processor.charge(card("4000000000000002", 12, 30, "123")))
        .hasMessage("Your card was declined.");
    assertThatThrownBy(() -> processor.charge(card("4000000000009995", 12, 30, "123")))
        .hasMessage("Your card has insufficient funds.");
  }

  @Test
  void toString_neverExposesFullNumberOrCvc() {
    assertThat(card("4242424242424242", 12, 30, "987").toString())
        .doesNotContain("424242424242")
        .doesNotContain("987")
        .contains("4242");
  }
}
