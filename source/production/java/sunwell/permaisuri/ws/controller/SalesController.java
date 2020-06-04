package sunwell.permaisuri.ws.controller;

import java.util.Arrays;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sunwell.permaisuri.bus.dto.customer.CustomerDTO;
import sunwell.permaisuri.bus.dto.inventory.ItemCategoryDTO;
import sunwell.permaisuri.bus.dto.inventory.ItemCategoryListDTO;
import sunwell.permaisuri.bus.dto.inventory.ItemDTO;
import sunwell.permaisuri.bus.dto.sales.CartDetailDTO;
import sunwell.permaisuri.bus.dto.sales.CartDetailListDTO;
import sunwell.permaisuri.bus.dto.sales.SalesInvoiceDTO;
import sunwell.permaisuri.bus.dto.sales.SalesInvoiceListDTO;
import sunwell.permaisuri.bus.dto.sales.SalesOrderDTO;
import sunwell.permaisuri.bus.dto.sales.SalesOrderListDTO;
import sunwell.permaisuri.bus.exception.OperationException;
import sunwell.permaisuri.bus.service.ExampleSvcInt;
import sunwell.permaisuri.bus.service.Filters;
import sunwell.permaisuri.bus.service.InventoryService;
import sunwell.permaisuri.bus.service.InventorySvc;
import sunwell.permaisuri.bus.service.ProductService;
import sunwell.permaisuri.bus.service.ProductSvc;
import sunwell.permaisuri.bus.service.SalesService;
import sunwell.permaisuri.bus.service.SalesSvc;
import sunwell.permaisuri.bus.service.StandardConstant;
import sunwell.permaisuri.bus.service.TransSvc;
import sunwell.permaisuri.bus.service.UserCredService;
import sunwell.permaisuri.bus.service.UserCredSvc;
import sunwell.permaisuri.core.entity.cred.UserCredential;
import sunwell.permaisuri.core.entity.customer.Customer;
import sunwell.permaisuri.core.entity.inventory.Item;
import sunwell.permaisuri.core.entity.inventory.ItemCategory;
import sunwell.permaisuri.core.entity.inventory.Merk;
import sunwell.permaisuri.core.entity.inventory.Metrics;
import sunwell.permaisuri.core.entity.inventory.ProductImage;
import sunwell.permaisuri.core.entity.sales.CartDetail;
import sunwell.permaisuri.core.entity.sales.CartDetailPK;
import sunwell.permaisuri.core.entity.sales.Payment;
import sunwell.permaisuri.core.entity.sales.PaymentImage;
import sunwell.permaisuri.core.entity.sales.SalesInvoice;
import sunwell.permaisuri.core.entity.sales.SalesOrder;
import sunwell.permaisuri.core.entity.sales.SalesOrderItem;
import sunwell.permaisuri.core.entity.warehouse.OnHandStock;
import sunwell.permaisuri.ws.annotation.RestEndpoint;
import sunwell.permaisuri.ws.controller.ServiceUtil.TokenInfo;

@RestEndpoint
public class SalesController
{
	@Autowired
	SalesService salesSvc ;
	
//	@Autowired
//	SalesService salesSvc ;
	
	@Autowired
	ProductService productSvc ;
	
	@Autowired
	UserCredService userCredSvc ;
	
	@Autowired
	InventoryService invSvc;
	
	@Autowired
	ServiceUtil svcUtil ;
	
//	@Autowired
//	ExampleSvcInt esi;
//	
//	@Autowired
//	TransSvc transSvc;
	
	@Inject
    MessageSource messageSource;
	
	@RequestMapping(value = "salesorders", method = RequestMethod.GET,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> getSalesOrders(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="systemId", required = false) Long _systemId,
//    		@RequestParam(value="soNo", required = false) String _soNo,
    		@RequestParam(value="customerId", required = false) Long _custId,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;

		try {
			Object mainData = null;
    		Page<SalesOrder> pageSO = null ;
    		SalesOrder so = null;
    		Customer cust = null;
			int totalPages = 0;
			long totalItems = 0;
			        
			long id = -1;
			
			if(_systemId != null) {
				so = salesSvc.findSalesOrder(_systemId);
				if(so != null)
					id = so.getCustomer().getSystemId();
				else
					id = -2;
			}
			else if(_custId != null){
				id = _custId;
			}
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_SALES_ORDERS, id);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_custId = ti.id;
			
			if(_systemId != null) 
				;// do nothing
            else {
            	if(_custId != null) {
            		pageSO = salesSvc.findSalesOrdersByCustId(_custId, _page);
            	}
            	else
            		pageSO = salesSvc.findAllSalesOrder(_page);            	
            }
            
            if(so != null) {
            	totalPages = 1;
            	totalItems = 1;
            	mainData = new SalesOrderDTO(so);
            }
            else if(pageSO != null && pageSO.getContent().size() > 0 ) {
            	totalPages = pageSO.getTotalPages();
            	totalItems = pageSO.getTotalElements();
            	List<SalesOrder> salesOrders = pageSO.getContent();
            	List<SalesOrderDTO> listSODTO = new LinkedList<>();
            	for(SalesOrder s : salesOrders) {
            		listSODTO.add(new SalesOrderDTO(s));
            	}
            	mainData = listSODTO;
        	}
            
//    		String sender="sendergmailid@gmail.com";//write here sender gmail id  
//    		String receiver="receiveremailid@gmail.com";//write here receiver id  
//    		m.sendMail(sender,receiver,"hi","welcome"); 
    		
//    		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//    	    mailSender.setHost("smtp.gmail.com");
//    	    mailSender.setPort(587);
//    	     
//    	    mailSender.setUsername("susantobenny50@gmail.com");
//    	    mailSender.setPassword("oneandonly");
//    	     
//    	    Properties props = mailSender.getJavaMailProperties();
//    	    props.put("mail.transport.protocol", "smtp");
//    	    props.put("mail.smtp.auth", "true");
//    	    props.put("mail.smtp.starttls.enable", "true");
//    	    props.put("mail.debug", "true");
//    	    
//    	    SimpleMailMessage message = new SimpleMailMessage(); 
//    	    message.setFrom("susantobenny50@gmail.com");
//            message.setTo("mideel_neutral@yahoo.com"); 
//            message.setSubject("Test"); 
//            message.setText("Hello");
//            mailSender.send(message);
            
            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
//		salesSvc.testAsynch();
//		esi.testMethod();
//		transSvc.transService();
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
	
	@RequestMapping(value = "salesorders", method = RequestMethod.GET,
			produces = "application/json", params="criteria"
	)
    public ResponseEntity<Map<String,Object>> getSalesOrders(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="criteria") List<String> _filters,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
	
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_SALES_ORDERS, -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
			
			Object mainData = null;
    		Page<SalesOrder> pageSO = null ;
			int totalPages = 0;
			long totalItems = 0;
			Filters filters =  svcUtil.convertToFilters(_filters, SalesOrder.class);
			
			pageSO = salesSvc.findSalesOrders(filters, _page);
			if(pageSO != null && pageSO.getNumberOfElements() > 0) {
            	totalPages = pageSO.getTotalPages();
            	totalItems = pageSO.getTotalElements();
            	List<SalesOrder> salesOrders = pageSO.getContent();
            	
            	// autthorization        		
            	for(SalesOrder so : salesOrders) {
            		if(ti.access == ServiceUtil.ACCESS_LIMITED && so.getCustomer().getSystemId() != ti.id)
            			return new ResponseEntity<Map<String,Object>>(svcUtil.returnErrorData(ServiceUtil.ERROR_NOT_PERMITTED),null, HttpStatus.OK);
            	}
            	
            	List<SalesOrderDTO> listSODTO = new LinkedList<>();
            	for(SalesOrder s : salesOrders) {
            		listSODTO.add(new SalesOrderDTO(s));
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
	
	@RequestMapping(value = "salesorders", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> addSalesOrder(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody SalesOrderDTO _dto) throws Exception  
	{     
		Map<String,Object> retData;

		try {
			Customer cust = null;
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_SALES_ORDERS, 
					_dto.getCustomer() != null ? _dto.getCustomer() : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_dto.setCustomer(ti.id);
    		
            SalesOrder data = _dto.getData();
            data.setCustomer(new Customer(_dto.getCustomer()));            
            
            data = salesSvc.addSalesOrder(data);
            retData = svcUtil.returnSuccessfulData(new SalesOrderDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "salesorders", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editSalesOrder(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody SalesOrderDTO _dto) throws Exception  
	{     
		Map<String,Object> retData;

		try {		
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_SALES_ORDERS, 
					_dto.getCustomer() != null ? _dto.getCustomer() : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_dto.setCustomer(ti.id);
			
            SalesOrder data = _dto.getData();
            System.out.println("canceled status: " + data.getCanceledStatus());
            data.setCustomer(new Customer(_dto.getCustomer()));
            data = salesSvc.editSalesOrder(data);
            retData = svcUtil.returnSuccessfulData(new SalesOrderDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "salesorders", method = RequestMethod.DELETE,
			produces = "application/json"
	)
	public ResponseEntity<Map<String,Object>> deleteSalesOrder(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam("systemId") Long _systemId) throws Exception 
	{
		Map<String,Object> retData;

    	try {
    		SalesOrder so = salesSvc.findSalesOrder(_systemId);
    		Customer cust = null;
    		if(so != null)
    			cust = so.getCustomer();
    		
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_SALES_ORDERS, cust != null ? cust.getSystemId() : -2);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
            SalesOrder data = salesSvc.deleteSalesOrder(_systemId);
            retData = svcUtil.returnSuccessfulData(new SalesOrderDTO(data), 1, 1);
    	}
    	catch(Exception e) {
    		retData = svcUtil.handleException(e);
    	}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
	
	@RequestMapping(value = "salesinvoices", method = RequestMethod.GET,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> getSalesInvoices(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="systemId", required = false) Long _systemId,
    		@RequestParam(value="invoiceNo", required = false) String _invNo,
    		@RequestParam(value="customerId", required = false) Long _custId,
    		Pageable _page ) throws Exception 
    {
		Map<String,Object> retData;

		try {
			Object mainData = null;
			SalesInvoice si = null;
    		Page<SalesInvoice> pageSI = null ;
    		int totalPages = 0;
			long totalItems = 0;
			Customer cust = null;
			long id = -1;
			
			if(_systemId != null) {
				si = salesSvc.findSalesInvoice(_systemId);
				if(si != null)
					id = si.getCustomer().getSystemId();
				else
					id = -2;
			}
			else if(_custId != null){
				id = _custId;
			}
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_SALES_INVOICES, id);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_custId = ti.id;
			
			if(_systemId != null) 
				;//do nothing
            else if(_invNo != null)
        		si = salesSvc.findSalesInvoiceByNo(_invNo);
            else {
            	if(_custId != null) {
        			pageSI = salesSvc.findSalesInvoicesByCustId(_custId, _page);
            	}
            	else
            		pageSI = salesSvc.findAllSalesInvoice(_page);
            }
            
            if(pageSI != null && pageSI.getNumberOfElements() > 0) {
            	List<SalesInvoice> invoices = pageSI.getContent();
            	List<SalesInvoiceDTO> invoicesData = new LinkedList<>();
            	for(SalesInvoice s : invoices)
            		invoicesData.add(new SalesInvoiceDTO(s));
            	mainData = invoicesData;
            	totalPages = pageSI.getTotalPages();
            	totalItems = pageSI.getTotalElements();
    		}
    		else if (si != null) {
    			mainData = new SalesInvoiceDTO(si);
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
	
	@RequestMapping(value = "salesinvoices", method = RequestMethod.GET,
			produces = "application/json", params="criteria"
	)
    public ResponseEntity<Map<String,Object>> getSalesInvoices(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="criteria") List<String> _filters,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
	
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_SALES_INVOICES, -1);
			retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		        	
			Object mainData = null;
    		Page<SalesInvoice> pageSI = null ;
			int totalPages = 0;
			long totalItems = 0;
			Filters filters =  svcUtil.convertToFilters(_filters, SalesInvoice.class);			
			pageSI = salesSvc.findSalesInvoices(filters, _page);
			if(pageSI != null && pageSI.getNumberOfElements() > 0) {
            	totalPages = pageSI.getTotalPages();
            	totalItems = pageSI.getTotalElements();
            	List<SalesInvoice> salesInvoices = pageSI.getContent();
           
            	//authorization
            	for(SalesInvoice si : salesInvoices) {
            		if(ti.access == ServiceUtil.ACCESS_LIMITED && si.getCustomer().getSystemId() != ti.id)
            			return new ResponseEntity<Map<String,Object>>(svcUtil.returnErrorData(ServiceUtil.ERROR_NOT_PERMITTED),null, HttpStatus.OK);
            	}
            	
            	List<SalesInvoiceDTO> listSIDTO = new LinkedList<>();
            	for(SalesInvoice s : salesInvoices) {
            		listSIDTO.add(new SalesInvoiceDTO(s));
            	}
            	mainData = listSIDTO;
        	}
            
            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
		
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
	
	@RequestMapping(value = "salesinvoices", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> createPayment(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody Payment _payment) throws Exception  
	{        
		Map<String,Object> retData;
		try {	
			Customer cust = null;
			
			SalesOrder so = salesSvc.findSalesOrder(_payment.getSoId());
			if(so != null)
				cust = so.getCustomer();
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_SALES_INVOICES, 
					cust != null ? cust.getSystemId() : -2);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
            SalesInvoice data = salesSvc.createPayment(_payment);
            retData = svcUtil.returnSuccessfulData(new SalesInvoiceDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "salesinvoices", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editPayment(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody Payment _payment) throws Exception  
	{        
		Map<String,Object> retData;
		try {	
			Customer cust = null;
			SalesInvoice si = salesSvc.findSalesInvoiceByNo(_payment.getInvoiceNo());
			if(si != null)
				cust = si.getCustomer();
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_SALES_INVOICES, 
					cust != null ? cust.getSystemId() : -2);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
            SalesInvoice data = salesSvc.editPayment(_payment);
            retData = svcUtil.returnSuccessfulData(new SalesInvoiceDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
	
	@RequestMapping(value = "salesinvoices", method = RequestMethod.DELETE,
			produces = "application/json"
	)
	public ResponseEntity<Map<String,Object>> deleteSalesInvoice(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam("systemId") Long _systemId) throws Exception 
	{
		Map<String,Object> retData;
    	try {
    		SalesInvoice si = salesSvc.findSalesInvoice(_systemId);
    		Customer cust = null;
    		if(si != null)
    			cust = si.getCustomer();
    		
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_SALES_INVOICES, cust != null ? cust.getSystemId() : -2);
    		retData = svcUtil.getErrorFromToken(ti, true);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		
    		SalesInvoice data = salesSvc.deleteSalesInvoice(_systemId);
            retData = svcUtil.returnSuccessfulData(new SalesInvoiceDTO(data), 1, 1);
    	}
    	catch(Exception e) {
    		retData = svcUtil.handleException(e);
    	}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
	
	@RequestMapping(value = "cartdetails", method = RequestMethod.GET,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> getCartDetails(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="customerId", required=false) Long _custId,
    		Pageable _page ) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_CART_DETAILS, _custId != null ? _custId : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_custId = ti.id ;
    	
    		Page<CartDetail> pageCartDetail = null ;
    		int totalPages = 0;
			long totalItems = 0;
			
        	pageCartDetail = salesSvc.findCartDetailByCustomerId(_custId, _page);
            
            if(pageCartDetail != null && pageCartDetail.getNumberOfElements() > 0) {
    			List<CartDetail> cartDetails = pageCartDetail.getContent();
    			List<CartDetailDTO> cartDetailsData = new LinkedList<>();
    			for(CartDetail cd : cartDetails) 
    				cartDetailsData.add(new CartDetailDTO(cd));
    			totalPages = pageCartDetail.getTotalPages();
    			totalItems = pageCartDetail.getTotalElements();
    			
    			retData = svcUtil.returnSuccessfulData(cartDetailsData, totalPages, totalItems);
    		}
		}
		catch(Exception e) {
			svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
	
	@RequestMapping(value = "cartdetails", method = RequestMethod.GET,
			produces = "application/json", params="criteria"
	)
    public ResponseEntity<Map<String,Object>> getCartDetails(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="criteria") List<String> _filters,
    		Pageable _page
    		) throws Exception 
    {
		Map<String,Object> retData = null;
	
		try {
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_CART_DETAILS, -1);
			retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    	
			Object mainData = null;
    		Page<CartDetail> pageCD = null ;
			int totalPages = 0;
			long totalItems = 0;
			Filters filters =  svcUtil.convertToFilters(_filters, CartDetail.class);			
			pageCD = salesSvc.findCartDetails(filters, _page);
			if(pageCD != null && pageCD.getNumberOfElements() > 0) {
            	totalPages = pageCD.getTotalPages();
            	totalItems = pageCD.getTotalElements();
            	List<CartDetail> cds = pageCD.getContent();
            	
            	//authorization
            	for(CartDetail cd : cds) {
            		if(ti.access == ServiceUtil.ACCESS_LIMITED && cd.getCustomer().getSystemId() != ti.id)
            			return new ResponseEntity<Map<String,Object>>(svcUtil.returnErrorData(ServiceUtil.ERROR_NOT_PERMITTED),null, HttpStatus.OK);
            	}
            	
            	List<CartDetailDTO> listCDDTO = new LinkedList<>();
            	for(CartDetail c : cds) {
            		listCDDTO.add(new CartDetailDTO(c));
            	}
            	mainData = listCDDTO;
        	}
            
            retData = svcUtil.returnSuccessfulData(mainData, totalPages, totalItems);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
		
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
    @RequestMapping(value = "cartdetails", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> addCartDetail(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody CartDetailDTO _dto) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CREATE_CART_DETAILS, _dto.getCustomer() != null ? _dto.getCustomer() : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_dto.setCustomer(ti.id);
    		
			CartDetail data = _dto.getData();
			data = salesSvc.addCartDetail(data);
			retData = svcUtil.returnSuccessfulData(new CartDetailDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "cartdetails", method = RequestMethod.PUT,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> editCartDetail(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody CartDetailDTO _dto) throws Exception 
    {
		Map<String,Object> retData = null;
		try {
			
			TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_UPDATE_CART_DETAILS, _dto.getCustomer() != null ? _dto.getCustomer() : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_dto.setCustomer(ti.id);
    		
			CartDetail data = _dto.getData();
			data = salesSvc.editCartDetail(data);
			retData = svcUtil.returnSuccessfulData(new CartDetailDTO(data), 1, 1);
		}
		catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "cartdetails", method = RequestMethod.DELETE,
			produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> deleteCartDetail(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam(value="customerId", required=false) Long _customerId, @RequestParam("itemId") Integer _itemId) throws Exception 
    {
		Map<String,Object> retData = null;
    	try {
    		
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_DELETE_CART_DETAILS, _customerId != null ? _customerId : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED) 
    			_customerId = ti.id;
    		
    		Customer cust = userCredSvc.findCustomer(_customerId);
    		CartDetail cd = salesSvc.deleteCartDetail(_customerId, _itemId);
            retData = svcUtil.returnSuccessfulData(new CartDetailDTO(cd), 1, 1);
    	}
    	catch(Exception e) {
			retData = svcUtil.handleException(e);
		}
        return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    }
    
    @RequestMapping(value = "checkout", method = RequestMethod.POST,
			consumes = "application/json", produces = "application/json"
	)
    public ResponseEntity<Map<String,Object>> checkOut(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestBody CustomerDTO _dto) throws Exception 
    {
		Map<String,Object> retData = null;
    	try {
    		
    		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_CHECK_OUT, 
    				_dto.getSystemId() != null ? _dto.getSystemId() : -1);
    		retData = svcUtil.getErrorFromToken(ti, false);
    		if(retData != null)
    			return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.OK);
    		if(ti.access == ServiceUtil.ACCESS_LIMITED)
    			_dto.setSystemId(ti.id);
    		
    		Customer cust = null;
			if(_dto.getSystemId() != null)
				cust = userCredSvc.findCustomer(_dto.getSystemId());
			
			List<CartDetail> carts = cust.getCartDetails();
			List<String> stockErrorMessages = new LinkedList<>();
			List<Item> notEnoughtStocks = new LinkedList<>();
			for(CartDetail cd : carts) {
				double ohQty = invSvc.getOnHandQtyByItem(cd.getItem());
				System.out.println("OH QTY: " + ohQty + " name: " + cd.getItem().getName());
				if(ohQty < cd.getQty()) 
					notEnoughtStocks.add(cd.getItem());  
			}
			
			if(notEnoughtStocks.size() > 0) {
				
				String errorMessage = "";
				List<Integer> itemIds = new LinkedList<>();
				
				for(Item i : notEnoughtStocks) {
					stockErrorMessages.add(svcUtil.getErrorMessageFromCode(
							StandardConstant.ERROR_NOT_ENOUGH_STOCK_FOR_ITEM, new Object[] {i}));
					
					itemIds.add(i.getSystemId());
				}
				for(String errMess : stockErrorMessages) {
					errorMessage += errMess + "\n";
				}
				
				errorMessage = errorMessage.substring(0, errorMessage.length() - 1);
				retData = new HashMap<>();
				retData.put("status", "error");
				retData.put("errorCode", StandardConstant.ERROR_NOT_ENOUGH_STOCK);
				retData.put("errorMessage", errorMessage);
				retData.put("items", itemIds);
			
				return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.BAD_REQUEST);
			}
			
    		SalesOrder so = salesSvc.checkOut(cust);
    		retData = svcUtil.returnSuccessfulData(new SalesOrderDTO(so), 1, 1);
    		return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.CREATED);
    	}
    	catch(Exception _e) {
    		retData = svcUtil.handleException(_e);
    		return new ResponseEntity<Map<String,Object>>(retData, null, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping(value = "payments/images", method = RequestMethod.GET
			,produces = {"image/png", "image/jpg"}
	)
    public ResponseEntity<byte[]> getImage(
    		@RequestHeader(value="Authorization", required = false) String _auth,
    		@RequestParam("invoice") Long _invoice) throws Exception
    {
		TokenInfo ti = svcUtil.authenticate(_auth, UserCredential.TASK_VIEW_PAYMENT_IMAGE, -1);
		if(ti.access != ServiceUtil.ACCESS_ALLOW) {
			return new ResponseEntity<byte[]>(null, null, HttpStatus.UNAUTHORIZED);
		}
		else {
			PaymentImage pi = salesSvc.findPaymentImageByInvoiceId(_invoice);
			byte[] imageData = pi != null ? pi.getImageData() : null;
			return new ResponseEntity<byte[]>(imageData, null, HttpStatus.OK);
		}
    }
    
}
