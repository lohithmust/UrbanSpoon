package com.ts.us.controller;
import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ts.us.dao.BranchDAO;
import com.ts.us.dao.CuisineDAO;
import com.ts.us.dao.RecipeDAO;
import com.ts.us.dao.RestaurantDAO;
import com.ts.us.dto.Branch;
import com.ts.us.dto.Restaurant;
import com.ts.us.exception.UrbanspoonException;
import com.ts.us.helper.UrbanspoonHelper;
import com.ts.us.util.FileUpload;

@Controller
public class FileUploadController {
	
	private static final String IMAGEPATH = "E:\\Maven Project\\us\\src\\main\\webapp\\Images\\";

	@RequestMapping("/restaurant_registrations")
	
	public ModelAndView restaurantRegisters(@RequestParam("name") String name,@RequestParam("govtregistrationid") String userName,
			@RequestParam("password") String password,@RequestParam("confirm_password") String confPassword,
			@RequestParam("registrationlogo") CommonsMultipartFile commonsMultipartFile)
					throws UrbanspoonException, FileUploadException {
	         System.out.println("Controller in rest registration");
		ModelAndView mv = new ModelAndView("registersuccess");
		Restaurant restaurant = new Restaurant();
		restaurant.setGovtRegistrationtId(userName);
		restaurant.setName(name);
		restaurant.setPassword(password);
		RestaurantDAO restaurantDAO = new RestaurantDAO();
		restaurant = restaurantDAO.insert(restaurant);
		if (restaurant.getId() != 0) {
			FileUpload.upload(IMAGEPATH+"restaurants",commonsMultipartFile, restaurant.getId() + ".jpg");
			restaurantDAO.updateLogoAddress(restaurant.getId(), restaurant.getId() + ".jpg");
			return mv;
		}
		return mv;
	}
	
	
	@RequestMapping("/addNewBranch")
	public ModelAndView addBranch(@RequestParam("location") String location,@RequestParam("city") String city,
			@RequestParam("state") String state,@RequestParam("country") String country,@RequestParam("postal_code") String postal_code,
			@RequestParam("branch_images") CommonsMultipartFile[] commonsMultipartFiles,HttpServletRequest request) throws UrbanspoonException {
		List<FileItem> fileItemsList = UrbanspoonHelper.getFileItems(request);

		System.out.println("I am in addnewBranch");
		Branch branch = new Branch();
		
		branch.setLocation(location);
		branch.setCity(city);
		branch.setState(state);
		branch.setCountry(country);
		branch.setPostalCode(Integer.parseInt(postal_code));
		
			
		System.out.println(branch);
		BranchDAO branchDAO = new BranchDAO();
		HttpSession session = request.getSession(false);
		System.out.println("SESSION"+ session);
		branch = branchDAO.insert((long)session.getAttribute("loggedUserId"), branch);//loggedUserId
		if (branch.getId() != 0) {
			int count = 1;
			for(CommonsMultipartFile commonsMultipartFile : commonsMultipartFiles){
				FileUpload.upload(IMAGEPATH+"branches", commonsMultipartFile, branch.getId() + "_" + count + ".jpg");
				branchDAO.addImage(branch.getId(), branch.getId() + "_" + count + ".jpg");
				count++;
			}
			
		}
		ModelAndView mv = new ModelAndView("restaurantHome");
		System.out.println(mv.getViewName());
		mv.addObject("cuisineList", new CuisineDAO().getCuisines(false));
		mv.addObject("branchList", new BranchDAO().getBranches((long)session.getAttribute("loggedInUserId") , true, true));
		mv.addObject("recipeList", new RecipeDAO().getRecipes());
		return mv;
	}

	@RequestMapping("/addrecipetobranch")
	public ModelAndView addRecipetoBranch(@RequestParam("uafgh") String recipe  ){
		return null;
	}

}