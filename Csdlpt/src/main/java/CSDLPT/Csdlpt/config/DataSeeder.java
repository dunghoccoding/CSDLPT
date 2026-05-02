package CSDLPT.Csdlpt.config;

import CSDLPT.Csdlpt.Entity.UserDocument;
import CSDLPT.Csdlpt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Chỉ tạo mẫu nếu database chưa có user nào
        if (userRepository.count() == 0) {
            System.out.println("🌱 Seeding default users into MongoDB...");
            
            UserDocument admin = new UserDocument("admin", passwordEncoder.encode("123456"), "ROLE_ADMIN");
            UserDocument userMb = new UserDocument("user_mb", passwordEncoder.encode("123456"), "ROLE_USER_MB");
            UserDocument userMn = new UserDocument("user_mn", passwordEncoder.encode("123456"), "ROLE_USER_MN");

            userRepository.saveAll(Arrays.asList(admin, userMb, userMn));
            
            System.out.println("✅ Default users created: admin, user_mb, user_mn (password: 123456)");
        }
    }
}
