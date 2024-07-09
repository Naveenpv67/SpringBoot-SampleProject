import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;

public class ObpHelperServiceTest {

    @Mock
    private ManagedChannel managedChannel;

    @InjectMocks
    private ObpHelperService obpHelperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetObpCaller() {
        String stringRequest = "testRequest";
        String appId = "testAppId";
        ObpApiType apiType = ObpApiType.SOME_API_TYPE;

        ObpGrpcUtil obpGrpcUtil = obpHelperService.getObpCaller(stringRequest, appId, apiType);

        assertNotNull(obpGrpcUtil);
        assertEquals(appId, obpGrpcUtil.getAppId());
        assertEquals(managedChannel, obpGrpcUtil.getManagedChannel());
        assertEquals(HttpMethod.POST.name(), obpGrpcUtil.getMethod());
        assertTrue(obpGrpcUtil.getQuery().isEmpty());
        assertTrue(obpGrpcUtil.getHeaders().isEmpty());
        assertEquals(apiType, obpGrpcUtil.getApiType());
        assertEquals(stringRequest, obpGrpcUtil.getBody());
    }
}
