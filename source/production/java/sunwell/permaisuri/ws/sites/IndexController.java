package sunwell.permaisuri.ws.sites;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import sunwell.permaisuri.ws.annotation.WebController;
import sunwell.permaisuri.ws.controller.ServiceUtil;
import sunwell.permaisuri.ws.exception.ResourceNotFoundException;

@WebController
public class IndexController
{
	
	@PersistenceContext
	EntityManager em ;
	
    @RequestMapping("/")
    public View index()
    {
    	System.out.println("INDEX IS CALLED");
        return new RedirectView("/welcome", true, false);
    }
    
    @RequestMapping("welcome")
    public String welcome()
    {
    	return "welcome";
    }
    
    @RequestMapping("activate")
    public String activate(
//    		@RequestParam(value="pwd") String _pwd,
    		@RequestParam(value="error", required = false) String _errMsg, Map<String, Object> _model)
    {
//    	if(_pwd == null || !_pwd.equals(ServiceUtil.PWD)) 
//    		throw new ResourceNotFoundException();
    	
    	if(_errMsg == null)
    		return "activate";
    	else {
    		_model.put("error", _errMsg);
    		return "error";
    	}
    }
}
