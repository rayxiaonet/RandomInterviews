package net.rayxiao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@SpringBootApplication
@Order(6)
@EnableOAuth2Client
@EnableAuthorizationServer
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
/**
 * Main applicaiton via Spring boot
 * and the rest requests controller
 */
public class BankApplication extends WebSecurityConfigurerAdapter {


    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Autowired
    private AccountRepository repository;

    @Autowired
    private AccountManager accountManager;

    private String[] getSessionUser() {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        Map<String, String> details = (Map<String, String>) userAuthentication.getDetails();
        return new String[]{details.get("id"), details.get("name")};

    }

    @RequestMapping({"/user", "/me"})
    public Map<String, String> user(Principal principal) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", principal.getName());
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        Map<String, String> details = (Map<String, String>) userAuthentication.getDetails();
        return map;
    }

    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public
    @ResponseBody
    Account balance() {
        return accountManager.getBalance(getSessionUser()[0]);
    }


    @RequestMapping(value = "/withdrawal", method = RequestMethod.POST)
    public
    @ResponseBody
    Account withdrawal(@RequestBody TransactionData data) {
        final BigDecimal amount;
        try {
            amount = new BigDecimal(data.getAmount());
        } catch (Exception e) {
            throw new InvalidAccountRequestException("Invalid amount");
        }
        return accountManager.withdraw(getSessionUser()[0], getSessionUser()[1], amount, data.getNotes());
    }

    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public
    @ResponseBody
    Account deposit(@RequestBody TransactionData data) {
        final BigDecimal amount;
        try {
            amount = new BigDecimal(data.getAmount());
        } catch (Exception e) {
            throw new InvalidAccountRequestException("Invalid amount");
        }
        return accountManager.deposit(getSessionUser()[0], getSessionUser()[1], amount, data.getNotes());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**", "/webjars/**").permitAll().anyRequest()
                .authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")).and().logout()
                .logoutSuccessUrl("/").permitAll().and().csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/me").authorizeRequests().anyRequest().authenticated();
        }
    }


    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }


    /**
     * Facebook oauth filter
     *
     * @return
     */
    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook() {
        return new ClientResources();
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(facebook(), "/login/facebook"));
        filter.setFilters(filters);
        return filter;
    }

    /**
     * Filter for OAuth Single Sign On verification
     *
     * @param client
     * @param path
     * @return
     */

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationFilter = new OAuth2ClientAuthenticationProcessingFilter(
                path);
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        oAuth2ClientAuthenticationFilter.setRestTemplate(oAuth2RestTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(),
                client.getClient().getClientId());
        tokenServices.setRestTemplate(oAuth2RestTemplate);
        oAuth2ClientAuthenticationFilter.setTokenServices(tokenServices);
        return oAuth2ClientAuthenticationFilter;
    }

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }

}


/**
 * Internal class used for oauth
 */
class ClientResources {

    @NestedConfigurationProperty
    private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

    @NestedConfigurationProperty
    private ResourceServerProperties resource = new ResourceServerProperties();

    public AuthorizationCodeResourceDetails getClient() {
        return client;
    }

    public ResourceServerProperties getResource() {
        return resource;
    }
}

/**
 * Internal class used for http post data mapping
 */
class TransactionData {
    private String amount;
    private String notes;

    public String getAmount() {
        return amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public TransactionData() {

    }

    public TransactionData(String amount, String notes) {
        this.amount = amount;
        this.notes = notes;
    }
}
