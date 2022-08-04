package com.mycompany.social.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "followers")
@Getter
@Setter
public class FollowerEntity implements Serializable {

    private static final long serialVersionUID = 1969558859768976537L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private UserEntity follower;
}
