package sunwell.permaisuri.ws.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sunwell.permaisuri.bus.repository.CustomerRepo;
import sunwell.permaisuri.bus.service.UserCredService;
import sunwell.permaisuri.bus.service.UserCredSvc;
import sunwell.permaisuri.bus.util.Util;
import sunwell.permaisuri.core.entity.cred.UserCredential;
import sunwell.permaisuri.core.entity.customer.Customer;

@Service
@Transactional
public class RegistrationSvc implements RegistrationService
{
	private static final String MAIL_SOURCE = "Sunwell";
	private static final String MAIL_SUBJECT = "Confirm your registration";
//	private static final String ACTIVATION_URL = "http://139.59.101.119:8080/permaisuri/resources/activate";
	private static final String ACTIVATION_URL = "http://localhost:8080/permaisuri/resources/activate";
	private static final String MAIL_CONTENT = "Please visit the following link to activate your registration:\n\n";
	
	@Autowired
	UserCredService userService;
	
	@Autowired
	CustomerRepo custRepo;
	
	@Autowired
	JavaMailSender mailSender;
	
//	@Async
	public Customer registerCustomer(Customer _cust) throws Exception {
		UserCredential uc = _cust.getUserCredential();
    	_cust.setType(UserCredential.TYPE_CUSTOMER);
		uc.setPwd(BCrypt.hashpw(uc.getPwd(), BCrypt.gensalt()));
		
		
        Customer cust = custRepo.saveAndFlush(_cust);
        
		userService.validate(uc.getUserName(), "12345");
        
		String token = Util.getRandomString(32) + cust.getSystemId() ;
		String mailContent = MAIL_CONTENT + ACTIVATION_URL + "?token=" + token;
		cust.getUserCredential().setRegistrationToken(token);
		sendEmail(MAIL_SOURCE, cust.getEmail(), MAIL_SUBJECT, mailContent);
		
		return cust;
	}
	
	private void sendEmail(String _from, String _to, String _subject, String _content) throws Exception {
		SimpleMailMessage message = new SimpleMailMessage(); 
	    message.setFrom(_from);
        message.setTo(_to); 
        message.setSubject(_subject); 
        message.setText(_content);
        mailSender.send(message);
	}
	
	
}
