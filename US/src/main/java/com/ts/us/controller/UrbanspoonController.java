package com.ts.us.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ts.us.dao.FeedbackDAO;
import com.ts.us.dto.Branch;
import com.ts.us.dto.Feedback;
import com.ts.us.dto.FeedbackType;
import com.ts.us.dto.Recipe;
import com.ts.us.dto.Restaurant;
import com.ts.us.dto.User;
import com.ts.us.exception.UrbanspoonException;
import com.ts.us.helper.UrbanspoonHelper;
import com.ts.us.util.DateUtility;


@Controller
public class UrbanspoonController {

	@RequestMapping("/UrbanspoonController")
	public ModelAndView getRestaurantList() {

		ModelAndView model = new ModelAndView("home");
		try {
			List<Restaurant> listRestaurants= UrbanspoonHelper.getRestaurants(true);
			model.addObject("restaurantsList", listRestaurants);


		} catch (UrbanspoonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

	@RequestMapping("/login")
	public ModelAndView loginAsUser(@RequestParam("userid") String user_id,@RequestParam("password") String password,@RequestParam("loginAs") String loginAs,HttpServletRequest request ) throws UrbanspoonException {

	ModelAndView mv = null;
	if(loginAs.equals("user")) {
		
		System.out.println("I am in login");
		
		
		boolean login = UrbanspoonHelper.loginAsUser(user_id, password,request);
		System.out.println(login);
		
		if(login) {
			
			mv = new ModelAndView("userHome");
			List<Restaurant> listRestaurants = UrbanspoonHelper.getRestaurants(true);
			mv.addObject("restaurantsList",listRestaurants );
             return mv;
		}
	}
	
	if(loginAs.equals("restaurant")) {
		   System.out.println("i am in restaurant login");
		boolean login= UrbanspoonHelper.loginAsRestaurantOwner(user_id, password,request);
		System.out.println(login);
        if(login) {
        	mv = new ModelAndView("restaurantHome");
        	
        	return mv;
        }
	}

		return null;
	}

     @RequestMapping(value="/user_registration")
     public ModelAndView userRegistration( User register) throws UrbanspoonException {
    	 ModelAndView mv = null;
    	boolean added =  UrbanspoonHelper.addUser(register);
    	 if(added) {
    		 mv = new ModelAndView("registersuccess");
    		 return mv;
    	 }
    	 return mv;
     }
     
     @RequestMapping("/restaurantregistration")
     public ModelAndView addRestaurant(HttpServletRequest request,HttpServletResponse response) throws UrbanspoonException {
    	 boolean isMultipart = ServletFileUpload.isMultipartContent(request);
    	 ModelAndView mv = null;
 		
 			if (isMultipart) {

 				List<FileItem> fileItemsList = UrbanspoonHelper.getFileItems(request);
 				String action = UrbanspoonHelper.getFormFeildValue(	fileItemsList, "action");
 				if (action != null) {
 					if (action.equals("restaurant_registration")) {
 						if (UrbanspoonHelper.addRestaurant(fileItemsList,request, response)) {
 							mv = new ModelAndView("redirect:home");
 							return mv;
 						}
 					}
 					if (action.equals("branch")) {
 						if (UrbanspoonHelper.addBranch(fileItemsList, request,response)) {
 							mv = new ModelAndView("redirect:home");
 							return mv;
 						}
 					}
 					if (action.equals("recipe_to_branch")) {
 						if (UrbanspoonHelper.addRecipeToBranch(fileItemsList,request, response)) {
 							mv = new ModelAndView("redirect:home");
 							return mv;
 						}
 					}
 				}
 			} 
 			return mv;
     }
     
     @RequestMapping("/branchfeedback")
     public ModelAndView branchFeedback(@RequestParam("restaurant_id")  int restaurantid,@RequestParam("branch_id") int branchid) throws UrbanspoonException {
    	 Restaurant restaurant = new Restaurant();
    	 Branch branch = new Branch();
    	
    	 restaurant = UrbanspoonHelper.getRestaurant(restaurantid, false);
    	 branch = UrbanspoonHelper.getBranch(branchid, false);
    	 List<FeedbackType> feedbacktype = UrbanspoonHelper.getFeedbackTypesList();
    	 ModelAndView mv = new ModelAndView("userHome");
    	 mv.addObject("restaurantname", restaurant.getName());
    	 mv.addObject("branchlocation", branch.getLocation());
    	 mv.addObject("branchid",branch.getId());
    	 mv.addObject("feedbackTypeList", feedbacktype);
    	 return mv;
    	
     }
     @RequestMapping("/addbranchfeedback")
     public ModelAndView addBranchFeedback(@RequestParam("branchid") String branchid,@RequestParam("comments") String comments,
    		 @RequestParam("rating") int rating,@RequestParam("feedbacktypeid") String feedbacktypeid,
    		 @RequestParam("visitedDate") String visitedDate,HttpServletRequest request) throws UrbanspoonException {
    	
    	 System.out.println("I am in addbranch");
    	 System.out.println(branchid + "hii");
    	 ModelAndView mv = null;
    	 Feedback feedback = new Feedback();
    		Branch branch = new Branch();
    		System.out.println(branchid);
    		branch.setId(Integer.parseInt(branchid));
    		//feedback.getBranch().setId(Integer.parseInt(branchid));
    		System.out.println(branch.getId());
    		User user = new User();
    	//	HttpSession httpsession = request.getSession(false);
    	//	httpsession.getAttribute("loggedUserId");
    		
    		user.setId(UrbanspoonHelper.getLoggedUserId(request));
    		System.out.println(user.getId());
    		//feedback.getUser().setId(UrbanspoonHelper.getLoggedUserId(request));
    		feedback.setBranch(branch);
    		feedback.setUser(user);
    		FeedbackType feedbackType = new FeedbackType();
    		feedbackType.setId(Integer.parseInt(feedbacktypeid));
    		feedback.setFeedbackType(feedbackType);
    		//feedback.getFeedbackType().setId(Integer.parseInt(feedbacktypeid));
    		feedback.setComments(comments);
    		feedback.setRatings(rating);
    		System.out.println(visitedDate);
   
    		feedback.setVisitedDate(DateUtility.convertStringToDate(visitedDate));
    		feedback.setFeedbackDate(new Date());
    		FeedbackDAO feedbackDAO = new FeedbackDAO();
    		feedback = feedbackDAO.insertBranchFeedback(feedback);
    		if(feedback != null) {
    			mv = new ModelAndView("thankYou");
    			return mv;
    		}
    	 return null;
    	 
     }
     
     @RequestMapping("/recipefeedback")
     public ModelAndView recipeFeedback(@RequestParam("recipeid") String recipeid,@RequestParam("branchid") String branchid,@RequestParam("restaurantid") String restaurantid) throws NumberFormatException, UrbanspoonException {
    	 
    	 ModelAndView mv = null;
    	 Recipe recipe = new Recipe();
    	 Branch branch = new Branch();
    	 Restaurant restaurant = new Restaurant();
    	 
    	restaurant = UrbanspoonHelper.getRestaurant(Integer.parseInt(restaurantid), false); 
    	recipe = UrbanspoonHelper.getRecipe(Integer.parseInt(recipeid));
    	branch = UrbanspoonHelper.getBranch(Integer.parseInt(branchid), false);
    	 
    	 
    	 mv = new ModelAndView("userHome");
    	 mv.addObject("restaurantname",restaurant.getName() );
    	 mv.addObject("branchlocation", branch.getLocation() );
    	 mv.addObject("recipename", recipe.getName());
    	 mv.addObject("branchid", branch.getId());
    	 mv.addObject("recipeid", recipe.getId());
    	 return mv;
    	 
    	 
     }
     
     @RequestMapping("/addrecipefeedback")
     public ModelAndView addRecipeFeedback(@RequestParam("branchid") String branchid,@RequestParam("recipeid") String recipeid,
    		 @RequestParam("comments") String comments,@RequestParam("rating") String ratings,@RequestParam("visitedDate") String visitedDate,HttpServletRequest request) throws UrbanspoonException {
    	 ModelAndView mv = null;
    	 Feedback feedback = new Feedback();
 		Branch branch = new Branch();
 		branch.setId(Integer.parseInt(branchid));
 		User user = new User();
 		user.setId(UrbanspoonHelper.getLoggedUserId(request));
 		feedback.setBranch(branch);
 		feedback.setUser(user);
 		FeedbackType feedbackType = new FeedbackType();
 		feedback.setFeedbackType(feedbackType);
 		Recipe recipe = new Recipe();
 		recipe.setId(Integer.parseInt(recipeid));
 		feedback.setRecipe(recipe);
 		feedback.setComments(comments);
 		feedback.setRatings(Integer.parseInt(ratings));
 		feedback.setVisitedDate(DateUtility.convertStringToDate(visitedDate));
 		feedback.setFeedbackDate(new Date());
 		FeedbackDAO feedbackDAO = new FeedbackDAO();
 		feedback = feedbackDAO.insertRecipeFeedback(feedback);
 		if (feedback.getId() != 0) {
 			mv = new ModelAndView("thankYou");
 			return mv;
 		}
    	 
    	 
    	 return null;
     }
}























