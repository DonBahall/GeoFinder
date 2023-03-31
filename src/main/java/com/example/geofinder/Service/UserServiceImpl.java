package com.example.geofinder.Service;

import com.example.geofinder.Model.*;
import com.example.geofinder.Repository.FriendRepository;

import com.example.geofinder.Repository.LocationRepository;
import com.example.geofinder.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl {
    final UserRepository repository;
    @Autowired
    private  LocationRepository locationRepository;
    final FriendRepository friendRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository repository, FriendRepository friendRepository) {
        this.repository = repository;
        this.friendRepository = friendRepository;
    }

    public void saveUser(UserDto userDto) {
        UserModel user = new UserModel(userDto.getName(), passwordEncoder.encode(userDto.getPassword()), userDto.getEmail(),
                Role.USER);
        repository.save(user);
    }

    public UserModel findUserByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public List<UserDto> userFriends(Long id) {
        List<UserDto> models = new ArrayList<>();
        List<FriendModel> friendModels = friendRepository.findAllByUserIdAndApply(id, Apply.ACCEPT);
        for (FriendModel friendModel : friendModels) {
            models.add(UserMapper.INSTANCE.map(repository.findById(friendModel.getFriendsId()).orElseThrow()));
        }
        return models;
    }
    public void addFriend(String email, Principal principal){
        UserModel model = repository.findByEmail(email).orElseThrow(()
                -> new UsernameNotFoundException("Invalid email"));
        Long idFriend = model.getId();
        String emailUser = principal.getName();
        UserModel user = repository.findByEmail(emailUser).orElseThrow(()
                -> new UsernameNotFoundException("Invalid email"));
        Long userId = user.getId();
        if(friendRepository.findByUserIdAndFriendsId(userId,idFriend) == null){
            FriendModel model1 = new FriendModel(userId,idFriend,Apply.SEND);
            friendRepository.save(model1);
        }
    }
    public Long getId(Principal principal){
        String email = principal.getName();
        UserModel user = repository.findByEmail(email).orElseThrow(()
                -> new UsernameNotFoundException("Invalid email"));
        return  user.getId();
    }
    public List<UserDto> getInvites(Long id){
       return friendRepository.findAllByFriendsIdAndApply(id,Apply.SEND).stream().
                       map((map) -> UserMapper.INSTANCE.map(repository.findById(map.getUserId()).orElseThrow()))
                       .toList();
    }
    public void acceptInvites(Long id, Long usId){
        FriendModel model = friendRepository.findByFriendsIdAndUserId(id,usId);
        model.setApply(Apply.ACCEPT);
        friendRepository.save(model);
    }
    public void declineInvites(Long id, Long frId){
        FriendModel model = friendRepository.findByUserIdAndFriendsId(id,frId);
        model.setApply(Apply.DECLINE);
        friendRepository.save(model);
    }
    public void createLocation(String name,String address1,
                               String address2,String city,
                               String state,String zip,Long userId){
        Location addressEntity = new Location(name,address1,address2,city,state,zip,userId);
        locationRepository.save(addressEntity);
    }
    public boolean unique(String email){
        return !repository.existsByEmail(email);
    }
}
