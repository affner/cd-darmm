package com.cwdarmm.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feeds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
