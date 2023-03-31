package com.example.geofinder.Repository;

import com.example.geofinder.Model.Apply;
import com.example.geofinder.Model.FriendModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<FriendModel,String> {
    List<FriendModel> findAllByFriendsIdAndApply(Long friendId, Apply apply);
    List<FriendModel> findAllByUserIdAndApply(Long friendId, Apply apply);
   FriendModel findByUserIdAndFriendsId(Long userId,Long friendsId);
    FriendModel findByFriendsIdAndUserId(Long friendsId,Long userId);
}
