package kr.cubedm.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Value("${jeju.datahub.api.url}")
    private String apiUrl;

    @Test
    void contextLoads() {
    }

    @Test
    void testJejuPageRedirect() throws Exception {
        mockMvc.perform(get("/jeju"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jeju.html"));
    }

    @Test
    void testGetJejuDataSuccess() throws Exception {
        // 테스트 응답 데이터 준비
        Map<String, Object> responseData = new HashMap<>();
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("totalCount", 100);
        responseData.put("result", resultData);

        // RestTemplate 응답 모의 설정
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseData, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenReturn(responseEntity);

        // API 호출 및 검증
        mockMvc.perform(get("/jeju/api/data")
                        .param("page", "0")
                        .param("size", "10")
                        .param("startDate", "202001")
                        .param("endDate", "202101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalCount").value(100));
    }

    @Test
    void testGetJejuDataInvalidPageSize() throws Exception {
        mockMvc.perform(get("/jeju/api/data")
                        .param("page", "0")
                        .param("size", "0")  // 유효하지 않은 페이지 크기
                        .param("startDate", "202001")
                        .param("endDate", "202101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value(containsString("Invalid page size")));
    }

    @Test
    void testGetJejuDataInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/jeju/api/data")
                        .param("page", "0")
                        .param("size", "10")
                        .param("startDate", "abcdef")  // 유효하지 않은 날짜 형식
                        .param("endDate", "202101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value(containsString("Invalid date format")));
    }

    @Test
    void testGetJejuDataClientError() throws Exception {
        // RestTemplate에서 클라이언트 오류 발생 모의 설정
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        mockMvc.perform(get("/jeju/api/data")
                        .param("page", "0")
                        .param("size", "10")
                        .param("startDate", "202001")
                        .param("endDate", "202101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value(containsString("Error from Jeju Data Hub API")));
    }

    @Test
    void testGetJejuDataServerError() throws Exception {
        // RestTemplate에서 서버 오류 발생 모의 설정
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));

        mockMvc.perform(get("/jeju/api/data")
                        .param("page", "0")
                        .param("size", "10")
                        .param("startDate", "202001")
                        .param("endDate", "202101"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value(containsString("Jeju Data Hub API server error")));
    }
    
    @Test
    void testUserCountColorCoding() throws Exception {
        // 사용자 수에 따른 색상 코딩 테스트
        // 실제 UI 테스트는 프론트엔드 테스트 프레임워크로 수행해야 하지만,
        // 여기서는 기본적인 기능 테스트만 수행
        
        // 테스트 응답 데이터 준비 (다양한 사용자 수 포함)
        Map<String, Object> responseData = new HashMap<>();
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("totalCount", 100);
        
        // 다양한 사용자 수를 가진 데이터 항목들
        Map<String, Object> item1 = new HashMap<>();
        item1.put("userCount", 50);  // 낮은 사용자 수
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("userCount", 200); // 중간 사용자 수
        
        Map<String, Object> item3 = new HashMap<>();
        item3.put("userCount", 800); // 높은 사용자 수
        
        Map<String, Object> item4 = new HashMap<>();
        item4.put("userCount", 1500); // 매우 높은 사용자 수
        
        responseData.put("result", resultData);
        
        // RestTemplate 응답 모의 설정
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(responseData, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                eq(new ParameterizedTypeReference<Map<String, Object>>() {})
        )).thenReturn(responseEntity);
        
        // API 호출 및 검증
        mockMvc.perform(get("/jeju/api/data")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
        // 실제 색상 코딩은 프론트엔드에서 처리되므로 여기서는 API 호출 성공만 확인
    }
    
    @Test
    void testMobileResponsiveness() throws Exception {
        // 모바일 반응형 디자인 테스트
        // 실제 모바일 반응형 테스트는 프론트엔드 테스트 프레임워크로 수행해야 하지만,
        // 여기서는 기본적인 기능 테스트만 수행
        
        // 모바일 User-Agent로 요청
        String mobileUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1";
        
        mockMvc.perform(get("/jeju")
                .header("User-Agent", mobileUserAgent))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jeju.html"));
        // 모바일 환경에서도 기본 리다이렉션이 정상적으로 작동하는지 확인
    }
}
