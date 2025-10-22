package in.indian.food.restro.userservice.controller;


import in.indian.food.restro.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/indianfoodrestro/users")
public class UserServiceController {

    @Autowired
    private UserService userService;

}
