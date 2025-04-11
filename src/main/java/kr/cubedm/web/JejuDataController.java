package kr.cubedm.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
@RequestMapping("/jeju")
public class JejuDataController {
    
    private static final Logger logger = Logger.getLogger(JejuDataController.class.getName());
    
    @Value("${jeju.datahub.api.url}")
    private String apiUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @GetMapping
    public String jejuPage(Model model) {
        return "redirect:/jeju.html";
    }
    
    @GetMapping("/api/data")
    @ResponseBody
    public ResponseEntity<?> getJejuData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "201601") String startDate,
            @RequestParam(defaultValue = "202201") String endDate) {
        
        try {
            // Validate input parameters
            if (size <= 0 || size > 100) {
                return createErrorResponse("Invalid page size. Must be between 1 and 100.", HttpStatus.BAD_REQUEST);
            }
            
            if (!isValidDateFormat(startDate) || !isValidDateFormat(endDate)) {
                return createErrorResponse("Invalid date format. Use YYYYMM format.", HttpStatus.BAD_REQUEST);
            }
            
            // Build the URL with query parameters
            String url = apiUrl + "?number=" + (page + 1) + "&limit=" + size;
            
            if (startDate != null && !startDate.isEmpty()) {
                url += "&startDate=" + startDate;
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                url += "&endDate=" + endDate;
            }
            
            logger.info("Requesting data from: " + url);
            
            // Make the request to the external API using exchange with ParameterizedTypeReference
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            logger.log(Level.WARNING, "Client error when calling Jeju Data Hub API", e);
            return createErrorResponse("Error from Jeju Data Hub API: " + e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (HttpServerErrorException e) {
            logger.log(Level.SEVERE, "Server error when calling Jeju Data Hub API", e);
            return createErrorResponse("Jeju Data Hub API server error: " + e.getMessage(), HttpStatus.valueOf(e.getStatusCode().value()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error when calling Jeju Data Hub API", e);
            return createErrorResponse("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private boolean isValidDateFormat(String date) {
        if (date == null || date.length() != 6) {
            return false;
        }
        try {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(4, 6));
            return year >= 1900 && year <= 2100 && month >= 1 && month <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", message);
        errorResponse.put("status", status.value());
        return new ResponseEntity<>(errorResponse, status);
    }
}
