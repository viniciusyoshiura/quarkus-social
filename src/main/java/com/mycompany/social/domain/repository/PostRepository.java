package com.mycompany.social.domain.repository;

import com.mycompany.social.domain.entity.PostEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository implements PanacheRepository<PostEntity> {



}
