package com.controller;

import com.models.Admin;
import com.models.User;
import com.service.AdminService;
import com.service.UserService;
import com.util.Decode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;

    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/checkUsername")
    public void checkUsername(@RequestParam("username") String username,
                              HttpServletResponse response) throws Exception{
        response.getWriter().print(userService.checkUsername(username));
    }

    @RequestMapping("/register")
    public String register(User user, HttpSession session){
        userService.register(user);
        session.setAttribute("user",user);
        return "login.html";
    }

    @RequestMapping("/loginCheck")
    public void loginCheck(@RequestParam("username")String username,
                             @RequestParam("password")String password,
                             HttpServletResponse response,
                             HttpSession session) throws Exception{
        System.out.println("iii");
        password = new String(Decode.decode(password));
        System.out.println(password);
        User user = userService.login(username,password);
        Admin admin = adminService.login(username,password);
        System.out.println("--");

        if(user==null&&admin==null){
            response.getWriter().print("wrong");
            System.out.println("wrongg");
        }else {
            if(admin==null){
                session.setAttribute("user",user);
                System.out.println(user.toString());
            }else {
                session.setAttribute("admin",admin);
                System.out.println(admin.toString());
            }
        }
    }

    @RequestMapping("/userLogin")
    public String login(HttpSession session){
        if(session.getAttribute("admin")==null){
            return "/getUserSurvey.action";
        }else {
            return "/admin.jsp";
        }
    }
}
