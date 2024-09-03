package com.eCommerce.controller;

import com.eCommerce.dto.*;
import com.eCommerce.model.Notifications;
import com.eCommerce.repository.NotificationsDao;
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
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerController extends RuntimeException {

    @Autowired
    private UserService userService;

    @RequestMapping("/orders")
    public String myOrders(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/Orders";
    }

    @RequestMapping("/cart")
    public String cart(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/ShowCart";
    }

    @RequestMapping("/check-stock")
    @ResponseBody
    public int checkStock(@RequestParam("pId") int pId, @RequestParam("color") String color, HttpServletRequest request) {

        int stock = userService.checkStock(pId, color, request);
        return stock;

    }

    @RequestMapping("/add-to-cart")
    @ResponseBody
    public int addToCart(@RequestParam("pId") int pId, @RequestParam("color") String color, @RequestParam("qty") int qty, HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        int ifExists = userService.addToCart(userId, pId, color, qty);

        if (ifExists == 1) {
            return 1;
        }
        return 0;

    }

    @RequestMapping("/show-cart")
    @ResponseBody
    public List<ShowProductsInCartDto> showCart(@RequestParam("searchText") String searchText, @RequestParam("yourFilters") String yourFilters, HttpSession session, HttpServletRequest request) {

        int userId = (int) session.getAttribute("userId");

        List<ShowProductsInCartDto> list = userService.showProductsInCart(userId, searchText, yourFilters, request);
        return list;

    }

    @RequestMapping("/delete-item-from-cart")
    @ResponseBody
    public void deleteItemFromCart(@RequestParam("productId") int productId, HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        userService.deleteItemFromCart(userId, productId);

    }

    @RequestMapping("/proceed-to-buy")
    @ResponseBody
    public ResponseEntity<Map<String, String>> proceedToBuy(@Valid @ModelAttribute("formData") ProceedToBuyDto proceedToBuyDto, BindingResult result, HttpSession session, HttpServletRequest request) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "proceedToBuy", ProceedToBuyDto.class, BindingResult.class, HttpSession.class, HttpServletRequest.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        }
        else {
            int userId = (int) session.getAttribute("userId");

            String ifPossible = userService.addToTempOrders(proceedToBuyDto, userId, request);

            if (ifPossible.equals("empty")) {
                map.put(ifPossible, "Please select an item to proceed further.");
                return ResponseEntity.badRequest().body(map);
            }
            else if (ifPossible.equals("qty")) {
                map.put(ifPossible, "Please Enter a Valid Quantity to buy!");
                return ResponseEntity.badRequest().body(map);
            }
            else if (ifPossible.equals("stock")) {
                map.put(ifPossible, "The Products are Out Of Stock!!");
                return ResponseEntity.badRequest().body(map);
            }
            else if (ifPossible.equals("cost")) {
                map.put(ifPossible, "There is something wrong in price calculation!");
                return ResponseEntity.badRequest().body(map);
            }
            else if (ifPossible.startsWith("stock")) {
                map.put(ifPossible, "The product " + ifPossible.substring(5) + " is out of stock!");
                return ResponseEntity.badRequest().body(map);
            }

            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/profile")
    public String myProfile(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/MyProfile";
    }

    @RequestMapping("/wishlist")
    public String myWishList(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/ShowWishlist";
    }

    @RequestMapping("/add-new-wishlist")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addNewWishlist(@ModelAttribute("formData") @Valid AddNewWishListDto addNewWishListDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "addNewWishlist", AddNewWishListDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String ifExists = userService.addNewWishList(userId, addNewWishListDto);

            if (ifExists.equals("This Name already Exists!")) {
                map.put("wishlistName", ifExists);
                return ResponseEntity.badRequest().body(map);
            } else {
                return ResponseEntity.ok().body(map);
            }

        }

    }

    @RequestMapping("/get-all-wishlist")
    @ResponseBody
    public List<GetAllWishlistIdAndName> getAllWishlist(HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        List<GetAllWishlistIdAndName> list = userService.getAllWishList(userId);
        return list;

    }

    @RequestMapping("/add-product-to-wishlist")
    @ResponseBody
    public void addProductToWishlist(@RequestParam("pId") int pId, @RequestParam("wishlistId") int wishlistId, @RequestParam("add") int add) {

        userService.addToWishList(pId, wishlistId, add);

    }

    @RequestMapping("/get-all-wishlists-in-wishlist")
    @ResponseBody
    public List<GetAllWishLists> getAllWishlistDataForWishListPage(HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        List<GetAllWishLists> list = userService.getAllWishListDataForWishListPage(userId);
        return list;

    }

    @RequestMapping("/get-all-wishlists-in-wishlist-of-friends")
    @ResponseBody
    public List<GetAllWishListsOfFriends> getAllWishlistDataOfFriendsForWishListPage(HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        List<GetAllWishListsOfFriends> list = userService.getAllWishListDataForFriendsForWishListPage(userId);
        return list;

    }

    @RequestMapping("/show-wishlist")
    @ResponseBody
    public List<ShowProductsInCartDto> showWishlist(@RequestParam("wishlistId") int wishlistId, @RequestParam("searchText") String searchText,
                                                    @RequestParam("yourFilters") String yourFilters) {

        List<ShowProductsInCartDto> list = userService.showProductsInWishlist(wishlistId, searchText, yourFilters);
        return list;

    }

    @RequestMapping("/delete-item-from-wishlist")
    @ResponseBody
    public void deleteItemFromWishlist(@RequestParam("productId") int productId, @RequestParam("wishlistId") int wishlistId) {

        userService.deleteItemFromWishlist(productId, wishlistId);

    }

    @RequestMapping("/share-wishlist")
    @ResponseBody
    public ResponseEntity<Map<String, String>> shareWishList(@ModelAttribute("formData") @Valid ShareWishListDto shareWishListDto, BindingResult result, HttpSession session, HttpServletRequest request) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "shareWishList", ShareWishListDto.class, BindingResult.class, HttpSession.class, HttpServletRequest.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");
            userService.shareWishList(shareWishListDto, userId, request);
            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/info")
    public String myInfo(Model model, HttpSession session) {

        int userId = (int) session.getAttribute("userId");
        EditPersonalInfoDto userData = userService.getDataOfUser(userId);
        model.addAttribute("userData", userData);

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/PersonalInfo";
    }

    @RequestMapping("/edit-personal-info")
    @ResponseBody
    public ResponseEntity<Map<String, String>> editPersonalInfo(@Valid @ModelAttribute EditPersonalInfoDto editPersonalInfoDto, BindingResult result, HttpSession session, HttpServletRequest request) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "editPersonalInfo", EditPersonalInfoDto.class, BindingResult.class, HttpSession.class, HttpServletRequest.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isValidGender = userService.editPersonalInfo(editPersonalInfoDto, userId, request);

            if (!isValidGender.equals("success")) {
                map.put("gender", isValidGender);
                return ResponseEntity.badRequest().body(map);
            }

            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/address")
    public String address(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/ManageAddr";
    }

    @RequestMapping("/get-address-type")
    @ResponseBody
    public boolean getAddressType(HttpSession session) {

        int userId = (int) session.getAttribute("userId");
        return userService.getAddressType(userId);

    }

    @RequestMapping("/get-all-addresses")
    @ResponseBody
    public List<AddAddressDto> getAllAddresses(HttpSession session) {

        int userId = (int) session.getAttribute("userId");
        return userService.getAllAddresses(userId);

    }

    @RequestMapping("/add-address")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addAddress(@Valid @ModelAttribute AddAddressDto addAddressDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "addAddress", AddAddressDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isValidType = userService.addAddress(addAddressDto, userId);

            if (!isValidType.equals("success")) {
                map.put("type1", isValidType);
                return ResponseEntity.badRequest().body(map);
            }

            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/edit-address")
    @ResponseBody
    public ResponseEntity<Map<String, String>> editAddress(@Valid @ModelAttribute AddAddressDto addAddressDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "editAddress", AddAddressDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isValidType = userService.editAddress(addAddressDto, userId);

            if (!isValidType.equals("success")) {
                map.put("type", isValidType);
                return ResponseEntity.badRequest().body(map);
            }

            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/delete-address")
    @ResponseBody
    public void deleteAddress(@RequestParam("id") int id) {

        userService.deleteAddress(id);

    }

    @RequestMapping("/reviews")
    public String myReviews(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/MyReviews";
    }

    @RequestMapping("/get-all-reviews")
    @ResponseBody
    public List<ShowReviewDto> getAllReviews(HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        List<ShowReviewDto> list = userService.showReview(userId);
        return list;

    }

    @RequestMapping("/edit-review")
    @ResponseBody
    public ResponseEntity<Map<String, String>> editReview(@Valid @ModelAttribute ShowReviewDto showReviewDto, BindingResult result) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "editReview", ShowReviewDto.class, BindingResult.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            userService.editReview(showReviewDto);
            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/delete-reviews")
    @ResponseBody
    public void deleteReviews(@RequestParam("id") int id) {

        userService.deleteReview(id);

    }

    @RequestMapping("/check-for-user-review")
    @ResponseBody
    public String checkForUserReview(@RequestParam("productId") int productId, HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        String ifEligible = userService.checkForUserReview(productId, userId);
        return ifEligible;

    }

    @RequestMapping("/add-review")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addReview(@Valid @ModelAttribute AddReviewDto addReviewDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "addReview", AddReviewDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");
            userService.addReview(addReviewDto, userId);
            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/wallet")
    public String myWallet(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/MyWallet";
    }

    @RequestMapping("/show-wallet")
    @ResponseBody
    public List<ShowWalletDto> showWallet(HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        List<ShowWalletDto> showWalletDtos = userService.showWallet(userId);
        return showWalletDtos;

    }

    @RequestMapping("/show-payment-history")
    @ResponseBody
    public List<ShowPaymentHistoryDto> showPaymentHistory(@RequestParam("curPage") Optional<Integer> curPage, @RequestParam("size") Optional<Integer> size, HttpSession session) {

        int currentPage = curPage.orElse(1);
        int pageSize = size.orElse(5);

        int userId = (int) session.getAttribute("userId");

        List<ShowPaymentHistoryDto> showWalletDtos = userService.showPaymentHistory(userId, currentPage, pageSize);
        return showWalletDtos;

    }

    @RequestMapping("/add-into-wallet")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addIntoWallet(@RequestParam("amount") Double amount, HttpSession session) {

        Map<String, String> map = new HashMap<>();

        if (amount == null || amount.toString().equals("")) {
            map.put("amount", "This field must not be null");
            return ResponseEntity.badRequest().body(map);
        }

        int userId = (int) session.getAttribute("userId");

        String isValid = userService.addIntoWallet(amount, userId);

        if (isValid.equals("Please Enter a Number")) {
            map.put("amount", isValid);
            return ResponseEntity.badRequest().body(map);
        } else if (isValid.equals("Amount must be between 10 and 50,000 INR")) {
            map.put("amount", isValid);
            return ResponseEntity.badRequest().body(map);
        } else {
            return ResponseEntity.ok().body(map);
        }

    }

    @RequestMapping("/checkout")
    public String checkOut(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/CheckOut";
    }

    @RequestMapping("/get-check-out-data")
    @ResponseBody
    public List<CheckOutDto> getAllDetailsForCheckout(HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        List<CheckOutDto> dataForCheckOut = userService.getDataForCheckOut(userId);
        return dataForCheckOut;

    }

    @RequestMapping("/get-coupons")
    @ResponseBody
    public List<GetCouponsDto> getCoupons(@RequestParam("total") Double total) {

        List<GetCouponsDto> list = userService.getCouponsFromMinPrice(total);
        return list;

    }

//    @RequestMapping("/get-wallet")
//    @ResponseBody
//    public boolean getWallet(@RequestParam("total") Double total, @RequestParam("walletAmount") Double walletAmount, HttpSession session) {
//
//        int userId  = (int) session.getAttribute("userId");
//
//        boolean test = userService.checkWalletAmounts(walletAmount, total, userId);
//        return test;
//
//    }

    @RequestMapping("/get-wallet")
    @ResponseBody
    public boolean getWallet(@RequestParam("total") Double total, HttpSession session) {

        int userId  = (int) session.getAttribute("userId");

        boolean test = userService.checkWalletAmounts(total, userId);
        return test;

    }

    @RequestMapping("/get-products-for-review")
    @ResponseBody
    public List<ShowProductsInCartDto> getproductsForReview(HttpSession session) {

        int userId  = (int) session.getAttribute("userId");

        List<ShowProductsInCartDto> productsForReviewOrder = userService.getProductsForReviewOrder(userId);
        return productsForReviewOrder;

    }

    @RequestMapping("/place-order")
    @ResponseBody
    public ResponseEntity<Map<String, String>> placeOrder(@Valid @ModelAttribute PlaceOrderDto placeOrderDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "placeOrder", PlaceOrderDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");
            boolean isValid = userService.placeOrder(placeOrderDto, userId);

            if (isValid) {
                return ResponseEntity.ok().body(map);
            }
            else {
                return ResponseEntity.badRequest().body(map);
            }

        }

    }

    @RequestMapping("/download-invoice")
    @ResponseBody
    public String downloadInvoice(HttpSession session, int orderId, HttpServletRequest request) throws MalformedURLException, FileNotFoundException {

        int userId  = (int) session.getAttribute("userId");

        String path = userService.downloadInvoice(userId, orderId, request);
        return path;

    }

    @RequestMapping("/get-data-for-orders")
    @ResponseBody
    public List<GetDataForOrdersDto> getDataForOrders(@RequestParam("tabId") int tabId, HttpSession session) {

        int userId  = (int) session.getAttribute("userId");

        List<GetDataForOrdersDto> list = userService.getDataForOrders(userId, tabId);
        return list;

    }

    @RequestMapping("/cancel-order")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cancelOrder(@Valid @ModelAttribute CancelOrderDto cancelOrderDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "cancelOrder", CancelOrderDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {
            int userId  = (int) session.getAttribute("userId");

            String isValid = userService.cancelOrder(cancelOrderDto, userId);

            if (!isValid.equals("success")) {
                map.put("error", isValid);
                return ResponseEntity.badRequest().body(map);
            }
            else {
                return ResponseEntity.ok().body(map);
            }
        }

    }

    @RequestMapping("/notification")
    public String showNotifications(Model model) {

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        return "customer/Notifications";
    }

    @RequestMapping("/if-notifications-on")
    @ResponseBody
    public boolean ifNotificationsOn(HttpSession session) {

        int userId  = (int) session.getAttribute("userId");
        return userService.isNotificationsOn(userId);

    }

    @RequestMapping("/get-notifications")
    @ResponseBody
    public List<GetNotificationsDto> getNotifications(@RequestParam("isSeen") boolean isSeen, HttpSession session) {

        int userId  = (int) session.getAttribute("userId");
        List<GetNotificationsDto> list = userService.getNotifications(userId, isSeen);
        return list;

    }

    @RequestMapping("/get-notifications-count")
    @ResponseBody
    public int getNotificationsCount(HttpSession session) {

        int userId  = (int) session.getAttribute("userId");
        int countForNotifications = userService.getCountForNotifications(userId);
        return countForNotifications;

    }

    @RequestMapping("/get-cart-count")
    @ResponseBody
    public int getCartCount(HttpSession session) {

        int userId  = (int) session.getAttribute("userId");
        int totalItemsInCart = userService.noOfItemsInCart(userId);
        return totalItemsInCart;

    }

    @RequestMapping("/add-notification-to-seen")
    @ResponseBody
    public String addNotificationsToSeen(@RequestParam("selectedIds") List<Integer> selectedIds, HttpSession session) {

        int userId  = (int) session.getAttribute("userId");
        String isValid = userService.addToSeenNotifications(selectedIds, userId);
        return isValid;

    }

    @RequestMapping("/stop-notifications")
    @ResponseBody
    public void stopNotifications(HttpSession session) {

        int userId  = (int) session.getAttribute("userId");
        userService.stopNotifications(userId);

    }

    @RequestMapping("/on-notifications")
    @ResponseBody
    public void onNotifications(HttpSession session) {

        int userId  = (int) session.getAttribute("userId");
        userService.onNotifications(userId);

    }

}
