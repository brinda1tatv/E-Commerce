package com.eCommerce.controller;

import com.eCommerce.dto.*;
import com.eCommerce.model.UserSessions;
import com.eCommerce.model.VerificationToken;
import com.eCommerce.service.AdminService;
import com.eCommerce.service.SessionRegistry;
import com.eCommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @RequestMapping("/login")
    public String login(Model model) {
        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);
        return "user/Login";
    }

    @PostMapping("/submitlogin")
    @ResponseBody
    public ResponseEntity<Map<String, String>> submitLogIn(@ModelAttribute("formData") @Valid LogInDto logInDto, BindingResult result, HttpServletRequest request) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "submitLogIn", LogInDto.class, BindingResult.class, HttpServletRequest.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {
            String isValid = adminService.submitLogIn(logInDto);

            if (isValid.equals("This mail is not registered or Your account may have blocked!")) {
                map.put("email", isValid);
                return ResponseEntity.badRequest().body(map);
            } else if (isValid.equals("Wrong Password!! If you don't remember then please click on Forgot Password.")) {
                map.put("pswd", isValid);
                return ResponseEntity.badRequest().body(map);
            } else {
                HttpSession session = request.getSession();
                SessionRegistry.addSession(session);
                session.setMaxInactiveInterval(60 * 60);
                GetRoleAndUserIdDto getRoleAndUserIdDto = userService.getRoleId(logInDto.getEmail());
                session.setAttribute("roleId", getRoleAndUserIdDto.getRoleId());
                session.setAttribute("userId", getRoleAndUserIdDto.getUserId());
                session.setAttribute("isBlocked", getRoleAndUserIdDto.isBlocked());
                session.setAttribute("isDeleted", getRoleAndUserIdDto.isDeleted());
                session.setAttribute("firstName", getRoleAndUserIdDto.getFirstName());

                map.put("roleId", String.valueOf(getRoleAndUserIdDto.getRoleId()));

                adminService.setSessionId(getRoleAndUserIdDto.getUserId(), session.getId());

                return ResponseEntity.ok().body(map);
            }
        }

    }

    @RequestMapping("/forgetpswd")
    public String forgetpswd(Model model) {
        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);
        return "user/ForgetPassword";
    }

    @PostMapping("/sendresetlink")
    @ResponseBody
    public ResponseEntity<Map<String, String>> submitForgetPassword(@ModelAttribute("email") @Valid ForgetPswdDto forgetPswdDto,
                                                                    BindingResult result, HttpServletRequest request) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "submitForgetPassword", ForgetPswdDto.class, BindingResult.class, HttpServletRequest.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {
            String isValid = userService.submitForgetPassword(forgetPswdDto, request);

            if (isValid.equals("This mail is not registered!")) {
                map.put("email", isValid);
                return ResponseEntity.badRequest().body(map);
            } else if (isValid.equals("You have exceeded maximun number of attempts. Please try again after 24 hours.")) {
                map.put("email", isValid);
                return ResponseEntity.badRequest().body(map);
            } else {
                return ResponseEntity.ok().body(map);
            }
        }

    }

    @RequestMapping("/resetpswd/{token}")
    public String resetpswd(@PathVariable("token") String token, Model model) {

        List<VerificationToken> list = userService.checkTokenOfResetLink(token);

        if (list.size() > 0) {
            List<ToShowCategoryInLandingPageDto> list1 = userService.ShowCategoriesInLandingPage();
            model.addAttribute("CatNameList", list1);
            model.addAttribute("tokenId", list.get(0).getId());
            return "user/ResetPassword";
        } else {
            return "common/ErrorForReset";
        }

    }

    @PostMapping("/submitresetpassword")
    @ResponseBody
    public ResponseEntity<Map<String, String>> submitResetPassword(@ModelAttribute("formData") @Valid ResetPasswordDto resetPasswordDto,
                                                                   BindingResult result) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "submitResetPassword", ResetPasswordDto.class, BindingResult.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {
            String isValid = userService.submitResetPassword(resetPasswordDto);

            if (isValid.equals("Access Denied!")) {
                map.put("confpswd", isValid);
                return ResponseEntity.badRequest().body(map);
            } else if (isValid.equals("Password don't match!")) {
                map.put("confpswd", isValid);
                return ResponseEntity.badRequest().body(map);
            } else {
                return ResponseEntity.ok().body(map);
            }
        }

    }

    @RequestMapping("/creatacc")
    public String createAccount(Model model) {
        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);
        return "user/CreateAccount";
    }

    @PostMapping("/generateotp")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateOtp(@ModelAttribute("formData") @Valid CreateAccountDto createAccountDto, BindingResult result, HttpServletRequest request) throws MethodArgumentNotValidException {

        System.out.println(createAccountDto);

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "generateOtp", CreateAccountDto.class, BindingResult.class, HttpServletRequest.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            String isEmailValid = adminService.generateOtp(createAccountDto.getEmail(), request);

            if (isEmailValid.equals("true")) {
                return ResponseEntity.ok().body(map);
            } else {
//                map.put("email", isEmailValid);
                map.put("otp", isEmailValid);
                return ResponseEntity.badRequest().body(map);
            }
        }

    }

    @PostMapping("/ifuserexists")
    @ResponseBody
    public boolean ifUserExists(@RequestParam("email") String email) {
        boolean userExists = adminService.ifUserExists(email);
        return userExists;
    }

    @PostMapping("/submitcreateacc")
    @ResponseBody
    public ResponseEntity<Map<String, String>> submitCreateAccount(@ModelAttribute("formData") @Valid OtpValidationDto OtpValidationDto, BindingResult result) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "submitCreateAccount", OtpValidationDto.class, BindingResult.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            String isOtpValid = adminService.submitCreateAccount(OtpValidationDto);

            if (isOtpValid.equals("Invalid OTP!!")) {
                map.put("otp", isOtpValid);
            } else {
                return ResponseEntity.ok().body(map);
            }

        }

        return ResponseEntity.badRequest().body(map);

    }

    @RequestMapping("/logout")
    public String logOut(HttpServletRequest request, Model model) {

        request.getSession(false).invalidate();

        return "redirect:/login";
    }

    @RequestMapping("/error")
    public String error(Model model) {

        return "common/ErrorForReset";
    }

    @RequestMapping("/c&c/sharedwishlist/{oldUserId}/{wishlistId}/{token}")
    public String accessSharedWishList(@PathVariable("oldUserId") int oldUserId, @PathVariable("wishlistId") int wishlistId,
                                 @PathVariable("token") String token, Model model, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");

        boolean isTokenValidated = userService.checkTokenForSharedList(token, wishlistId);

        if (isTokenValidated) {

            if (userId != null) {

                boolean validateUser = userService.validateUserForAccessingWishList(userId, wishlistId);
                userService.accessSharedWishList(oldUserId, userId, wishlistId);

                if (validateUser) {
                    return "redirect:/customer/wishlist";
                } else {
                    return "redirect:/noaccess";
                }

            } else {
                List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
                model.addAttribute("CatNameList", list);
                return "/user/SharedWishList";
            }

        } else {
            return "redirect:/noaccess";
        }

    }

    @RequestMapping("/noaccess")
    public String accessDenied(Model model) {

        return "common/AccessDenied";
    }

    @RequestMapping("/loader")
    public String loader(Model model) {

        return "common/Loader";
    }


}
