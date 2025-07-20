package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_feeds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountFeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountDefinition account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private FeedDefinition feed;
}
