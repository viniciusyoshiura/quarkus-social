package com.mycompany.social.domain.repository;

import com.mycompany.social.domain.entity.FollowerEntity;
import com.mycompany.social.domain.entity.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<FollowerEntity> {

    public boolean follows(UserEntity follower, UserEntity user){
        Map<String, Object> mapParams = Parameters.with("follower", follower)
                .and("user", user).map();
//        mapParams.put("follower", follower);
//        mapParams.put("user", user);

        PanacheQuery<FollowerEntity> query = find("follower = :follower and user = :user ", mapParams);
        Optional<FollowerEntity> result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<FollowerEntity> findByUser(Long userId){
        PanacheQuery<FollowerEntity> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        Map<String, Object> mapParams = Parameters
                .with("userId", userId)
                .and("followerId", followerId)
                .map();
//        mapParams.put("userId", userId);
//        mapParams.put("followerId", followerId);

        delete("follower.id =:followerId and user.id =: userId", mapParams);
    }

}
