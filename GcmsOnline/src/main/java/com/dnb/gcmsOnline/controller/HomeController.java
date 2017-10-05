package com.dnb.gcmsOnline.controller;

import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dnb.gcms.common.domain.DunsSearchInfo;
import com.dnb.gcms.search.service.SearchServiceImpl;
import com.dnb.gcmsOnline.service.GcmsTextTypesService;
import com.dnb.model.GcmsTextTypes;

@Controller
@ComponentScan("com.dnb.gcms.search.service")
public class HomeController {
	
	@Value("${hometleaf.message:test}")
	private String message = "Hello World";
	
	@Autowired
	private GcmsTextTypesService gcmsTextTypesService;
	
	@Autowired
	private SearchServiceImpl searchServiceImpl;
	
	@RequestMapping("/")
	String home(ModelMap model) {
		DunsSearchInfo dunsSearchInfo = new DunsSearchInfo();
		model.addAttribute("dunsSearchInfo",dunsSearchInfo);
		return "home";
		
	}
	
	@RequestMapping("/test")
	String index(ModelMap model) {
		return "home";
	}

	@RequestMapping("/allText")
	String allTextTypes(ModelMap model) {
		List<GcmsTextTypes> list = gcmsTextTypesService.getAllGcmsTextTypes();
		model.addAttribute("textTypesList",list);
		return "allTextTypes";
	}
	
	@RequestMapping(value="/home", method=RequestMethod.POST)
	public String dunsSearch(@ModelAttribute DunsSearchInfo dunsSearchInfo, ModelMap model,final RedirectAttributes redirectAttributes){
		try {
			dunsSearchInfo = searchServiceImpl.getSearchResult(dunsSearchInfo.getDuns());
		}catch(JSONException jsonException){
			dunsSearchInfo = null;
			model.addAttribute("jsonException", "JSON Exception");
		}catch(HttpClientErrorException httpClientErrorException){
			dunsSearchInfo = null;
			model.addAttribute("dunsSearchInfo", dunsSearchInfo);
			model.addAttribute("connection", "Unable to deliver the response. Try sometime later");
			model.addAttribute("httpClientErrorException", "Http Client Error Exception");
		}catch(ResourceAccessException resourceAccessException){
			dunsSearchInfo = null;
			model.addAttribute("dunsSearchInfo", dunsSearchInfo);
			model.addAttribute("connection", "Unable to deliver the response. Try sometime later");
			System.out.println(resourceAccessException);
			System.out.println("Error Break Up");
			System.out.println(resourceAccessException.getMessage());
			return "search-results";
		}catch(Exception e){
			dunsSearchInfo = null;
			model.addAttribute("errorObject", "No results found for this search criteria.");
			return "search-results";
		}
		redirectAttributes.addFlashAttribute("dunsSearchInfo", dunsSearchInfo);
		model.addAttribute("dunsSearchInfo", dunsSearchInfo);
//		return "redirect:searchdata";
		return "search-results";
	}
	
	@RequestMapping(value = "/searchdata", method = RequestMethod.GET)
	public String searchResult(@ModelAttribute("dunsSearchInfo") final Object mapping1FormObject,final ModelMap model){
		model.addAttribute("dunsSearchInfo",mapping1FormObject);
		return "search-results";
	}

}

