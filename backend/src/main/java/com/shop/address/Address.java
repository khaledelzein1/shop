package com.shop.address;

import com.shop.common.domain.BaseEntity;
import com.shop.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
public class Address extends BaseEntity {

  private String label;

  @Column(nullable = false)
  private String street;

  @Column(nullable = false)
  private String city;

  @Column(name = "zip_code", nullable = false)
  private String zipCode;

  @Column(nullable = false)
  private String country;

  @Column(name = "is_default", nullable = false)
  private boolean defaultAddress = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
