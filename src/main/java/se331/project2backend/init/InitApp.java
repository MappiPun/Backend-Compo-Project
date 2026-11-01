package se331.project2backend.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se331.project2backend.entity.*;
import se331.project2backend.repository.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mock data: inserted when the backend starts and the database is still empty
 * (so the data is not duplicated when the backend restarts with ddl-auto: update).
 *
 * Accounts:  admin / adminpassword   and   user1 ... user36 / password
 */
@Component
@RequiredArgsConstructor
public class InitApp implements ApplicationListener<ApplicationReadyEvent> {

    private final LevelRepository levelRepository;
    private final QuestionRepository questionRepository;
    private final ELearningRepository eLearningRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String[] FIRST_NAMES = {
            "Somchai", "Maneenat", "Ananda", "Kanya", "Niran", "Ploy", "Papon", "Rapheephat", "Sukritpong", "Pimchanok",
            "Thanawat", "Chanida", "Wichai", "Suda", "Kittisak", "Nattaya", "Apinya", "Prasert", "Jirapat", "Orawan",
            "Anan", "Duangjai", "Chaiwat", "Siriporn", "Boonmee", "Napat", "Patcharee", "Teerapong", "Waraporn", "Sakda",
            "Mali", "Narong", "Kamolwan", "Phakin", "Yaowalak", "Thitiporn"};
    private static final String[] SURNAMES = {
            "Jaidee", "Srisuk", "Wong", "Ratanaporn", "Prakarn", "Chareon", "Punyanitya", "Aeiphingchai", "Wongpanya", "Boonmee",
            "Saetang", "Kaewsai", "Thongdee", "Rungruang", "Phromma", "Sukjai", "Chaiyasit", "Nakorn", "Intharat", "Lertwit",
            "Mekha", "Suwan", "Pongsri", "Wattana", "Singh", "Khamfu", "Dechawat", "Yodsuk", "Pradit", "Manee",
            "Ruengsri", "Thammarat", "Sawat", "Noppakun", "Piyasak", "Charoen"};
    private static final String[] PROVINCES = {
            "Chiang Mai", "Bangkok", "Phuket", "Hat Yai", "Khon Kaen", "Chiang Rai", "Nakhon Ratchasima",
            "Udon Thani", "Surat Thani", "Lampang"};

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (levelRepository.count() > 0 || userRepository.count() > 0) {
            return; // the data is already in the database
        }
        List<Level> levels = createLevels();
        createQuestions(levels);
        createELearning(levels);
        createUsers(levels);
    }

    // ------------------------------------------------------------------ levels, benefits, services
    private List<Level> createLevels() {
        Level l1 = level(1, "Beginner Passport", "Welcome to CAMT Passport. Start learning and unlock more services.", 3);
        l1.addBenefit(benefit("Access to Basic eLearning Courses"));
        l1.addBenefit(benefit("Level 1 Digital Badge"));
        l1.addService(service("Basic Library Access", "L1-LIB", "Borrow up to 2 books at a time"));

        Level l2 = level(2, "Intermediate Passport", "You know the basics. Time to build real skills.", 3);
        l2.addBenefit(benefit("Access to Intermediate eLearning Content"));
        l2.addBenefit(benefit("Level 2 Digital Badge"));
        l2.addBenefit(benefit("Priority Workshop Registration"));
        l2.addService(service("Standard Library Access", "L2-LIB", "Borrow up to 5 books at a time"));
        l2.addService(service("Co-Working Space Access", "L2-WORK", "Access to CAMT Study Hub"));

        Level l3 = level(3, "Advanced Passport", "Advanced learners get mentors and premium resources.", 4);
        l3.addBenefit(benefit("Access to Advanced eLearning & Masterclasses"));
        l3.addBenefit(benefit("Level 3 Digital Badge"));
        l3.addBenefit(benefit("1-on-1 Mentorship Sessions"));
        l3.addService(service("Premium Library Access", "L3-LIB", "Borrow up to 10 books & access research databases"));
        l3.addService(service("Lab Equipment Reservation", "L3-LAB", "Reserve high-performance PCs and VR headsets"));

        Level l4 = level(4, "Expert Passport", "The highest level of CAMT Passport.", 4);
        l4.addBenefit(benefit("Full Unrestricted Access to All Resources"));
        l4.addBenefit(benefit("Level 4 Elite Badge"));
        l4.addBenefit(benefit("Industry Certification Sponsorship"));
        l4.addService(service("VIP Lounge & Incubator Access", "L4-VIP", "24/7 access to CAMT Innovation Lounge"));
        l4.addService(service("Innovation Fund Application", "L4-FUND", "Apply for the CAMT startup seed fund"));

        return levelRepository.saveAll(List.of(l1, l2, l3, l4));
    }

    private Level level(int number, String title, String description, int passScore) {
        return Level.builder().levelNumber(number).title(title).description(description).passScore(passScore).build();
    }

    private Benefit benefit(String description) {
        return Benefit.builder().description(description).build();
    }

    private ServiceItem service(String name, String badge, String description) {
        return ServiceItem.builder().name(name).badge(badge).description(description).build();
    }

    // ------------------------------------------------------------------ questions (level 1-3 have an exam)
    private void createQuestions(List<Level> levels) {
        Level l1 = levels.get(0), l2 = levels.get(1), l3 = levels.get(2);
        List<Question> qs = new ArrayList<>();
        qs.add(q(l1, "What is the primary core value of CAMT Passport?", 0, "Continuous Learning", "Gaming Only", "Social Networking", "Food Service"));
        qs.add(q(l1, "How many main service levels exist in CAMT Passport?", 2, "2 Levels", "3 Levels", "4 Levels", "5 Levels"));
        qs.add(q(l1, "What do you need to do to upgrade to the next level?", 1, "Pay a fee", "Pass the exam of your level", "Wait one month", "Invite a friend"));
        qs.add(q(l1, "Which level does every new user start at?", 0, "Level 1", "Level 2", "Level 3", "Level 4"));
        qs.add(q(l1, "What does CAMT stand for?", 3, "College of Art, Music and Theatre", "Center of Applied Math and Tech", "College of Agriculture, Marine and Tourism", "College of Arts, Media and Technology"));

        qs.add(q(l2, "In Vue 3, which Composition API function is used to create reactive state for objects?", 1, "ref()", "reactive()", "computed()", "watch()"));
        qs.add(q(l2, "Which state management library is recommended for Vue 3?", 2, "Vuex 3", "Redux", "Pinia", "Context API"));
        qs.add(q(l2, "Which HTTP method is normally used to create a new resource in a REST API?", 1, "GET", "POST", "DELETE", "HEAD"));
        qs.add(q(l2, "Which Vue directive is used to render a list?", 0, "v-for", "v-if", "v-show", "v-once"));
        qs.add(q(l2, "What does the HTTP status code 404 mean?", 2, "Server error", "Unauthorized", "Not found", "Created"));

        qs.add(q(l3, "What is the main advantage of CI/CD pipelines in deployment?", 0, "Automated testing & integration", "Slower releases", "Manual FTP upload", "Harder debugging"));
        qs.add(q(l3, "Which tool is commonly used for containerizing applications?", 0, "Docker", "Photoshop", "Nginx", "Vite"));
        qs.add(q(l3, "Which annotation marks a class as a JPA entity?", 2, "@Component", "@Service", "@Entity", "@Bean"));
        qs.add(q(l3, "Which kind of token does our backend return after a successful login?", 1, "Session cookie", "JWT", "CSRF token", "API key"));
        qs.add(q(l3, "In Spring Data JPA, what does findByNameContaining do?", 3, "Deletes by name", "Exact match", "Sorts by name", "Searches names that contain the text"));
        qs.add(q(l3, "What does Pageable provide to a repository query?", 0, "Page number, page size and sorting", "Authentication", "Caching", "Validation"));
        questionRepository.saveAll(qs);
    }

    private Question q(Level level, String text, int correct, String... options) {
        return Question.builder().level(level).questionText(text).correctAnswer(correct)
                .options(new ArrayList<>(List.of(options))).build();
    }

    // ------------------------------------------------------------------ eLearning videos
    private void createELearning(List<Level> levels) {
        List<ELearningContent> list = new ArrayList<>();
        list.add(video(levels.get(0), "Introduction to Core Foundations", "บทเรียนพื้นฐานสำหรับผู้เริ่มต้น Level 1", "https://www.youtube.com/embed/dQw4w9WgXcQ"));
        list.add(video(levels.get(0), "How the CAMT Passport Works", "ระบบ Level, Exam และสิทธิประโยชน์", "https://www.youtube.com/embed/LLjfal8jCYI"));
        list.add(video(levels.get(0), "Study Skills for Beginners", "เทคนิคการเรียนรู้ด้วยตนเอง", "https://www.youtube.com/embed/C-CYwNz3z8w"));
        list.add(video(levels.get(1), "Vue 3 Fundamentals", "เริ่มต้นกับ Vue 3 และ Composition API", "https://www.youtube.com/embed/Soy4jGPHr3g"));
        list.add(video(levels.get(1), "REST API Basics", "พื้นฐาน REST API และ HTTP", "https://www.youtube.com/embed/-MTSQjw5DrM"));
        list.add(video(levels.get(2), "Docker in 100 Seconds", "ทำความรู้จัก Docker และ Container", "https://www.youtube.com/embed/Gjnup-PuquQ"));
        list.add(video(levels.get(2), "CI/CD Explained", "Pipeline สำหรับ Build และ Deploy อัตโนมัติ", "https://www.youtube.com/embed/scEDHsr3APg"));
        list.add(video(levels.get(3), "Advanced System Design", "ออกแบบระบบขนาดใหญ่", "https://www.youtube.com/embed/MbjObHmDbZo"));
        eLearningRepository.saveAll(list);
    }

    private ELearningContent video(Level level, String title, String description, String url) {
        return ELearningContent.builder().level(level).title(title).description(description).videoUrl(url).build();
    }

    // ------------------------------------------------------------------ users + exam history
    private void createUsers(List<Level> levels) {
        String adminPassword = passwordEncoder.encode("adminpassword");
        String userPassword = passwordEncoder.encode("password"); // BCrypt once, the same hash for every mock user

        userRepository.save(AppUser.builder()
                .username("admin").password(adminPassword)
                .name("System").surname("Administrator").email("admin@example.com").phone("0614837295")
                .province("Chiang Mai").role(Role.ADMIN).level(4).active(true)
                .profileImage("https://api.dicebear.com/7.x/bottts/svg?seed=Admin")
                .build());

        Random random = new Random(331); // fixed seed: the same mock data every time
        List<Level> examLevels = levels.subList(0, 3);
        List<AppUser> users = new ArrayList<>();
        for (int i = 1; i <= 36; i++) {
            String first = FIRST_NAMES[i - 1];
            int userLevel = switch (i % 6) {
                case 0 -> 4;
                case 1, 5 -> 1;
                case 2 -> 2;
                case 3 -> 3;
                default -> 1 + random.nextInt(4);
            };
            AppUser user = AppUser.builder()
                    .username("user" + i).password(userPassword)
                    .name(first).surname(SURNAMES[i - 1])
                    .email("user" + i + "@example.com")
                    .phone("08" + String.format("%08d", 10000000 + random.nextInt(89999999)))
                    .province(PROVINCES[(i * 7) % PROVINCES.length])
                    .role(Role.USER).level(userLevel)
                    .active(i % 12 != 0) // user12, user24 and user36 are deactivated
                    .profileImage("https://api.dicebear.com/7.x/bottts/svg?seed=" + first)
                    .build();

            // exam history: one failed attempt (sometimes) and the passed exam for every level below the user's level
            Instant when = Instant.now().minus(60, ChronoUnit.DAYS);
            for (int lv = 1; lv < userLevel; lv++) {
                Level examLevel = examLevels.get(lv - 1);
                int total = questionCountOf(lv);
                int pass = examLevel.getPassScore();
                if (random.nextInt(3) == 0) {
                    when = when.plus(1 + random.nextInt(4), ChronoUnit.DAYS).plus(random.nextInt(600), ChronoUnit.MINUTES);
                    user.getExamHistories().add(history(user, lv, random.nextInt(pass), total, false, null, when));
                }
                when = when.plus(1 + random.nextInt(6), ChronoUnit.DAYS).plus(random.nextInt(600), ChronoUnit.MINUTES);
                int score = pass + random.nextInt(total - pass + 1);
                user.getExamHistories().add(history(user, lv, score, total, true, lv + 1, when));
            }
            // some users at level 1-3 failed their current exam
            if (userLevel <= 3 && i % 4 == 0) {
                when = Instant.now().minus(1 + random.nextInt(10), ChronoUnit.DAYS);
                int pass = examLevels.get(userLevel - 1).getPassScore();
                user.getExamHistories().add(history(user, userLevel, Math.max(0, pass - 1 - random.nextInt(2)),
                        questionCountOf(userLevel), false, null, when));
            }
            users.add(user);
        }
        userRepository.saveAll(users); // the exam histories are saved by the cascade
    }

    private int questionCountOf(int level) {
        return (int) questionRepository.countByLevel_LevelNumber(level);
    }

    private ExamHistory history(AppUser user, int level, int score, int total, boolean passed, Integer awarded, Instant at) {
        return ExamHistory.builder().user(user).level(level).score(score).totalQuestions(total)
                .passed(passed).awardedLevel(awarded).attemptedAt(at).build();
    }
}
