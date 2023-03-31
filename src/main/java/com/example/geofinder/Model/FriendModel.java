package com.example.geofinder.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FriendModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private Long friendsId;
    private Apply apply;


    public FriendModel(Long userId, Long friendsId, Apply apply) {
        this.userId = userId;
        this.friendsId = friendsId;
        this.apply = apply;
    }
}
