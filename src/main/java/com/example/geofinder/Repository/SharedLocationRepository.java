package com.example.geofinder.Repository;

import com.example.geofinder.Model.SharedLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedLocationRepository extends JpaRepository<SharedLocation,Long> {

    List<SharedLocation> findAllByFriendId(Long friendId);
    Optional<SharedLocation> findByFriendId(Long friendId);
}
