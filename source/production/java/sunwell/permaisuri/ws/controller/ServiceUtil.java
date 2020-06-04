package sunwell.permaisuri.ws.controller;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Util.java
 *
 * Created on Dec 6, 2017, 2:39:45 PM
 */


import aegwyn.core.web.model.UserSession;



import aegwyn.core.web.model.UserSessionContainer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import sunwell.permaisuri.bus.dto.StandardDTO;
import sunwell.permaisuri.bus.exception.OperationException;
import sunwell.permaisuri.bus.service.Filters;
import sunwell.permaisuri.bus.service.Filters.Filter;
import sunwell.permaisuri.bus.service.StandardConstant;
import sunwell.permaisuri.bus.service.UserCredService;
import sunwell.permaisuri.bus.service.UserCredSvc;
import sunwell.permaisuri.bus.util.Util;
import sunwell.permaisuri.core.entity.contact.Contact;
import sunwell.permaisuri.core.entity.cred.UserCredential;
import sunwell.permaisuri.ws.controller.ServiceUtil.TokenInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


/**
 *
 * @author Benny
 */
@Service
public class ServiceUtil 
{    
	private static String SECRET_CODE = "sunwellsystem";
	
	public static final String SYMBOL_EQUAL = "_eq_";
	public static final String SYMBOL_GREATER_THAN = "_gt_";
	public static final String SYMBOL_LESS_THAN = "_lt_";	
	public static final String SYMBOL_EQUAL_GREATER_THAN = "_egt_";	
	public static final String SYMBOL_EQUAL_LESS_THAN = "_elt_"; 
	public static final String SYMBOL_LIKE = "_lk_"; 
	
	public static final int OWNER_SELF = 0;
	public static final int OWNER_OTHER = 1;
	public static final int OWNER_UNSPECIFIED = 2;
	
	public static final int ACCESS_ALLOW = 0;
	public static final int ACCESS_DENY = 1;
	public static final int ACCESS_LIMITED = 2;
	
	public static final int TOKEN_VALID = 1000;
	public static final int ERROR_INTERNAL_SERVER = 1001;
	public static final int ERROR_BAD_REQUEST = 1002;
	public static final int ERROR_NOT_PERMITTED = 1003;
	public static final int ERROR_NOT_ACTIVE = 1004;
	public static final int ERROR_TOKEN_INVALID = 1005;
	public static final int ERROR_TOKEN_EXPIRED = 1006;
	public static final int ERROR_MAIL_FAIL = 1007;
//	public static final int ERROR_NO_LOGIN_SESSION = 2;
	

	public static final Map<String,String> SYMBOL_MAP;
	
	public static final String PWD = "sunwellsystem";
	
	
	
	public static final String[] SYMBOLS = {SYMBOL_EQUAL, 
											SYMBOL_GREATER_THAN,
											SYMBOL_LESS_THAN,
											SYMBOL_EQUAL_GREATER_THAN,
											SYMBOL_EQUAL_LESS_THAN,
											SYMBOL_LIKE};
	
	static {
		SYMBOL_MAP = new HashMap<>();
		SYMBOL_MAP.put(SYMBOL_EQUAL, Filters.COMPARISON_EQUAL);
		SYMBOL_MAP.put(SYMBOL_GREATER_THAN, Filters.COMPARISON_GREATER_THAN);
		SYMBOL_MAP.put(SYMBOL_LESS_THAN, Filters.COMPARISON_LESS_THAN);
		SYMBOL_MAP.put(SYMBOL_EQUAL_GREATER_THAN, Filters.COMPARISON_EQUAL_GREATER_THAN);
		SYMBOL_MAP.put(SYMBOL_EQUAL_LESS_THAN, Filters.COMPARISON_EQUAL_LESS_THAN);
		SYMBOL_MAP.put(SYMBOL_LIKE, Filters.COMPARISON_LIKE);
	}
	
	
	static boolean DEV = true;

//	@Autowired
//    InventoryFacade stockFacade;
//    
//    @Autowired
//    ProductFacade productFacade;
//    
//    @Autowired
//    GenericFacade genericFacade;
    
    @Inject
    MessageSource messageSource;
    
//    @Inject
//    UserSessionContainer usc;
    
    @Inject
    HttpServletRequest request ;
    
    @Autowired
    UserCredService ucSvc;
    
    @Autowired
    JavaMailSender mailSender;
  
    
    private Map<Integer, String> errorMap = new HashMap<>();
    
    
    public ServiceUtil() {
    		errorMap.put(StandardConstant.NO_ERROR, "{no_error}");
    		errorMap.put(ERROR_INTERNAL_SERVER, "error_internal_server");
    		errorMap.put(ERROR_NOT_ACTIVE, "error_not_active");
    		errorMap.put(ERROR_BAD_REQUEST, "error_bad_request");
    		errorMap.put(ERROR_NOT_PERMITTED, "error_not_permitted");
    		errorMap.put(ERROR_TOKEN_INVALID, "error_token_invalid");
    		errorMap.put(ERROR_TOKEN_EXPIRED, "error_token_expired");
    		errorMap.put(ERROR_MAIL_FAIL, "error_mail_fail");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_USER_GROUP, "error_cant_find_user_group");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_USER, "error_cant_find_user");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_PRODUCT, "error_cant_find_product");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_INVOICE, "error_cant_find_invoice");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_CUSTOMER, "error_cant_find_customer");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_PAYMENT, "error_cant_find_payment");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_CATEGORY, "error_cant_find_category");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_MERK, "error_cant_find_merk");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_METRIC, "error_cant_find_metric");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_SHIPMENT, "error_cant_find_shipment");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_SALES_ORDER, "error_cant_find_so");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_STOCK_RESERVATION, "error_cant_find_sr");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_DELIVERY_ORDER, "error_cant_find_do");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_SALES_RETURN, "error_cant_find_sales_return");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_CART_DETAIL, "error_cant_find_cart_detail");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_SALES_OFFICER, "error_cant_find_sales_officer");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_STOCK, "error_cant_find_stock");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_STOCK, "error_cant_find_stock");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_PRODUCT_IMAGE, "error_cant_find_product_image");
    		errorMap.put(StandardConstant.ERROR_CANT_FIND_PAYMENT_IMAGE, "error_cant_find_payment_image");
    		errorMap.put(StandardConstant.ERROR_NOT_ENOUGH_STOCK, "error_not_enough_sell_price_level");
    		errorMap.put(StandardConstant.ERROR_NO_CUSTOMER, "error_no_customer");
    		errorMap.put(StandardConstant.ERROR_NO_ITEM_IN_THE_CART, "error_no_item_in_the_cart");
    		errorMap.put(StandardConstant.ERROR_NOT_ENOUGH_STOCK, "error_not_enough_stock");
    		errorMap.put(StandardConstant.ERROR_NOT_ENOUGH_STOCK_FOR_ITEM, "error_not_enough_stock_for_item");
    		errorMap.put(StandardConstant.ERROR_USER_IS_NOT_ENABLED, "error_user_is_not_enabled");
//    		errorMap.put(StandardConstant.ERROR_CANT_FIND_SPECIFIED_OBJECT, "error_cant_find_object");
//    		errorMap.put(StandardConstant.ERROR_NO_LOGIN_SESSION, "error_no_login_session");
//    		errorMap.put(StandardConstant.ERROR_CANT_FIND_TENANT, "error_cant_find_tenant");
    }
    
//    public boolean validateLogin(String _sessionString) {
//        return getUser (_sessionString) != null ? true : false;
//    }
    
//    public UserSession getSession(String _sessionString, boolean _createNew) {
//        return usc.getSession (_sessionString, _createNew);
//    }
//    
//    public UserSessionContainer getSessionContainer() {
//        return usc;
//    }
//    
//    public UserCredential getUser(String _sessionString) {
//        UserSession session = usc.getSession (_sessionString, false);
//        if(session == null)
//            return null;
//        
//        UserCredential usr = (UserCredential)session.getObject ("user");
//        return usr;
//    }
    
    public static void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) 
    {
        try {
            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];
            
            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
            	out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeToFile(byte[] _bytes, String uploadedFileLocation) 
    {
        try {
            OutputStream out = new FileOutputStream(new File(
                            uploadedFileLocation));
            int read = 0;

            out = new FileOutputStream(new File(uploadedFileLocation));
            out.write (_bytes);
            out.flush();
            out.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getErrorMessageFromCode(Integer _code) 
    {
//    	String key = errorMap.get(_code);
//    	return messageSource.getMessage(key, null, request.getLocale());
    	return getErrorMessageFromCode(_code, null);
    }
    
    public String getErrorMessageFromCode(Integer _code, Object[] _args) 
    {
    	String key = errorMap.get(_code);
    	return messageSource.getMessage(key, (_args != null && _args.length > 0) ? _args : null, request.getLocale());
    }
    
    public  String getErrorMessageFromException(ConstraintViolationException _e) 
    {
    	String messages = "";
		for (ConstraintViolation v : _e.getConstraintViolations()) {
			String msg = v.getMessage();
			messages += msg + "\n";
		}
		messages = messages.substring(0, messages.length() - 1);
    	System.out.println("Error messages: " + messages);
		
    	return messages;
    }
    
    public void handleException(StandardDTO _retval, Exception _e) throws Exception 
    {
    	System.out.println("Error: " + _e.getMessage() + " class: " + _e.getClass().toString()
	    			+ " IS instanceof OperationException: " + (_e instanceof OperationException));
	    _e.printStackTrace();
	    	
	    if (_e instanceof OperationException) {
	    	OperationException ex = (OperationException) _e;
	    	_retval.setErrorCode(ex.getErrorCode());
	    	_retval.setErrorMessage(getErrorMessageFromCode(ex.getErrorCode()));
	    }
	    else if (_e instanceof ConstraintViolationException) {
	    	_retval.setErrorCode(StandardConstant.ERROR_CONSTRAINT);
	    	_retval.setErrorMessage(getErrorMessageFromException((ConstraintViolationException)_e));
    	}
    	else {
    		throw _e;
    	}
    }
    
    public String getErrorMessageFromException(Exception _e) {
    	System.out.println("Error: " + _e.getMessage() + " class: " + _e.getClass().toString()
    			+ " IS instanceof OperationException: " + (_e instanceof OperationException));
    _e.printStackTrace();
    	
	    if (_e instanceof OperationException) {
	    	OperationException ex = (OperationException) _e;
	    	return getErrorMessageFromCode(ex.getErrorCode());
	    }
	    else if (_e instanceof ConstraintViolationException) {
	    	return getErrorMessageFromException((ConstraintViolationException)_e);
		}
		else {
			return _e.getMessage();
		}
    }
    
    public Map<String,Object> handleException(Exception _e) throws Exception
    {    	
    	Map<String,Object> data = null;
	    _e.printStackTrace();
	    if (_e instanceof OperationException) {
    		OperationException ex = (OperationException) _e;
    		int errCode = ex.getErrorCode();
    		String errMessage = getErrorMessageFromCode(ex.getErrorCode());
    		data = returnErrorData(errCode,  errMessage);
    	}
	    else if (_e instanceof ConstraintViolationException) {
//			_retval.setErrorCode(StandardConstant.ERROR_CONSTRAINT);
			String errMessage = getErrorMessageFromException((ConstraintViolationException)_e);
			data = returnErrorData(StandardConstant.ERROR_CONSTRAINT, errMessage);
		}
	    else if(_e instanceof MailException) {
	    	data = returnErrorData(ERROR_MAIL_FAIL);
	    }
		else {
			throw _e;
		}
	    return data;
	}
    
    public Map<String,Object> returnSuccessfulData(Object _object, int _totPages, long _totItems) 
    {
    	Map<String,Object> data = new HashMap<>();
    	data.put("status", "successful" );
    	data.put("totalPages", _totPages );
    	data.put("totalItems", _totItems);
    	data.put("data", _object);
//    	data.putAll(_addData);
    	return data;
    }
    
    public Map<String,Object> returnErrorData(int _errCode, String _msg)
    {
    	Map<String,Object> data = new HashMap<>();
    	data.put("status", "error" );
    	data.put("errorCode", _errCode);
    	data.put("errorMessage", _msg);
    	
    	return data;
    }
    
    public Map<String,Object> returnErrorData(int _errCode, Object[] _args)
    {
    	Map<String,Object> data = new HashMap<>();
    	data.put("status", "error" );
    	data.put("errorCode", _errCode);
    	data.put("errorMessage", getErrorMessageFromCode(_errCode, _args));
    	
    	return data;
    }
    
    public Map<String,Object> returnErrorData(int _errCode)
    {
    	Map<String,Object> data = new HashMap<>();
    	data.put("status", "error" );
    	data.put("errorCode", _errCode);
    	data.put("errorMessage", getErrorMessageFromCode(_errCode));
    	
    	return data;
    }
    
//    public Map<String, Object> makeOnHandStockFilter(OnHandStockDTO _dto) {
//        ObjectMapper oMapper = new ObjectMapper();
//        oMapper.setSerializationInclusion (JsonInclude.Include.NON_NULL);
//        Map<String, Object> map = oMapper.convertValue(_dto, Map.class);
//        System.out.println("MAP: " + map);
//        
//        Map<String, Object> prodDTO = (Map<String, Object>)map.get ("product");
//        if(prodDTO != null && prodDTO.get ("systemId") != null) {
//            Product prod = genericFacade.findById (prodDTO.get ("systemId"), Product.class);
//            map.put ("product", prod);
//        }
//        else
//            map.remove ("product");
//        
//        Map<String, Object> wDTO = (Map<String, Object>)map.get ("warehouse");
//        if(wDTO != null && wDTO.get ("systemId") != null) {
//            Warehouse wrh = genericFacade.findById (wDTO.get ("systemId"), Warehouse.class);
//            map.put ("warehouse", wrh);
//        }
//        else
//            map.remove ("warehouse");
//        
//        System.out.println("MAP: " + map);
//        
//        return map;
//    }
    
    public String getToken(String _userName) throws Exception
    {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 1);
		String jwt = Jwts.builder().setSubject("login")
									.setExpiration(cal.getTime())
									.claim("name", _userName)
									.claim("scope", "Test Scope")
									.signWith(SignatureAlgorithm.HS256, SECRET_CODE.getBytes("UTF-8"))
									.compact();
		
		System.out.println("JWT: " + jwt);
		return jwt;
    }
    
//    public String getToken(UserCredential _uc) throws Exception
//    {
//		return getToken(_uc.getSystemId(), _uc.getUserName(), _uc.getType());
//    }
    
    public String getToken(long _id, String _userName, String _type) throws Exception
    {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 1);
		String jwt = Jwts.builder().setSubject("login")
									.setExpiration(cal.getTime())
									.claim("id", _id)
									.claim("name", _userName)
									.claim("type", _type)
									.signWith(SignatureAlgorithm.HS256, SECRET_CODE.getBytes("UTF-8"))
									.compact();
		
		System.out.println("JWT: " + jwt);
		return jwt;
    }
    
    public String getToken(Contact _c) throws Exception
    {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 1);
		String jwt = Jwts.builder().setSubject("login")
									.setExpiration(cal.getTime())
									.claim("id", _c.getSystemId())
									.claim("userCredentialId", _c.getUserCredential().getSystemId())
									.claim("name",_c.getUserCredential().getUserName())
									.claim("type", _c.getType())
									.signWith(SignatureAlgorithm.HS256, SECRET_CODE.getBytes("UTF-8"))
									.compact();
		
		System.out.println("JWT: " + jwt);
		return jwt;
    }
    
//    public static int checkAuth(String _auth) throws Exception {
//		try {
//			
////			if(true)
////				return StandardConstant.TOKEN_VALID;
//						
//			if (_auth == null || _auth.length() <= 0)
//				return StandardConstant.ERROR_TOKEN_INVALID;
//
//			String[] auth = _auth.split(" ");
//
//			if (auth.length < 2)
//				return StandardConstant.ERROR_TOKEN_INVALID;
//
//			Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_CODE.getBytes("UTF-8")).parseClaimsJws(auth[1]);
//			String name = (String) claims.getBody().get("name");
//			String scope = (String) claims.getBody().get("scope");
//			System.out.println("NAME: " + name);
//			return StandardConstant.TOKEN_VALID;
//		}
//		catch (ExpiredJwtException _e) {
//			System.out.println("Error: " + _e.getMessage());
//			_e.printStackTrace();
//			return StandardConstant.ERROR_TOKEN_EXPIRED;
//		}
//		catch (Exception _e) {
//			System.out.println("Error: " + _e.getMessage());
//			_e.printStackTrace();
//			return StandardConstant.ERROR_TOKEN_INVALID;
//		}
//    }
    
//    public TokenInfo checkAuth(String _auth) throws Exception
//    {
//		try {
////			if(true)
////				return StandardConstant.TOKEN_VALID;
//			
//			if (_auth == null || _auth.length() <= 0)
//				return new TokenInfo (-1, null, null, ERROR_TOKEN_INVALID, ACCESS_DENY);
//
//			String[] auth = _auth.split(" ");
//
//			if (auth.length < 2)
//				return new TokenInfo (-1, null, null, ERROR_TOKEN_INVALID, ACCESS_DENY);
//
//			Jws<Claims> claims = Jwts.parser().setSigningKey (SECRET_CODE.getBytes("UTF-8")).parseClaimsJws(auth[1]);
//			int id = (Integer) claims.getBody().get("id");
//			String name = (String) claims.getBody().get("name");
//			String type = (String) claims.getBody().get("type");
//			System.out.println("NAME: " + name);
//			
//			return new TokenInfo(id, name, type, TOKEN_VALID, ACCESS_ALLOW);
//		}
//		catch (ExpiredJwtException _e) {
//			System.out.println("Error: " + _e.getMessage());
//			_e.printStackTrace();
//			return new TokenInfo(-1, null, null, ERROR_TOKEN_EXPIRED, ACCESS_DENY);
//		}
//		catch (Exception _e) {
//			System.out.println("Error: " + _e.getMessage());
//			_e.printStackTrace();
//			return new TokenInfo(-1, null, null, ERROR_TOKEN_INVALID, ACCESS_DENY);
//		}
//    }
    
    public TokenInfo authenticate (String _auth, String _task, long _id) throws Exception 
    {
		try {	
			
//			if(true) 
//				return new TokenInfo(1,null,null,TOKEN_VALID,ACCESS_ALLOW);
			
			TokenInfo ti = getTokenInfo(_auth);
						
			int ownerStat = _id == -1 ? OWNER_UNSPECIFIED : ti.id == _id ? OWNER_SELF : OWNER_OTHER;
			System.out.println(" Type: " + ti.type + " task: " + _task + " owner: " + ownerStat);
			int accessStat = isTypeAllowed(ti.type, _task, ownerStat);
			ti.access = accessStat;
			return ti;
		}
		catch (ExpiredJwtException _e) {
			System.out.println("Error: " + _e.getMessage());
			_e.printStackTrace();
			return new TokenInfo(-1, null, null, ERROR_TOKEN_EXPIRED, ACCESS_DENY);
		}
		catch (Exception _e) {
			System.out.println("Error: " + _e.getMessage());
			_e.printStackTrace();
			return new TokenInfo(-1, null, null, ERROR_TOKEN_INVALID, ACCESS_DENY);
		}
    }
    
    public TokenInfo getTokenInfo (String _auth) throws Exception 
    {		
    	
		if (_auth == null || _auth.length() <= 0)
			return new TokenInfo(-1, null, null, ERROR_TOKEN_INVALID, -1);

		String[] auth = _auth.split(" ");

		if (auth.length < 2)
			return new TokenInfo(-1, null, null, ERROR_TOKEN_INVALID, -1);

		Jws<Claims> claims = Jwts.parser().setSigningKey (SECRET_CODE.getBytes("UTF-8")).parseClaimsJws(auth[1]);
		long id = (Integer) claims.getBody().get ("id");
		String name = (String) claims.getBody().get("name");
		String type = (String) claims.getBody().get("type");
		return new TokenInfo(id, name, type, TOKEN_VALID, -1);
    }
    
//    public int checkAuth (String _auth, String _task, int _id) throws Exception 
//    {
//		try {	
//			
//			if(true)
//				return TOKEN_VALID;
//			
//			if (_auth == null || _auth.length() <= 0)
//				return ERROR_TOKEN_INVALID;
//
//			String[] auth = _auth.split(" ");
//
//			if (auth.length < 2)
//				return ERROR_TOKEN_INVALID;
//
//			Jws<Claims> claims = Jwts.parser().setSigningKey (SECRET_CODE.getBytes("UTF-8")).parseClaimsJws(auth[1]);
//			int id = (Integer) claims.getBody().get ("userCredentialId");
//			String name = (String) claims.getBody().get("name");
//			String type = (String) claims.getBody().get("type");
////			UserCredential uc = ucSvc.findUserByName(name);
////			if(!uc.equals(_uc))
////				return StandardConstant.ERROR_TOKEN_INVALID;
//			
//			boolean self = id == _id ;
//			if (!isTypeAllowed(type, _task, self))
//				return ERROR_TOKEN_INVALID;
//			
//			return TOKEN_VALID;
//						
//		}
//		catch (ExpiredJwtException _e) {
//			System.out.println("Error: " + _e.getMessage());
//			_e.printStackTrace();
//			return ERROR_TOKEN_EXPIRED;
//		}
//		catch (Exception _e) {
//			System.out.println("Error: " + _e.getMessage());
//			_e.printStackTrace();
//			return ERROR_TOKEN_INVALID;
//		}
//    }
//    
    public boolean isTypeAllowedForLimitedAccess(String _type, String _task) {
    	switch (_type) 
		{
			case UserCredential.TYPE_ADMIN:
				return true;
			case UserCredential.TYPE_SALES_OFFICER:
				switch (_task) {
					default :
						return false;
				}
			case UserCredential.TYPE_DRIVER:
				switch (_task) {
					default :
						return false;
				}
			case UserCredential.TYPE_CUSTOMER:
				switch (_task) {
					case UserCredential.TASK_VIEW_CUSTOMERS:
					case UserCredential.TASK_UPDATE_CUSTOMERS:
					case UserCredential.TASK_DELETE_CUSTOMERS:
					case UserCredential.TASK_VIEW_CART_DETAILS:
					case UserCredential.TASK_CREATE_CART_DETAILS:
					case UserCredential.TASK_UPDATE_CART_DETAILS:
					case UserCredential.TASK_DELETE_CART_DETAILS:
					case UserCredential.TASK_VIEW_SALES_ORDERS:
					case UserCredential.TASK_CREATE_SALES_ORDERS:
					case UserCredential.TASK_UPDATE_SALES_ORDERS:
					case UserCredential.TASK_DELETE_SALES_ORDERS:
					case UserCredential.TASK_VIEW_SALES_INVOICES:
					case UserCredential.TASK_CREATE_SALES_INVOICES:
					case UserCredential.TASK_UPDATE_SALES_INVOICES:
					case UserCredential.TASK_DELETE_SALES_INVOICES:
						return true;
					default:
						return false;
				}
			default:
				return false;
		}
    }
    
    public int isTypeAllowed (String _type, String _task, int _owner)
	{
    	switch (_owner) {
	    	case OWNER_SELF :
		    	switch (_type) 
				{
					case UserCredential.TYPE_ADMIN:
						return ACCESS_ALLOW;
					case UserCredential.TYPE_SALES_OFFICER:
						switch (_task) {
							default :
								return ACCESS_DENY;
						}
					case UserCredential.TYPE_DRIVER:
						switch (_task) {
							default :
								return ACCESS_DENY;
						}
					case UserCredential.TYPE_CUSTOMER:
						switch (_task) {
							case UserCredential.TASK_VIEW_CUSTOMERS:
							case UserCredential.TASK_UPDATE_CUSTOMERS:
							case UserCredential.TASK_DELETE_CUSTOMERS:
							case UserCredential.TASK_VIEW_CART_DETAILS:
							case UserCredential.TASK_CREATE_CART_DETAILS:
							case UserCredential.TASK_UPDATE_CART_DETAILS:
							case UserCredential.TASK_DELETE_CART_DETAILS:
							case UserCredential.TASK_VIEW_SALES_ORDERS:
							case UserCredential.TASK_CREATE_SALES_ORDERS:
							case UserCredential.TASK_UPDATE_SALES_ORDERS:
							case UserCredential.TASK_DELETE_SALES_ORDERS:
							case UserCredential.TASK_VIEW_SALES_INVOICES:
							case UserCredential.TASK_CREATE_SALES_INVOICES:
							case UserCredential.TASK_UPDATE_SALES_INVOICES:
							case UserCredential.TASK_DELETE_SALES_INVOICES:
							case UserCredential.TASK_CHECK_OUT:
								return ACCESS_ALLOW;
							default:
								return ACCESS_DENY;
						}
					default:
						return ACCESS_DENY;
				}
	    	case OWNER_OTHER :
		    	switch (_type) 
				{
					case UserCredential.TYPE_ADMIN:
						return ACCESS_ALLOW;
					case UserCredential.TYPE_SALES_OFFICER:
						switch (_task) {
							default :
								return ACCESS_DENY;
						}
					case UserCredential.TYPE_DRIVER:
						switch (_task) {
							default :
								return ACCESS_DENY;
						}
					case UserCredential.TYPE_CUSTOMER:
						switch (_task) {
							case UserCredential.TASK_VIEW_PRODUCTS:
							case UserCredential.TASK_VIEW_CATEGORIES:
							case UserCredential.TASK_VIEW_MERKS:
								return ACCESS_ALLOW;
							default:
								return ACCESS_DENY;
					}
					default:
						return ACCESS_DENY;
				}
	    	case OWNER_UNSPECIFIED :
	    		System.out.println("Unspec switched");
		    	switch (_type) 
				{
					case UserCredential.TYPE_ADMIN:
						return ACCESS_ALLOW;
					case UserCredential.TYPE_SALES_OFFICER:
						switch (_task) {
							default :
								return ACCESS_DENY;
						}
					case UserCredential.TYPE_DRIVER:
						switch (_task) {
							default :
								return ACCESS_DENY;
						}
					case UserCredential.TYPE_CUSTOMER:
						System.out.println("Cust switched");
						switch (_task) {
							case UserCredential.TASK_VIEW_CUSTOMERS:
							case UserCredential.TASK_UPDATE_CUSTOMERS:
							case UserCredential.TASK_DELETE_CUSTOMERS:
							case UserCredential.TASK_VIEW_CART_DETAILS:
							case UserCredential.TASK_CREATE_CART_DETAILS:
							case UserCredential.TASK_UPDATE_CART_DETAILS:
							case UserCredential.TASK_DELETE_CART_DETAILS:
							case UserCredential.TASK_VIEW_SALES_ORDERS:
							case UserCredential.TASK_CREATE_SALES_ORDERS:
							case UserCredential.TASK_UPDATE_SALES_ORDERS:
							case UserCredential.TASK_DELETE_SALES_ORDERS:
							case UserCredential.TASK_VIEW_SALES_INVOICES:
							case UserCredential.TASK_CREATE_SALES_INVOICES:
							case UserCredential.TASK_UPDATE_SALES_INVOICES:
							case UserCredential.TASK_DELETE_SALES_INVOICES:
							case UserCredential.TASK_CHECK_OUT:
								System.out.println("Limited switched");
								return ACCESS_LIMITED;
							case UserCredential.TASK_VIEW_PRODUCTS:
							case UserCredential.TASK_VIEW_CATEGORIES:
							case UserCredential.TASK_VIEW_STOCKS:
							case UserCredential.TASK_VIEW_MERKS:
							case UserCredential.TASK_VIEW_PAYMENT_IMAGE:
								return ACCESS_ALLOW;
							default:
								return ACCESS_DENY;
						}
					default:
						return ACCESS_DENY;
				}
		    	default :
		    		return ACCESS_DENY;
    	}
		
	}
    
	public boolean isTypeAllowed (String _type, String _task, boolean _self)
	{
		switch (_type) 
		{
			case UserCredential.TYPE_ADMIN:
				return true;
			case UserCredential.TYPE_SALES_OFFICER:
				switch (_task) {
	
				}
			case UserCredential.TYPE_DRIVER:
				switch (_task) {
	
				}
			case UserCredential.TYPE_CUSTOMER:
				if (_self) {
					switch (_task) {
						case UserCredential.TASK_VIEW_CUSTOMERS:
						case UserCredential.TASK_UPDATE_CUSTOMERS:
						case UserCredential.TASK_DELETE_CUSTOMERS:
						case UserCredential.TASK_VIEW_CART_DETAILS:
						case UserCredential.TASK_CREATE_CART_DETAILS:
						case UserCredential.TASK_UPDATE_CART_DETAILS:
						case UserCredential.TASK_DELETE_CART_DETAILS:
						case UserCredential.TASK_VIEW_SALES_ORDERS:
						case UserCredential.TASK_CREATE_SALES_ORDERS:
						case UserCredential.TASK_UPDATE_SALES_ORDERS:
						case UserCredential.TASK_DELETE_SALES_ORDERS:
						case UserCredential.TASK_VIEW_SALES_INVOICES:
						case UserCredential.TASK_CREATE_SALES_INVOICES:
						case UserCredential.TASK_UPDATE_SALES_INVOICES:
						case UserCredential.TASK_DELETE_SALES_INVOICES:
							return true;
						default:
							return false;
					}
				}
				else {
					switch (_task) {
						case UserCredential.TASK_VIEW_PRODUCTS:
						case UserCredential.TASK_VIEW_CATEGORIES:
							return true;
						default:
							return false;
					}
				}
			default:
				return false;
		}
	}
	
	public Filters convertToFilters(List<String> _params, Class _class) throws Exception {
		if(_params == null || _params.size() <= 0)
			return null;
		
		Filters filters = new Filters();
		for(String _p : _params) {
			System.out.println("line: " + _p);
			String op = findOperator(_p);
			
			if(op == null)
				throw new Exception("Can't find the operator: " + _p);
			
			String[] line = _p.split(op);
			if(line.length != 2)
				throw new Exception("Wrong criteria format");
			
//			String typeName = "";
//			Object value = null;
			
//			try {
//				if(line[0].contains("_")) {
//					System.out.println("NESTED DETECTED");
//					value = new Filters.Nested(line[1]);
//				}
//				else {
//					System.out.println("LOOKING FOR TYPE");
//					typeName = Util.findType(_class, line[0]);
//					switch(typeName) {
//						case Util.TYPE_CALENDAR :
//							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//							Calendar cal = Calendar.getInstance();
//							cal.setTime(sdf.parse(line[1]));
//							value = cal;
//							break;
//						case Util.TYPE_INTEGER :
//						case Util.TYPE_INT :
//							value = new Integer(line[1]);
//							break;
//						case Util.TYPE_DB :
//						case Util.TYPE_DOUBLE :
//							value = new Double(line[1]);
//							break;
//						case Util.TYPE_STRING :
//							value = line[1];
//							break;
//						default : 
//							throw new Exception("Unknown type: " + typeName);
//					}
//				}
//			}
//			catch(NoSuchMethodException _e) {
//				value = new Filters.Unknown(line[1]);
//			}
			
//			if(line[0].contains("_")) {
//				value = new Filters.Nested(line[1]);
//			}
//			else {
//				try {
//					typeName = Util.findType(_class, line[0]);
//					switch(typeName) {
//						case Util.TYPE_CALENDAR :
//							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//							Calendar cal = Calendar.getInstance();
//							cal.setTime(sdf.parse(line[1]));
//							value = cal;
//							break;
//						case Util.TYPE_INTEGER :
//						case Util.TYPE_INT :
//							value = new Integer(line[1]);
//							break;
//						case Util.TYPE_DB :
//						case Util.TYPE_DOUBLE :
//							value = new Double(line[1]);
//							break;
//						case Util.TYPE_STRING :
//							value = line[1];
//							break;
//					}
//				}
//				catch(NoSuchMethodException _e) {
//					value = new Filters.Unknown(line[1]);
//				}
//				
//				
//			}
			
			Filter f = new Filter(line[0], SYMBOL_MAP.get(op), line[1]);
			filters.addFilter(f);
		}
		
		return filters;
	}
	
	public ResponseEntity<Map<String,Object>> checkAuth(String _auth, String _taskType, Long _id) throws Exception {
		TokenInfo ti = authenticate(_auth, _taskType, _id != null ? _id : -1);
		
		Map<String,Object> retData = null;
		
		if (ti.status != TOKEN_VALID) {
			String errMessage = getErrorMessageFromCode(ti.status);
			retData = returnErrorData(ti.status, errMessage);
			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
		} 
		else {
			if (ti.access == ServiceUtil.ACCESS_DENY) {
				retData = returnErrorData(ServiceUtil.ERROR_NOT_PERMITTED);
				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);
			}
			if(ti.access == ServiceUtil.ACCESS_LIMITED) {
				_id = ti.id;
			}
			return null;
		}
	}
	
	public Map<String,Object> getErrorFromToken(TokenInfo _ti, boolean _denyLimitedAccess) throws Exception {
		
		Map<String,Object> retData = null;
		
		if (_ti.status != TOKEN_VALID) 
			return returnErrorData(_ti.status);
		else if (_ti.access == ServiceUtil.ACCESS_DENY) 
			return returnErrorData(ServiceUtil.ERROR_NOT_PERMITTED);
		else if (_ti.access == ServiceUtil.ACCESS_LIMITED && _denyLimitedAccess) 
			return returnErrorData(ServiceUtil.ERROR_NOT_PERMITTED);
		else
			return null;
	}
	
	public void sendEmail(String _from, String _to, String _subject, String _content) throws Exception {
		SimpleMailMessage message = new SimpleMailMessage(); 
	    message.setFrom(_from);
        message.setTo(_to); 
        message.setSubject(_subject); 
        message.setText(_content);
        mailSender.send(message);
	}
	
	
	
	private String findOperator(String _s) {
		for(String sym : SYMBOLS) {
			if(_s.contains(sym))
				return sym;
		}
		
		return null;
	}
	
	public static class TokenInfo
	{
		public long id;
		public String name;
		public String type;
		public int status ;
		public int access ;
		
		public TokenInfo (long _id, String _name, String _type, int _status, int _access) {
			id = _id;
			name = _name;
			type =_type;
			status = _status;
			access = _access;
		}
	}
}
