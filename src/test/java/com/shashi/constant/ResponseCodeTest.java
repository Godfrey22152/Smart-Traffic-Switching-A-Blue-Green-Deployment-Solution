package com.shashi.constant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class ResponseCodeTest {

    @Test
    void testEnumValues() {
        // Ensure enum constants are properly initialized
        assertNotNull(ResponseCode.SUCCESS, "SUCCESS constant should be initialized");
        assertNotNull(ResponseCode.FAILURE, "FAILURE constant should be initialized");
        assertEquals(200, ResponseCode.SUCCESS.getCode(), "SUCCESS code should be 200");
        assertEquals("OK", ResponseCode.SUCCESS.getMessage(), "SUCCESS message should be 'OK'");
    }

    @Test
    void testGetMessageByStatusCode() {
        // Test the reverse lookup method
        Optional<ResponseCode> responseCode = ResponseCode.getMessageByStatusCode(200);
        assertTrue(responseCode.isPresent(), "ResponseCode for status 200 should be present");
        assertEquals(ResponseCode.SUCCESS, responseCode.get(), "ResponseCode for status 200 should be SUCCESS");

        responseCode = ResponseCode.getMessageByStatusCode(422);
        assertTrue(responseCode.isPresent(), "ResponseCode for status 422 should be present");
        assertEquals(ResponseCode.FAILURE, responseCode.get(), "ResponseCode for status 422 should be FAILURE");

        responseCode = ResponseCode.getMessageByStatusCode(999);
        assertFalse(responseCode.isPresent(), "ResponseCode for status 999 should not be present");
    }

    @Test
    void applicationStarts() {
        // Basic instantiation test for the enum
        ResponseCode responseCode = ResponseCode.SUCCESS;
        assertNotNull(responseCode, "ResponseCode should be initialized properly");
    }
}
