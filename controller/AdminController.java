package com.eCommerce.controller;

import com.eCommerce.dto.*;
import com.eCommerce.model.Role;
import com.eCommerce.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @RequestMapping("/home")
    public String home() {

        return "admin/Home";
    }

    @RequestMapping("/category")
    @InventoryAdminTask
    public String Category(Model model) {
        model.addAttribute("catId", 1);
        model.addAttribute("toAddCategoryDto", new ToAddCategoryDto());
        return "admin/Category";
    }

    @PostMapping("/ajax-to-show-category")
    @ResponseBody
    @InventoryAdminTask
    public List<ToShowCategoryDto> ajaxToShowCategory(@RequestParam("search") String search, @RequestParam("curPage") Optional<Integer> curPage, @RequestParam("size") Optional<Integer> size) {

        int currentPage = curPage.orElse(1);
        int pageSize = size.orElse(5);

        List<ToShowCategoryDto> list = adminService.showCategory(search, currentPage, pageSize);

        return list;
    }

    @PostMapping("/submit-add-category")
    @ResponseBody
    @InventoryAdminTask
    public HashMap<String, String> submitAddCategory(@Valid @ModelAttribute("toAddCategoryDto") ToAddCategoryDto toAddCategoryDto, BindingResult result, HttpSession session) throws JsonProcessingException {

        if (result.hasErrors()) {

            HashMap<String, String> map = new HashMap<>();

            for (ObjectError error : result.getAllErrors()) {
                String field = ((FieldError) error).getField();
                String errorMsg = error.getDefaultMessage();
                map.put(field, errorMsg);
            }

            return map;

        } else {

            int userId = (int) session.getAttribute("userId");

            String ifExists = adminService.AddCategory(toAddCategoryDto, userId);

            HashMap<String, String> map = new HashMap<>();

            if (ifExists.equals("catName")) {
                map.put(ifExists, "The name already exists!");
                return map;
            } else if (ifExists.startsWith("NotNumber")) {
                map.put(ifExists.substring(9), "Please Enter Characters only.");
                return map;
            } else if (ifExists.equals("success")) {
                return null;
            } else if (ifExists.startsWith("Null")) {
                map.put(ifExists.substring(4), "Please Enter A Sub Category Name");
                return map;
            } else if (ifExists.startsWith("SubCategory")) {
                map.put(ifExists, "The sub category name already exists!");
                return map;
            } else if (ifExists.startsWith("Description")) {
                map.put(ifExists, "Please Enter A Description");
                return map;
            }

        }

        return null;

    }

    @PostMapping("/edit-category")
    @ResponseBody
    @InventoryAdminTask
    public HashMap<String, String> editCategory(@Valid @ModelAttribute EditCategoryDto editCategoryDto, BindingResult result, HttpSession session) {

        if (result.hasErrors()) {

            HashMap<String, String> map = new HashMap<>();

            for (ObjectError error : result.getAllErrors()) {
                String field = ((FieldError) error).getField();
                String errorMsg = error.getDefaultMessage();
                map.put(field, errorMsg);
            }

            return map;

        } else {
            int userId = (int) session.getAttribute("userId");

            boolean ifExists = adminService.editCategory(editCategoryDto, userId);

            if (!ifExists) {
                HashMap<String, String> map = new HashMap<>();
                String name = "name";
                map.put(name, "The name already exists!");

                return map;
            }

        }

        return null;
    }

    @PostMapping("/deletecategory")
    @ResponseBody
    @InventoryAdminTask
    public void deleteCategory(@RequestParam("categoryId") int categoryId) {

        adminService.deleteCategory(categoryId);

    }

    @RequestMapping("/subcategory/{catId}")
    @InventoryAdminTask
    public String subCategory(Model model, @PathVariable("catId") int categryId) {

        model.addAttribute("categryId", categryId);
        return "admin/SubCategory";

    }

    @PostMapping("/subcategory/ajax-to-show-subcategory")
    @ResponseBody
    @InventoryAdminTask
    public List<ToShowSubCategoryDto> ajaxToShowSubCategory(@RequestParam("catId") int catId, @RequestParam("search") String search, @RequestParam("curPage") Optional<Integer> curPage, @RequestParam("size") Optional<Integer> size) {

        int currentPage = curPage.orElse(1);
        int pageSize = size.orElse(5);

        List<ToShowSubCategoryDto> list = adminService.showAllSubCategory(catId, search, currentPage, pageSize);

        return list;
    }

    @PostMapping("/subcategory/submit-add-subcategory")
    @ResponseBody
    @InventoryAdminTask
    public HashMap<String, String> submitAddSubCategory(@Valid @ModelAttribute ToAddSubCategoryDto toAddCategoryDto, BindingResult result) {

        if (result.hasErrors()) {

            HashMap<String, String> map = new HashMap<>();

            for (ObjectError error : result.getAllErrors()) {
                String field = ((FieldError) error).getField();
                String errorMsg = error.getDefaultMessage();
                map.put(field, errorMsg);
            }

            return map;

        } else {
            boolean ifExists = adminService.AddSubCategory(toAddCategoryDto);

            if (!ifExists) {
                HashMap<String, String> map = new HashMap<>();
                String name = "catName";
                map.put(name, "The name already exists!");

                return map;
            }

        }
        return null;

    }

    @PostMapping("/subcategory/edit-subcategory")
    @ResponseBody
    @InventoryAdminTask
    public HashMap<String, String> editSubCategory(@Valid @ModelAttribute EditSubCategoryDto editSubCategoryDto, BindingResult result) {

        System.out.println(editSubCategoryDto);

        if (result.hasErrors()) {

            HashMap<String, String> map = new HashMap<>();

            for (ObjectError error : result.getAllErrors()) {
                String field = ((FieldError) error).getField();
                String errorMsg = error.getDefaultMessage();
                map.put(field, errorMsg);
            }

            return map;

        } else {
            boolean ifExists = adminService.editSubCategory(editSubCategoryDto);

            if (!ifExists) {
                HashMap<String, String> map = new HashMap<>();
                String name = "name";
                map.put(name, "The name already exists!");

                return map;
            }

        }

        return null;

    }

    @PostMapping("/subcategory/deletesubcategory")
    @ResponseBody
    @InventoryAdminTask
    public void deleteSubCategory(@RequestParam("subcateId") int subcateId, @RequestParam("catId") int catId) {

        adminService.deleteSubCategory(subcateId, catId);

    }

    @RequestMapping("/product")
    @InventoryAdminTask
    public String product(Model model) {
        return "admin/Product";
    }

    @PostMapping("/ajax-to-show-product")
    @ResponseBody
    @InventoryAdminTask
    public List<ShowProductDto> ajaxToShowProduct(@RequestParam("search") String search, @RequestParam("curPage") int curPage) {

        List<ShowProductDto> list = adminService.showProducts(search, curPage);

        return list;
    }

    @RequestMapping("/addproduct")
    @InventoryAdminTask
    public String addProduct(Model model) {
        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);
        return "admin/AddProduct";
    }

    @PostMapping("/deleteproduct")
    @ResponseBody
    @InventoryAdminTask
    public void deleteProduct(@RequestParam("productId") int productId) {

        adminService.deleteProduct(productId);

    }

    @RequestMapping("/editproduct/{productId}")
    @InventoryAdminTask
    public String editProduct(@PathVariable("productId") int productId, Model model) {

        model.addAttribute("productId", productId);

        List<ToShowCategoryInLandingPageDto> list = userService.ShowCategoriesInLandingPage();
        model.addAttribute("CatNameList", list);

        EditProductDto editProductDto = adminService.showProductDetailsForEdit(productId);
        model.addAttribute("editProductDto", editProductDto);

        return "admin/EditProduct";

    }

    @PostMapping("/subcategoryname")
    @ResponseBody
    @InventoryAdminTask
    public List<ShowSubCatNameDto> showSubCats(@RequestParam("categryyId") int catId) {

        List<ShowSubCatNameDto> list = adminService.showSubCatNames(catId);
        return list;

    }

    @PostMapping("/submit-add-product")
    @ResponseBody
    @InventoryAdminTask
    public ResponseEntity<Map<String, String>> submitAddProduct(@ModelAttribute("formData") @Valid AddProductDto addProductDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "submitAddProduct", AddProductDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isAdded = adminService.addProduct(addProductDto, session, userId);

            if (isAdded.equals("Product Name Already Exists!")) {
                map.put("name", isAdded);
            } else if (isAdded.equals("Enter enough number of files")) {
                map.put("formFileMultiple", isAdded);
            } else if (isAdded.equals("Enter proper fileName in proper order as per the given instructions")) {
                map.put("formFileMultiple", isAdded);
            } else if (isAdded.equals("Enter proper discount!")) {
                map.put("discount", isAdded);
            } else {
                return ResponseEntity.ok().body(map);
            }

        }

        return ResponseEntity.badRequest().body(map);

    }

    @PostMapping("/submit-edit-product")
    @ResponseBody
    @InventoryAdminTask
    public ResponseEntity<Map<String, String>> submitEditProduct(@ModelAttribute("formData") @Valid AddProductDto addProductDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "submitEditProduct", AddProductDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isAdded = adminService.editProduct(addProductDto, session, userId);

            if (isAdded.equals("Enter enough number of files")) {
                map.put("formFileMultiple", isAdded);
            } else if (isAdded.equals("Enter proper fileName in proper order as per the given instructions")) {
                map.put("formFileMultiple", isAdded);
            } else if (isAdded.equals("Enter proper discount!")) {
                map.put("discount", isAdded);
            } else {
                return ResponseEntity.ok().body(map);
            }

        }

        return ResponseEntity.badRequest().body(map);

    }

    @RequestMapping("/coupons")
    @SalesAdminTask
    public String showCoupons(Model model) {

        return "admin/AddCoupon";
    }

    @PostMapping("/show-coupons")
    @ResponseBody
    @SalesAdminTask
    public List<AddCouponDto> showCoupons(@RequestParam("search") String search, @RequestParam("curPage") int curPage) {

        List<AddCouponDto> list = adminService.showCoupons(search, curPage);
        return list;

    }

    @PostMapping("/add-coupon")
    @ResponseBody
    @SalesAdminTask
    public ResponseEntity<Map<String, String>> addCoupon(@ModelAttribute("formData") @Valid AddCouponDto addCouponDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "addCoupon", AddCouponDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isAdded = adminService.addCoupon(addCouponDto, userId);

            if (isAdded.equals("This code is already taken! Please enter unique coupon code.")) {
                map.put("code", isAdded);
                return ResponseEntity.badRequest().body(map);
            } else if (isAdded.equals("Please enter a valid end date (It must be of today or after today)")) {
                map.put("endDate", isAdded);
                return ResponseEntity.badRequest().body(map);
            } else if (isAdded.equals("Please enter a valid amount")) {
                map.put("minAmount", isAdded);
                return ResponseEntity.badRequest().body(map);
            } else if (isAdded.equals("please enter a valid discount")) {
                map.put("discount", isAdded);
                return ResponseEntity.badRequest().body(map);
            } else {
                return ResponseEntity.ok().body(map);
            }

        }

    }

    @PostMapping("/edit-coupon")
    @ResponseBody
    @SalesAdminTask
    public ResponseEntity<Map<String, String>> editCoupon(@ModelAttribute("formData") @Valid AddCouponDto addCouponDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "editCoupon", AddCouponDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isAdded = adminService.editCoupon(addCouponDto, userId);

            if (isAdded.equals("This code doesn't exists!")) {
                map.put("code-edit", isAdded);
                return ResponseEntity.badRequest().body(map);
            } else if (isAdded.equals("Please enter a valid end date (It must be of today or after today)")) {
                map.put("endDate-edit", isAdded);
                return ResponseEntity.badRequest().body(map);
            } else if (isAdded.equals("Please enter a valid amount")) {
                map.put("minAmount-edit", isAdded);
                return ResponseEntity.badRequest().body(map);
            } else if (isAdded.equals("please enter a valid discount")) {
                map.put("discount-edit", isAdded);
                return ResponseEntity.badRequest().body(map);
            } else {
                return ResponseEntity.ok().body(map);
            }

        }

    }

    @PostMapping("/delete-coupon")
    @ResponseBody
    @SalesAdminTask
    public void deleteCoupon(@RequestParam("id") int id, HttpSession session) {

        int userId = (int) session.getAttribute("userId");

        adminService.deleteCoupon(id, userId);

    }

    @RequestMapping("/profile")
    public String myProfile(Model model, HttpSession session) {

        int userId = (int) session.getAttribute("userId");
        EditPersonalInfoDto userData = userService.getDataOfUser(userId);
        model.addAttribute("userData", userData);

        return "admin/MyProfile";
    }

    @RequestMapping("/edit-profile")
    @ResponseBody
    public ResponseEntity<Map<String, String>> editProfile(@Valid @ModelAttribute EditPersonalInfoDto editPersonalInfoDto, BindingResult result, HttpSession session, HttpServletRequest request) throws MethodArgumentNotValidException {

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

    @RequestMapping("/users")
    @MasterAdminTask
    public String viewUsers() {

        return "admin/Users";
    }

    @RequestMapping("/show-users")
    @ResponseBody
    @MasterAdminTask
    public List<GetAllUsersDto> showUsers(@RequestParam("search") String searchText, @RequestParam("curPage") int curPage, @RequestParam("filters") String filters) {

        List<GetAllUsersDto> list = adminService.showUsers(searchText, curPage, filters, 0);
        return list;

    }

    @RequestMapping("/get-roles")
    @ResponseBody
    @MasterAdminTask
    public List<Role> getRoles() {

        List<Role> list = adminService.getRoles();
        return list;

    }

    @RequestMapping("/block-user")
    @ResponseBody
    @MasterAdminTask
    public void blockUser(@RequestParam("userId") int userId) {

        adminService.blockUser(userId);

    }

    @RequestMapping("/unblock-user")
    @ResponseBody
    @MasterAdminTask
    public void unBlockUser(@RequestParam("userId") int userId) {

        adminService.unBlockUser(userId);

    }

    @RequestMapping("/delete-user")
    @ResponseBody
    @MasterAdminTask
    public void deleteUser(@RequestParam("userId") int userId) {

        adminService.deleteUser(userId);

    }

    @RequestMapping("/edit-user")
    @ResponseBody
    @MasterAdminTask
    public ResponseEntity<Map<String, String>> editUser(@Valid @ModelAttribute EditUserDto editUserDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "editUser", EditPersonalInfoDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isValid = adminService.editUser(editUserDto, userId);

            if (isValid.equals("Enter a valid role")) {
                map.put("role", isValid);
                return ResponseEntity.badRequest().body(map);
            } else if (isValid.equals("Enter a valid user")) {
                map.put("email", isValid);
                return ResponseEntity.badRequest().body(map);
            }

            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/orders")
    @SalesAdminTask
    public String viewOrders() {

        return "admin/Orders";
    }

    @RequestMapping("/show-orders")
    @ResponseBody
    @SalesAdminTask
    public List<GetAllOrdersDto> showOrders(@RequestParam("search") String searchText, @RequestParam("curPage") int curPage, @RequestParam("filters") String filters) {

        List<GetAllOrdersDto> list = adminService.showOrders(searchText, curPage, filters);
        return list;

    }

    @RequestMapping("/cancel-order")
    @ResponseBody
    @SalesAdminTask
    public ResponseEntity<Map<String, String>> cancelOrder(@Valid @ModelAttribute CancelOrderDto cancelOrderDto, BindingResult result, HttpSession session) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "cancelOrder", CancelOrderDto.class, BindingResult.class, HttpSession.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {
            int userId = (int) session.getAttribute("userId");

            String isValid = userService.cancelOrder(cancelOrderDto, userId);

            if (!isValid.equals("success")) {
                map.put("error", isValid);
                return ResponseEntity.badRequest().body(map);
            } else {
                return ResponseEntity.ok().body(map);
            }
        }

    }

    @RequestMapping("/orderdetails/{oId}")
    @SalesAdminTask
    public String viewOrderDetails(@PathVariable("oId") int oId, Model model) {

        model.addAttribute("oId", oId);
        return "admin/OrderDetails";

    }

    @RequestMapping("/show-order-details")
    @ResponseBody
    @SalesAdminTask
    public List<GetOrderDetailsDto> showOrderDetails(@RequestParam("search") String searchText, @RequestParam("curPage") int curPage, @RequestParam("orderId") int orderId) {

        List<GetOrderDetailsDto> list = adminService.showOrderDetails(searchText, curPage, orderId);
        return list;

    }

    @RequestMapping("/cancelleditems")
    @SalesAdminTask
    public String viewCancelledItems() {

        return "admin/CancelledItems";
    }

    @RequestMapping("/show-cancelled-orders")
    @ResponseBody
    @SalesAdminTask
    public List<GetCancelledItems> showCancelledOrders(@RequestParam("search") String searchText, @RequestParam("curPage") int curPage) {

        List<GetCancelledItems> list = adminService.showCancelledItems(searchText, curPage);
        return list;

    }

    @RequestMapping("/admins")
    @MasterAdminTask
    public String viewAdmins() {

        return "admin/Admins";
    }

    @RequestMapping("/show-admins")
    @ResponseBody
    @MasterAdminTask
    public List<GetAllUsersDto> showAdmins(@RequestParam("search") String searchText, @RequestParam("curPage") int curPage, @RequestParam("filters") String filters) {

        List<GetAllUsersDto> list = adminService.showUsers(searchText, curPage, filters, 1);
        return list;

    }

    @RequestMapping("/add-admin")
    @ResponseBody
    @MasterAdminTask
    public ResponseEntity<Map<String, String>> addAdmin(@Valid @ModelAttribute AddAdminDto addAdminDto, BindingResult result, HttpSession session, HttpServletRequest request) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "addAdmin", AddAdminDto.class, BindingResult.class, HttpSession.class, HttpServletRequest.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isValid = adminService.addAdmin(addAdminDto, userId, request);

            if (isValid.equals("Enter a valid role")) {
                map.put("role", isValid);
                return ResponseEntity.badRequest().body(map);
            } else if (isValid.equals("Enter a valid gender")) {
                map.put("gender", isValid);
                return ResponseEntity.badRequest().body(map);
            }

            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/carriers")
    @MasterAdminTask
    public String viewDelivery() {

        return "admin/Carriers";
    }

    @RequestMapping("/add-carrier")
    @ResponseBody
    @MasterAdminTask
    public ResponseEntity<Map<String, String>> addCarrier(@Valid @ModelAttribute AddCarrierDto addCarrierDto, BindingResult result, HttpSession session, HttpServletRequest request) throws MethodArgumentNotValidException {

        Map<String, String> map = new HashMap<>();

        if (result.hasErrors()) {
            Method method = ReflectionUtils.findMethod(getClass(), "addCarrier", AddCarrierDto.class, BindingResult.class, HttpSession.class, HttpServletRequest.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(methodParameter, result);
        } else {

            int userId = (int) session.getAttribute("userId");

            String isValid = adminService.addCarrier(addCarrierDto, userId, request);

            if (isValid.equals("Enter a valid gender")) {
                map.put("gender", isValid);
                return ResponseEntity.badRequest().body(map);
            }

            return ResponseEntity.ok().body(map);

        }

    }

    @RequestMapping("/show-carriers")
    @ResponseBody
    @MasterAdminTask
    public List<AddCarrierDto> showCarriers(@RequestParam("search") String searchText, @RequestParam("curPage") int curPage) {

        List<AddCarrierDto> list = adminService.showCarriers(searchText, curPage);
        return list;

    }

    @RequestMapping("/get-relevent-orders")
    @ResponseBody
    @MasterAdminTask
    public List<Integer> getReleventOrders(@RequestParam("carrierId") int carrierId) {

        List<Integer> list = adminService.getReleventOrders(carrierId);
        return list;

    }

    @RequestMapping("/assign-orders-to-carrier")
    @ResponseBody
    @MasterAdminTask
    public void assignOrdersToCarrier(@RequestParam("orderIds") List<Integer> orderIds, @RequestParam("carrierId") int carrierId) {

        adminService.assignOrdersToCarrier(orderIds, carrierId);

    }

    @RequestMapping("/get-dashboard-data")
    @ResponseBody
    public AdminDashboardDataDto getDashboardData() {

        AdminDashboardDataDto adminDashData = adminService.getAdminDashData();
        return adminDashData;

    }

    @PostMapping("/if-user-exists")
    @ResponseBody
    public boolean ifUserExists(@RequestParam("email") String email) {
        boolean userExists = adminService.ifUserExists(email);
        return userExists;
    }

}
