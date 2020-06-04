package sunwell.permaisuri.ws.controller;
//package sunwell.permaisuri.ws.controller;
//
//import aegwyn.core.web.model.UserSession;
//
//
//import aegwyn.core.web.model.UserSessionContainer;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import sunwell.permaisuri.bus.dto.cred.UserCredentialDTO;
//import sunwell.permaisuri.bus.dto.cred.UserGroupDTO;
//import sunwell.permaisuri.bus.dto.cred.UserGroupListDTO;
//import sunwell.permaisuri.bus.dto.cred.UserListDTO;
//import sunwell.permaisuri.bus.dto.customer.CustomerDTO;
//import sunwell.permaisuri.bus.dto.customer.CustomerListDTO;
//import sunwell.permaisuri.bus.service.ExampleSvc;
//import sunwell.permaisuri.bus.service.SalesSvc;
//import sunwell.permaisuri.bus.service.StandardConstant;
//import sunwell.permaisuri.bus.service.UserCredSvc;
//import sunwell.permaisuri.ws.annotation.RestEndpoint;
//import sunwell.permaisuri.core.entity.cred.UserCredential;
//import sunwell.permaisuri.core.entity.cred.UserGroup;
//import sunwell.permaisuri.core.entity.customer.Customer;
//import sunwell.permaisuri.core.entity.example.A;
//import sunwell.permaisuri.core.entity.sales.SalesOrder;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.FileSystems;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//import javax.ejb.TransactionAttribute;
//import javax.ejb.TransactionAttributeType;
//import javax.inject.Inject;
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.StreamingOutput;
//import org.mindrot.jbcrypt.BCrypt;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//
///**
// *
// * @author Benny
// */
//@RestEndpoint
//public class LoginController 
//{
////	@Autowired
////    TenantFacade tenantService;
//	
////	@Autowired
////    TenantSvc tenantService;
//    
////	@Autowired
////    CustomerFacade custService;
//	
////	@Autowired
////    CustomerSvc custService;
//    
////	@Autowired
////    UserCredFacade userService;
//	
//	@Autowired
//	ExampleSvc exSvc;
//	
//	@Autowired
//	SalesSvc salesSvc;
//	
//	@Autowired
//    UserCredSvc userService;
//    
////	@Autowired
////    GenericFacade genericService;
//    
//    @Inject
//    UserSessionContainer usc;
//    
//    @Inject
//    ServletContext sCtx;
//    
//    @Inject
//    MessageSource messageSource;
//    
//    @Autowired
//    ServiceUtil svcUtil;
//    
//    @Inject
//    HttpServletRequest request;
//    
//	@RequestMapping(value = "login", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<UserCredentialDTO> login(@RequestBody UserCredentialDTO _dto) throws Exception
//    {
//        UserCredentialDTO retval = new UserCredentialDTO();
//		
//		try {
//			UserCredential usr = userService.validate(_dto.getUserName(), _dto.getPwd());
//			
//			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.HOUR, 1);
//			String jwt = ServiceUtil.getToken(usr.getUserName());
//
//			UserSession us = usc.newSession();
//			us.setSessionName("Login");
//			us.addObject("user", usr);
//			us.addObject("realPassword", _dto.getPwd());
//			us.setLastActivity(Calendar.getInstance());
//			retval.setData(usr);
//			retval.setSessionString(us.getSessionId());
//			retval.setToken(jwt);
//		}
//		catch (Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        	
//        return new ResponseEntity<UserCredentialDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//	@RequestMapping(value = "logout", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//	public ResponseEntity<UserCredentialDTO> logout(
//			@RequestHeader(value="Authorization", required=false) String _auth,
//			@RequestBody UserCredentialDTO _dto) throws Exception
//    {
//		UserCredentialDTO retval = new UserCredentialDTO();
//		
//		int tokenStat = ServiceUtil.checkAuth(_auth);
//		if(tokenStat != StandardConstant.TOKEN_VALID) {
//			retval.setErrorCode(tokenStat);
//			retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//			return new ResponseEntity<UserCredentialDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//		}
//		
//        if(!svcUtil.validateLogin (_dto.getSessionString ())) {
//            retval.setErrorCode (StandardConstant.ERROR_NO_LOGIN_SESSION);
//            retval.setErrorMessage(messageSource.getMessage("error_no_login_session", null
//            		, request.getLocale()));
//        }
//        else {
//            usc.removeSession (_dto.getSessionString ());
//        }
//        return new ResponseEntity<UserCredentialDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//	@RequestMapping(value = "users", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//	public ResponseEntity<UserListDTO> getUsers(
//		@RequestHeader(value="Authorization", required=false) String _auth,
//		@RequestParam(value="sessionString", required=false) String _sessionString,
//		@RequestParam(value="systemId", required = false) Integer _systemId,
//		@RequestParam(value="name", required = false) String _name,
//		@RequestParam(value="groupId", required = false) Integer _groupId,
//		Pageable _page) throws Exception 
//	{
//	    UserListDTO retval = new UserListDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			retval.setErrorCode(tokenStat);
//    			retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//    			return new ResponseEntity<UserListDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//    		}
//
//    		UserCredential usr = null;
//			Page<UserCredential> pageUsers = null;
//    		
//    		if(_systemId != null) {
//    			usr = userService.findUser(_systemId);
//    		}
//    		else if(_name != null) {
//    			usr = userService.findUserByName( _name);
//    		}
//    		else {
//        		if(_groupId != null) {
//        			pageUsers = userService.findUserByGroupId(_groupId, _page);
//        		}
//        		else {
//        			pageUsers = userService.findAllUsers(_page);
//        		}
//    		}
//    		
//    		if(pageUsers != null && pageUsers.getNumberOfElements() > 0) {
//    			retval.setTotalItems(pageUsers.getTotalElements());
//    			retval.setTotalPages(pageUsers.getTotalPages());
//    			retval.setData(pageUsers.getContent());
//    		}
//    		else if (usr != null) {
//    			retval.setTotalItems((long)1);
//    			retval.setTotalPages(1);
//    			retval.setData(Arrays.asList(new UserCredential[] {usr}));
//    		}
//    	}
//    	catch(Exception e) {
//    		svcUtil.handleException(retval, e);
//    	}
//        
//        return new ResponseEntity<UserListDTO>(retval, null, HttpStatus.OK);
//    }
//    
//	@RequestMapping(value = "users", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<UserCredentialDTO> addUser(
//			@RequestHeader(value="Authorization", required=false) String _auth,
//    		@RequestBody UserCredentialDTO _dto) throws Exception 
//	{
//		UserCredentialDTO retval = new UserCredentialDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			retval.setErrorCode(tokenStat);
//    			retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//    			return new ResponseEntity<UserCredentialDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//    		}
//    		
//            UserCredential data = _dto.getData();
//            UserCredential entity = userService.addUser (data, _dto.getImgData(), 
//            		sCtx.getInitParameter ("imagePath"));
//            retval.setData (entity);
//    	}
//    	catch(Exception e) {
//    		svcUtil.handleException(retval, e);
//    	}
//        return new ResponseEntity<UserCredentialDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//	@RequestMapping(value = "users", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<UserCredentialDTO> editUser(
//			@RequestHeader(value="Authorization", required=false) String _auth,
//    		@RequestBody UserCredentialDTO _dto) throws Exception 
//	{
//		UserCredentialDTO retval = new UserCredentialDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			retval.setErrorCode(tokenStat);
//    			retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//    			return new ResponseEntity<UserCredentialDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//    		}
//    		UserCredential data = _dto.getData();
//            UserCredential entity = userService.editUser (data, _dto.getImgData(), 
//            		sCtx.getInitParameter ("imagePath"));
//            retval.setData (entity);
//    	}
//    	catch(Exception e) {
//    		svcUtil.handleException(retval, e);
//    	}
//        return new ResponseEntity<UserCredentialDTO>(retval, null, HttpStatus.CREATED);
//    }    
//    
//    
//	@RequestMapping(value = "users", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//	public ResponseEntity<UserCredentialDTO> deleteUser(
//		@RequestHeader(value="Authorization", required=false) String _auth,
//		@RequestParam("systemId") Integer _systemId) throws Exception 
//	{
//		UserCredentialDTO retval = new UserCredentialDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			retval.setErrorCode(tokenStat);
//    			retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//    			return new ResponseEntity<UserCredentialDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//    		}
//		    UserCredential usr = userService.deleteUser(_systemId);
//		    retval.setData(usr);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<UserCredentialDTO>(retval, null, HttpStatus.OK);
//    }
//    
//	@RequestMapping(value = "usergroups", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//	public ResponseEntity<UserGroupListDTO> getUserGroups(
//		@RequestHeader(value="Authorization", required=false) String _auth,
//		@RequestParam(value="sessionString", required=false) String _sessionString,
//		@RequestParam(value="systemId", required = false) Integer _systemId,
//		@RequestParam(value="name", required = false) String _name,
//		Pageable _page
//		) throws Exception 
//	{
//		UserGroupListDTO retval = new UserGroupListDTO();
//        UserCredential user = svcUtil.getUser (_sessionString);
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			retval.setErrorCode(tokenStat);
//    			retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//    			return new ResponseEntity<UserGroupListDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//    		}
//    		
//    		UserGroup ug = null;
//    		Page<UserGroup> pageGroups = null ;
//    		if(_systemId != null) {
//    			ug = userService.findUserGroup(_systemId);
//    		}
//    		else if(_name != null) {
//    			ug = userService.findUserGroupByName(_name);
//    		}
//    		else {
//    			pageGroups = userService.findAllUserGroups(_page);
//    		}
//    		
//    		if(pageGroups != null && pageGroups.getNumberOfElements() > 0) {
//    			retval.setTotalItems(pageGroups.getTotalElements());
//    			retval.setTotalPages(pageGroups.getTotalPages());
//    			retval.setData(pageGroups.getContent());
//    		}
//    		else if (ug != null) {
//    			retval.setTotalItems((long)1);
//    			retval.setTotalPages(1);
//    			retval.setData(Arrays.asList(new UserGroup[] {ug}));
//    		}
//    	}
//    	catch(Exception e) {
//    		svcUtil.handleException(retval, e);
//    	}
//        return new ResponseEntity<UserGroupListDTO>(retval, null, HttpStatus.OK);
//    }
//    
//	@RequestMapping(value = "usergroups", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<UserGroupDTO> addUserGroup(
//    		@RequestHeader(value="Authorization", required=false) String _auth,
//    		@RequestBody UserGroupDTO _dto) throws Exception  
//	{
//		UserGroupDTO retval = new UserGroupDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			retval.setErrorCode(tokenStat);
//    			retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//    			return new ResponseEntity<UserGroupDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//    		}
//    		
//            UserGroup data = _dto.getData();
//            UserGroup entity = userService.addUserGroup (data);
//            retval.setData (entity);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<UserGroupDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//	@RequestMapping(value = "usergroups", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<UserGroupDTO> editUserGroup(
//    		@RequestHeader(value="Authorization", required=false) String _auth,
//    		@RequestBody UserGroupDTO _dto) throws Exception {
//		UserGroupDTO retval = new UserGroupDTO();
//        UserCredential user = svcUtil.getUser (_dto.getSessionString ());
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			retval.setErrorCode(tokenStat);
//    			retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//    			return new ResponseEntity<UserGroupDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//    		}
//            UserGroup data = _dto.getData();
//            UserGroup entity = userService.editUserGroup (data);
//            retval.setData (entity);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<UserGroupDTO>(retval, null, HttpStatus.CREATED);
//    }    
//    
//	@RequestMapping(value = "usergroups", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//	public ResponseEntity<UserGroupDTO> deleteUserGroup(
//			@RequestHeader(value="Authorization", required=false) String _auth,
//			@RequestParam("systemId") Integer _systemId) throws Exception 
//	{
//		UserGroupDTO retval = new UserGroupDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			retval.setErrorCode(tokenStat);
//    			retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//    			return new ResponseEntity<UserGroupDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//    		}
//            UserGroup group = userService.deleteUserGroup(_systemId);
//            retval.setData(group);
//    	}
//    	catch(Exception e) {
//    		svcUtil.handleException(retval, e);
//    	}
//        return new ResponseEntity<UserGroupDTO>(retval, null, HttpStatus.OK);
//    }
//    
//	@RequestMapping(value = "customers", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//	public ResponseEntity<CustomerListDTO> getCustomers(
////		@RequestParam("sessionString") String _sessionString,
//		@RequestHeader(value="Authorization", required = false) String _auth,
//		@RequestParam(value = "sessionString", required = false) String _sessionString,
//		@RequestParam(value="systemId", required = false) Long _systemId,
//		@RequestParam(value="email", required = false) String _email,
//		@RequestParam(value="groupId", required = false) Integer _groupId,
//		Pageable _page
//		) throws Exception 
//	{
//		CustomerListDTO retval = new CustomerListDTO();
//
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<CustomerListDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			
//            Customer cust = null;
//            Page<Customer> pageCust = null;
//            System.out.println("PAGE NO: " + _page.getPageNumber());
//            
//    		if(_systemId != null) {
//    			cust = userService.findCustomer(_systemId);
//    		}
//    		else if(_email != null) {
//    			cust = userService.findCustomerByEmail(_email);
//    		}
//    		else {
//    			pageCust = userService.findCustomerByGroupId(_groupId, _page);
//    		}
//    		
//    		if(pageCust != null && pageCust.getNumberOfElements() > 0) {
//    			retval.setTotalItems(pageCust.getTotalElements());
//    			retval.setTotalPages(pageCust.getTotalPages());
//    			retval.setData(pageCust.getContent());
//    		}
//    		else if(cust != null) {
//    			System.out.println("TOT CART ETAILS: " + (cust.getCartDetails() != null ? cust.getCartDetails().size() : 0));
//    			retval.setTotalItems(1l);
//    			retval.setTotalPages(1);
//    			retval.setData(Arrays.asList(new Customer[] {cust}));
//    		}
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<CustomerListDTO>(retval, null, HttpStatus.OK);
//    }
//    
//	@RequestMapping(value = "customers", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<CustomerDTO> addCustomer(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody CustomerDTO _dto) throws Exception {
//		CustomerDTO retval = new CustomerDTO();
//        try {
//        	int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<CustomerDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//            Customer cust = userService.addCustomer(_dto.getData());            
//            retval.setData (cust);
//        }
//        catch(Exception e) {
//    		svcUtil.handleException(retval, e);
//        }
//        return new ResponseEntity<CustomerDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//	@RequestMapping(value = "customers", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<CustomerDTO> editCustomer(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody CustomerDTO _dto) throws Exception {
//		CustomerDTO retval = new CustomerDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<CustomerDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			Customer cust = userService.editCustomer(_dto.getData());
//			retval.setData(cust);
//		}
//		catch (Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<CustomerDTO>(retval, null, HttpStatus.CREATED);
//    }   
//
//	@RequestMapping(value = "customers", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//	public ResponseEntity<CustomerDTO> deleteCustomer(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("systemId") Long _systemId) throws Exception 
//	{
//		CustomerDTO retval = new CustomerDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<CustomerDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//            Customer customer = userService.deleteCustomer(_systemId);
//            retval.setData(customer);
//    	}
//    	catch(Exception e) {
//    		svcUtil.handleException(retval, e);
//    	}
//        return new ResponseEntity<CustomerDTO>(retval, null, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "users/images", method = RequestMethod.GET,
//    		produces = {"image/jpg", "image/png"}
//	)
//    public ResponseEntity<byte[]> getImage(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("sessionString")String _sessionString,
//    		@RequestParam("systemId")Integer _systemId,
//    		@RequestParam("image") String _image) throws Exception
//    { 
//    	int tokenStat = ServiceUtil.checkAuth(_auth);
//		if(tokenStat != StandardConstant.TOKEN_VALID) {
//			return new ResponseEntity<byte[]>(null, null, HttpStatus.UNAUTHORIZED); 
//		}
//		
//        UserCredential user = null;
//        if(_systemId != null) {
//            user = userService.findUser(_systemId);
//        }
//        else if(_sessionString != null) {
//            UserSession us = usc.getSession (_sessionString, false);
//            if(us != null) {
//                UserCredential u = null;
//                user = (UserCredential)us.getObject ("user");
//            }
//        }
//        
//        if(user == null) {
//            System.out.println ("user can't be found id: " + _systemId);
//            return null;
//        }
//        
//        String path = sCtx.getInitParameter ("imagePath") + "users/" +_systemId + "/" + _image;
//        File file = new File(path);
//        if(file.exists ()) { 
//            FileInputStream fis = new FileInputStream(file);
//            long length = file.length ();
//            byte[] filecontent = new byte[(int)length];
//            fis.read(filecontent,0,(int)length);
//            
//            return new ResponseEntity<byte[]>(filecontent, null, HttpStatus.OK);
//            
//        }
//        else {
//            System.out.println ("File doesn't exist, tid: " + _systemId + " image: " + _image);
//            return new ResponseEntity<byte[]>(null, null, HttpStatus.NO_CONTENT);
//        }
//    }
//}
//
//
