package com.example.geofinder.Controller;

import com.example.geofinder.Model.*;
import com.example.geofinder.Repository.LocationRepository;
import com.example.geofinder.Repository.SharedLocationRepository;
import com.example.geofinder.Service.CustomUserDetailsService;
import com.example.geofinder.Service.UserServiceImpl;

import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Controller
public class HostController {

    final UserServiceImpl service;
    final LocationRepository locationRepository;
    final CustomUserDetailsService serviceDetails;
    final SharedLocationRepository sharedLocationRepository;

    public HostController(UserServiceImpl service, LocationRepository locationRepository, CustomUserDetailsService serviceDetails, SharedLocationRepository sharedLocationRepository) {
        this.service = service;
        this.locationRepository = locationRepository;
        this.serviceDetails = serviceDetails;
        this.sharedLocationRepository = sharedLocationRepository;
    }


    @GetMapping("/lobby")
    public String mainPage(Model model, Principal principal) {
        Long userId = service.getId(principal);
        List<UserDto> friends = service.userFriends(userId);
        model.addAttribute("friends", friends);
        return "lobby";
    }

    @GetMapping("/accept")
    public String acceptInvite(Principal principal, @RequestParam Long id) {
        Long userId = service.getId(principal);
        service.acceptInvites(userId, id);
        return "redirect:/lobby";
    }

    @PostMapping("/send")
    public String sendLocation(Principal principal, @RequestParam Long friendId, @RequestParam String nameOfLoc,
                               @RequestParam String sendLocation) {

        Long userId = service.getId(principal);
        Type type = Type.READ;
        if (sendLocation.equals("admin")) {
            type = Type.ADMIN;
        }
        SharedLocation checkForAuthorities = sharedLocationRepository.findByFriendId(userId).orElse(null);
        if (checkForAuthorities == null || checkForAuthorities.getRole().equals(Type.ADMIN)) {
            Location locationCheck = locationRepository.findByName(nameOfLoc);
            if (locationCheck.getUserId().equals(userId))
                try {
                    Location location = locationRepository.findByUserIdAndName(userId, nameOfLoc);
                    SharedLocation location1 = new SharedLocation(userId, friendId, location.getId(), type);
                    sharedLocationRepository.save(location1);
                } catch (UsernameNotFoundException e) {
                    e.fillInStackTrace();
                }
        }

        return "redirect:/lobby";
    }

    @GetMapping("/myLocation")
    public String myLocation(Principal principal, Model model) {
        Long userId = service.getId(principal);
        model.addAttribute("loc", locationRepository.findAllByUserId(userId));
        List<SharedLocation> location = sharedLocationRepository.findAllByFriendId(userId);
        List<Location> shared = new ArrayList<>();
        for (SharedLocation sharedLocation : location) {
            Long id = sharedLocation.getLocationId();
            Location loc = locationRepository.findById(id).orElse(null);
            shared.add(loc);
        }
        model.addAttribute("sharedLocation", shared);
        return "myLocation";
    }

    @GetMapping("/decline")
    public String declineInvite(Principal principal, @RequestParam Long id) {
        Long userId = service.getId(principal);
        service.declineInvites(userId, id);
        return "redirect:/lobby";
    }

    @PostMapping("/delete")
    public String deleteLocation(@RequestParam Long id) {
        locationRepository.deleteById(id);
        return "redirect:/myLocation";
    }



    @GetMapping("/invites")
    public String invitesPage(Model model, Principal principal) {
        Long userId = service.getId(principal);
        List<UserDto> friendModels = service.getInvites(userId);
        model.addAttribute("invites", friendModels);
        return "invites";
    }



    @PostMapping("/newFriend")
    public String addFriend(@RequestParam String newFriend, Principal principal) {
        service.addFriend(newFriend, principal);
        return "redirect:/lobby";
    }


    @PostMapping("/address")
    public String saveLocation(@RequestParam String name,
                               @RequestParam String address1,
                               @RequestParam String address2,
                               @RequestParam String city,
                               @RequestParam String state,
                               @RequestParam String zip,
                               Principal principal) {
        Long userId = service.getId(principal);
        service.createLocation(name, address1, address2, city, state, zip, userId);
        return "redirect:/lobby";
    }
}
