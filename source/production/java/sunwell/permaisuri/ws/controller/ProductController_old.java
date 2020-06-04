package sunwell.permaisuri.ws.controller;
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
///**
// * LoginService.java
// *
// * Created on Oct 16, 2017, 10:46:29 AM
// */
//
//package sunwell.permaisuri.ws.controller;
//
//import aegwyn.core.web.model.UserSession;
//
//
//
//import aegwyn.core.web.model.UserSessionContainer;
//import sunwell.permaisuri.bus.dto.customer.CustomerDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemCategoryDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemCategoryListDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemListDTO;
//import sunwell.permaisuri.bus.service.ProductSvc;
//import sunwell.permaisuri.bus.service.StandardConstant;
//import sunwell.permaisuri.core.entity.cred.UserGroup;
//import sunwell.permaisuri.core.entity.inventory.Item;
//import sunwell.permaisuri.core.entity.inventory.ItemCategory;
//import sunwell.permaisuri.core.entity.inventory.ItemShipmentInfo;
//import sunwell.permaisuri.core.entity.inventory.Merk;
//import sunwell.permaisuri.core.entity.inventory.Metrics;
//import sunwell.permaisuri.ws.annotation.RestEndpoint;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URL;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//import javax.ejb.TransactionAttribute;
//import javax.ejb.TransactionAttributeType;
//import javax.inject.Inject;
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;
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
//
//import org.apache.commons.io.IOUtils;
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
///**
// *
// * @author Benny
// */
//@RestEndpoint
//public class ProductController
//{
//	@Autowired
//	ProductSvc productService;
//    
//	@Autowired
//    ServiceUtil svcUtil;
//    
//    @Inject
//    ServletContext sCtx;
//    
//    @Inject
//    MessageSource messageSource;
//    
//    @Inject
//    HttpServletRequest request;
//    
//    @RequestMapping(value = "categories", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<ItemCategoryListDTO> getCategories(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam(value="systemId", required = false) Integer _systemId,
//    		@RequestParam(value="name", required = false) String _name,
//    		@RequestParam(value="code", required = false) String _code,
//    		Pageable _page
//    		) throws Exception 
//    {
//	  	Thread.sleep (2000);
//        ItemCategoryListDTO retval = new ItemCategoryListDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<ItemCategoryListDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			
//			ItemCategory ic = null;
//    		Page<ItemCategory> pageCategory = null ;
//            
//            if(_systemId != null) {
//        			ic = productService.findCategory(_systemId);
//        		}
//    		else if(_name != null) {
//    			ic = productService.findCategoryByName(_name);
//    		}
//    		else if(_code != null) {
//    			ic = productService.findCategoryByCode(_code);
//    		}
//            else {
//            	pageCategory = productService.findAllCategories(_page);
//            	System.out.println("PAGE: " + pageCategory.getNumber() + " TOT E: " + pageCategory.getTotalElements() 
//            		+ " PAGES: " + pageCategory.getTotalPages() );
//            }
//            
//            if(pageCategory != null && pageCategory.getNumberOfElements() > 0) {
//    			retval.setTotalItems(pageCategory.getTotalElements());
//    			retval.setTotalPages(pageCategory.getTotalPages());
//    			retval.setData(pageCategory.getContent());
//    		}
//    		else if (ic != null) {
//    			retval.setTotalItems((long)1);
//    			retval.setTotalPages(1);
//    			retval.setData(Arrays.asList(new ItemCategory[] {ic}));
//    		}
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<ItemCategoryListDTO>(retval, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "categories", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<ItemCategoryDTO> addCategory(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody ItemCategoryDTO _dto) throws Exception 
//    {
//    	ItemCategoryDTO retval = new ItemCategoryDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<ItemCategoryDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			ItemCategory data = productService.addCategory(_dto.getData());
//            retval.setData (data);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<ItemCategoryDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "categories", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<ItemCategoryDTO> editCategory(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody ItemCategoryDTO _dto) throws Exception 
//    {
//    	ItemCategoryDTO retval = new ItemCategoryDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<ItemCategoryDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//    		ItemCategory data = productService.editCategory(_dto.getData());
//    		retval.setData(data);
//    	}
//    	catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<ItemCategoryDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "categories", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//    public ResponseEntity<ItemCategoryDTO> deleteCategory(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("systemId") Integer _systemId) throws Exception 
//    {
//    	ItemCategoryDTO retval = new ItemCategoryDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<ItemCategoryDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			ItemCategory ctgr = productService.deleteCategory(_systemId);
//	        retval.setData(ctgr);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<ItemCategoryDTO>(retval, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "items", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<ItemListDTO> getItems(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//			@RequestParam(value="systemId", required = false) Integer _systemId,
//			@RequestParam(value="name", required = false) String _name,
//			@RequestParam(value="categoryId", required = false) Integer _categoryId,
//			Pageable _page ) throws Exception 
//    {
//    	ItemListDTO retval = new ItemListDTO();
//        try {
//        	int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<ItemListDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			
//        	Item item = null;
//            Page<Item> pageItems = null;
//            if(_systemId != null) {
//        			item = productService.findItem(_systemId);
//        		}
//    		else if(_name != null) {
//    			item = productService.findItemByName(_name);
//    		}
//    		else if(_categoryId != null) {
//    			pageItems = productService.findByCategoryId(_categoryId, _page);
//    		}
//    		else {
//				List<Item> items = productService.findAllItems();
//        		retval.setTotalItems((long)(items != null ? items.size() : 0));
//    			retval.setTotalPages((items != null && items.size() > 0) ? 1 : 0);
//    			retval.setData(items);
//    		}
//            
//            if(pageItems != null && pageItems.getNumberOfElements() > 0) {
//    			retval.setTotalItems(pageItems.getTotalElements());
//    			retval.setTotalPages(pageItems.getTotalPages());
//    			retval.setData(pageItems.getContent());
//    		}
//    		else if (item != null) {
//    			retval.setTotalItems((long)1);
//    			retval.setTotalPages(1);
//    			retval.setData(Arrays.asList(new Item[] {item}));
//    		}
//        }
//        catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//            
//        return new ResponseEntity<ItemListDTO>(retval, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "items", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<ItemDTO> addItem(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody ItemDTO _dto) throws Exception  {        
//    	ItemDTO retval = new ItemDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//            Item data = _dto.getData();
//            
//            if(_dto.getMerk() != null) {
//            	Merk merk = productService.findMerkByName(_dto.getMerk());
//            	if(merk == null) {
//            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_MERK); 
//            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_MERK));
//	            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
//            	}
//            	data.setMerk(merk);
//            }
//            
////		            if(_dto.getShipmentInfo() != null) {
////		            	ItemShipmentInfo isf = productService.findShipmentByName(_dto.getShipmentInfo());
////		            	if(isf == null) {
////		            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_SHIPMENT); 
////		            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_SHIPMENT));
////			            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
////		            	}
////		            	data.setShipmentInfo(isf);
////		            }
//            
//            if(_dto.getMetricInv() != null) {
//            	Metrics metric = productService.findMetrics(_dto.getMetricInv());
//            	if(metric == null) {
//            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_METRIC); 
//            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC));
//	            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
//            	}
//            	data.setMetricInv(metric);
//            }
//            
//            if(_dto.getMetricPurchase() != null) {
//            	Metrics metric = productService.findMetrics(_dto.getMetricPurchase());
//            	if(metric == null) {
//            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_METRIC); 
//            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC));
//	            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
//            	}
//            	data.setMetricPurchase(metric);
//            }
//            
//            if(_dto.getMetricSales() != null) {
//            	Metrics metric = productService.findMetrics(_dto.getMetricSales());
//            	if(metric == null) {
//            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_METRIC); 
//            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC));
//	            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
//            	}
//            	data.setMetricSales(metric);
//            }
//            
//            data = productService.addItem(data, _dto.getImageData(), sCtx.getInitParameter ("imagePath"));
//            retval.setData(data);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "items", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<ItemDTO> editItem(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody ItemDTO _dto) throws Exception  
//    {        
//    	ItemDTO retval = new ItemDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			
//            Item data = _dto.getData();
//            
//            if(_dto.getMerk() != null) {
//            	Merk merk = productService.findMerkByName(_dto.getMerk());
//            	if(merk == null) {
//            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_MERK); 
//            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_MERK));
//	            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
//            	}
//            	data.setMerk(merk);
//            }
//            
////		            if(_dto.getShipmentInfo() != null) {
////		            	ItemShipmentInfo isf = productService.findShipmentByName(_dto.getShipmentInfo());
////		            	if(isf == null) {
////		            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_SHIPMENT); 
////		            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_SHIPMENT));
////			            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
////		            	}
////		            	data.setShipmentInfo(isf);
////		            }
//            
//            if(_dto.getMetricInv() != null) {
//            	Metrics metric = productService.findMetrics(_dto.getMetricInv());
//            	if(metric == null) {
//            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_METRIC); 
//            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC));
//	            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
//            	}
//            	data.setMetricInv(metric);
//            }
//            
//            if(_dto.getMetricPurchase() != null) {
//            	Metrics metric = productService.findMetrics(_dto.getMetricPurchase());
//            	if(metric == null) {
//            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_METRIC); 
//            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC));
//	            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
//            	}
//            	data.setMetricPurchase(metric);
//            }
//            
//            if(_dto.getMetricSales() != null) {
//            	Metrics metric = productService.findMetrics(_dto.getMetricSales());
//            	if(metric == null) {
//            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_METRIC); 
//            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC));
//	            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
//            	}
//            	data.setMetricSales(metric);
//            }
//            
//            
//            data = productService.editItem(data, _dto.getImageData(), sCtx.getInitParameter ("imagePath"));
//            retval.setData(data);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
////    @RequestMapping(value = "products", method = RequestMethod.PUT,
////			consumes = "application/json", produces = "application/json"
////	)
////    public ResponseEntity<ProductDTO> editProduct(@RequestBody ProductDTO _dto) throws Exception {
////        ProductDTO retval = new ProductDTO();
////        User user = svcUtil.getUser (_dto.getSessionString ());
////        if(user == null) {
////            retval.setErrorCode (StandardConstant.ERROR_NO_LOGIN_SESSION);
////            retval.setErrorMessage(messageSource.getMessage("error_no_login_session", null
////            		, request.getLocale()));
////        }
////        else {
////	        	try {
////		        Product data = _dto.getData();
////		        data.setTenant(user.getTenant());
////		        Product entity = productService.editProduct(data, 
////		        		_dto.getImgData(), sCtx.getInitParameter ("imagePath"));
////		        retval.setData (entity);
////	        	}
////	        	catch(Exception e) {
////        			svcUtil.handleException(retval, e);
////        		}
////        }
////        return new ResponseEntity<ProductDTO>(retval, null, HttpStatus.CREATED);
////    }
//    
//    @RequestMapping(value = "items", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//    public ResponseEntity<ItemDTO> deleteItem(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("systemId") Integer _systemId) throws Exception 
//    {
//		ItemDTO retval = new ItemDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//            Item item = productService.deleteItem(_systemId);
//            retval.setData(item);
//    	}
//    	catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = "products/images", method = RequestMethod.GET
////			,produces = {"image/png", "image/jpg"}
//	)
//    public ResponseEntity<byte[]> getImage(@RequestParam("sessionString")String _sessionString,
//    		@RequestParam(value = "tenantId", required = false)String _tenantId,
//    		@RequestParam("image") String _image) throws Exception
//    {
//        System.out.println("getImages called");
////    		Tenant tenant = null;
////        if(_tenantId != null) {
////            tenant = genericService.findById (_tenantId, Tenant.class);
////        }
////        else if(_sessionString != null) {
////            UserSession us = svcUtil.getSession (_sessionString, false);
////            if(us != null) {
////                User user = null;
////                user = (User)us.getObject ("user");
////                tenant = user.getTenant ();
////            }
////        }
////        
////        if(tenant == null) {
////            System.out.println ("Tenant can't be found tid: " + _tenantId);
////            return null;
////        }
//        
//        String path = sCtx.getInitParameter ("imagePath") + "products/"  +  _image;
//        System.out.println ("PATH: " + path);
//        File file = new File(path);
//        if(file.exists ()) { 
//            FileInputStream fis = new FileInputStream(file);
//            long length = file.length ();
//            byte[] filecontent = new byte[(int)length];
//            fis.read(filecontent,0,(int)length); 
//            return new ResponseEntity<byte[]>(filecontent, null, HttpStatus.OK);
//        	
//        }
//        else {
//            System.out.println ("File doesn't exist, image: " + _image);
//            return null;
//        }
//    }
//}
//
