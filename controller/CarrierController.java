package com.eCommerce.controller;

import com.eCommerce.dto.GetAllCarrierOrders;
import com.eCommerce.dto.GetCarrierMyProfileDto;
import com.eCommerce.service.CarrierService;
import com.eCommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("carrier")
public class CarrierController {

    @Autowired
    private CarrierService carrierService;

    @Autowired
    private UserService userService;

    @RequestMapping("/home")
    public String home() {

        return "carrier/Home";

    }

    @RequestMapping("/show-orders-to-carrier")
    @ResponseBody
    public List<GetAllCarrierOrders> showOrdersToCarrier(@RequestParam("search") String searchText, @RequestParam("curPage") int curPage, @RequestParam("filters") String filters, HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        List<GetAllCarrierOrders> list = carrierService.showCarrierOrders(searchText, curPage, filters, userId);
        return list;

    }

    @RequestMapping("/complete-order")
    @ResponseBody
    public void completeOrder(@RequestParam("carrierOrderId") int carrierOrderId) {

        carrierService.completeOrder(carrierOrderId);

    }

    @RequestMapping("can-not-deliver")
    @ResponseBody
    public ResponseEntity<String> canNotDeliver(@RequestParam("carrierOrderId") int carrierOrderId, @RequestParam("reasonText") String reasonText) {

        String isValid = carrierService.notBeDeliveredOrder(carrierOrderId, reasonText);

        if (!isValid.equals("success")) {
            return ResponseEntity.badRequest().body(isValid);
        }

        return ResponseEntity.ok().body(isValid);

    }

    @RequestMapping("/profile")
    public String myProfile(Model model, HttpSession session) {

        int userId = (int) session.getAttribute("userId");
        GetCarrierMyProfileDto userData = carrierService.getDataOfCarrier(userId);
        model.addAttribute("userData", userData);

        return "carrier/MyProfile";
    }

    @RequestMapping("/edit-carrier-profile")
    @ResponseBody
    public ResponseEntity<Map<String, String>> editCarrierProfile(@Valid @ModelAttribute GetCarrierMyProfileDto getCarrierMyProfileDto, BindingResult result, HttpSession session, HttpServletRequest request) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "editCarrierProfile", GetCarrierMyProfileDto.class, BindingResult.class, HttpSession.class, HttpServletRequest.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isValidGender = carrierService.editPersonalInfo(getCarrierMyProfileDto, userId, request);

            if (!isValidGender.equals("success")) {
                map.put("gender", isValidGender);
                return ResponseEntity.badRequest().body(map);
            }

            return ResponseEntity.ok().body(map);

        }

    }

}
