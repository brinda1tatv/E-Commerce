package com.eCommerce.controller;

import com.eCommerce.dto.*;
import com.eCommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

//    Logger log = LogManager.getLogger(UserController.class);

    @RequestMapping("/contactus")
    public String contactUs(Model model) {
        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);
        model.addAttribute("contactUsDto", new ContactUsDto());

        return "user/ContactUs";
    }

    @PostMapping("/submit-contact-us")
    public ResponseEntity<Map<String, String>> submitContactUs(@Valid @ModelAttribute("contactUsDto") ContactUsDto contactUsDto, BindingResult result) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "submitContactUs", ContactUsDto.class, BindingResult.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        }
        else {
            userService.contactUs(contactUsDto);
            return ResponseEntity.ok().body(map);
        }

    }

    @RequestMapping("/clutchandcarry")
    public String landingPage(Model model) {
        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "user/Landing";
    }

    @RequestMapping("/get-must-haves")
    @ResponseBody
    public List<MustHavesDto> getMustHaves() {

        List<MustHavesDto> mustHaves = userService.getMustHaves();
        return mustHaves;

    }

    @RequestMapping("/help")
    public String help(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "user/Help";
    }

    @RequestMapping("/products/{search}")
    public String products(@PathVariable("search") String search, Model model) {

        model.addAttribute("search", search);

        if (search.startsWith("cate10")) {
            String catName = search.substring(6);
            model.addAttribute("searchTxt", catName);
        } else {
            model.addAttribute("searchTxt", search);
        }

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        List<String> brandsName = userService.getBrandsNameForFilter();
        model.addAttribute("brandsName", brandsName);

        List<GetSellerNameDto> sellerNameForFilters = userService.getSellerNameForFilters();
        model.addAttribute("sellerName", sellerNameForFilters);

        ShowMaxAndMinPrice showMaxAndMinPrice = userService.getMaxAndMinPrice();
        model.addAttribute("showMaxAndMinPrice", showMaxAndMinPrice);

        return "user/Products";

    }

    @RequestMapping("/showproducts-in-cards")
    @ResponseBody
    public List<ShowProductInCardsDto> showProductsInCards(@RequestParam("searchTxt") String search, @RequestParam("curPage") int curPage,
                                                           @RequestParam("filters") List<String> filters, @RequestParam("isSorting") String isSorting, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");

        List<ShowProductInCardsDto> listOfProductInCards = userService.ShowProductsInCards(search, curPage, filters, isSorting, userId);

        return listOfProductInCards;

    }

    @RequestMapping("/productdetails/{productId}")
    public String productDetails(@PathVariable("productId") int productId, Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        ProductDetailsDto productDetailsList = userService.showProductDetails(productId);
        model.addAttribute("product", productDetailsList);

        model.addAttribute("pId", productId);

        return "user/ProductDetails";
    }


}
