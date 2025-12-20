@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAccountRepository userRepo;

    public AuthController(UserAccountRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping("/register")
    public UserAccount register(@RequestBody UserAccount user) {
        return userRepo.save(user);
    }

    @PostMapping("/login")
    public String login() {
        return "JWT_TOKEN"; // dummy
    }
}
