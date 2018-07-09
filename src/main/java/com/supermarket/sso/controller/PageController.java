package com.supermarket.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**   
 * @ClassName: PageController   
 * @Description: page jump   
 * @author: KKL 
 * @date: 2018年7月8日 下午10:34:37      
 */  
@Controller
public class PageController {
	
	@RequestMapping("/page/register")
	public String showRegisterPage() {
		return "register";
	}
	
	@RequestMapping("/page/login")
	public String showLoginPage(String redirect,Model model) {
		model.addAttribute("redirect", redirect);
		return "login";
	}

}
