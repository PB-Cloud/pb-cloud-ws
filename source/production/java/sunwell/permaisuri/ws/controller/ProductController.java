/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * LoginService.java
 *
 * Created on Oct 16, 2017, 10:46:29 AM
 */

package sunwell.permaisuri.ws.controller;

import aegwyn.core.web.model.UserSession;





import aegwyn.core.web.model.UserSessionContainer;
import sunwell.permaisuri.bus.dto.customer.CustomerDTO;
import sunwell.permaisuri.bus.dto.inventory.ItemCategoryDTO;
import sunwell.permaisuri.bus.dto.inventory.ItemCategoryListDTO;
import sunwell.permaisuri.bus.dto.inventory.ItemDTO;
import sunwell.permaisuri.bus.dto.inventory.ItemListDTO;
import sunwell.permaisuri.bus.dto.inventory.MerkDTO;
import sunwell.permaisuri.bus.dto.inventory.OnHandStockDTO;
import sunwell.permaisuri.bus.service.Filters;
import sunwell.permaisuri.bus.service.InventoryService;
import sunwell.permaisuri.bus.service.InventorySvc;
import sunwell.permaisuri.bus.service.ProductService;
import sunwell.permaisuri.bus.service.ProductSvc;
import sunwell.permaisuri.bus.service.StandardConstant;
import sunwell.permaisuri.core.entity.cred.UserCredential;
import sunwell.permaisuri.core.entity.customer.Customer;
import sunwell.permaisuri.core.entity.inventory.Item;
import sunwell.permaisuri.core.entity.inventory.ItemCategory;
import sunwell.permaisuri.core.entity.inventory.Merk;
import sunwell.permaisuri.core.entity.inventory.Metrics;
import sunwell.permaisuri.core.entity.inventory.ProductImage;
import sunwell.permaisuri.core.entity.inventory.ProductSellPrice;
import sunwell.permaisuri.core.entity.sales.CartDetail;
import sunwell.permaisuri.core.entity.sales.SalesInvoice;
import sunwell.permaisuri.core.entity.warehouse.Gudang;
import sunwell.permaisuri.core.entity.warehouse.OnHandStock;
import sunwell.permaisuri.core.entity.warehouse.OnHandStockPK;
import sunwell.permaisuri.ws.annotation.RestEndpoint;
import sunwell.permaisuri.ws.controller.ServiceUtil.TokenInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
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
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
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

import org.apache.commons.io.IOUtils;
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

/**
 *
 * @author Benny
 */
@RestEndpoint
public class ProductController
{
	@Autowired
	ProductService productService;
	
	@Autowired
	InventoryService invSvc;
    
	@Autowired
    ServiceUtil svcUtil;
    
    @Inject
    ServletContext sCtx;
    
    @Inject
    MessageSource messageSource;
    
    @Inject
    HttpServletRequest request;
    
    @RequestMapping(value = "categories", method = RequestMethod.GET,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> getCategories(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="systemId", required = false) Integer _systemId,
    		@RequestParam(value="name", required = false) String _name,
    		@RequestParam(value="code", required = false) String _code,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
		
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_CATEGORIES, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			
			Object mainData = null;
			ItemCategory ic = null;
    		Page<ItemCategory> pageCategory = null ;
    		int totalPages = 0;
			long totalItems = 0;
            
            if(_systemId != null) {
    			ic = productService.findCategory(_systemId);
    		}
    		else if(_name != null) {
    			ic = productService.findCategoryByName(_name);
    		}
    		else if(_code != null) {
    			ic = productService.findCategoryByCode(_code);
    		}
            else {
            	pageCategory = productService.findAllCategories(_page);
            }
            
            if(pageCategory != null && pageCategory.getNumberOfElements() > 0) {
    			List<ItemCategory> categories = pageCategory.getContent();
    			List<ItemCategoryDTO> categoriesData = new LinkedList<>();
    			for(ItemCategory i : categories) {
    				categoriesData.add(new ItemCategoryDTO(i));
    			}
    			mainData = categoriesData;
    			totalPages = pageCategory.getTotalPages();
    			totalItems = pageCategory.getTotalElements();
    		}
    		else if (ic != null) {
    			mainData = new ItemCategoryDTO(ic);
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
    
    @RequestMapping(value = "categories", method = RequestMethod.GET,
			produces = "application/json", params="criteria"
	)
    public ResponseEntity<Map<String,Object>> getCategories(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="criteria") List<String> _filters,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
	
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_CATEGORIES, -1);
			retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
			Object mainData = null;
    		Page<ItemCategory> pageCategories = null ;
			int totalPages = 0;
			long totalItems = 0;
			Filters filters =  svcUtil.convertToFilters(_filters, ItemCategory.class);			
			pageCategories = productService.findCategories(filters, _page);
			if(pageCategories != null && pageCategories.getNumberOfElements() > 0) {
            	totalPages = pageCategories.getTotalPages();
            	totalItems = pageCategories.getTotalElements();
            	List<ItemCategory> categories = pageCategories.getContent();
            	List<ItemCategoryDTO> listCategoryDTO = new LinkedList<>();
            	for(ItemCategory i : categories) {
            		listCategoryDTO.add(new ItemCategoryDTO(i));
            	}
            	mainData = listCategoryDTO;
        	}
            
            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
		
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
    @RequestMapping(value = "categories", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> addCategory(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody ItemCategoryDTO _dto) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_CATEGORIES, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			ItemCategory data = productService.addCategory(_dto.getData());
			retData = svcUtil.returnSuccessfulData(new ItemCategoryDTO(data), 1, 1);
			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
        
    }
    
    @RequestMapping(value = "categories", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editCategory(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody ItemCategoryDTO _dto) throws Exception 
    {
		Map<String,Object> retData = null;
		
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_CATEGORIES, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			ItemCategory data = productService.addCategory(_dto.getData());
			retData = svcUtil.returnSuccessfulData(new ItemCategoryDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "categories", method = RequestMethod.DELETE,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> deleteCategory(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam("systemId") Integer _systemId) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_CATEGORIES, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			ItemCategory data = productService.deleteCategory(_systemId);
			retData = svcUtil.returnSuccessfulData(new ItemCategoryDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
    @RequestMapping(value = "merks", method = RequestMethod.GET,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> getMerks(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="systemId", required = false) Integer _systemId,
    		@RequestParam(value="name", required = false) String _name,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
		
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_MERKS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			
			Object mainData = null;
			Merk merk = null;
    		Page<Merk> pageMerk = null ;
    		int totalPages = 0;
			long totalItems = 0;
            
            if(_systemId != null) {
    			merk = productService.findMerk(_systemId);
    		}
    		else if(_name != null) {
    			merk = productService.findMerkByName(_name);
    		}
            else {
            	pageMerk = productService.findAllMerks(_page);
            }
            
            if(pageMerk != null && pageMerk.getNumberOfElements() > 0) {
    			List<Merk> merks = pageMerk.getContent();
    			List<MerkDTO> merksData = new LinkedList<>();
    			for(Merk m : merks) {
    				merksData.add(new MerkDTO(m));
    			}
    			mainData = merksData;
    			totalPages = pageMerk.getTotalPages();
    			totalItems = pageMerk.getTotalElements();
    		}
    		else if (merk != null) {
    			mainData = new MerkDTO(merk);
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
    
    @RequestMapping(value = "merks", method = RequestMethod.GET,
			produces = "application/json", params="criteria"
	)
    public ResponseEntity<Map<String,Object>> getMerks(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="criteria") List<String> _filters,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
	
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_MERKS, -1);
			retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
			Object mainData = null;
    		Page<Merk> pageMerks = null ;
			int totalPages = 0;
			long totalItems = 0;
			Filters filters =  svcUtil.convertToFilters(_filters, Merk.class);			
			pageMerks = productService.findMerks(filters, _page);
			if(pageMerks != null && pageMerks.getNumberOfElements() > 0) {
            	totalPages = pageMerks.getTotalPages();
            	totalItems = pageMerks.getTotalElements();
            	List<Merk> merks = pageMerks.getContent();
            	List<MerkDTO> listMerkDTO = new LinkedList<>();
            	for(Merk m : merks) {
            		listMerkDTO.add(new MerkDTO(m));
            	}
            	mainData = listMerkDTO;
        	}
            
            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
		
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
    @RequestMapping(value = "merks", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> addMerk(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody MerkDTO _dto) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_MERKS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			Merk data = productService.addMerk(_dto.getData());
			retData = svcUtil.returnSuccessfulData(new MerkDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "merks", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editMerk(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody MerkDTO _dto) throws Exception 
    {
		Map<String,Object> retData = null;
		
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_MERKS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			Merk data = productService.addMerk(_dto.getData());
			retData = svcUtil.returnSuccessfulData(new MerkDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "merks", method = RequestMethod.DELETE,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> deleteMerk(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam("systemId") Integer _systemId) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_CATEGORIES, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			Merk data = productService.deleteMerk(_systemId);
			retData = svcUtil.returnSuccessfulData(new MerkDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
    @RequestMapping(value = "items", method = RequestMethod.GET,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> getItems(
    		@RequestHeader(value="Authorization", required = false) String _auth,
			@RequestParam(value="systemId", required = false) Integer _systemId,
			@RequestParam(value="name", required = false) String _name,
			@RequestParam(value="categoryId", required = false) Integer _categoryId,
			Pageable _page ) throws Exception 
    {
		Map<String,Object> retData = null;
        try {
        	TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_PRODUCTS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			
			Object mainData = null;
        	Item item = null;
            Page<Item> pageItems = null;
            int totalPages = 0;
			long totalItems = 0;
			
            if(_systemId != null) {
    			item = productService.findItem(_systemId);
    		}
    		else if(_name != null) {
    			item = productService.findItemByName(_name);
    		}
    		else {
    			if(_categoryId != null) {
        			pageItems = productService.findByCategoryId(_categoryId, _page);
        		}
    			else 
    				pageItems = productService.findAllItems(_page);
    		}
            if(pageItems != null && pageItems.getNumberOfElements() > 0) {
            	List<Item> items = pageItems.getContent();
    			List<ItemDTO> itemsData = new LinkedList<>();
    			for(Item i : items) {
    				itemsData.add(new ItemDTO(i));
    			}
    			mainData = itemsData;
    			totalPages = pageItems.getTotalPages();
    			totalItems = pageItems.getTotalElements();
    		}
    		else if (item != null) {
    			mainData = new ItemDTO(item);
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
    
    @RequestMapping(value = "items", method = RequestMethod.GET,
			produces = "application/json", params="criteria"
	)
    public ResponseEntity<Map<String,Object>> getItems(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="criteria") List<String> _filters,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
	
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_PRODUCTS, -1);
			retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
        	
			Object mainData = null;
    		Page<Item> pageItems = null ;
			int totalPages = 0;
			long totalItems = 0;
			Filters filters =  svcUtil.convertToFilters(_filters, Customer.class);			
			pageItems = productService.findItems(filters, _page);
			if(pageItems != null && pageItems.getNumberOfElements() > 0) {
            	totalPages = pageItems.getTotalPages();
            	totalItems = pageItems.getTotalElements();
            	List<Item> items = pageItems.getContent();
            	List<ItemDTO> listItemDTO = new LinkedList<>();
            	for(Item i : items) {
            		listItemDTO.add(new ItemDTO(i));
            	}
            	mainData = listItemDTO;
        	}
            
            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
		
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
    @RequestMapping(value = "items", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> addItem(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody ItemDTO _dto) throws Exception  
    {        
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_PRODUCTS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			
            Item data = _dto.getData();
            data = productService.addItem(data, _dto.getProductImage());
            retData = svcUtil.returnSuccessfulData(new ItemDTO(data), 1, 1) ;
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "items", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editItem(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody ItemDTO _dto) throws Exception  
    {        
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_PRODUCTS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
            Item data = _dto.getData();
               
            data = productService.editItem(data, _dto.getProductImage());
            retData = svcUtil.returnSuccessfulData(new ItemDTO(data), 1, 1) ;
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "items", method = RequestMethod.DELETE,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> deleteItem(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam("systemId") Integer _systemId) throws Exception 
    {
		Map<String,Object> retData = null;
    	try {
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_PRODUCTS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
            Item data = productService.deleteItem(_systemId);
            retData = svcUtil.returnSuccessfulData(new ItemDTO(data), 1, 1);
    	}
    	catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
    @RequestMapping(value = "stocks", method = RequestMethod.GET,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> getStocks(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="itemId", required = false) Integer _itemId,
    		@RequestParam(value="warehouseId", required = false) Integer _warehouseId,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
		
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_STOCKS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
			Object mainData = null;
			OnHandStock oh = null;
    		Page<OnHandStock> pageOhs = null ;
    		int totalPages = 0;
			long totalItems = 0;
			
			if(_itemId != null)
        		pageOhs = invSvc.findOnHandByItemId(_itemId, _page);
        	else if (_warehouseId != null)
        		pageOhs = invSvc.findOnHandByWarehouseId(_warehouseId, _page);
        	else
        		pageOhs = invSvc.findAllOnHandStock(_page);
            
            if(pageOhs != null && pageOhs.getNumberOfElements() > 0) {
    			List<OnHandStock> stocks = pageOhs.getContent();
    			List<OnHandStockDTO> stocksData = new LinkedList<>();
    			for(OnHandStock ohs : stocks) {
    				stocksData.add(new OnHandStockDTO(ohs));
    			}
    			mainData = stocksData;
    			totalPages = pageOhs.getTotalPages();
    			totalItems = pageOhs.getTotalElements();
    		}
    		else if (oh != null) {
    			mainData = new OnHandStockDTO(oh);
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
    
    @RequestMapping(value = "stocks", method = RequestMethod.GET,
			produces = "application/json", params="criteria"
	)
    public ResponseEntity<Map<String,Object>> getStocks(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="criteria") List<String> _filters,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
	
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_STOCKS, -1);
			retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
			Object mainData = null;
    		Page<OnHandStock> pageStocks = null ;
			int totalPages = 0;
			long totalItems = 0;
			Filters filters =  svcUtil.convertToFilters(_filters, OnHandStock.class);			
			pageStocks = invSvc.findOnHandStocks(filters, _page);
			if(pageStocks != null && pageStocks.getNumberOfElements() > 0) {
            	totalPages = pageStocks.getTotalPages();
            	totalItems = pageStocks.getTotalElements();
            	List<OnHandStock> stocks = pageStocks.getContent();            	
            	List<OnHandStockDTO> listOnHandDTO = new LinkedList<>();
            	for(OnHandStock oh : stocks) {
            		listOnHandDTO.add(new OnHandStockDTO(oh));
            	}
            	mainData = listOnHandDTO;
        	}
            
            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
		
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
        
    @RequestMapping(value = "stocks", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> addStock(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody OnHandStockDTO _dto) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_STOCKS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			OnHandStock data = _dto.getData();
			data = invSvc.addOnHand(data);
			retData = svcUtil.returnSuccessfulData(new OnHandStockDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "stocks", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editStock(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody OnHandStockDTO _dto) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_STOCKS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			OnHandStock data = _dto.getData();
			System.out.println("WR: " + data.getWarehouse().getName() + " id: " + data.getWarehouse().getSystemId());
			data = invSvc.editOnHand(data);
			retData = svcUtil.returnSuccessfulData(new OnHandStockDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    @RequestMapping(value = "stocks", method = RequestMethod.DELETE,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> deleteStock(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam("itemId") Integer _itemId,
    		@RequestParam("warehouseId") Integer _warehouseId) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_STOCKS, -1);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);
			OnHandStock oh = invSvc.deleteOnHand(new OnHandStock(new Item(_itemId), new Gudang(_warehouseId), -1));
			retData = svcUtil.returnSuccessfulData(new OnHandStockDTO(oh), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
    @RequestMapping(value = "products/images", method = RequestMethod.GET
			,produces = {"image/png", "image/jpg"}
	)
    public ResponseEntity<byte[]> getImage(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam("product") String _product) throws Exception
    {
		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_PRODUCT_IMAGE, -1);
		if(ti.access != ServiceUtil.ACCESS_ALLOW) {
			return new ResponseEntity<byte[]>(null, null, HttpStatus.UNAUTHORIZED);
		}
		else {
			ProductImage pi = productService.findProductImageByProductName(_product);
			byte[] imageData = pi != null ? pi.getImageData() : null;
			return new ResponseEntity<byte[]>(imageData, null, HttpStatus.OK);
		}
    }

//    @RequestMapping(value = "products/images", method = RequestMethod.GET
////			,produces = {"image/png", "image/jpg"}
//	)
//    public ResponseEntity<byte[]> getImage(@RequestParam("sessionString")String _sessionString,
//    		@RequestParam("image") String _image) throws Exception
//    {
//        System.out.println("getImages called");
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
}



