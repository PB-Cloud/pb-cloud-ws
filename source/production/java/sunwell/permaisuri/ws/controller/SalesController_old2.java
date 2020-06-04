//package sunwell.permaisuri.ws.controller;
//
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import javax.inject.Inject;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import sunwell.permaisuri.bus.dto.customer.CustomerDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemCategoryDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemCategoryListDTO;
//import sunwell.permaisuri.bus.dto.inventory.ItemDTO;
//import sunwell.permaisuri.bus.dto.sales.CartDetailDTO;
//import sunwell.permaisuri.bus.dto.sales.CartDetailListDTO;
//import sunwell.permaisuri.bus.dto.sales.SalesInvoiceDTO;
//import sunwell.permaisuri.bus.dto.sales.SalesInvoiceListDTO;
//import sunwell.permaisuri.bus.dto.sales.SalesOrderDTO;
//import sunwell.permaisuri.bus.dto.sales.SalesOrderListDTO;
//import sunwell.permaisuri.bus.exception.OperationException;
//import sunwell.permaisuri.bus.service.Filters;
//import sunwell.permaisuri.bus.service.InventorySvc;
//import sunwell.permaisuri.bus.service.ProductSvc;
//import sunwell.permaisuri.bus.service.SalesSvc;
//import sunwell.permaisuri.bus.service.StandardConstant;
//import sunwell.permaisuri.bus.service.UserCredSvc;
//import sunwell.permaisuri.core.entity.cred.UserCredential;
//import sunwell.permaisuri.core.entity.customer.Customer;
//import sunwell.permaisuri.core.entity.inventory.Item;
//import sunwell.permaisuri.core.entity.inventory.ItemCategory;
//import sunwell.permaisuri.core.entity.inventory.Merk;
//import sunwell.permaisuri.core.entity.inventory.Metrics;
//import sunwell.permaisuri.core.entity.sales.CartDetail;
//import sunwell.permaisuri.core.entity.sales.CartDetailPK;
//import sunwell.permaisuri.core.entity.sales.Payment;
//import sunwell.permaisuri.core.entity.sales.SalesInvoice;
//import sunwell.permaisuri.core.entity.sales.SalesOrder;
//import sunwell.permaisuri.core.entity.sales.SalesOrderItem;
//import sunwell.permaisuri.core.entity.warehouse.OnHandStock;
//import sunwell.permaisuri.ws.annotation.RestEndpoint;
//import sunwell.permaisuri.ws.controller.ServiceUtil.TokenInfo;
//
//@RestEndpoint
//public class SalesController_old2
//{
//	@Autowired
//	SalesSvc salesSvc ;
//	
//	@Autowired
//	ProductSvc productSvc ;
//	
//	@Autowired
//	UserCredSvc userCredSvc ;
//	
//	@Autowired
//	InventorySvc invSvc;
//	
//	@Autowired
//	ServiceUtil svcUtil ;
//	
//	@Inject
//    MessageSource messageSource;
//	
//	@RequestMapping(value = "salesorders", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> getSalesOrders(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam(value="systemId", required = false) Integer _systemId,
////    		@RequestParam(value="soNo", required = false) String _soNo,
//    		@RequestParam(value="customerId", required = false) Long _custId,
//    		Pageable _page
//    		) throws Exception 
//    {
//		Map<String,Object> retData = null;
//
//		try {
//			Object mainData = null;
//    		Page<SalesOrder> pageSO = null ;
//    		SalesOrder so = null;
//    		Customer cust = null;
//			int totalPages = 0;
//			long totalItems = 0;
//			            
////            if(_custId != null) {
////    			cust = userCredSvc.findCustomer(_custId);
////    		}
////            
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_VIEW_SALES_ORDERS, 
////    				cust != null ? cust.getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			long id = -1;
//			
//			if(_systemId != null) {
//				so = salesSvc.findSalesOrder(_systemId);
//				id = so.getCustomer().getSystemId();
//			}
//			else if(_custId != null){
//				id = _custId;
//			}
//			
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_SALES_ORDERS, id);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
//    			_custId = ti.id;
//			
//			if(_systemId != null) {
//				// do nothing;
//				//so = salesSvc.findSalesOrder(_systemId);
//			}
////            else if(_soNo != null)
////        		so = salesSvc.findSalesOrderByNo(_soNo);
//            else {
//            	if(_custId != null) {
//            		pageSO = salesSvc.findSalesOrdersByCustId(_custId, _page);
//            	}
//            	else
//            		pageSO = salesSvc.findAllSalesOrder(_page);            	
//            }
//            
//            if(so != null) {
//            	totalPages = 1;
//            	totalItems = 1;
//            	mainData = new SalesOrderDTO(so);
//            }
//            else if(pageSO != null && pageSO.getContent().size() > 0 ) {
//            	totalPages = pageSO.getTotalPages();
//            	totalItems = pageSO.getTotalElements();
//            	List<SalesOrder> salesOrders = pageSO.getContent();
//            	List<SalesOrderDTO> listSODTO = new LinkedList<>();
//            	for(SalesOrder s : salesOrders) {
//            		listSODTO.add(new SalesOrderDTO(s));
//            	}
//            	mainData = listSODTO;
//        	}
//            
//            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//		
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//	
//	@RequestMapping(value = "salesorders", method = RequestMethod.GET,
//			produces = "application/json", params="criteria"
//	)
//    public ResponseEntity<Map<String,Object>> getSalesOrders(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam(value="criteria") List<String> _filters,
//    		Pageable _page
//    		) throws Exception 
//    {
//		Map<String,Object> retData = null;
//	
//		try {
//			Object mainData = null;
//    		Page<SalesOrder> pageSO = null ;
//			int totalPages = 0;
//			long totalItems = 0;
//			Filters filters =  svcUtil.convertToFilters(_filters, SalesOrder.class);			
//			pageSO = salesSvc.findSalesOrders(filters, _page);
//			if(pageSO != null && pageSO.getNumberOfElements() > 0) {
//            	totalPages = pageSO.getTotalPages();
//            	totalItems = pageSO.getTotalElements();
//            	List<SalesOrder> salesOrders = pageSO.getContent();
//            	List<SalesOrderDTO> listSODTO = new LinkedList<>();
//            	for(SalesOrder s : salesOrders) {
//            		listSODTO.add(new SalesOrderDTO(s));
//            	}
//            	mainData = listSODTO;
//        	}
//            
//            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//		
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//	
//	@RequestMapping(value = "salesorders", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> addSalesOrder(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody SalesOrderDTO _dto) throws Exception  
//	{     
//		Map<String,Object> retData;
//
//		try {
//			Customer cust = null;
//			
////			if(_dto.getCustomer() != null) {
////				cust = userCredSvc.findCustomer(_dto.getCustomer());
////			}
////			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CREATE_SALES_ORDERS, 
////    				cust != null ? cust.getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//						
////			if(_dto.getCustomer() != null) 
////				cust = userCredSvc.findCustomer(_dto.getCustomer());
//			
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_SALES_ORDERS, 
//					_dto.getCustomer() != null ? _dto.getCustomer() : -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    		
//            SalesOrder data = _dto.getData();
//            data.setCustomer(new Customer(_dto.getCustomer()));
//            
////        	if(cust == null) {
////        		int errCode = StandardConstant.ERROR_CANT_FIND_CUSTOMER; 
////        		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_CUSTOMER);
////        		retData = svcUtil.returnErrorData(errCode,  errMessage);
////            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////        	}
////        	data.setCustomer(cust);
//            
////            if(data.getItems() != null && data.getItems().size() > 0) {
////            	for(SalesOrderItem sItem : data.getItems()) {
////            		Item item = productSvc.findItemByName(sItem.getItem().getName());
////            		if(item == null) {
////            			int errCode = StandardConstant.ERROR_CANT_FIND_PRODUCT; 
////                		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_PRODUCT);
////                		retData = svcUtil.returnErrorData(errCode,  errMessage);
////    	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////            		}
////            		sItem.setItem(item);
////            	}
////            }		            
//            
//            data = salesSvc.addSalesOrder(data);
//            retData = svcUtil.returnSuccessfulData(new SalesOrderDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//	
//	@RequestMapping(value = "salesorders", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> editSalesOrder(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody SalesOrderDTO _dto) throws Exception  
//	{     
//		Map<String,Object> retData;
//
//		try {
//			SalesOrder so = null;
////			Customer cust = null;
//			if(_dto.getSystemId() != null)
//				 so = salesSvc.findSalesOrder(_dto.getSystemId());
//			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_UPDATE_SALES_ORDERS, 
////    				so != null ? so.getCustomer().getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			
////			if(_dto.getCustomer() != null) 
////				cust = userCredSvc.findCustomer(_dto.getCustomer());
//			
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_SALES_ORDERS, 
//					_dto.getCustomer() != null ? _dto.getCustomer() : -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			
//            SalesOrder data = _dto.getData();
//            data.setCustomer(new Customer(_dto.getCustomer()));
////            if(_dto.getCustomer() != null) {
////            	Customer cust = userCredSvc.findCustomer(_dto.getCustomer());
////	        	if(cust == null) {
////	        		int errCode = StandardConstant.ERROR_CANT_FIND_CUSTOMER; 
////	        		String errMessage = svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_CUSTOMER);
////	        		retData = svcUtil.returnErrorData(errCode,  errMessage);
////	            	return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
////	        	}
////	        	data.setCustomer(cust);
////            }
//            
//            
////            if(data.getItems() != null && data.getItems().size() > 0) {
////            	for(SalesOrderItem sItem : data.getItems()) {
////            		Item item = productSvc.findItemByName(sItem.getItem().getName());
////            		sItem.setItem(item);
////            	}
////            }		            
//            
//            data = salesSvc.editSalesOrder(data);
//            retData = svcUtil.returnSuccessfulData(new SalesOrderDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//	
//	@RequestMapping(value = "salesorders", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//	public ResponseEntity<Map<String,Object>> deleteSalesOrder(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("systemId") Integer _systemId) throws Exception 
//	{
//		Map<String,Object> retData;
//
//    	try {
//    		SalesOrder so = salesSvc.findSalesOrder(_systemId);
//    		Customer cust = null;
//    		if(so != null)
//    			cust = so.getCustomer();
//    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_SALES_ORDERS, cust != null ? cust.getSystemId() : -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_DELETE_SALES_ORDERS, 
////					so != null ? so.getCustomer().getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);
////			}
//            SalesOrder data = salesSvc.deleteSalesOrder(_systemId);
//            retData = svcUtil.returnSuccessfulData(new SalesOrderDTO(data), 1, 1);
//    	}
//    	catch(Exception e) {
//    		retData = svcUtil.handleException(e);
//    	}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//	
//	@RequestMapping(value = "salesinvoices", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> getSalesInvoices(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam(value="systemId", required = false) Integer _systemId,
//    		@RequestParam(value="invoiceNo", required = false) String _invNo,
//    		@RequestParam(value="customerId", required = false) Long _custId,
//    		Pageable _page ) throws Exception 
//    {
//		Map<String,Object> retData;
//
//		try {
//			Object mainData = null;
//			SalesInvoice si = null;
//    		Page<SalesInvoice> pageSI = null ;
//    		int totalPages = 0;
//			long totalItems = 0;
//			Customer cust = null;
//            
////            if(_custId != null) {
////    			cust = userCredSvc.findCustomer(_custId);
////    		}
////            
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_VIEW_SALES_INVOICES, 
////    				cust != null ? cust.getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);
////			}
//			
//			long id = -1;
//			
//			if(_systemId != null) {
//				si = salesSvc.findSalesInvoice(_systemId);
//				id = si.getCustomer().getSystemId();
//			}
//			else if(_custId != null){
//				id = _custId;
//			}
//			
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_SALES_INVOICES, id);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
//    			_custId = ti.id;
//			
//			if(_systemId != null) {
//				//do nothing
////				si = salesSvc.findSalesInvoice(_systemId);;
//			}
//            else if(_invNo != null)
//        		si = salesSvc.findSalesInvoiceByNo(_invNo);
//            else {
//            	if(_custId != null) {
//        			pageSI = salesSvc.findSalesInvoicesByCustId(_custId, _page);
//            	}
//            	else
//            		pageSI = salesSvc.findAllSalesInvoice(_page);
//            }
//            
//            if(pageSI != null && pageSI.getNumberOfElements() > 0) {
//            	List<SalesInvoice> invoices = pageSI.getContent();
//            	List<SalesInvoiceDTO> invoicesData = new LinkedList<>();
//            	for(SalesInvoice s : invoices)
//            		invoicesData.add(new SalesInvoiceDTO(s));
//            	mainData = invoicesData;
//            	totalPages = pageSI.getTotalPages();
//            	totalItems = pageSI.getTotalElements();
//    		}
//    		else if (si != null) {
//    			mainData = new SalesInvoiceDTO(si);
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
//	@RequestMapping(value = "salesinvoices", method = RequestMethod.GET,
//			produces = "application/json", params="criteria"
//	)
//    public ResponseEntity<Map<String,Object>> getSalesInvoices(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam(value="criteria") List<String> _filters,
//    		Pageable _page
//    		) throws Exception 
//    {
//		Map<String,Object> retData = null;
//	
//		try {
//			Object mainData = null;
//    		Page<SalesInvoice> pageSI = null ;
//			int totalPages = 0;
//			long totalItems = 0;
//			Filters filters =  svcUtil.convertToFilters(_filters, SalesInvoice.class);			
//			pageSI = salesSvc.findSalesInvoices(filters, _page);
//			if(pageSI != null && pageSI.getNumberOfElements() > 0) {
//            	totalPages = pageSI.getTotalPages();
//            	totalItems = pageSI.getTotalElements();
//            	List<SalesInvoice> salesInvoices = pageSI.getContent();
//            	List<SalesInvoiceDTO> listSIDTO = new LinkedList<>();
//            	for(SalesInvoice s : salesInvoices) {
//            		listSIDTO.add(new SalesInvoiceDTO(s));
//            	}
//            	mainData = listSIDTO;
//        	}
//            
//            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//		
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//	
//	@RequestMapping(value = "salesinvoices", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> createPayment(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody Payment _payment) throws Exception  
//	{        
//		Map<String,Object> retData;
//		try {	
//			Customer cust = null;
//			
//			SalesOrder so = salesSvc.findSalesOrder(_payment.getSoId());
//			if(so != null)
//				cust = so.getCustomer();
//			
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_SALES_INVOICES, 
//					cust != null ? cust.getSystemId() : -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			
////			if(_payment.getSoNo() != null) {
////				SalesOrder so = salesSvc.findSalesOrderByNo(_payment.getSoNo());
////				if(so != null)
////					cust = so.getCustomer();
////			}
//			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CREATE_SALES_INVOICES, 
////    				cust != null ? cust.getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);
////			}
//            SalesInvoice data = salesSvc.createPayment(_payment);
//            retData = svcUtil.returnSuccessfulData(new SalesInvoiceDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//	
//	@RequestMapping(value = "salesinvoices", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> editPayment(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody Payment _payment) throws Exception  
//	{        
//		Map<String,Object> retData;
//		try {	
//			Customer cust = null;
//			SalesInvoice si = salesSvc.findSalesInvoiceByNo(_payment.getInvoiceNo());
//			if(si != null)
//				cust = si.getCustomer();
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_SALES_INVOICES, 
//					cust != null ? cust.getSystemId() : -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			
////			if(_payment.getSoNo() != null) {
////				SalesOrder so = salesSvc.findSalesOrderByNo(_payment.getSoNo());
////				if(so != null)
////					cust = so.getCustomer();
////			}
//			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CREATE_SALES_INVOICES, 
////    				cust != null ? cust.getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);
////			}
//            SalesInvoice data = salesSvc.editPayment(_payment);
//            retData = svcUtil.returnSuccessfulData(new SalesInvoiceDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//	
//	@RequestMapping(value = "salesinvoices", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//	public ResponseEntity<Map<String,Object>> deleteSalesInvoice(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("systemId") Integer _systemId) throws Exception 
//	{
//		Map<String,Object> retData;
//    	try {
//    		SalesInvoice si = salesSvc.findSalesInvoice(_systemId);
//    		Customer cust = null;
//    		if(si != null)
//    			cust = si.getCustomer();
//    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_SALES_INVOICES, cust != null ? cust.getSystemId() : -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_DELETE_SALES_INVOICES, 
////					si != null ? si.getCustomer().getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);
////			}
//    		SalesInvoice data = salesSvc.deleteSalesInvoice(_systemId);
//            retData = svcUtil.returnSuccessfulData(new SalesInvoiceDTO(data), 1, 1);
//    	}
//    	catch(Exception e) {
//    		retData = svcUtil.handleException(e);
//    	}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//	
//	@RequestMapping(value = "cartdetails", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> getCartDetails(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("customerId") Long _custId,
//    		Pageable _page ) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		try {
////			Customer cust = userCredSvc.findCustomer(_custId);
////			long id = -1;
////			
////			if(_systemId != null) {
////				si = salesSvc.findSalesInvoice(_systemId);
////				id = si.getCustomer().getSystemId();
////			}
////			else if(_custId != null){
////				id = _custId;
////			}
//			
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_CART_DETAILS, _custId);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    	
//			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_VIEW_CART_DETAILS, 
////					cust != null ? cust.getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//    		Page<CartDetail> pageCartDetail = null ;
//    		int totalPages = 0;
//			long totalItems = 0;
//			
//        	pageCartDetail = salesSvc.findCartDetailByCustomerId(_custId, _page);
//            
//            if(pageCartDetail != null && pageCartDetail.getNumberOfElements() > 0) {
//    			List<CartDetail> cartDetails = pageCartDetail.getContent();
//    			List<CartDetailDTO> cartDetailsData = new LinkedList<>();
//    			for(CartDetail cd : cartDetails) 
//    				cartDetailsData.add(new CartDetailDTO(cd));
//    			totalPages = pageCartDetail.getTotalPages();
//    			totalItems = pageCartDetail.getTotalElements();
//    			
//    			retData = svcUtil.returnSuccessfulData(cartDetailsData, totalPages, totalItems);
//    		}
//		}
//		catch(Exception e) {
//			svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "cartdetails", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> addCartDetail(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody CartDetailDTO _dto) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		try {
//			
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_CART_DETAILS, _dto.getCustomer() != null ? _dto.getCustomer() : -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			
////			Customer cust = null;
////			if(_dto.getCustomer() != null)
////				cust = userCredSvc.findCustomer(_dto.getCustomer());
//			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CREATE_CART_DETAILS, 
////					cust != null ? cust.getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			
//			CartDetail data = _dto.getData();
////			Item item = productSvc.findItemByName(_dto.getItem());
////			if(item == null)
////				throw new OperationException(StandardConstant.ERROR_CANT_FIND_PRODUCT, null);
////			data.getItem().setSystemId(item.getSystemId());
//			data = salesSvc.addCartDetail(data);
//			retData = svcUtil.returnSuccessfulData(new CartDetailDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "cartdetails", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> editCartDetail(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody CartDetailDTO _dto) throws Exception 
//    {
//		Map<String,Object> retData = null;
//		try {
////			Customer cust = null;
////			if(_dto.getCustomer() != null)
////				cust = userCredSvc.findCustomer(_dto.getCustomer());
////			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_UPDATE_CART_DETAILS, 
////					cust != null ? cust.getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED); 
////			}
//			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_CART_DETAILS, _dto.getCustomer() != null ? _dto.getCustomer() : -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//			CartDetail data = _dto.getData();
////			Item item = productSvc.findItemByName(_dto.getItem());
////			if(item == null)
////				throw new OperationException(StandardConstant.ERROR_CANT_FIND_PRODUCT, null);
////			data.getItem().setSystemId(item.getSystemId());
//			data = salesSvc.editCartDetail(data);
//			retData = svcUtil.returnSuccessfulData(new CartDetailDTO(data), 1, 1);
//		}
//		catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "cartdetails", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> deleteCartDetail(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("customerId") Long _customerId, @RequestParam("itemId") Integer _itemId) throws Exception 
//    {
//		Map<String,Object> retData = null;
//    	try {
//    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_CART_DETAILS, _customerId);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    		
//    		Customer cust = userCredSvc.findCustomer(_customerId);
//			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_DELETE_CART_DETAILS, 
////					cust != null ? cust.getUserCredential().getSystemId() : -1);
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);  
////			}
//    		CartDetail cd = salesSvc.deleteCartDetail(_customerId, _itemId);
//            retData = svcUtil.returnSuccessfulData(new CartDetailDTO(cd), 1, 1);
//    	}
//    	catch(Exception e) {
//			retData = svcUtil.handleException(e);
//		}
//        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "checkout", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<Map<String,Object>> checkOut(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody CustomerDTO _dto) throws Exception 
//    {
//		Map<String,Object> retData = null;
//    	try {
//    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, 
//    				_dto.getSystemId() != null ? _dto.getSystemId() : -1);
//    		retData = svcUtil.checkToken(ti);
//    		if(retData != null)
//    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
//    		
//    		Customer cust = null;
//			if(_dto.getSystemId() != null)
//				cust = userCredSvc.findCustomer(_dto.getSystemId());
//			
////			int tokenStat = svcUtil.checkAuth(_auth, UserCredential.TASK_CHECK_OUT, 
////					cust != null ? cust.getUserCredential().getSystemId() : -1);
////			
////			if(tokenStat != StandardConstant.TOKEN_VALID) {
////				String errMessage = svcUtil.getErrorMessageFromCode(tokenStat);
////				retData = svcUtil.returnErrorData(tokenStat, errMessage);
////				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.UNAUTHORIZED);  
////			}
//			
//			List<CartDetail> carts = cust.getCartDetails();
//			List<String> stockErrorMessages = new LinkedList<>();
//			List<Item> notEnoughtStocks = new LinkedList<>();
//			for(CartDetail cd : carts) {
//				double ohQty = invSvc.getOnHandQtyByItem(cd.getItem());
//				System.out.println("OH QTY: " + ohQty + " name: " + cd.getItem().getName());
//				if(ohQty < cd.getQty()) 
//					notEnoughtStocks.add(cd.getItem());  
////					stockErrorMessages.add(svcUtil.getErrorMessageFromCode(
////							StandardConstant.ERROR_NOT_ENOUGH_STOCK_FOR_ITEM, new Object[] {cd.getItem()}));
//			}
//			
//			if(notEnoughtStocks.size() > 0) {
//				
//				String errorMessage = "";
//				List<Integer> itemIds = new LinkedList<>();
//				
//				for(Item i : notEnoughtStocks) {
//					stockErrorMessages.add(svcUtil.getErrorMessageFromCode(
//							StandardConstant.ERROR_NOT_ENOUGH_STOCK_FOR_ITEM, new Object[] {i}));
//					
//					itemIds.add(i.getSystemId());
//				}
//				for(String errMess : stockErrorMessages) {
//					errorMessage += errMess + "\n";
//				}
//				
//				errorMessage = errorMessage.substring(0, errorMessage.length() - 1);
//				retData = new HashMap<>();
//				retData.put("status", "error");
//				retData.put("errorCode", StandardConstant.ERROR_NOT_ENOUGH_STOCK);
//				retData.put("errorMessage", errorMessage);
//				retData.put("items", itemIds);
//			
//				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
//			}
//			
//    		SalesOrder so = salesSvc.checkOut(cust);
//    		retData = svcUtil.returnSuccessfulData(new SalesOrderDTO(so), 1, 1);
//    		return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
//    	}
//    	catch(Exception _e) {
//    		retData = svcUtil.handleException(_e);
//    		return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.INTERNAL_SERVER_ERROR);
//    	}
//    	
//    }
//    
//}
