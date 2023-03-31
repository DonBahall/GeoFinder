package com.example.geofinder

import com.example.geofinder.Controller.AuthConroller
import com.example.geofinder.Model.Location
import com.example.geofinder.Model.SharedLocation
import com.example.geofinder.Model.Type
import com.example.geofinder.Model.UserDto
import com.example.geofinder.Repository.LocationRepository
import com.example.geofinder.Repository.SharedLocationRepository
import com.example.geofinder.Service.UserServiceImpl
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.validation.BindingResult
import spock.lang.Specification
import static org.mockito.Mockito.when;

import java.security.Principal

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class GeoFinderApplicationSpec extends Specification {

    @Autowired
    private MockMvc mockMvc

    @MockBean
    private UserServiceImpl service

    @MockBean
    private AuthConroller controller

    @MockBean
    private BindingResult bindingResult

    @MockBean
    private SharedLocationRepository sharedLocationRepository

    @MockBean
    private LocationRepository locationRepository

    def  "successful user registration should redirect to login page"() {
        given:
        def userDto = new UserDto(email: "testuser@example.com", password: "testpassword")
        def bindingResult = Mock(BindingResult)

        when:
        def result = controller.addUser(userDto, bindingResult)

        then:
        assert result == "redirect:/login"
        1 * service.unique("testuser@example.com") >> true
        1 * service.saveUser(userDto)
    }

     def "user registration with existing email should fail"() {
        given:
        def userDto = new UserDto(email: "existinguser@example.com", password: "testpassword")
        def bindingResult = Mock(BindingResult)
        1 * service.unique("existinguser@example.com") >> false

        when:
        def result = controller.addUser(userDto, bindingResult)

        then:
        assert result == null
        1 * bindingResult.rejectValue("email", "300", "User already registered !!!")
        0 * service.saveUser(userDto)
    }

    def "user registration with empty email should fail"() {
        given:
        def userDto = new UserDto(email: "", password: "testpassword")
        def bindingResult = Mock(BindingResult)

        when:
        def result = controller.addUser(userDto, bindingResult)

        then:
        assert result == null
        1 * bindingResult.rejectValue("email", "300", "Email is not initialization !!!")
        0 * service.saveUser(userDto)
    }
    def "should add a shared location for a user"() {
        given:
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "testUser"
            }
        }
        Long friendId = 1L
        String nameOfLoc = "testLocation"
        String sendLocation = "admin"
        Long userId = 2L
        Location location = new Location(userId: userId, name: nameOfLoc)
        SharedLocation sharedLocation = new SharedLocation(userId: userId, friendId: friendId, locationId: location.id, role: Type.ADMIN)

        service.getId(principal) >> userId
        locationRepository.findByName(nameOfLoc) >> location
        sharedLocationRepository.findByFriendId(userId) >> null
        locationRepository.findByUserIdAndName(userId, nameOfLoc) >> location
        sharedLocationRepository.save(_) >> sharedLocation

        when:
        def result = mockMvc.perform(post("/send")
                .param("friendId", friendId.toString())
                .param("nameOfLoc", nameOfLoc)
                .param("sendLocation", sendLocation)
                .principal(principal))

        then:
        result.andExpect(status().is3xxRedirection())
        result.andExpect(redirectedUrl("/lobby"))
    }
    def "should return myLocation template with locations and shared locations"() {
        given:
        def userId = 1L
        def locations = [new Location(userId: userId, name: "loc1"), new Location(userId: userId, name: "loc2")]
        when(locationRepository.findAllByUserId(userId)).thenReturn(locations)

        def sharedLocations = [
                new SharedLocation(id: 1L, friendId: userId, locationId: 2L, role: Type.ADMIN),
                new SharedLocation(id: 2L, friendId: userId, locationId: 3L, role: Type.READ)
        ]
        when(sharedLocationRepository.findAllByFriendId(userId)).thenReturn(sharedLocations)

        expect:
        mockMvc.perform(get("/myLocation"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("loc", locations))
                .andExpect(model().attribute("sharedLocation", [
                        locationRepository.findById(2L).orElse(null),
                        locationRepository.findById(3L).orElse(null)
                ]))
                .andExpect(view().name("myLocation"))
    }
}
