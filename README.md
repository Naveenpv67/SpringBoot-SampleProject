import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LandingPageBuilderTest {

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private LandingPageBuilder landingPageBuilder;

    @Test
    void testParseResponse_NullPointerException() throws Exception {
        String debosdata = LandingPageMockData.getLandingPageDeboseDataJson_Failure();
        when(mapper.readTree(anyString())).thenThrow(NullPointerException.class);

        Exception exception = assertThrows(MbDematException.class, () -> {
            landingPageBuilder.parseResponse(debosdata);
        });

        assertEquals(ApplicationConstants.ERR_KIND_INTERNAL, exception.getType());
        assertEquals(ApplicationConstants.INTERNAL_SERVER_ERROR_CODE, exception.getErrCode());
    }

    @Test
    void testParseResponse_JsonProcessingException() throws Exception {
        String debosdata = LandingPageMockData.getLandingPageDeboseDataJson_Failure();
        when(mapper.readTree(anyString())).thenThrow(JsonProcessingException.class);

        Exception exception = assertThrows(MbDematException.class, () -> {
            landingPageBuilder.parseResponse(debosdata);
        });

        assertEquals(ApplicationConstants.ERR_KIND_INTERNAL, exception.getType());
        assertEquals(ApplicationConstants.INTERNAL_SERVER_ERROR_CODE, exception.getErrCode());
    }

    @Test
    void testParseResponse_MbDematException() throws Exception {
        String debosdata = LandingPageMockData.getLandingPageDeboseDataJson_Failure();
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode respStatNode = mock(JsonNode.class);
        
        when(mapper.readTree(anyString())).thenReturn(rootNode);
        when(rootNode.path("S: Envelope").path("S: Body").path("ns11:doDematLanding PageResponse").path("return").path("respstat")).thenReturn(respStatNode);
        when(respStatNode.path("respcode").asText()).thenReturn("1008");

        Exception exception = assertThrows(MbDematException.class, () -> {
            landingPageBuilder.parseResponse(debosdata);
        });

        assertEquals(ApplicationConstants.ERR_KIND_OBP, exception.getType());
    }
}
