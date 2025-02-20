package com.mycompany.jobhunter.config;

import com.mycompany.jobhunter.domain.entity.*;
import com.mycompany.jobhunter.domain.entity.enumeration.GenderEnum;
import com.mycompany.jobhunter.domain.entity.enumeration.LevelEnum;
import com.mycompany.jobhunter.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final SubscriberRepository subscriberRepository;
    private final ResumeRepository resumeRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            JobRepository jobRepository,
            SkillRepository skillRepository,
            CompanyRepository companyRepository,
            SubscriberRepository subscriberRepository,
            ResumeRepository resumeRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
        this.resumeRepository = resumeRepository;
        this.subscriberRepository = subscriberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception{
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();
        long countCompanies = companyRepository.count();
        long countSkills = skillRepository.count();
        long countResumes = resumeRepository.count();
        long countSubscribers = subscriberRepository.count();
        long countJobs = jobRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a company", "/api/v1/companies", "POST", "COMPANIES"));
            arr.add(new Permission("Update a company", "/api/v1/companies", "PUT", "COMPANIES"));
            arr.add(new Permission("Delete a company", "/api/v1/companies/{id}", "DELETE", "COMPANIES"));
            arr.add(new Permission("Get a company by id", "/api/v1/companies/{id}", "GET", "COMPANIES"));
            arr.add(new Permission("Get companies with pagination", "/api/v1/companies", "GET", "COMPANIES"));

            arr.add(new Permission("Create a job", "/api/v1/jobs", "POST", "JOBS"));
            arr.add(new Permission("Update a job", "/api/v1/jobs", "PUT", "JOBS"));
            arr.add(new Permission("Delete a job", "/api/v1/jobs/{id}", "DELETE", "JOBS"));
            arr.add(new Permission("Get a job by id", "/api/v1/jobs/{id}", "GET", "JOBS"));
            arr.add(new Permission("Get jobs with pagination", "/api/v1/jobs", "GET", "JOBS"));

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a resume", "/api/v1/resumes", "POST", "RESUMES"));
            arr.add(new Permission("Update a resume", "/api/v1/resumes", "PUT", "RESUMES"));
            arr.add(new Permission("Delete a resume", "/api/v1/resumes/{id}", "DELETE", "RESUMES"));
            arr.add(new Permission("Get a resume by id", "/api/v1/resumes/{id}", "GET", "RESUMES"));
            arr.add(new Permission("Get resumes with pagination", "/api/v1/resumes", "GET", "RESUMES"));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

            arr.add(new Permission("Create a subscriber", "/api/v1/subscribers", "POST", "SUBSCRIBERS"));
            arr.add(new Permission("Update a subscriber", "/api/v1/subscribers", "PUT", "SUBSCRIBERS"));
            arr.add(new Permission("Delete a subscriber", "/api/v1/subscribers/{id}", "DELETE", "SUBSCRIBERS"));
            arr.add(new Permission("Get a subscriber by id", "/api/v1/subscribers/{id}", "GET", "SUBSCRIBERS"));
            arr.add(new Permission("Get subscribers with pagination", "/api/v1/subscribers", "GET", "SUBSCRIBERS"));

            arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));

            arr.add(new Permission("Send mail", "/api/v1/email", "GET", "EMAILS"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin has full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setAge(25);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        Company c = new Company();
        if(countCompanies == 0) {
            c.setName("FPT");
            c.setDescription("FPT software");

            companyRepository.save(c);
        }


        Skill s1 = new Skill();
        Skill s2 = new Skill();
        Skill s3 = new Skill();
        Skill s4 = new Skill();
        if( countSkills == 0) {
            s1.setName("Java spring boot");
            s2.setName("NodeJs");
            s3.setName("Django");
            s4.setName("Lua");

            skillRepository.save(s1);
            skillRepository.save(s2);
            skillRepository.save(s3);
            skillRepository.save(s4);
        }



        Resume r1 = new Resume();
        Resume r2 = new Resume();
        if(countResumes == 0) {
            r1.setEmail("cv1@gmail.com");
            r1.setUrl("http://www.google.com");
            r2.setEmail("cv2@gmail.com");
            r2.setUrl("http://www.google.com");

            resumeRepository.save(r1);
            resumeRepository.save(r2);
        }

        Job j1 = new Job();
        Job j2 = new Job();
        if(countJobs == 0) {
            j1.setName("Java Junior");
            j1.setLocation("Ho Chi Minh");
            j1.setSalary(3000);
            j1.setQuantity(3);
            j1.setLevel(LevelEnum.JUNIOR);
            j1.setDescription("Java Junior with 3-yoe");
            j1.setActive(true);
            j1.setCompany(c);
            j1.setSkills(Arrays.asList(s1));
            j1.setResumes(Arrays.asList(r1, r2));

            j2.setName("Nodejs Junior");
            j2.setLocation("Ho Chi Minh");
            j2.setSalary(3000);
            j2.setQuantity(3);
            j2.setLevel(LevelEnum.JUNIOR);
            j2.setDescription("Nodejs Junior with 3-yoe");
            j2.setActive(true);
            j2.setCompany(c);
            j2.setSkills(Arrays.asList(s2));
            j2.setResumes(Arrays.asList(r1, r2));

            jobRepository.save(j1);
            jobRepository.save(j2);

        }


        Subscriber newSubscriber1 = new Subscriber();
        Subscriber newSubscriber2 = new Subscriber();
        if(countSubscribers == 0) {
            newSubscriber1.setEmail("kio217a@gmail.com");
            newSubscriber1.setName("Anh Khoa");
            newSubscriber1.setSkills(Arrays.asList(s1, s2));

            newSubscriber2.setEmail("pdanhkhoa21@gmail.com");
            newSubscriber2.setName("Anh Khoaa");
            newSubscriber2.setSkills(Arrays.asList(s2, s3, s4));

            subscriberRepository.save(newSubscriber1);
            subscriberRepository.save(newSubscriber2);
        }

        if (countPermissions > 0 &&
                countRoles > 0 &&
                countUsers > 0 &&
                countCompanies > 0 &&
                countSkills > 0 &&
                countResumes > 0 &&
                countJobs > 0
        ) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }
}
