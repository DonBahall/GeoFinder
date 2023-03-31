package com.example.geofinder.Repository;

import com.example.geofinder.Model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByUserId(Long userId);
    List<Location> findAllByUserId(Long userId);
    Location findByUserIdAndName(Long userId,String name);
    boolean existsByName(String name);
    Location findByName(String name);
    Optional<Location> findById(Long id);
}
