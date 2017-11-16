package net.rayxiao;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;


/**
 * Created by rxiao on 10/31/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)

@SpringApplicationConfiguration(classes = {BankApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)


public class BankApplicationTests {
    @Autowired
    private AccountRepository repository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Mock
    private Authentication userAuth;
    @Mock
    private OAuth2Authentication authentication;
    @Mock
    SecurityContext securityContext;

    static MongodExecutable mongodExecutable = null;

    @BeforeClass
    public static void setartEmbeddedMongo() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();

        int port = 12345;
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

        mongodExecutable = starter.prepare(mongodConfig);
        MongodProcess mongod = mongodExecutable.start();
        MongoClient mongo = new MongoClient("localhost", port);


    }

    @Before
    public void setUp() throws IOException {


        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Map<String, String> authDetails = new HashMap<String, String>();
        authDetails.put("id", "123");
        authDetails.put("name", "ray xiao");
        repository.delete("123");
        when(authentication.getUserAuthentication()).thenReturn(userAuth);

        when(userAuth.getDetails()).thenReturn(authDetails);


    }

    @AfterClass
    public static void tearDown() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }

    }

    @Test
    public void depositWithInvalidAmount() throws Exception {

        String depositJson = "{\"amount\":\"lolt99.12\",\"notes\":\"this is a test\"}";
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/deposit")
                .contentType(contentType)
                .content(depositJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        // System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void depositWithWrongUrl() throws Exception {

        String depositJson = "{\"amount\":\"99.12\",\"notes\":\"this is a test\"}";
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/deposit1")
                .contentType(contentType)
                .content(depositJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
        // System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void depositWithError() throws Exception {

        String depositJson = "{\"amount\":\"990000.12\",\"notes\":\"this is a test\"}";
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/deposit")
                .contentType(contentType)
                .content(depositJson))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
        // System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void happyPath() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/balance")
                .contentType(contentType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("")))
                .andReturn();

        String depositJson = "{\"amount\":\"99.12\",\"notes\":\"this is a test\"}";
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/deposit")
                .contentType(contentType)
                .content(depositJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(99.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("ray xiao")))
                .andReturn();

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/balance")
                .contentType(contentType)
                .content(depositJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(99.12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("ray xiao")))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());

        String withdrawalJson = "{\"amount\":\"66.07\",\"notes\":\"this is a test\"}";

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/withdrawal")
                .contentType(contentType)
                .content(withdrawalJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance", Matchers.is(33.05)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is("123")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("ray xiao")))
                .andReturn();

    }


}
