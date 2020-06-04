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
//
//
//import aegwyn.core.web.model.UserSessionContainer;
//import sunwell.permaisuri.bus.dto.customer.CustomerDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemCategoryDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemCategoryListDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemListDTO;
//import sunwell.permaisuri.bus.dto.inventory.OnHandStockDTO;
//import sunwell.permaisuri.bus.service.InventorySvc;
//import sunwell.permaisuri.bus.service.ProductSvc;
//import sunwell.permaisuri.bus.service.StandardConstant;
//import sunwell.permaisuri.core.entity.cred.UserCredential;
//import sunwell.permaisuri.core.entity.inventory.Item;
//import sunwell.permaisuri.core.entity.inventory.ItemCategory;
//import sunwell.permaisuri.core.entity.inventory.Merk;
//import sunwell.permaisuri.core.entity.inventory.Metrics;
//import sunwell.permaisuri.core.entity.inventory.ProductSellPrice;
//import sunwell.permaisuri.core.entity.warehouse.Gudang;
//import sunwell.permaisuri.core.entity.warehouse.OnHandStock;
//import sunwell.permaisuri.core.entity.warehouse.OnHandStockPK;
//import sunwell.permaisuri.ws.annotation.RestEndpoint;
//import sunwell.permaisuri.ws.controller.ServiceUtil.TokenInfo;
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
//import java.util.Map;
//
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
//public class ProductController_old2
//{
//	@Autowired
//	ProductSvc productService;
//	
//	@Autowired
//	InventorySvc invSvc;
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
//    public ResponseEntity<Map<String,Object>> getCategories(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam(value="systemId", required = false) Integer _systemId,
//    		@RequestParam(value="name", required = false) String _name,
//    		@RequestParam(value="code", required = false) String _code,
//    		Pageable _page
//    		) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		
//		try {
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_VIEW_CATEGORIES, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			
//			Object mainData = null;
//			ItemCategory ic = null;
//    		Page<ItemCategory> pageCategory = null ;
//    		int totalPages = 0;
//			long totalItems = 0;
//            
//            if(_systemId != null) {
//    			ic = productService.findCategory(_systemId);
//    		}
//    		else if(_name != null) {
//    			ic = productService.findCategoryByName(_name);
//    		}
//    		else if(_code != null) {
//    			ic = productService.findCategoryByCode(_code);
//    		}
//            else {
//            	pageCategory = productService.findAllCategories(_page);
//            }
//            
//            if(pageCategory != null && pageCategory.getNumberOfElements() > 0) {
//    			List<ItemCategory> categories = pageCategory.getContent();
//    			List<ItemCategoryDTO> categoriesData = new LinkedList<>();
//    			for(ItemCategory i : categories) {
//    				categoriesData.add(new ItemCategoryDTO(i));
//    			}
//    			mainData = categoriesData;
//    			totalPages = pageCategory.getTotalPages();
//    			totalItems = pageCategory.getTotalElements();
//    		}
//    		else if (ic != null) {
//    			mainData = new ItemCategoryDTO(ic);
//    			totalPages = 1;
//    			totalItems = 1;
//    		}
//            
//            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "categories", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> addCategory(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody ItemCategoryDTO _dto) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CREATE_CATEGORIES, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			ItemCategory data = productService.addCategory(_dto.getData());
//			retData = svcUtil.returnSuccessfulData(new ItemCategoryDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "categories", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> editCategory(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody ItemCategoryDTO _dto) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		
//		try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_UPDATE_CATEGORIES, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			ItemCategory data = productService.addCategory(_dto.getData());
//			retData = svcUtil.returnSuccessfulData(new ItemCategoryDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "categories", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> deleteCategory(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("systemId") Integer _systemId) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_DELETE_CATEGORIES, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			ItemCategory data = productService.deleteCategory(_systemId);
//			retData = svcUtil.returnSuccessfulData(new ItemCategoryDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "items", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> getItems(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//			@RequestParam(value="systemId", required = false) Integer _systemId,
//			@RequestParam(value="name", required = false) String _name,
//			@RequestParam(value="categoryId", required = false) Integer _categoryId,
//			Pageable _page ) throws Exception 
//    {
//		Map<String,Object> retData = null;
//        try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_VIEW_PRODUCTS, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//        	
//        	TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			
//			Object mainData = null;
//        	Item item = null;
//            Page<Item> pageItems = null;
//            int totalPages = 0;
//			long totalItems = 0;
//			
//            if(_systemId != null) {
//    			item = productService.findItem(_systemId);
//    		}
//    		else if(_name != null) {
//    			item = productService.findItemByName(_name);
//    		}
//    		else {
//    			if(_categoryId != null) {
//        			pageItems = productService.findByCategoryId(_categoryId, _page);
//        		}
//    			else 
//    				pageItems = productService.findAllItems(_page);
//    		}
//            
//            if(pageItems != null && pageItems.getNumberOfElements() > 0) {
//            	List<Item> items = pageItems.getContent();
//    			List<ItemDTO> itemsData = new LinkedList<>();
//    			for(Item i : items) {
//    				itemsData.add(new ItemDTO(i));
//    			}
//    			mainData = itemsData;
//    			totalPages = pageItems.getTotalPages();
//    			totalItems = pageItems.getTotalElements();
//    		}
//    		else if (item != null) {
//    			mainData = new ItemDTO(item);
//    			totalPages = 1;
//    			totalItems = 1;
//    		}
//            
//            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
//        }
//        catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//            
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "items", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> addItem(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody ItemDTO _dto) throws Exception  
//    {        
//		Map<String,Object> retData = null;
//		try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CREATE_PRODUCTS, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			
//            Item data = _dto.getData();
//            
////            if(_dto.getMerk() != null) {
////            	Merk merk = productService.findMerkByName(_dto.getMerk());
////            	if(merk == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_MERK);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_MERK, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMerk(merk);
////            }
////            
////            if(_dto.getMetric() != null) {
////            	Metrics metric = productService.findMetrics(_dto.getMetric());
////            	if(metric == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_METRIC, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMetric(metric);
////            }
//            
////            if(data.getSellPrices() != null && data.getSellPrices().size() > 0) {
////            	for(ProductSellPrice psp : data.getSellPrices()) {
////            		psp.setPriceLevel(productService.findSellPriceLevelByName(psp.getPriceLevel().getName()));
////            	}
////            }
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
////            if(_dto.getMetricInv() != null) {
////            	Metrics metric = productService.findMetrics(_dto.getMetricInv());
////            	if(metric == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_METRIC, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMetricInv(metric);
////            }
////            
////            if(_dto.getMetricPurchase() != null) {
////            	Metrics metric = productService.findMetrics(_dto.getMetricPurchase());
////            	if(metric == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_METRIC, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMetricPurchase(metric);
////            }
////            
////            if(_dto.getMetricSales() != null) {
////            	Metrics metric = productService.findMetrics(_dto.getMetricSales());
////            	if(metric == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_METRIC, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMetricSales(metric);
////            }
//            
////            data = productService.addItem(data, _dto.getImageData(), sCtx.getInitParameter ("imagePath"));
//            data = productService.addItem(data);
//            retData = svcUtil.returnSuccessfulData(new ItemDTO(data), 1, 1) ;
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "items", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> editItem(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody ItemDTO _dto) throws Exception  
//    {        
//		Map<String,Object> retData = null;
//		try {
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_UPDATE_PRODUCTS, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//            Item data = _dto.getData();
//                        
////            if(_dto.getMerk() != null) {
////            	Merk merk = productService.findMerkByName(_dto.getMerk());
////            	if(merk == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_MERK);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_MERK, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMerk(merk);
////            }
////            
////            if(_dto.getMetric() != null) {
////            	Metrics metric = productService.findMetrics(_dto.getMetric());
////            	if(metric == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_METRIC, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMetric(metric);
////            }
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
////            if(_dto.getMetricInv() != null) {
////            	Metrics metric = productService.findMetrics(_dto.getMetricInv());
////            	if(metric == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_METRIC, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMetricInv(metric);
////            }
////            
////            if(_dto.getMetricPurchase() != null) {
////            	Metrics metric = productService.findMetrics(_dto.getMetricPurchase());
////            	if(metric == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_METRIC, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMetricPurchase(metric);
////            }
////            
////            if(_dto.getMetricSales() != null) {
////            	Metrics metric = productService.findMetrics(_dto.getMetricSales());
////            	if(metric == null) {
////            		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_METRIC);
////            		retData = svcUtil.returnErrorData(StandardConstant.ERROR_CANT_FIND_METRIC, errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            	}
////            	data.setMetricSales(metric);
////            }
//            
////            data = productService.editItem(data, _dto.getImageData(), sCtx.getInitParameter ("imagePath"));
//            data = productService.editItem(data);
//            retData = svcUtil.returnSuccessfulData(new ItemDTO(data), 1, 1) ;
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
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
//    public ResponseEntity<Map<String,Object>> deleteItem(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("systemId") Integer _systemId) throws Exception 
//    {
//		Map<String,Object> retData = null;
//    	try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_DELETE_PRODUCTS, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//            Item data = productService.deleteItem(_systemId);
//            retData = svcUtil.returnSuccessfulData(new ItemDTO(data), 1, 1);
//    	}
//    	catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "stocks", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> getStocks(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam(value="itemId", required = false) Integer _itemId,
//    		@RequestParam(value="warehouseId", required = false) Integer _warehouseId,
//    		Pageable _page
//    		) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		
//		try {
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_VIEW_STOCKS, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			
//			Object mainData = null;
//			OnHandStock oh = null;
//    		Page<OnHandStock> pageOhs = null ;
//    		int totalPages = 0;
//			long totalItems = 0;
//			
//			if(_itemId != null)
//        		pageOhs = invSvc.findOnHandByItemId(_itemId, _page);
//        	else if (_warehouseId != null)
//        		pageOhs = invSvc.findOnHandByWarehouseId(_warehouseId, _page);
//        	else
//        		pageOhs = invSvc.findAllOnHandStock(_page);
//            
////            if(_itemId != null && _warehouseId != null) {
////    			oh = invSvc.findOnHandStock(new OnHandStockPK(_itemId, _warehouseId));
////    		}
////            else {
////            	if(_itemId != null)
////            		pageOhs = invSvc.findOnHandByItemId(_itemId, _page);
////            	else if (_warehouseId != null)
////            		pageOhs = invSvc.findOnHandByWarehouseId(_warehouseId, _page);
////            	else
////            		pageOhs = invSvc.findAllOnHandStock(_page);
////            }
//            
//            if(pageOhs != null && pageOhs.getNumberOfElements() > 0) {
//    			List<OnHandStock> stocks = pageOhs.getContent();
//    			List<OnHandStockDTO> stocksData = new LinkedList<>();
//    			for(OnHandStock ohs : stocks) {
//    				stocksData.add(new OnHandStockDTO(ohs));
//    			}
//    			mainData = stocksData;
//    			totalPages = pageOhs.getTotalPages();
//    			totalItems = pageOhs.getTotalElements();
//    		}
//    		else if (oh != null) {
//    			mainData = new OnHandStockDTO(oh);
//    			totalPages = 1;
//    			totalItems = 1;
//    		}
//            
//            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "stocks", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> addStock(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody OnHandStockDTO _dto) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CREATE_CATEGORIES, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			OnHandStock data = _dto.getData();
////			data.setItem(productService.findItemByName(_dto.getItem()));
////			data.setWarehouse(invSvc.findWarehouseByName(_dto.getWarehouse()));
//			
//			data = invSvc.addOnHand(data);
//			retData = svcUtil.returnSuccessfulData(new OnHandStockDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "stocks", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> editStock(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody OnHandStockDTO _dto) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CREATE_CATEGORIES, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			OnHandStock data = _dto.getData();
////			data.setItem(productService.findItemByName(_dto.getItem()));
////			data.setWarehouse(invSvc.findWarehouseByName(_dto.getWarehouse()));
//			System.out.println("WR: " + data.getWarehouse().getName() + " id: " + data.getWarehouse().getSystemId());
//			data = invSvc.editOnHand(data);
//			retData = svcUtil.returnSuccessfulData(new OnHandStockDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//    
////    @RequestMapping(value = "stocks", method = RequestMethod.DELETE,
////			produces = "application/json"
////	)
////    public ResponseEntity<Map<String,Object>> deleteStock(
////    		@RequestHeader(value="Authorization", required = false) String _auth, OnHandStockDTO _dto) throws Exception 
////    {
////		Map<String,Object> retData = null;
////		try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_DELETE_CATEGORIES, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
////			OnHandStock data = _dto.getData();
////			data.setItem(productService.findItemByName(_dto.getItem()));
////			data.setWarehouse(invSvc.findWarehouseByName(_dto.getWarehouse()));
////			OnHandStock oh = invSvc.deleteOnHand(_dto.getData());
////			retData = svcUtil.returnSuccessfulData(new OnHandStockDTO(oh), 1, 1);
////		}
////		catch(Exception e) {
////			retData = svcUtil.handleException(e);
////		}
////        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
////    }
//    
//    @RequestMapping(value = "stocks", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> deleteStock(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("itemId") Integer _itemId,
//    		@RequestParam("warehouseId") Integer _warehouseId) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		try {
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_DELETE_CATEGORIES, -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			OnHandStock oh = invSvc.deleteOnHand(new OnHandStock(new Item(_itemId), new Gudang(_warehouseId), -1));
////			OnHandStock oh = invSvc.deleteOnHand(new OnHandStock(new Item(_itemId), new Gudang(_warehouseId), null, null, null, -1));
//			retData = svcUtil.returnSuccessfulData(new OnHandStockDTO(oh), 1, 1);
////			List<OnHandStock> stocks = invSvc.deleteOnHand(_itemId, _warehouseId);
////			List<OnHandStockDTO> stocksData = new LinkedList<>();
////			for(OnHandStock ohs : stocks) {
////				stocksData.add(new OnHandStockDTO(ohs));
////			}
////			retData = svcUtil.returnSuccessfulData(stocksData, 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
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
