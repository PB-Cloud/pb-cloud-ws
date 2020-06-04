package sunwell.permaisuri.ws.controller;
//package sunwell.permaisuri.ws.controller;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
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
//import sunwell.permaisuri.bus.dto.cred.UserGroupDTO;
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
//import sunwell.permaisuri.bus.service.ProductSvc;
//import sunwell.permaisuri.bus.service.SalesSvc;
//import sunwell.permaisuri.bus.service.StandardConstant;
//import sunwell.permaisuri.bus.service.UserCredSvc;
//import sunwell.permaisuri.core.entity.cred.UserGroup;
//import sunwell.permaisuri.core.entity.customer.Customer;
//import sunwell.permaisuri.core.entity.hr.SalesPerson;
//import sunwell.permaisuri.core.entity.inventory.Item;
//import sunwell.permaisuri.core.entity.inventory.ItemCategory;
//import sunwell.permaisuri.core.entity.inventory.ItemShipmentInfo;
//import sunwell.permaisuri.core.entity.inventory.Merk;
//import sunwell.permaisuri.core.entity.inventory.Metrics;
//import sunwell.permaisuri.core.entity.sales.CartDetail;
//import sunwell.permaisuri.core.entity.sales.CartDetailPK;
//import sunwell.permaisuri.core.entity.sales.Payment;
//import sunwell.permaisuri.core.entity.sales.SOItem;
//import sunwell.permaisuri.core.entity.sales.SOItemPK;
//import sunwell.permaisuri.core.entity.sales.SalesInvoice;
//import sunwell.permaisuri.core.entity.sales.SalesOrder;
//import sunwell.permaisuri.ws.annotation.RestEndpoint;
//
//@RestEndpoint
//public class SalesController
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
//	ServiceUtil svcUtil ;
//	
//	@RequestMapping(value = "salesorders", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<SalesOrderListDTO> getSalesOrders(
////    public ResponseEntity<Map<String,Object>> getSalesOrders(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam(value="systemId", required = false) Integer _systemId,
//    		@RequestParam(value="soNo", required = false) String _soNo,
//    		@RequestParam(value="customerId", required = false) Integer _custId,
//    		Pageable _page
//    		) throws Exception 
//    {
//        SalesOrderListDTO retval = new SalesOrderListDTO();
//        
//
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<SalesOrderListDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			
//			SalesOrder so = null;
//			Page<SalesOrder> pageSO = null ;
//			            
//            if(_systemId != null) {
//    			so = salesSvc.findSalesOrder(_systemId);
//    		}
//            else if(_soNo != null)
//        		so = salesSvc.findSalesOrderByNo(_soNo);
//            else {
//            	if(_custId != null) {
//        			pageSO = salesSvc.findSalesOrdersByCustId(_custId, _page);
//            	}
//            	else
//            		pageSO = salesSvc.findAllSalesOrder(_page);
//            	
//            	System.out.println("PAGE: " + pageSO.getNumber() + " TOT E: " + pageSO.getTotalElements() 
//            		+ " PAGES: " + pageSO.getTotalPages() );
//            }
//            
//            if(pageSO != null && pageSO.getNumberOfElements() > 0) {
//    			retval.setTotalItems(pageSO.getTotalElements());
//    			retval.setTotalPages(pageSO.getTotalPages());
//    			retval.setData(pageSO.getContent());
//    		}
//    		else if (so != null) {
//    			retval.setTotalItems((long)1);
//    			retval.setTotalPages(1);
//    			retval.setData(Arrays.asList(new SalesOrder[] {so}));
//    		}
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}	
//		
//        return new ResponseEntity<SalesOrderListDTO>(retval, null, HttpStatus.OK);
//    }
//	
//	@RequestMapping(value = "salesorders", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<SalesOrderDTO> addSalesOrder(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody SalesOrderDTO _dto) throws Exception  
//	{     
//    	SalesOrderDTO retval = new SalesOrderDTO();
//
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<SalesOrderDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			
//            SalesOrder data = _dto.getData();
//            
//            if(_dto.getCustomer() != null) {
//            	Customer customer = userCredSvc.findCustomer(_dto.getCustomer());
//            	if(customer == null) {
//            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_CUSTOMER); 
//            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_CUSTOMER));
//	            	return new ResponseEntity<SalesOrderDTO>(retval, null, HttpStatus.BAD_REQUEST);
//            	}
//            	data.setCustomer(customer);
//            }
//            
////		            if(_dto.getSalesPerson() != null) {
////		            	SalesPerson sp = productService.findShipmentByName(_dto.getShipmentInfo());
////		            	if(isf == null) {
////		            		retval.setErrorCode (StandardConstant.ERROR_CANT_FIND_SHIPMENT); 
////		            		retval.setErrorMessage(svcUtil.getErrorMessageFromCode(StandardConstant.ERROR_CANT_FIND_SHIPMENT));
////			            	return new ResponseEntity<ItemDTO>(retval, null, HttpStatus.BAD_REQUEST);
////		            	}
////		            	data.setShipmentInfo(isf);
////		            }
//            
//            if(data.getItems() != null && data.getItems().size() > 0) {
//            	for(SOItem sItem : data.getItems()) {
//            		Item item = productSvc.findItemByName(sItem.getItem().getName());
//            		System.out.println("Item name: " + item.getName() + " ID: " + item.getSystemId());
//            		sItem.setItem(item);
//            	}
//            }		            
//            
//            data = salesSvc.addSalesOrder(data);
//            retval.setData(data);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<SalesOrderDTO>(retval, null, HttpStatus.CREATED);
//    }
//	
//	@RequestMapping(value = "salesorders", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<SalesOrderDTO> editSalesOrder(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody SalesOrderDTO _dto) throws Exception  {        
//    	SalesOrderDTO retval = new SalesOrderDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<SalesOrderDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//            SalesOrder data = _dto.getData();
//            if(data.getItems() != null && data.getItems().size() > 0) {
//            	for(SOItem sItem : data.getItems()) {
//            		Item item = productSvc.findItemByName(sItem.getItem().getName());
//            		sItem.setItem(item);
//            	}
//            }		            
//            
//            data = salesSvc.editSalesOrder(data);
//            retval.setData(data);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<SalesOrderDTO>(retval, null, HttpStatus.CREATED);
//    }
//	
//	@RequestMapping(value = "salesorders", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//	public ResponseEntity<SalesOrderDTO> deleteSalesOrder(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("systemId") Integer _systemId) throws Exception 
//	{
//		SalesOrderDTO retval = new SalesOrderDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<SalesOrderDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//            SalesOrder so = salesSvc.deleteSalesOrder(_systemId);
//            retval.setData(so);
//    	}
//    	catch(Exception e) {
//    		svcUtil.handleException(retval, e);
//    	}
//        return new ResponseEntity<SalesOrderDTO>(retval, null, HttpStatus.OK);
//    }
//	
//	@RequestMapping(value = "salesinvoices", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<SalesInvoiceListDTO> getSalesInvoices(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam(value="systemId", required = false) Integer _systemId,
//    		@RequestParam(value="invoiceNo", required = false) String _invNo,
//    		@RequestParam(value="customerId", required = false) Integer _custId,
//    		Pageable _page ) throws Exception 
//    {
//        SalesInvoiceListDTO retval = new SalesInvoiceListDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<SalesInvoiceListDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			
//			SalesInvoice si = null;
//    		Page<SalesInvoice> pageSI = null ;
//            
//            if(_systemId != null) {
//    			si = salesSvc.findSalesInvoice(_systemId);
//    		}
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
//    			retval.setTotalItems(pageSI.getTotalElements());
//    			retval.setTotalPages(pageSI.getTotalPages());
//    			retval.setData(pageSI.getContent());
//    		}
//    		else if (si != null) {
//    			retval.setTotalItems((long)1);
//    			retval.setTotalPages(1);
//    			retval.setData(Arrays.asList(new SalesInvoice[] {si}));
//    		}
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<SalesInvoiceListDTO>(retval, null, HttpStatus.OK);
//    }
//	
//	@RequestMapping(value = "salesinvoices", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<SalesInvoiceDTO> createPayment(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody Payment _payment) throws Exception  
//	{        
//		SalesInvoiceDTO retval = new SalesInvoiceDTO();
//		try {	
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<SalesInvoiceDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//            SalesInvoice data = salesSvc.createPayment(_payment);
//            retval.setData(data);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<SalesInvoiceDTO>(retval, null, HttpStatus.CREATED);
//    }
//	
//	@RequestMapping(value = "salesinvoices", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<SalesInvoiceDTO> editPayment(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody Payment _payment) throws Exception  
//	{        
//		SalesInvoiceDTO retval = new SalesInvoiceDTO();
//		try {		
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<SalesInvoiceDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//            SalesInvoice data = salesSvc.editPayment(_payment);
//            retval.setData(data);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<SalesInvoiceDTO>(retval, null, HttpStatus.CREATED);
//    }
//	
//	@RequestMapping(value = "salesinvoices", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//	public ResponseEntity<SalesInvoiceDTO> deleteSalesInvoice(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("systemId") Integer _systemId) throws Exception 
//	{
//		SalesInvoiceDTO retval = new SalesInvoiceDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<SalesInvoiceDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//    		SalesInvoice si = salesSvc.deleteSalesInvoice(_systemId);
//            retval.setData(si);
//    	}
//    	catch(Exception e) {
//    		svcUtil.handleException(retval, e);
//    	}
//        return new ResponseEntity<SalesInvoiceDTO>(retval, null, HttpStatus.OK);
//    }
//	
//	@RequestMapping(value = "cartdetails", method = RequestMethod.GET,
//			produces = "application/json"
//	)
//    public ResponseEntity<CartDetailListDTO> getCartDetails(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("customerId") Long _custId,
//    		Pageable _page ) throws Exception 
//    {
//        CartDetailListDTO retval = new CartDetailListDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<CartDetailListDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//    		Page<CartDetail> pageCartDetail = null ;
//        		            	
//        	pageCartDetail = salesSvc.findCartDetailByCustomerId(_custId, _page);
//        	System.out.println("PAGE: " + pageCartDetail.getNumber() + " TOT E: " + pageCartDetail.getTotalElements() 
//        		+ " PAGES: " + pageCartDetail.getTotalPages() );
//            
//            if(pageCartDetail != null && pageCartDetail.getNumberOfElements() > 0) {
//    			retval.setTotalItems(pageCartDetail.getTotalElements());
//    			retval.setTotalPages(pageCartDetail.getTotalPages());
//    			retval.setData(pageCartDetail.getContent());
//    		}
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<CartDetailListDTO>(retval, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "cartdetails", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<CartDetailDTO> addCartDetail(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody CartDetailDTO _dto) throws Exception {
//    	CartDetailDTO retval = new CartDetailDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<CartDetailDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			CartDetail data = _dto.getData();
//			Item item = productSvc.findItemByName(_dto.getItem());
//			if(item == null)
//				throw new OperationException(StandardConstant.ERROR_CANT_FIND_PRODUCT, null);
//			data.getItem().setSystemId(item.getSystemId());
//			data = salesSvc.addCartDetail(data);
//            retval.setData (data);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<CartDetailDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "cartdetails", method = RequestMethod.PUT,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<CartDetailDTO> editCartDetail(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody CartDetailDTO _dto) throws Exception {
//    	CartDetailDTO retval = new CartDetailDTO();
//		try {
//			int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<CartDetailDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//			
//			CartDetail data = _dto.getData();
//			Item item = productSvc.findItemByName(_dto.getItem());
//			if(item == null)
//				throw new OperationException(StandardConstant.ERROR_CANT_FIND_PRODUCT, null);
//			data.getItem().setSystemId(item.getSystemId());
//			data = salesSvc.editCartDetail(data);
//            retval.setData (data);
//		}
//		catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<CartDetailDTO>(retval, null, HttpStatus.CREATED);
//    }
//    
//    @RequestMapping(value = "cartdetails", method = RequestMethod.DELETE,
//			produces = "application/json"
//	)
//    public ResponseEntity<CartDetailDTO> deleteCartDetail(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestParam("customerId") Long _customerId, @RequestParam("itemId") Integer _itemId) throws Exception 
//    {
//    	CartDetailDTO retval = new CartDetailDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<CartDetailDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//    		CartDetail cd = salesSvc.deleteCartDetail(_customerId, _itemId);
//            retval.setData(cd);
//    	}
//    	catch(Exception e) {
//			svcUtil.handleException(retval, e);
//		}
//        return new ResponseEntity<CartDetailDTO>(retval, null, HttpStatus.OK);
//    }
//    
//    @RequestMapping(value = "checkout", method = RequestMethod.POST,
//			consumes = "application/json", produces = "application/json"
//	)
//    public ResponseEntity<SalesOrderDTO> checkOut(
//    		@RequestHeader(value="Authorization", required = false) String _auth,
//    		@RequestBody CustomerDTO _cust) throws Exception 
//    {
//    	SalesOrderDTO retval = new SalesOrderDTO();
//    	try {
//    		int tokenStat = ServiceUtil.checkAuth(_auth);
//			if(tokenStat != StandardConstant.TOKEN_VALID) {
//				retval.setErrorCode(tokenStat);
//				retval.setErrorMessage(svcUtil.getErrorMessageFromCode(tokenStat));
//				return new ResponseEntity<SalesOrderDTO>(retval, null, HttpStatus.UNAUTHORIZED); 
//			}
//    		Customer customer = userCredSvc.findCustomer(_cust.getSystemId());
//    		System.out.println("CUST ID: " + customer != null ? customer.getSystemId() : 0);
//    		SalesOrder so = salesSvc.checkOut(customer);
//    		retval.setData(so);
//    	}
//    	catch(Exception _e) {
//    		svcUtil.handleException(retval,  _e);
//    	}
//    	return new ResponseEntity<SalesOrderDTO>(retval, null, HttpStatus.OK);
//    }
//    
//}
