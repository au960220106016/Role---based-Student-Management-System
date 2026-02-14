package com.example.ScopeIndiaProject.Controller;

import com.example.ScopeIndiaProject.Model.ContactMail;
import com.example.ScopeIndiaProject.Model.CourseModel;
import com.example.ScopeIndiaProject.Model.RegisterModel;
import com.example.ScopeIndiaProject.Repository.*;
import com.example.ScopeIndiaProject.Service.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
// Home Page
    @GetMapping("/")
    public String LoadSite(Model model){
        return "home";
    }
    @GetMapping("/home")
    public String HomePage(Model model){
        return "home";
    }

//About Page
    @GetMapping("/about")
    public String AboutPage(Model model){
        return "about";
    }

//Register Page
    @Autowired
    FormService formService;
    @Autowired
    RegisterEmailService registerEmailService;

    @GetMapping("/register")
    public String RegisterPage(Model model){
        model.addAttribute("addStudent", new RegisterModel());
        return "register";
    }
    @PostMapping("/register")
    public String AddStudent(@ModelAttribute("addStudent") @Valid RegisterModel registerModel,
                             BindingResult result,
                             @RequestParam("photoFile") MultipartFile file,
                             @RequestParam(value = "hobbies", required = false) String[] hobbies) throws IOException, MessagingException {
        if (result.hasErrors()) {
            return "register";  // Redirect back to the form if validation fails
        }
        if (!file.isEmpty()) {
            registerModel.setPhoto(file.getBytes());
        }

        List<String> hobbiesList = (hobbies != null) ? Arrays.asList(hobbies) : Collections.emptyList();
        registerModel.setHobbies(hobbiesList);
        formService.insertData(registerModel);
        String subject = "Welcome to Scope India!";
        String message = "<h3><b>Dear " + registerModel.getFirstname() + ",</b></h3>" +
                "<p>Thank you for registering with Scope India! <br/> " +
                "We are excited to have you with us!" + "We will get back to you shortly</p>" +
                "<p><b>Best Regards,<br/>Scope India Team</b></p>";
        registerEmailService.SendRegisterEmail(registerModel.getEmail(),subject,message);
        return "redirect:/home";
    }

//Contact Page
    @Autowired
    ContactMailService contactMailService;

    @GetMapping("/contact")
    public String ContactPage(Model model){
        ContactMail contactMail=new ContactMail();
        model.addAttribute("contactForm",contactMail);
        return "contact";
    }
    @PostMapping("/receiveEmail")
    public String receiveMail(@ModelAttribute("contactForm") ContactMail contactMail) throws MessagingException {
        //To Admin...
        String admin="gopiikrisshnam@gmail.com";
        String subjectToAdmin="User Message - "+contactMail.getSubject();
        String messageToAdmin="<h3><b>Dear Admin,</b></h3>" +
                "<p> From : " +contactMail.getName()+"</p>"+
                "<p> email : " +contactMail.getFrom()+"</p>"+
                "<p> message : " +contactMail.getMessage()+"</p>";
        contactMailService.sendContactMail(admin,subjectToAdmin,messageToAdmin);
        // To User...
        String user=contactMail.getFrom();
        String subjectToUser="Response Mail!";
        String messageToUser="<h3><b>Dear " +contactMail.getName()+",</b></h3>" +
                "<p> We got your Message. Our team will respond to you shortly! </p>"+
                "<p><b>Best Regards,<br/>Scope India Team</b></p>";
        contactMailService.sendContactMail(user,subjectToUser,messageToUser);
        return "redirect:/contact";
    }

//Login Page
    @Autowired
    private GeneratePasswordRepository generatePasswordRepository;

    @GetMapping("/login")
    public String LoginPage(Model model){
        return "login";
    }
    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            @RequestParam(name = "keepLoggedIn", defaultValue = "false") boolean keepLoggedIn,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        Optional<RegisterModel> useremail=generatePasswordRepository.findByEmail(email);
        if(useremail.isPresent()){
            RegisterModel user=useremail.get();
            if(user.getPassword().equals(password)){
                HttpSession session = request.getSession();
                session.setAttribute("loggedInUserEmail", user.getEmail());
            // Set session timeout based on checkbox value
                if (keepLoggedIn) {
                    session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days in seconds
                } else {
                    session.setMaxInactiveInterval(60); // Default session timeout (30 minutes)
                }
                return "redirect:/dashboard";  // Redirect to home page
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid password. Try again.");
                return "redirect:/login";
            }
        }else{
            redirectAttributes.addFlashAttribute("errorMessage", "Email not found. Please register.");
            return "redirect:/login";
        }
    }

//Student Dashboard
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/dashboard")
    public String dashboardPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null || email.isEmpty()) {
            return "redirect:/login";  // Redirect if no valid session or cookie
        }
        Optional<RegisterModel> studentDetails = studentRepository.findByEmail(email);
        if (studentDetails.isPresent()) {
            RegisterModel studentData = studentDetails.get();
            model.addAttribute("student", studentData);
        // Convert image only if it's not null
            String base64Image = studentData.getPhoto() != null ? studentData.generateBase64Image() : "";
            model.addAttribute("base64Image", base64Image);

            Integer courseId = (studentData.getCourse() != null) ? studentData.getCourse().getId() : null;

            if (courseId != null) {
                Optional<CourseModel> course = courseRepository.findById(courseId);
                if (course.isPresent()) {
                    model.addAttribute("enrolledCourseName", course.get().getCoursename());
                } else {
                    model.addAttribute("enrolledCourseName", "Not Enrolled");
                }
            } else {
                model.addAttribute("enrolledCourseName", "Not Enrolled");
            }
        } else {
            session.invalidate();  // Clear session if user is not found
            return "redirect:/login";
        }
        return "dashboard";
    }

//Profile Edit Page
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/edit/{email}")
    public String editProfile(@PathVariable String email, Model model) {
        Optional<RegisterModel> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            model.addAttribute("addStudent", userOptional.get()); // Pass user details to the form
            return "profile-edit"; // Thymeleaf template name
        } else {
            return "redirect:/dashboard"; // If user not found, redirect
        }
    }
    @PostMapping("/user/update")
    public String updateProfile(@ModelAttribute("addStudent") RegisterModel updatedUser,
                                @RequestParam("photoFile") MultipartFile photoFile) throws IOException {
        Optional<RegisterModel> existingUserOptional = userRepository.findByEmail(updatedUser.getEmail());
        if (existingUserOptional.isPresent()) {
            RegisterModel existingUser = existingUserOptional.get();
        // Update fields
            existingUser.setFirstname(updatedUser.getFirstname());
            existingUser.setLastname(updatedUser.getLastname());
            existingUser.setGender(updatedUser.getGender());
            existingUser.setDob(updatedUser.getDob());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setCountry(updatedUser.getCountry());
            existingUser.setState(updatedUser.getState());
            existingUser.setCity(updatedUser.getCity());
        // file upload
            if (!photoFile.isEmpty()) {
                existingUser.setPhoto(photoFile.getBytes());
            }
            userRepository.save(existingUser);
            return "redirect:/dashboard"; // Redirect back to dashboard after update
        } else {
            return "redirect:/dashboard"; // If user not found, redirect
        }
    }

//Forgot Password Page
    @Autowired
    GenerateOtpService generateOtpService;
    @GetMapping("/forgot-password")
    public String ForgotPasswordPage(Model model){
        return "forgot-password";
    }
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email")String email)throws MessagingException{
        boolean userexists=generateOtpService.generateOtp(email);
        if(userexists){
            return "temp-password-login";
        }else{
            return "register";
        }
    }

//Generate Password Page
    @Autowired
    GeneratePasswordService generatePasswordService;
    @GetMapping("/generate-password")
    public String GeneratePasswordPage(Model model){
        return "generate-password";
    }
    @PostMapping("/generate-password")
    public String generatePassword(@RequestParam("email") String email) throws MessagingException{
        boolean userexists= generatePasswordService.generatePassword(email);
        if(userexists){
            return "login";
        }else{
            return "register";
        }
    }

//Reset Password
    @Autowired
    ResetPasswordService resetPasswordService;
    @GetMapping("/reset-password")
    public String ResetPasswordPage(Model model){
        return "reset-password";
    }
    @PostMapping("/reset-password")
    public String setNewPassword(@RequestParam("email") String email,
                                 @RequestParam("new_password") String newPassword,
                                 @RequestParam("confirm_password") String confirmPassword,
                                 RedirectAttributes redirectAttributes){
        if(!newPassword.equals(confirmPassword)){
            redirectAttributes.addFlashAttribute("message", "Password does not match. Try again!");
            return "redirect:/reset-password";
        }
        Boolean success=resetPasswordService.SetPassword(email,newPassword);
        if(success){
            redirectAttributes.addFlashAttribute("errorMessage", "Password reset successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Email not found! Please register.");
        }
        return "redirect:/login";
    }

//Temporary Password Page
    @Autowired
    private  GenerateOtpRepository generateOtpRepository;
    @GetMapping("/temp-password-login")
    public String TempPasswordPage(Model model){
        return "temp-password-login";
    }
    @PostMapping("/temp-login")
    public String tempLogin(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            HttpSession session,
                            RedirectAttributes redirectAttributes){
        Optional<RegisterModel> useremail=generateOtpRepository.findByEmail(email);
        if(useremail.isPresent()){
            RegisterModel user=useremail.get();
            if(user.getOtp().equals(otp)){
                return "reset-password";
            }else{
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid OTP. Try again.");
                return "redirect:/login";
            }
        }else{
            redirectAttributes.addFlashAttribute("errorMessage", "Email not found! Please register.");
            return "redirect:/login";
        }
    }

//Change Password
    @GetMapping("/changePassword")
    public String changePasswordPage(HttpSession session,Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null || email.isEmpty()) {
            return "redirect:/login";
        }
        Optional<RegisterModel> studentDetails = studentRepository.findByEmail(email);
        if (studentDetails.isPresent()) {
            model.addAttribute("studentEmail", email); // Pass email to form
            return "change-password"; // Return Change Password page
        } else {
            session.invalidate();  // Clear session if user is not found
            return "redirect:/login";  // Redirect to login
        }
    }
    @PostMapping("/changepw")
    public String ChangePassword(@RequestParam("email") String email,
                                 @RequestParam("oldpassword") String oldPw,
                                 @RequestParam("newpassword") String newPw,
                                 HttpSession session,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes){
        Optional<RegisterModel> useremail=studentRepository.findByEmail(email);
        if(useremail.isPresent()){
           RegisterModel user=useremail.get();
           if(user.getPassword().equals(oldPw)){
               user.setPassword(newPw);
               session.invalidate();  // Clear session
           // Remove the userEmail cookie
               Cookie deleteCookie = new Cookie("userEmail", "");
               deleteCookie.setMaxAge(0);
               deleteCookie.setPath("/");
               response.addCookie(deleteCookie);
               return "login";
           }else{
               redirectAttributes.addFlashAttribute("errorMessage", "Incorrect password. Try again!");
               return "redirect:/changePassword";
           }
        }else{
            redirectAttributes.addFlashAttribute("errorMessage", "Email not found! Please register.");
            return "redirect:/login";
        }
    }

//Course Page
    @GetMapping("/courses")
    public String coursesPage(HttpSession session, Model model,@ModelAttribute("JoinMessage") String joinMessage) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null || email.isEmpty()) {
            return "redirect:/login";
        }
        List<CourseModel> courses = courseRepository.findAll();
        model.addAttribute("courses", courses);
    // Fetch the registered course for the logged-in user
        Optional<RegisterModel> registeredUser = studentRepository.findByEmail(email);
        Integer registeredCourseId = null;
        if (registeredUser.isPresent() && registeredUser.get().getCourse() != null) {
            registeredCourseId = registeredUser.get().getCourse().getId();
        }
        model.addAttribute("registeredCourseId", registeredCourseId);
        return "courses";
    }
    @PostMapping("/registerCourse/{id}")
    public String registerCourse(@PathVariable("id") Integer courseid, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null || email.isEmpty()) {
            return "redirect:/login";
        }
    // Fetch the course from the database
        Optional<CourseModel> optionalCourse = courseRepository.findById(courseid);
        if (optionalCourse.isEmpty()) {
            return "redirect:/courses"; // If course not found, redirect to courses page
        }
        CourseModel course = optionalCourse.get();
        Optional<RegisterModel> existingRegistration = studentRepository.findByEmail(email);
        RegisterModel registration;
        if (existingRegistration.isPresent()) {
        // User already registered, update their course
            registration = existingRegistration.get();
        } else {
        // New user, create a new record
            registration = new RegisterModel();
            registration.setEmail(email);
        }
    // Set the course properly to maintain foreign key relation
        registration.setCourse(course);
        formService.insertData(registration);
        redirectAttributes.addFlashAttribute("JoinMessage", "You have successfully joined " + course.getCoursename());
        redirectAttributes.addFlashAttribute("registeredCourseId", course.getId());
        return "redirect:/courses";
    }

    @GetMapping("/dashboard-about")
    public String DashboardAboutPage(Model model){
        return "dashboard-about";
    }

    @GetMapping("/dashboard-contact")
    public String DashboardContactPage(Model model){
        ContactMail contactMail=new ContactMail();
        model.addAttribute("contactForm",contactMail);
        return "dashboard-contact";
    }

//Logout Page
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();  // Clear session
    // Remove the userEmail cookie
        Cookie deleteCookie = new Cookie("userEmail", "");
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);
        return "redirect:/";  // Redirect to home or login page
    }
}
