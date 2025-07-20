package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_markets")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountMarket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String account;
    private String market;
}
