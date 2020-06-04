package sunwell.permaisuri.ws.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import sunwell.permaisuri.bus.dto.contact.ContactDTO;
import sunwell.permaisuri.bus.dto.cred.UserCredentialDTO;
import sunwell.permaisuri.bus.dto.cred.UserListDTO;
import sunwell.permaisuri.bus.dto.customer.CustomerDTO;
import sunwell.permaisuri.bus.dto.hr.SalesOfficerDTO;
import sunwell.permaisuri.bus.dto.inventory.ItemCategoryDTO;
import sunwell.permaisuri.bus.dto.sales.CartDetailDTO;
import sunwell.permaisuri.bus.service.ExampleSvc;
import sunwell.permaisuri.bus.service.Filters;
import sunwell.permaisuri.bus.service.SalesSvc;
import sunwell.permaisuri.bus.service.StandardConstant;
import sunwell.permaisuri.bus.service.UserCredService;
import sunwell.permaisuri.bus.service.UserCredSvc;
import sunwell.permaisuri.ws.annotation.RestEndpoint;
import sunwell.permaisuri.ws.controller.ServiceUtil.TokenInfo;
import sunwell.permaisuri.ws.service.RegistrationService;
import sunwell.permaisuri.core.entity.contact.Contact;
import sunwell.permaisuri.core.entity.cred.UserCredential;
import sunwell.permaisuri.core.entity.customer.Customer;
import sunwell.permaisuri.core.entity.example.A;
import sunwell.permaisuri.core.entity.hr.SalesOfficer;
import sunwell.permaisuri.core.entity.inventory.ItemCategory;
import sunwell.permaisuri.core.entity.sales.CartDetail;
import sunwell.permaisuri.core.entity.sales.SalesOrder;
import sunwell.permaisuri.core.entity.warehouse.OnHandStock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
//import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;


/**
 *
 * @author Benny
 */
@RestEndpoint
public class LoginController 
{
	
//	@Autowired
//	ExampleSvc exSvc;
	
//	@Autowired
//	SalesSvc salesSvc;
	
	@Autowired
    UserCredService userService;
	
	@Autowired
	RegistrationService regSvc;
    
//    @Inject
//    UserSessionContainer usc;
    
    @Inject
    ServletContext sCtx;
    
    @Inject
    MessageSource messageSource;
    
    @Autowired
    ServiceUtil svcUtil;
    
    @Inject
    HttpServletRequest request;
    
	@RequestMapping(value = "login", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> login(@RequestBody UserCredentialDTO _dto) throws Exception
    {
        UserCredentialDTO retval = new UserCredentialDTO();
        Map<String,Object> retData = null;
		try {
			UserCredential usr = userService.validate(_dto.getUserName(), _dto.getPwd());
			
			if(usr == null) {
				retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_USER);
				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
			}
			
			if(!usr.isEnabled()) {
				retData = svcUtil.returnErrorData(StandardConstant.ERROR_USER_IS_NOT_ENABLED);
				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
			}
			
			Contact c = userService.findContactByCred(usr);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, 1);
			String jwt = svcUtil.getToken(c);
			retData = svcUtil.returnSuccessfulData(new ContactDTO(c), 1, 1);
			retData.put("token", jwt);

		}
		catch (Exception e) {
			svcUtil.handleException(retval, e);
		}
        	
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "activate", method = RequestMethod.GET
//			produces = "application/json"
	)
    public View active(@RequestParam(value="token") String _token) throws Exception
    {
        UserCredentialDTO retval = new UserCredentialDTO();
        Map<String,Object> retData = null;
		try {
			UserCredential usr = userService.findUserByToken(_token);
			
			if(usr == null) {
				String errMsg = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_USER);
				return new RedirectView("/activate?error=" + errMsg, true, false);
//				retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_USER);
				//				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
			}
			
			usr.setEnabled(true);
			usr.setRegistrationToken(null);
			userService.editUser(usr, null, null, false);
		
			Contact c = userService.findContactByCred(usr);
			retData = svcUtil.returnSuccessfulData(new ContactDTO(c), 1, 1);
			return new RedirectView("/activate", true, false);
		}
		catch (Exception e) {
			String errMsg = svcUtil.getErrorMessageFromException(e);
			return new RedirectView("/activate?error=" + errMsg, true, false);
			
//			svcUtil.handleException(retval, e);
		}
        	
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
//	@RequestMapping(value = "logout", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//	public ResponseEntity<Map<String,Object>> logout(
//			@RequestHeader(value="Authorization", required=false) String _auth,
//			@RequestBody UserCredentialDTO _dto) throws Exception
//    {
//		Map<String,Object> retData = null;
//		
//		TokenInfo tokenInfo = svcUtil.checkAuth(_auth);
//		if (tokenInfo.status != ServiceUtil.TOKEN_VALID) {
//			String errMessage = svcUtil.getErrorMessageFromCode(tokenInfo.status);
//			retData = svcUtil.returnErrorData(tokenInfo.status, errMessage);
//			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);  
//		}
//		
////        if(!svcUtil.validateLogin (_dto.getSessionString ())) {
////			String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_NO_LOGIN_SESSION);
////			retData = svcUtil.returnErrorData(StandardConstant.ERROR_NO_LOGIN_SESSION, errMessage);
////			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);
////        }
////        else {
////            usc.removeSession (_dto.getSessionString ());
////        }
//        return new ResponseEntity<Map<String,Object>> (retData, null, HttpStatus.CREATED);
//    }
    
	
    
//	@RequestMapping(value = "usergroups", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//	public ResponseEntity<Map<String,Object>> getUserGroups (
//		@RequestHeader(value="Authorization", required=false) String _auth,
//		@RequestParam(value="sessionString", required=false) String _sessionString,
//		@RequestParam(value="systemId", required = false) Integer _systemId,
//		@RequestParam(value="name", required = false) String _name,
//		Pageable _page
//		) throws Exception 
//	{
//		Map<String,Object> retData = null;
//
//    	try {
////    		int tokenStat = ServiceUtil.checkAuth(_auth);
//    		int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_VIEW_USER_GROUPS, -1);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
//				retData = svcUtil.returnErrorData(tokenStat, errMessage);
//				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);
//    		}
//    		
//    		Object mainData = null;
//    		UserGroup ug = null;
//    		Page<UserGroup> pageGroups = null ;
//    		int totalPages = 0;
//			long totalItems = 0;
//			
//    		if (_systemId != null) {
//    			ug = userService.findUserGroup(_systemId);
//    		}
//    		else if (_name != null) {
//    			ug = userService.findUserGroupByName(_name);
//    		}
//    		else {
//    			pageGroups = userService.findAllUserGroups(_page);
//    		}
//    		
//    		if (pageGroups != null && pageGroups.getNumberOfElements() > 0) {
//    			List<UserGroup> userGroups = pageGroups.getContent();
//    			List<UserGroupDTO> userGroupsData = new LinkedList<>();
//    			for (UserGroup u : userGroups) {
//    				userGroupsData.add(new UserGroupDTO(u));
//    			}
//    			mainData = userGroupsData;
//    			totalPages = pageGroups.getTotalPages();
//    			totalItems = pageGroups.getTotalElements();
//    		}
//    		else if (ug != null) {
//    			mainData = new UserGroupDTO(ug);
//    			totalPages = 1;
//    			totalItems = 1;
//    		}
//    		
//    		retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
//    		return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    	}
//    	catch (Exception e) {
//    		retData = svcUtil.handleException(e);
//    		return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    	}
//    }
//    
//	@RequestMapping(value = "usergroups", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> addUserGroup(
//    		@RequestHeader(value="Authorization", required=false) String _auth,
//    		@RequestBody UserGroupDTO _dto) throws Exception  
//	{
//		Map<String,Object> retData = null;
//		
//		try {
////			int tokenStat = ServiceUtil.checkAuth(_auth);
//    		int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CREATE_USER_GROUPS, -1);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
//				retData = svcUtil.returnErrorData(tokenStat, errMessage);
//				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
//    		}
//    		
//            UserGroup data = userService.addUserGroup (_dto.getData());
//            retData = svcUtil.returnSuccessfulData(new UserGroupDTO(data), 1, 1);
//            
//            return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//		}
//		catch (Exception e) {
//			retData = svcUtil.handleException(e);
//			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//		}
//    }
//    
//	@RequestMapping(value = "usergroups", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> editUserGroup(
//    		@RequestHeader(value="Authorization", required=false) String _auth,
//    		@RequestBody UserGroupDTO _dto) throws Exception  
//	{
//		Map<String,Object> retData = null;
//		
//		try {
////			int tokenStat = ServiceUtil.checkAuth(_auth);
//    		int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_UPDATE_USER_GROUPS, -1);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
//				retData = svcUtil.returnErrorData(tokenStat, errMessage);
//				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
//    		}
//    		
//            UserGroup data = userService.editUserGroup (_dto.getData());
//            retData = svcUtil.returnSuccessfulData(new UserGroupDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }    
//    
//	@RequestMapping(value = "usergroups", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//	public ResponseEntity<Map<String,Object>> deleteUserGroup(
//			@RequestHeader(value="Authorization", required=false) String _auth,
//			@RequestParam("systemId") Integer _systemId) throws Exception 
//	{
//		Map<String,Object> retData = null;
//		
//    	try {
////    		int tokenStat = ServiceUtil.checkAuth(_auth);
//    		int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_DELETE_USER_GROUPS, -1);
//    		if(tokenStat != StandardConstant.TOKEN_VALID) {
//    			String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
//				retData = svcUtil.returnErrorData(tokenStat, errMessage);
//				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
//    		}
//            UserGroup data = userService.deleteUserGroup(_systemId);
//            retData = svcUtil.returnSuccessfulData(new UserGroupDTO(data), 1, 1);
//    	}
//    	catch(Exception e) {
//			retData = svcUtil.handleException(e);
//    	}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
    
	@RequestMapping(value = "customers", method = RequestMethod.GET,
			produces = "application/json"
	)
	public ResponseEntity<Map<String,Object>> getCustomers(
		@RequestHeader(value="Authorization", required = false) String _auth,
		@RequestParam(value="systemId", required = false) Long _systemId,
		@RequestParam(value="email", required = false) String _email,
//		@RequestParam(value="groupId", required = false) Integer _groupId,
		Pageable _page
		) throws Exception 
	{
		Map<String,Object> retData = null;

		try {
			Object mainData = null;
            Customer cust = null;
            Page<Customer> pageCust = null;
            int totalPages = 0;
			long totalItems = 0;
			
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_CUSTOMERS, _systemId != null ? _systemId : -1l);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_systemId = ti.id;
			            
    		if (_systemId != null)
    			cust = userService.findCustomer(_systemId);
    		else if(_email != null) {
				cust = userService.findCustomerByEmail(_email);
    		}
    		else {
    			pageCust = userService.findAllCustomers(_page);
    		}
//    		else {
//    			pageCust = userService.findCustomerByGroupId(_groupId, _page);
//    		}
    		
    		if (pageCust != null && pageCust.getNumberOfElements() > 0) {
    			List<Customer> customers = pageCust.getContent();
    			List<CustomerDTO> customersData = new LinkedList<>();
    			for (Customer c : customers) {
    				customersData.add(new CustomerDTO(c));
    			}
    			mainData = customersData;
    			totalPages = pageCust.getTotalPages();
    			totalItems = pageCust.getTotalElements();
    		}
    		else if (cust != null) {
    			mainData = new CustomerDTO(cust);
    			totalPages = 1;
    			totalItems = 1;
    		}
    		
    		retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch (Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
	
	@RequestMapping(value = "customers", method = RequestMethod.GET,
			produces = "application/json", params="criteria"
	)
    public ResponseEntity<Map<String,Object>> getCustomers(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="criteria") List<String> _filters,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
	
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_CUSTOMERS, -1);
			retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
			Object mainData = null;
    		Page<Customer> pageCustomer = null ;
			int totalPages = 0;
			long totalItems = 0;
			Filters filters =  svcUtil.convertToFilters(_filters, Customer.class);			
			pageCustomer = userService.findCustomers(filters, _page);
			if(pageCustomer != null && pageCustomer.getNumberOfElements() > 0) {
            	totalPages = pageCustomer.getTotalPages();
            	totalItems = pageCustomer.getTotalElements();
            	List<Customer> customers = pageCustomer.getContent();
            	
            	//authorization
            	if(ti.access == ServiceUtil.ACCESS_LIMITED) {
	            	for(Customer cust : customers) {
	            		if(cust.getSystemId() != ti.id)
	            			return new ResponseEntity<Map<String,Object>>(svcUtil.returnErrorData(ServiceUtil.ERROR_NOT_PERMITTED),null, HttpStatus.OK);
	            	}
            	}
            	
            	List<CustomerDTO> listCustDTO = new LinkedList<>();
            	for(Customer c : customers) {
            		listCustDTO.add(new CustomerDTO(c));
            	}
            	mainData = listCustDTO;
        	}
            
            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
	@RequestMapping(value = "customers", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> addCustomer(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody CustomerDTO _dto) throws Exception 
	{
		Map<String,Object> retData = null;
		
        try {
        	TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_CUSTOMERS, -1l);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
//            Customer data = userService.addCustomer(_dto.getData());    
            Customer data = regSvc.registerCustomer(_dto.getData());            
            retData = svcUtil.returnSuccessfulData(new CustomerDTO(data), 1, 1);
        }
        catch (Exception e) {
    		retData = svcUtil.handleException(e);
        }
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
	@RequestMapping(value = "customers", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editCustomer(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody CustomerDTO _dto) throws Exception 
	{
		Map<String,Object> retData = null;
		
        try {
        	TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_CUSTOMERS, _dto.getSystemId() != null ? _dto.getSystemId() : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_dto.setSystemId(ti.id);
    		
            Customer data = userService.editCustomer(_dto.getData(), true);            
            retData = svcUtil.returnSuccessfulData(new CustomerDTO(data), 1, 1);
        }
        catch (Exception e) {
    		retData = svcUtil.handleException(e);
        }
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }

	@RequestMapping(value = "customers", method = RequestMethod.DELETE,
			produces = "application/json"
	)
	public ResponseEntity<Map<String,Object>> deleteCustomer(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="systemId", required=false) Long _systemId) throws Exception 
	{
		Map<String,Object> retData = null;
		Customer cust = userService.findCustomer(_systemId);
		
    	try {
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_CUSTOMERS, _systemId != null ? _systemId : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_systemId = ti.id;
    		
    		ResponseEntity<Map<String,Object>> errorRetval = svcUtil.checkAuth(_auth, UserCredential.TASK_DELETE_CUSTOMERS, _systemId);
			if(errorRetval != null)
				return errorRetval;
            Customer data = userService.deleteCustomer(_systemId);
            retData = svcUtil.returnSuccessfulData(new CustomerDTO(data), 1, 1);
    	}
    	catch (Exception e) {
    		retData = svcUtil.handleException(e);
    	}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "salesofficers", method = RequestMethod.GET,
			produces = "application/json"
	)
	public ResponseEntity<Map<String,Object>> getSalesOfficers(
		@RequestHeader(value="Authorization", required = false) String _auth,
		@RequestParam(value="systemId", required = false) Long _systemId,
		@RequestParam(value="email", required = false) String _email,
		Pageable _page
		) throws Exception 
	{
		Map<String,Object> retData = null;

		try { 
    		
			Object mainData = null;
            SalesOfficer so = null;
            Page<SalesOfficer> pageSO = null;
            int totalPages = 0;
			long totalItems = 0;
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_SALES_OFFICERS, _systemId != null ? _systemId : -1l);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_systemId = ti.id;
			
    		if (_systemId != null) 
    			so = userService.findSalesOfficer(_systemId);
    		else if (_email != null) {
    			so = userService.findSalesOfficerByEmail(_email);
    		}
    		else {
    			pageSO = userService.findAllSalesOfficers(_page);
    		}
    		
    		if (pageSO != null && pageSO.getNumberOfElements() > 0) {
    			List<SalesOfficer> officers = pageSO.getContent();
    			List<SalesOfficerDTO> officersData = new LinkedList<>();
    			for (SalesOfficer s : officers) {
    				officersData.add(new SalesOfficerDTO(s));
    			}
    			mainData = officersData;
    			totalPages = pageSO.getTotalPages();
    			totalItems = pageSO.getTotalElements();
    		}
    		else if (so != null) {
    			mainData = new SalesOfficerDTO(so);
    			totalPages = 1;
    			totalItems = 1;
    		}
    		
    		retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch (Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
	
	@RequestMapping(value = "salesofficers", method = RequestMethod.GET,
			produces = "application/json", params="criteria"
	)
    public ResponseEntity<Map<String,Object>> getSalesOfficers(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="criteria") List<String> _filters,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
	
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_SALES_OFFICERS, -1);
			retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);  
    		
			Object mainData = null;
    		Page<SalesOfficer> pageSO = null ;
			int totalPages = 0;
			long totalItems = 0;
			Filters filters =  svcUtil.convertToFilters(_filters, SalesOfficer.class);			
			pageSO = userService.findSalesOfficers(filters, _page);
			if(pageSO != null && pageSO.getNumberOfElements() > 0) {
            	totalPages = pageSO.getTotalPages();
            	totalItems = pageSO.getTotalElements();
            	List<SalesOfficer> officers = pageSO.getContent();
            	
            	//authorization
            	for(SalesOfficer s : officers) {
            		if(ti.access == ServiceUtil.ACCESS_LIMITED && s.getSystemId() != ti.id)
            			return new ResponseEntity<Map<String,Object>>(svcUtil.returnErrorData(ServiceUtil.ERROR_NOT_PERMITTED),null, HttpStatus.OK);
            	}
            	
            	List<SalesOfficerDTO> listSODTO = new LinkedList<>();
            	for(SalesOfficer so : officers) {
            		listSODTO.add(new SalesOfficerDTO(so));
            	}
            	mainData = listSODTO;
        	}
            
            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
		
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
	@RequestMapping(value = "salesofficers", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> addSalesOfficer(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody SalesOfficerDTO _dto) throws Exception
	{
		Map<String,Object> retData = null;
		
        try {
        	TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_SALES_OFFICERS, -1l);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
            SalesOfficer data = userService.addSalesOfficer(_dto.getData());            
            retData = svcUtil.returnSuccessfulData(new SalesOfficerDTO(data), 1, 1);
        }
        catch(Exception e) {
    		retData = svcUtil.handleException(e);
        }
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
	@RequestMapping(value = "salesofficers", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editSalesOfficer(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody SalesOfficerDTO _dto) throws Exception
	{
		Map<String,Object> retData = null;
		
        try {
        	TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_SALES_OFFICERS, _dto.getSystemId() != null ? _dto.getSystemId() : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_dto.setSystemId(ti.id);
    		
            SalesOfficer data = userService.editSalesOfficer(_dto.getData(), true);            
            retData = svcUtil.returnSuccessfulData(new SalesOfficerDTO(data), 1, 1);
        }
        catch(Exception e) {
    		retData = svcUtil.handleException(e);
        }
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }

	@RequestMapping(value = "salesofficers", method = RequestMethod.DELETE,
			produces = "application/json"
	)
	public ResponseEntity<Map<String,Object>> deleteSalesOfficer(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="systemId", required=false) Long _systemId) throws Exception 
	{
		Map<String,Object> retData = null;
		SalesOfficer so = userService.findSalesOfficer(_systemId);
    	try {
    		
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_SALES_OFFICERS, _systemId != null ? _systemId : -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED) 
    			_systemId = ti.id;
    		
            SalesOfficer data = userService.deleteSalesOfficer(_systemId);
            retData = svcUtil.returnSuccessfulData(new SalesOfficerDTO(data), 1, 1);
    	}
    	catch(Exception e) {
    		retData = svcUtil.handleException(e);
    	}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
	
	
	
	@RequestMapping(value = "users", method = RequestMethod.GET,
			produces = "application/json"
	)
	public ResponseEntity<Map<String,Object>> getUsers(
		@RequestHeader(value="Authorization", required=false) String _auth,
		@RequestParam(value="sessionString", required=false) String _sessionString,
		@RequestParam(value="systemId", required = false) Integer _systemId,
		@RequestParam(value="name", required = false) String _name,
//		@RequestParam(value="groupId", required = false) Integer _groupId,
		Pageable _page) throws Exception 
	{
		Map<String,Object> retData = null;
		
    	try {
    		
    		Contact c = userService.findContactByCredId(_systemId);
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_USERS, 
    				_systemId == null ? -1 : c != null ? c.getSystemId() : -2);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED) {
				c = userService.findContact(ti.id);
    			_systemId = c.getUserCredential().getSystemId();
    		}
    		
    		Object mainData = null;
    		UserCredential usr = null;
			Page<UserCredential> pageUsers = null;
			int totalPages = 0;
			long totalItems = 0;
    		
    		if(_systemId != null) {
    			usr = userService.findUser(_systemId);
    		}
    		else {
    			if(_name != null) {
        			usr = userService.findUserByName( _name);
        		}
//    			else if(_groupId != null) {
//        			pageUsers = userService.findUserByGroupId(_groupId, _page);
//        		}
        		else {
        			pageUsers = userService.findAllUsers(_page);
        		}
    		}
    		
    		if(pageUsers != null && pageUsers.getNumberOfElements() > 0) {
    			List<UserCredential> users = pageUsers.getContent();
    			List<UserCredentialDTO> usersData = new LinkedList<>();
    			for(UserCredential u : users) {
    				usersData.add(new UserCredentialDTO(u));
    			}
    			mainData = usersData;
    			totalPages = pageUsers.getTotalPages();
    			totalItems = pageUsers.getTotalElements();
    		}
    		else if (usr != null) {
    			mainData = new UserCredentialDTO(usr);
    			totalPages = 1;
    			totalItems = 1;
    		}
    		
    		retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
    	}
    	catch(Exception e) {
    		retData = svcUtil.handleException(e);
    	}
        
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
	@RequestMapping(value = "users", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> addUser(
			@RequestHeader(value="Authorization", required=false) String _auth,
    		@RequestBody UserCredentialDTO _dto) throws Exception 
	{
		Map<String,Object> retData = null;
    	try {
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_USERS, -1l);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
            UserCredential data = userService.addUser (_dto.getData());
            retData = svcUtil.returnSuccessfulData(new UserCredentialDTO(data), 1, 1);
            
            return new ResponseEntity<Map<String,Object>> (retData, null, HttpStatus.CREATED);
    	}
    	catch (Exception e) {
    		retData = svcUtil.handleException(e);
    		return new ResponseEntity<Map<String,Object>> (retData, null, HttpStatus.OK);
    	}
    }
    
	@RequestMapping(value = "users", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editUser(
			@RequestHeader(value="Authorization", required=false) String _auth,
    		@RequestBody UserCredentialDTO _dto) throws Exception 
	{
		Map<String,Object> retData = null;
    	try {
    		Contact c = userService.findContactByCredId(_dto.getSystemId());
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_USERS, 
    				_dto.getSystemId() == null ? -1 : c != null ? c.getSystemId() : -2);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED) {
				c = userService.findContact(ti.id);
    			_dto.setSystemId(c.getUserCredential().getSystemId());
    		}
    		
            UserCredential data = userService.editUser (_dto.getData(), _dto.getImgData(), sCtx.getInitParameter ("imagePath"), true);
            retData = svcUtil.returnSuccessfulData(new UserCredentialDTO(data), 1, 1);
            
            return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    	}
    	catch(Exception e) {
    		retData = svcUtil.handleException(e);
    		return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    	}
    }    
    
	@RequestMapping(value = "users", method = RequestMethod.DELETE,
			produces = "application/json"
	)
	public ResponseEntity<Map<String,Object>> deleteUser(
		@RequestHeader(value="Authorization", required=false) String _auth,
		@RequestParam(value="systemId", required=false) Integer _systemId) throws Exception 
	{
		Map<String,Object> retData = null;
		try {
			Contact c = userService.findContactByCredId(_systemId);
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_USERS, 
    				_systemId == null ? -1 : c != null ? c.getSystemId() : -2);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED) {
				c = userService.findContact(ti.id);
    			_systemId = c.getUserCredential().getSystemId() ;
    		}
    		
		    UserCredential data = userService.deleteUser(_systemId);
            retData = svcUtil.returnSuccessfulData(new UserCredentialDTO(data), 1, 1);
            
            return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
		}
		catch(Exception e) {
    		retData = svcUtil.handleException(e);
    		return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
		}
    }

    @RequestMapping(value = "users/images", method = RequestMethod.GET,
    		produces = {"image/jpg", "image/png"}
	)
    public ResponseEntity<byte[]> getImage(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam("sessionString")String _sessionString,
    		@RequestParam("systemId")Integer _systemId,
    		@RequestParam("image") String _image) throws Exception
    { 
//    	int tokenStat = ServiceUtil.checkAuth(_auth);
//		if(tokenStat != StandardConstant.TOKEN_VALID) {
//			return new ResponseEntity<byte[]>(null, null, HttpStatus.UNAUTHORIZED); 
//		}
		
        UserCredential user = null;
        if (_systemId != null) {
            user = userService.findUser(_systemId);
        }
//        else if (_sessionString != null) {
//            UserSession us = usc.getSession (_sessionString, false);
//            if (us != null) {
//                UserCredential u = null;
//                user = (UserCredential)us.getObject ("user");
//            }
//        }
        
        if (user == null) {
            System.out.println ("user can't be found id: " + _systemId);
            return null;
        }
        
        String path = sCtx.getInitParameter ("imagePath") + "users/" +_systemId + "/" + _image;
        File file = new File(path);
        if (file.exists ()) { 
            FileInputStream fis = new FileInputStream(file);
            long length = file.length ();
            byte[] filecontent = new byte[(int)length];
            fis.read(filecontent,0,(int)length);
            
            return new ResponseEntity<byte[]>(filecontent, null, HttpStatus.OK);
            
        }
        else {
            System.out.println ("File doesn't exist, tid: " + _systemId + " image: " + _image);
            return new ResponseEntity<byte[]>(null, null, HttpStatus.NO_CONTENT);
        }
    }
}


