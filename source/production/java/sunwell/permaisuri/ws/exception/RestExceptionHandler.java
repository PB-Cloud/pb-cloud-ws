package sunwell.permaisuri.ws.exception;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import sunwell.permaisuri.bus.service.StandardConstant;
import sunwell.permaisuri.ws.annotation.RestEndpointAdvice;
import sunwell.permaisuri.ws.controller.ServiceUtil;

@RestEndpointAdvice
public class RestExceptionHandler
{
	@Autowired
	ServiceUtil svcUtil; 
	
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e)
    {
    	System.out.println("ERROR, CATCH ConstraintViolationException: " + e.getMessage() + " class: " + e.getClass().getName()); 
		e.printStackTrace();
        ErrorResponse errors = new ErrorResponse();
        for(ConstraintViolation violation : e.getConstraintViolations())
        {
            ErrorItem error = new ErrorItem();
            error.setCode(violation.getMessageTemplate());
            error.setMessage(violation.getMessage());
            errors.addError(error);
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handle(Exception e)
    {
		System.out.println("ERROR, CATCH EXCEPTION: " + e.getMessage() ); 
		e.printStackTrace();
		Map<String,Object> retval = svcUtil.returnErrorData(ServiceUtil.ERROR_INTERNAL_SERVER, e.getMessage());
        return new ResponseEntity<>(retval, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> handle(HttpMessageNotReadableException e)
    {
		System.out.println("ERROR< CATCH HttpMessageNotReadableException: " + e.getMessage()); 
		e.printStackTrace();
		Map<String,Object> retval = svcUtil.returnErrorData(ServiceUtil.ERROR_BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(retval, HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("unused")
    public static class ErrorItem
    {
        private String code;
        private String message;

        @XmlAttribute
        public String getCode()
        {
            return code;
        }

        public void setCode(String code)
        {
            this.code = code;
        }

        @XmlValue
        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }
    }

    @SuppressWarnings("unused")
    @XmlRootElement(name = "errors")
    public static class ErrorResponse
    {
        private List<ErrorItem> errors = new ArrayList<>();

        @XmlElement(name = "error")
        public List<ErrorItem> getErrors()
        {
            return errors;
        }

        public void setErrors(List<ErrorItem> errors)
        {
            this.errors = errors;
        }

        public void addError(ErrorItem error)
        {
            this.errors.add(error);
        }
    }
}
