import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class ViewStatementRestControllerTest {

    @Mock
    private DematLandingPageService dematLandingPageService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsDAO userDetailsDAO;

    @Mock
    private TransactionLogging transactionLogging;

    @Mock
    private BlackoutHelper blackoutHelper;

    @Mock
    private HttpServletRequest mockedRequest;

    @InjectMocks
    private ViewStatementRestController viewStatementRestController;

    private DematAccDetailsListRequest dematAccDetailsListRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dematAccDetailsListRequest = new DematAccDetailsListRequest();
        dematAccDetailsListRequest.setHashUserId("testUserId");
    }

    @Test
    void testGetDematAccDetails_whenSuccess() throws Exception {
        DematLandingPageResponse expectedResponse = new DematLandingPageResponse();
        expectedResponse.setStatus("success");
        expectedResponse.setMessage("Details retrieved successfully");
        
        when(dematLandingPageService.getDematAccDetails(anyString(), anyString(), any())).thenReturn(expectedResponse);

        ResponseEntity<DematLandingPageResponse> response = viewStatementRestController.getDematAccDetails(mockedRequest, "giga123", "EN", dematAccDetailsListRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        assertEquals("Details retrieved successfully", response.getBody().getMessage());
    }

    @Test
    void testGetDematAccDetails_whenBadRequest() throws Exception {
        // Simulate a bad request by sending null request object
        ResponseEntity<DematLandingPageResponse> response = viewStatementRestController.getDematAccDetails(mockedRequest, "giga123", "EN", null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetDematAccDetails_whenInternalServerError() throws Exception {
        when(dematLandingPageService.getDematAccDetails(anyString(), anyString(), any())).thenThrow(new RuntimeException("Internal Error"));

        ResponseEntity<DematLandingPageResponse> response = viewStatementRestController.getDematAccDetails(mockedRequest, "giga123", "EN", dematAccDetailsListRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDematAccDetailsListRequest() {
        DematAccDetailsListRequest request = new DematAccDetailsListRequest();
        request.setHashUserId("testUserId");
        
        assertEquals("testUserId", request.getHashUserId());
    }

    @Test
    void testDematLandingPageResponse() {
        DematLandingPageResponse response = new DematLandingPageResponse();
        response.setStatus("success");
        response.setMessage("Details retrieved successfully");
        
        assertEquals("success", response.getStatus());
        assertEquals("Details retrieved successfully", response.getMessage());
    }
}
