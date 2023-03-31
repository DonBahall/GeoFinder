package com.example.geofinder.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private Long friendId;
    private Long locationId;
    private Type role;

    public SharedLocation(Long userId, Long friendId, Long locationId, Type role) {
        this.userId = userId;
        this.friendId = friendId;
        this.locationId = locationId;
        this.role = role;
    }
}
