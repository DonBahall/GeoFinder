package com.example.geofinder

import com.example.geofinder.Controller.AuthConroller
import com.example.geofinder.Controller.HostController
import com.example.geofinder.Model.UserDto
import com.example.geofinder.Service.UserServiceImpl
import org.junit.Before
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.validation.BindingResult
import spock.lang.Specification
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.Mockito;

@WebMvcTest
@AutoConfigureMockMvc
class GeoFinderApplicationSpec extends Specification {

     UserServiceImpl service
     BindingResult bindingResultMock;
     AuthConroller controller
     MockMvc mockMvc;

    @Before
    def setup() {
        service = Mockito.mock(UserServiceImpl.class);
        bindingResultMock = Mockito.mock(BindingResult.class);
        controller = new HostController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    void "successful user registration should redirect to login page"() {
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

    private def "user registration with existing email should fail"() {
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

   private def "user registration with empty email should fail"() {
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
}
