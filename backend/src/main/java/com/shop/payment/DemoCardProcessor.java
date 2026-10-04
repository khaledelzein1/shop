package com.shop.payment;

import java.time.Clock;
import java.time.YearMonth;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Processeur de carte simulé, utilisé quand aucune clé Stripe n'est configurée. Fait les mêmes
 * contrôles qu'un vrai processeur (Luhn, date d'expiration, CVC) mais ne débite rien. Les numéros
 * de test de Stripe gardent leur comportement (4242… accepté, 4000…0002 refusé, etc.) pour que les
 * scénarios de test restent valables le jour où l'on passe sur Stripe.
 */
@Component
public class DemoCardProcessor {

  /** Numéros de test Stripe qui simulent un refus, avec le message renvoyé au client. */
  private static final Map<String, String> DECLINED_TEST_CARDS =
      Map.of(
          "4000000000000002", "Your card was declined.",
          "4000000000009995", "Your card has insufficient funds.",
          "4000000000000069", "Your card has expired.",
          "4000000000000127", "Your card's security code is incorrect.");

  private final Clock clock;

  public DemoCardProcessor() {
    this(Clock.systemUTC());
  }

  DemoCardProcessor(Clock clock) {
    this.clock = clock;
  }

  /** Accepte ou refuse la carte ; en cas de refus, lève {@link PaymentDeclinedException}. */
  public ApprovedPayment charge(CardDetails card) {
    String number = card.number().replaceAll("[\\s-]", "");
    if (!number.matches("\\d{12,19}") || !passesLuhn(number)) {
      throw new PaymentDeclinedException("Your card number is invalid.");
    }
    CardBrand brand = CardBrand.detect(number);

    int year = card.expYear() < 100 ? 2000 + card.expYear() : card.expYear();
    if (YearMonth.of(year, card.expMonth()).isBefore(YearMonth.now(clock))) {
      throw new PaymentDeclinedException("Your card has expired.");
    }

    int cvcLength = brand == CardBrand.AMEX ? 4 : 3;
    if (!card.cvc().matches("\\d{" + cvcLength + "}")) {
      throw new PaymentDeclinedException("Your card's security code is invalid.");
    }

    String declineReason = DECLINED_TEST_CARDS.get(number);
    if (declineReason != null) {
      throw new PaymentDeclinedException(declineReason);
    }
    return new ApprovedPayment(brand.label, number.substring(number.length() - 4));
  }

  /** Clé de contrôle de Luhn : attrape la plupart des fautes de frappe dans un numéro de carte. */
  static boolean passesLuhn(String digits) {
    int sum = 0;
    boolean doubleIt = false;
    for (int i = digits.length() - 1; i >= 0; i--) {
      int d = digits.charAt(i) - '0';
      if (doubleIt) {
        d *= 2;
        if (d > 9) {
          d -= 9;
        }
      }
      sum += d;
      doubleIt = !doubleIt;
    }
    return sum % 10 == 0;
  }

  public record ApprovedPayment(String cardBrand, String cardLast4) {}

  enum CardBrand {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMEX("American Express"),
    OTHER("Card");

    private final String label;

    CardBrand(String label) {
      this.label = label;
    }

    static CardBrand detect(String number) {
      if (number.startsWith("4")) {
        return VISA;
      }
      if (number.startsWith("34") || number.startsWith("37")) {
        return AMEX;
      }
      int prefix2 = Integer.parseInt(number.substring(0, 2));
      int prefix4 = Integer.parseInt(number.substring(0, 4));
      if ((prefix2 >= 51 && prefix2 <= 55) || (prefix4 >= 2221 && prefix4 <= 2720)) {
        return MASTERCARD;
      }
      return OTHER;
    }
  }
}
