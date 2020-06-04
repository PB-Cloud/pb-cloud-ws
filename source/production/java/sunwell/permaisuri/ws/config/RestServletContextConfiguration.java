package sunwell.permaisuri.ws.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import sunwell.permaisuri.ws.annotation.RestEndpoint;
import sunwell.permaisuri.ws.annotation.RestEndpointAdvice;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebMvc
@EnableScheduling
@EnableAsync(
        mode = AdviceMode.PROXY, // proxyTargetClass = false,
        order = Ordered.HIGHEST_PRECEDENCE
)
@EnableTransactionManagement(
        mode = AdviceMode.PROXY, // proxyTargetClass = false,
        order = Ordered.LOWEST_PRECEDENCE
)
@ComponentScan(
        basePackages = "sunwell.permaisuri.ws",
        useDefaultFilters = false,
        includeFilters =
        @ComponentScan.Filter({RestEndpoint.class, RestEndpointAdvice.class, Service.class})
)
//@ImportResource("classpath:/sunwell/permaisuri/ws/config/restServletContext.xml")
public class RestServletContextConfiguration extends WebMvcConfigurerAdapter
{
	@Inject ApplicationContext applicationContext;
    @Inject ObjectMapper objectMapper;
    @Inject Marshaller marshaller;
    @Inject Unmarshaller unmarshaller;
    @Inject SpringValidatorAdapter validator;

    @Bean
    public LocaleResolver localeResolver()
    {
        return new AcceptHeaderLocaleResolver();
    }
    
    @Override
    public Validator getValidator()
    {
        return this.validator;
    }
    
    @Override
    public void configureMessageConverters(
            List<HttpMessageConverter<?>> converters
    ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(sdf);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
	        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
	        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
	        .withSetterVisibility(JsonAutoDetect.Visibility.NONE));
        
    		converters.add(new SourceHttpMessageConverter<>());
        converters.add(new ByteArrayHttpMessageConverter());
        
        MarshallingHttpMessageConverter xmlConverter =
                new MarshallingHttpMessageConverter();
        xmlConverter.setSupportedMediaTypes(Arrays.asList(
                new MediaType("application", "xml"),
                new MediaType("text", "xml")
        ));
        xmlConverter.setMarshaller(this.marshaller);
        xmlConverter.setUnmarshaller(this.unmarshaller);
        converters.add(xmlConverter);

        MappingJackson2HttpMessageConverter jsonConverter =
                new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Arrays.asList(
                new MediaType("application", "json"),
                new MediaType("text", "json")
        ));
        jsonConverter.setObjectMapper(this.objectMapper);
        converters.add(jsonConverter);
    }

    @Override
    public void configureContentNegotiation(
            ContentNegotiationConfigurer configurer)
    {
        configurer.favorPathExtension(false).favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers)
    {
//	    Sort defaultSort = new Sort(new Sort.Order(Sort.Direction.ASC, "systemId")); 
//	    Pageable defaultPageable = new PageRequest(0, 10, defaultSort);
	    Pageable defaultPageable = new PageRequest(0, 10);
	    SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
	    // sortParameter defaults to "sort" 
	    sortResolver.setSortParameter("$paging.sort"); 
//	    sortResolver.setFallbackSort(defaultSort);
	    PageableHandlerMethodArgumentResolver pageableResolver =
	    new PageableHandlerMethodArgumentResolver(sortResolver);
	    pageableResolver.setMaxPageSize(200); 
	    pageableResolver.setOneIndexedParameters(true); 
	    // page starts at 1, not 0 
	    // pageProperty defaults to "page" and sizeProperty to "size"
	    // The following is equal to .setPageProperty("$paging.page") and
	    // .setSizeProperty("$paging.size"); 
	    pageableResolver.setPrefix("$paging."); 
	    pageableResolver.setFallbackPageable(defaultPageable);
	    resolvers.add(sortResolver);
	    resolvers.add(pageableResolver); 
    }
   
    @Override
    public void addFormatters(FormatterRegistry registry) {
	    if(!(registry instanceof FormattingConversionService)) {
		    System.out.println("Unable to register Spring Data JPA converter.");
		    return; 
	    }
	    
	    // DomainClassConverter adds itself to the registry DomainClassConverter<FormattingConversionService> converter =
	    DomainClassConverter<FormattingConversionService> converter = new DomainClassConverter<>((FormattingConversionService)registry); 
	    converter.setApplicationContext(this.applicationContext);
    }
    
    
}
