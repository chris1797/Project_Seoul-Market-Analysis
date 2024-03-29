package com.mac.demo.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;


@RequestMapping("/home")
@RestController
public class HomeController {
	
//	홈화면
	@GetMapping("")
	public ModelAndView home(HttpSession session) {

		ModelAndView mav = new ModelAndView("/home/home");

		if(session.getAttribute("idMac")!=null) {
			String user_id = session.getAttribute("idMac").toString();
			mav.addObject("idMac", user_id);
		}
		
		return mav;
	}
	
//	데이터 출처
	@GetMapping("/dataSource")
	public ModelAndView dataSource(HttpSession session) {

		ModelAndView mav = new ModelAndView("/home/dataSource");

		if(session.getAttribute("idMac")!=null) {
			String user_id = session.getAttribute("idMac").toString();
			mav.addObject("idMac",user_id);
		}
		
		return mav;
	}
	
//	사이트소개
	@GetMapping("/siteIntroduction")
	public ModelAndView siteIntroduction(Model model,HttpSession session) {

		ModelAndView mav = new ModelAndView("/home/siteIntroduction");

		if(session.getAttribute("idMac")!=null) {
			String uid = session.getAttribute("idMac").toString();
			mav.addObject("idMac",uid);
		}
		return mav;
	}
	

	
	
}