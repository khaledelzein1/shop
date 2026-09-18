export interface Address {
  id: number;
  label: string | null;
  street: string;
  city: string;
  zipCode: string;
  country: string;
  defaultAddress: boolean;
}

export type AddressPayload = Omit<Address, 'id'>;
