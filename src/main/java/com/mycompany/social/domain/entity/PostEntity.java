package com.mycompany.social.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class PostEntity implements Serializable {

    private static final long serialVersionUID = 7100199686599195321L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_text", nullable = false)
    private String text;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @PrePersist
    public void prePersist(){
        setDateTime(LocalDateTime.now());
    }

}
