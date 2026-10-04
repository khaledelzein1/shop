/**
 * Aides du formulaire de carte intégré : détection de la marque, mise en forme pendant la saisie
 * et contrôles côté navigateur (le backend refait les mêmes contrôles, qui font foi).
 */
export type CardBrand = 'visa' | 'mastercard' | 'amex' | null;

export function digitsOnly(value: string): string {
  return value.replace(/\D/g, '');
}

export function detectBrand(digits: string): CardBrand {
  if (/^4/.test(digits)) return 'visa';
  if (/^3[47]/.test(digits)) return 'amex';
  if (/^(5[1-5]|2(2[2-9][1-9]|2[3-9]\d|[3-6]\d\d|7[01]\d|720))/.test(digits)) return 'mastercard';
  return null;
}

/** "4242424242424242" → "4242 4242 4242 4242" ; American Express en 4-6-5. */
export function formatCardNumber(digits: string): string {
  if (detectBrand(digits) === 'amex') {
    return [digits.slice(0, 4), digits.slice(4, 10), digits.slice(10, 15)].filter(Boolean).join(' ');
  }
  return digits.match(/.{1,4}/g)?.join(' ') ?? '';
}

export function maxCardDigits(brand: CardBrand): number {
  return brand === 'amex' ? 15 : 16;
}

export function cvcLength(brand: CardBrand): number {
  return brand === 'amex' ? 4 : 3;
}

/** Clé de Luhn : détecte la plupart des fautes de frappe dans un numéro de carte. */
export function passesLuhn(digits: string): boolean {
  let sum = 0;
  let doubleIt = false;
  for (let i = digits.length - 1; i >= 0; i--) {
    let d = Number(digits[i]);
    if (doubleIt) {
      d *= 2;
      if (d > 9) d -= 9;
    }
    sum += d;
    doubleIt = !doubleIt;
  }
  return digits.length >= 12 && sum % 10 === 0;
}

/** "1230" → "12 / 30" pendant la saisie. */
export function formatExpiry(digits: string): string {
  const d = digits.slice(0, 4);
  return d.length > 2 ? `${d.slice(0, 2)} / ${d.slice(2)}` : d;
}

/** Mois et année (sur 4 chiffres) d'une date "MM / AA", ou null si incomplète ou invalide. */
export function parseExpiry(value: string): { month: number; year: number } | null {
  const d = digitsOnly(value);
  if (d.length !== 4) return null;
  const month = Number(d.slice(0, 2));
  if (month < 1 || month > 12) return null;
  return { month, year: 2000 + Number(d.slice(2)) };
}

export function isExpired(expiry: { month: number; year: number }, now = new Date()): boolean {
  const current = now.getFullYear() * 12 + now.getMonth() + 1;
  return expiry.year * 12 + expiry.month < current;
}
