/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.plasync.server.gcm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import play.libs.Json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class GcmSenderTest {

  private final String regId = "15;16";
  private final String collapseKey = "collapseKey";
  private final boolean delayWhileIdle = true;
  private final boolean dryRun = true;
  private final String restrictedPackageName = "package.name";
  private final int retries = 42;
  private final int ttl = 108;
  private final String authKey = "4815162342";
  
  private final GcmMessage message =
      new GcmMessage.Builder()
          .collapseKey(collapseKey)
          .delayWhileIdle(delayWhileIdle)
          .dryRun(dryRun)
          .restrictedPackageName(restrictedPackageName)
          .timeToLive(ttl)
          .addData("k0", null)
          .addData(null, "v0")
          .addData("k1", "v1")
          .addData("k2", "v2")
          .addData("k3", "v3")
          .build();

  private final InputStream exceptionalStream = new InputStream() {

    @Override
    public int read() throws IOException {
      throw new IOException();
    }};

  // creates a Mockito Spy so we can stub internal methods
  @Spy private GcmSender sender = new GcmSender(authKey);

  @Mock private HttpURLConnection mockedConn;
  private final ByteArrayOutputStream outputStream = 
      new ByteArrayOutputStream();
  private GcmResult GcmResult;

  @Before
  public void setFixtures() {
    GcmResult = new GcmResult.Builder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_null() {
    new GcmSender(null);
  }

  @Test
  public void testSend_noRetryOk() throws Exception {
    doNotSleep();
    doReturn(GcmResult).when(sender).sendNoRetry(message, regId);
    sender.send(message, regId, 0);
  }

  @Test(expected = IOException.class)
  public void testSend_noRetryFail() throws Exception {
    doNotSleep();
    doReturn(null).when(sender).sendNoRetry(message, regId);
    sender.send(message, regId, 0);
  }

  @Test(expected = IOException.class)
  public void testSend_noRetryException() throws Exception {
    doThrow(new IOException()).when(sender).sendNoRetry(message, regId);
    sender.send(message, regId, 0);
  }

  @Test
  public void testSend_retryOk() throws Exception {
    doNothing().when(sender).sleep(anyInt());
    doReturn(null) // fails 1st time
        .doReturn(null) // fails 2nd time
        .doReturn(GcmResult) // succeeds 3rd time
        .when(sender).sendNoRetry(message, regId);
    sender.send(message, regId, 2);
    verify(sender, times(3)).sendNoRetry(message, regId);
  }

  @Test(expected = IOException.class)
  public void testSend_retryFails() throws Exception {
    doNothing().when(sender).sleep(anyInt());
    doReturn(null) // fails 1st time
        .doReturn(null) // fails 2nd time
        .doReturn(null) // fails 3rd time
        .when(sender).sendNoRetry(message, regId);
    sender.send(message, regId, 2);
    verify(sender, times(3)).sendNoRetry(message, regId);
  }

  @Test
  public void testSend_retryExponentialBackoff() throws Exception {
    ArgumentCaptor<Long> capturedSleep = ArgumentCaptor.forClass(Long.class);
    int total = retries + 1; // fist attempt + retries
    doNothing().when(sender).sleep(anyInt());
    doReturn(null).when(sender).sendNoRetry(message, regId);
    try {
      sender.send(message, regId, retries);
      fail("Should have thrown IOEXception");
    } catch (IOException e) {
      String message = e.getMessage();
      assertTrue("invalid message:" + message, message.contains("" + total));
    }
    verify(sender, times(total)).sendNoRetry(message, regId);
    verify(sender, times(retries)).sleep(capturedSleep.capture());
    long backoffRange = GcmSender.BACKOFF_INITIAL_DELAY;
    for (long value : capturedSleep.getAllValues()) {
      assertTrue(value >= backoffRange / 2);
      assertTrue(value <= backoffRange * 3 / 2);
      if (2 * backoffRange < GcmSender.MAX_BACKOFF_DELAY) {
        backoffRange *= 2;
      }
    }
  }

  @Test
  public void testSendNoRetry_ok() throws Exception {
    setResponseExpectations(200, "id=4815162342");
    GcmResult GcmResult = sender.sendNoRetry(message, regId);
    assertNotNull(GcmResult);
    assertEquals("4815162342", GcmResult.getMessageId());
    assertNull(GcmResult.getCanonicalRegistrationId());
    assertNull(GcmResult.getErrorCodeName());
    assertRequestBody();
  }

  @Test
  public void testSendNoRetry_ok_canonical() throws Exception {
    setResponseExpectations(200, "id=4815162342\nregistration_id=108");
    GcmResult GcmResult = sender.sendNoRetry(message, regId);
    assertNotNull(GcmResult);
    assertEquals("4815162342", GcmResult.getMessageId());
    assertEquals("108", GcmResult.getCanonicalRegistrationId());
    assertNull(GcmResult.getErrorCodeName());
    assertRequestBody();
  }

  @Test
  public void testSendNoRetry_unauthorized() throws Exception {
    setResponseExpectations(401, "");
    try {
      sender.sendNoRetry(message, regId);
      fail("Should have thrown InvalidGcmRequestException");
    } catch (InvalidGcmRequestException e) {
      assertEquals(401, e.getHttpStatusCode());
    }
    assertRequestBody();
  }

  @Test
  public void testSendNoRetry_unauthorized_nullStream() throws Exception {
    setResponseExpectations(401, null);
    try {
      sender.sendNoRetry(message, regId);
      fail("Should have thrown InvalidGcmRequestException");
    } catch (InvalidGcmRequestException e) {
      assertEquals(401, e.getHttpStatusCode());
      assertEquals("", e.getDescription());
    }
    assertRequestBody();
  }

  @Test
  public void testSendNoRetry_error() throws Exception {
    setResponseExpectations(200, "Error=D'OH!");
    GcmResult GcmResult = sender.sendNoRetry(message, regId);
    assertNull(GcmResult.getMessageId());
    assertNull(GcmResult.getCanonicalRegistrationId());
    assertEquals("D'OH!", GcmResult.getErrorCodeName());
    assertRequestBody();
  }

  @Test
  public void testSendNoRetry_serviceUnavailable() throws Exception {
    setResponseExpectations(503, "");
    GcmResult GcmResult = sender.sendNoRetry(message, regId);
    assertNull(GcmResult);
    assertRequestBody();
  }

  @Test
  public void testSendNoRetry_internalServerError() throws Exception {
    setResponseExpectations(500, "");
    GcmResult GcmResult = sender.sendNoRetry(message, regId);
    assertNull(GcmResult);
    assertRequestBody();
  }

  @Test
  public void testSendNoRetry_ioException_post() throws Exception {
    when(mockedConn.getOutputStream()).thenThrow(new IOException());
    doReturn(mockedConn).when(sender)
        .getConnection(GcmConstants.GCM_SEND_ENDPOINT);
    GcmResult GcmResult = sender.sendNoRetry(message, regId);
    assertNull(GcmResult);
    assertRequestBody();
  }

  @Test
  public void testSendNoRetry_ioException_errorStream() throws Exception {
    when(mockedConn.getResponseCode()).thenReturn(42);
    when(mockedConn.getOutputStream()).thenReturn(outputStream);
    when(mockedConn.getErrorStream()).thenReturn(exceptionalStream);
    doReturn(mockedConn).when(sender)
        .getConnection(GcmConstants.GCM_SEND_ENDPOINT);
    try {
      sender.sendNoRetry(message, regId);
    } catch (InvalidGcmRequestException e) {
      assertEquals(42, e.getHttpStatusCode());
    }
    assertRequestBody();
  }

  @Test
  public void testSendNoRetry_ioException_inputStream() throws Exception {
    when(mockedConn.getResponseCode()).thenReturn(200);
    when(mockedConn.getOutputStream()).thenReturn(outputStream);
    when(mockedConn.getInputStream()).thenReturn(exceptionalStream);
    doReturn(mockedConn).when(sender)
        .getConnection(GcmConstants.GCM_SEND_ENDPOINT);
    GcmResult GcmResult = sender.sendNoRetry(message, regId);
    assertNull(GcmResult);
    assertRequestBody();
  }

  @Test(expected = IOException.class)
  public void testSendNoRetry_emptyBody() throws Exception {
    setResponseExpectations(200, "");
    sender.sendNoRetry(message, regId);
  }

  @Test(expected = IOException.class)
  public void testSendNoRetry_noToken() throws Exception {
    setResponseExpectations(200, "no token");
    sender.sendNoRetry(message, regId);
  }

  @Test(expected = IOException.class)
  public void testSendNoRetry_invalidToken() throws Exception {
    setResponseExpectations(200, "bad=token");
    sender.sendNoRetry(message, regId);
  }

  @Test(expected = IOException.class)
  public void testSendNoRetry_emptyToken() throws Exception {
    setResponseExpectations(200, "token=");
    sender.sendNoRetry(message, regId);
  }

  @Test
  public void testSendNoRetry_invalidHttpStatusCode() throws Exception {
    setResponseExpectations(108, "id=4815162342");
    try {
      sender.sendNoRetry(message, regId);
    } catch (InvalidGcmRequestException e) {
      assertEquals(108, e.getHttpStatusCode());
      assertEquals("id=4815162342", e.getDescription());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSendNoRetry_noRegistrationId() throws Exception {
    sender.sendNoRetry(new GcmMessage.Builder().build(), (String) null);
  }

  @Test()
  public void testSend_json_failsPostingJSON_null() throws Exception {
    List<String> regIds = Arrays.asList("108");
    doReturn(null).when(sender).sendNoRetry(message, regIds);
    try {
      sender.send(message, regIds, 0);
    } catch(IOException e) {
      assertNotNull(e.getMessage());
    }
    verify(sender, times(1)).sendNoRetry(message, regIds);
  }

  @Test()
  public void testSend_json_failsPostingJSON_IOException() throws Exception {
    List<String> regIds = Arrays.asList("108");
    IOException gcmException = new IOException();
    doThrow(gcmException).when(sender).sendNoRetry(message, regIds);
    try {
      sender.send(message, regIds, 0);
    } catch(IOException e) {
      assertNotNull(e.getMessage());
      assertNotSame(gcmException, e);
    }
    verify(sender, times(1)).sendNoRetry(message, regIds);
  }

  @Test()
  public void testSend_json_allAttemptsFail() throws Exception {
    doNothing().when(sender).sleep(anyInt());
    // mock sendNoRetry
    GcmResult unaivalableResult =
        new GcmResult.Builder().errorCode("Unavailable").build();
    // for the intermediate request, only the multicast id matters
    MulticastResult mockedResult = new MulticastResult.Builder(0, 0, 0, 42)
      .addResult(unaivalableResult).build();
    List<String> regIds = Arrays.asList("108");
    doReturn(mockedResult).when(sender).sendNoRetry(message, regIds);
    MulticastResult actualResult = sender.send(message, regIds, 2);
    assertNotNull(actualResult);
    assertEquals(1, actualResult.getTotal());
    assertEquals(0, actualResult.getSuccess());
    assertEquals(1, actualResult.getFailure());
    assertEquals(0, actualResult.getCanonicalIds());
    assertEquals(42, actualResult.getMulticastId());
    assertEquals(1, actualResult.getResults().size());
    assertResult(actualResult.getResults().get(0), null, "Unavailable", null);
    verify(sender, times(3)).sendNoRetry(message, regIds);
  }

  @Test()
  public void testSend_json_secondAttemptOk() throws Exception {
    doNothing().when(sender).sleep(anyInt());
    // mock sendNoRetry
    GcmResult unaivalableResult =
        new GcmResult.Builder().errorCode("Unavailable").build();
    GcmResult okResult =
        new GcmResult.Builder().messageId("42").build();
    // for the intermediate request, only the multicast id matters
    MulticastResult mockedResult1 = new MulticastResult.Builder(0, 0, 0, 100)
        .addResult(unaivalableResult).build();
    MulticastResult mockedResult2 = new MulticastResult.Builder(0, 0, 0, 200)
        .addResult(okResult).build();
    List<String> regIds = Arrays.asList("108");
    doReturn(mockedResult1) // fist time it fails
        .doReturn(mockedResult2) // second time it succeeds
        .when(sender).sendNoRetry(message, regIds);
    MulticastResult actualResult = sender.send(message, regIds, 10);
    assertNotNull(actualResult);
    assertEquals(1, actualResult.getTotal());
    assertEquals(1, actualResult.getSuccess());
    assertEquals(0, actualResult.getFailure());
    assertEquals(0, actualResult.getCanonicalIds());
    assertEquals(100, actualResult.getMulticastId());
    assertEquals(1, actualResult.getResults().size());
    assertResult(actualResult.getResults().get(0), "42", null, null);
    List<Long> retryMulticastIds = actualResult.getRetryMulticastIds();
    assertEquals(1, retryMulticastIds.size());
    assertEquals(200, retryMulticastIds.get(0).longValue());
    verify(sender, times(2)).sendNoRetry(message, regIds);
  }

  @Test()
  public void testSend_json_ok() throws Exception {
    doNothing().when(sender).sleep(anyInt());
    /*
     * The following scenario is mocked below:
     *
     * input: 4, 8, 15, 16, 23, 42
     *
     * 1st call (multicast_id:100): 4,16:ok 8,15: unavailable
     *                              23:internalServerError, 42:error,
     * 2nd call: whole post failed
     * 3rd call (multicast_id:200): 8,15: unavailable, 23:ok
     * 4th call (multicast_id:300): 8:error, 15:unavailable
     * 5th call (multicast_id:400): 15:unavailable
     *
     * output: total:6, success:3, error: 3, canonicals: 0, multicast_id: 100
     *         results: ok, error, unavailable, ok, ok, error
     */
    GcmResult unaivalableResult =
        new GcmResult.Builder().errorCode("Unavailable").build();
    GcmResult internalServerErrorResult =
        new GcmResult.Builder().errorCode("InternalServerError").build();
    GcmResult errorResult =
        new GcmResult.Builder().errorCode("D'OH!").build();
    GcmResult okResultMsg4 =
        new GcmResult.Builder().messageId("msg4").build();
    GcmResult okResultMsg16 =
        new GcmResult.Builder().messageId("msg16").build();
    GcmResult okResultMsg23 =
        new GcmResult.Builder().messageId("msg23").build();
    MulticastResult result1stCall = new MulticastResult.Builder(0, 0, 0, 100)
        .addResult(okResultMsg4)
        .addResult(unaivalableResult)
        .addResult(unaivalableResult)
        .addResult(okResultMsg16)
        .addResult(internalServerErrorResult)
        .addResult(errorResult)
        .build();
    doReturn(result1stCall).when(sender).sendNoRetry(message,
        Arrays.asList("4", "8", "15", "16", "23", "42"));
    MulticastResult result2ndCall = null;
    MulticastResult result3rdCall = new MulticastResult.Builder(0, 0, 0, 200)
      .addResult(unaivalableResult)
      .addResult(unaivalableResult)
      .addResult(okResultMsg23)
      .build();
    // must next 2nd and 3rd calls on same mock setup since input is the same
    doReturn(result2ndCall).doReturn(result3rdCall).when(sender)
        .sendNoRetry(message, Arrays.asList("8", "15", "23"));
    MulticastResult result4thCall = new MulticastResult.Builder(0, 0, 0, 300)
      .addResult(errorResult)
      .addResult(unaivalableResult)
      .build();
    doReturn(result4thCall).when(sender).sendNoRetry(message,
        Arrays.asList("8", "15"));
    MulticastResult result5thCall = new MulticastResult.Builder(0, 0, 0, 400)
      .addResult(unaivalableResult)
      .build();
    doReturn(result5thCall).when(sender).sendNoRetry(message,
        Arrays.asList("15"));

    // call it
    MulticastResult actualResult = sender.send(message,
        Arrays.asList("4", "8", "15", "16", "23", "42"), 4);

    // assert results
    assertNotNull(actualResult);
    assertEquals(6, actualResult.getTotal());
    assertEquals(3, actualResult.getSuccess());
    assertEquals(3, actualResult.getFailure());
    assertEquals(0, actualResult.getCanonicalIds());
    assertEquals(100, actualResult.getMulticastId());
    List<GcmResult> actualResults = actualResult.getResults();
    assertEquals(6, actualResults.size());
    assertResult(actualResults.get(0), "msg4", null, null); // 4
    assertResult(actualResults.get(1), null, "D'OH!", null); // 8
    assertResult(actualResults.get(2), null, "Unavailable", null); // 15
    assertResult(actualResults.get(3), "msg16", null, null); // 16
    assertResult(actualResults.get(4), "msg23", null, null); // 23
    assertResult(actualResults.get(5), null, "D'OH!", null); // 42
    List<Long> retryMulticastIds = actualResult.getRetryMulticastIds();
    assertEquals(3, retryMulticastIds.size());
    assertEquals(200, retryMulticastIds.get(0).longValue());
    assertEquals(300, retryMulticastIds.get(1).longValue());
    assertEquals(400, retryMulticastIds.get(2).longValue());
    verify(sender, times(5)).sendNoRetry(eq(message), anyListOf(String.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSendNoRetry_json_nullRegIds() throws Exception {
    sender.sendNoRetry(message, (List<String>) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSendNoRetry_json_emptyRegIds() throws Exception {
    sender.sendNoRetry(message, Collections.<String>emptyList());
  }

  @Test
  public void testSendNoRetry_json_badRequest() throws Exception {
    setResponseExpectations(42, "bad json");
    try {
      sender.sendNoRetry(message, Arrays.asList("108"));
    }
    catch (InvalidGcmRequestException e) {
      assertEquals(42, e.getHttpStatusCode());
      assertEquals("bad json", e.getDescription());
      assertRequestJsonBody("108");
    }
  }

  @Test
  public void testSendNoRetry_json_badRequest_nullError() throws Exception {
    setResponseExpectations(42, null);
    try {
      sender.sendNoRetry(message, Arrays.asList("108"));
    } catch (InvalidGcmRequestException e) {
      assertEquals(42, e.getHttpStatusCode());
      assertEquals("", e.getDescription());
      assertRequestJsonBody("108");
    }
  }

  @Test
  public void testSendNoRetry_json_ioException_post() throws Exception {
    when(mockedConn.getOutputStream()).thenThrow(new IOException());
    doReturn(mockedConn).when(sender)
        .getConnection(GcmConstants.GCM_SEND_ENDPOINT);
    MulticastResult multicastResult = sender.sendNoRetry(message,
        Arrays.asList("4", "8", "15"));
    assertNull(multicastResult);
  }

  @Test
  public void testSendNoRetry_json_ioException_errorStream() throws Exception {
    when(mockedConn.getResponseCode()).thenReturn(42);
    when(mockedConn.getOutputStream()).thenReturn(outputStream);
    when(mockedConn.getErrorStream()).thenReturn(exceptionalStream);
    doReturn(mockedConn).when(sender)
        .getConnection(GcmConstants.GCM_SEND_ENDPOINT);
    try {
      sender.sendNoRetry(message, Arrays.asList("4", "8", "15"));
    } catch (InvalidGcmRequestException e) {
      assertEquals(42, e.getHttpStatusCode());
    }
  }

  @Test
  public void testSendNoRetry_json_ioException_inputStream() throws Exception {
    when(mockedConn.getResponseCode()).thenReturn(200);
    when(mockedConn.getOutputStream()).thenReturn(outputStream);
    when(mockedConn.getInputStream()).thenReturn(exceptionalStream);
    doReturn(mockedConn).when(sender)
        .getConnection(GcmConstants.GCM_SEND_ENDPOINT);
    MulticastResult multicastResult = sender.sendNoRetry(message,
        Arrays.asList("4", "8", "15"));
    assertNull(multicastResult);
  }

  @Test()
  public void testSendNoRetry_json_ok() throws Exception {
    String json = replaceQuotes("\n"
        + "{"
        + "  'multicast_id': 108,"
        + "  'success': 2,"
        + "  'failure': 1,"
        + "  'canonical_ids': 1,"
        + "  'results': ["
        + "    {'message_id': '16'}, "
        + "    {'error': 'DOH!'}, "
        + "    {'message_id': '23', 'registration_id': '42'}"
        + "  ]"
        + "}");
    setResponseExpectations(200, json);
    MulticastResult multicastResult = sender.sendNoRetry(message,
        Arrays.asList("4", "8", "15"));
    assertNotNull(multicastResult);
    assertEquals(3, multicastResult.getTotal());
    assertEquals(2, multicastResult.getSuccess());
    assertEquals(1, multicastResult.getFailure());
    assertEquals(1, multicastResult.getCanonicalIds());
    assertEquals(108, multicastResult.getMulticastId());
    List<GcmResult> results = multicastResult.getResults();
    assertNotNull(results);
    assertEquals(3, results.size());
    assertResult(results.get(0), "16", null, null);
    assertResult(results.get(1), null, "DOH!", null);
    assertResult(results.get(2), "23", null, "42");
    assertRequestJsonBody("4", "8", "15");
  }

  // replace ' by ", otherwise JSON strins would need to escape double-quotes
  private String replaceQuotes(String json) {
    return json.replaceAll("'", "\"");
  }

  private void assertResult(GcmResult GcmResult, String messageId, String error,
      String canonicalRegistrationId) {
    assertEquals(messageId, GcmResult.getMessageId());
    assertEquals(error, GcmResult.getErrorCodeName());
    assertEquals(canonicalRegistrationId, GcmResult.getCanonicalRegistrationId());
  }

  private void assertRequestJsonBody(String...expectedRegIds) throws Exception {
    ArgumentCaptor<String> capturedBody = ArgumentCaptor.forClass(String.class);
    verify(sender).post(eq(GcmConstants.GCM_SEND_ENDPOINT), eq("application/json"),
        capturedBody.capture());
    // parse body
    String body = capturedBody.getValue();
    JsonNode json = Json.parse(body);
    assertEquals(ttl, json.get("time_to_live").getIntValue());
    assertEquals(collapseKey, json.get("collapse_key").getTextValue());
    assertEquals(delayWhileIdle, json.get("delay_while_idle").getBooleanValue());
    assertEquals(dryRun, json.get("dry_run").getBooleanValue());
    assertEquals(restrictedPackageName, json.get("restricted_package_name").getTextValue());
    @SuppressWarnings("unchecked")
    JsonNode payload = json.get("data");
    assertNotNull("no payload", payload);
    assertEquals("wrong payload size", 5, payload.size());
//    assertEquals("v0", payload.get("null").getTextValue());
    // Sender converts null keys to empty string
    assertEquals("v0", payload.get("").getTextValue());
    assertNull(payload.get("v0"));
    assertEquals("v1", payload.get("k1").getTextValue());
    assertEquals("v2", payload.get("k2").getTextValue());
    assertEquals("v3", payload.get("k3").getTextValue());
    JsonNode actualRegIds = json.get("registration_ids");
    assertTrue(actualRegIds.isArray());
    assertEquals("Wrong number of regIds",
        expectedRegIds.length, actualRegIds.size());
    for (int i = 0; i < expectedRegIds.length; i++) {
      String expectedRegId = expectedRegIds[i];
      String actualRegId = actualRegIds.get(i).asText();
      assertEquals("invalid regId at index " + i, expectedRegId, actualRegId);
    }
  }

  @Test
  public void testNewKeyValues() {
    Map<String, String> x = GcmSender.newKeyValues("key", "value");
    assertEquals(1, x.size());
    assertEquals("value", x.get("key"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNewKeyValues_nullKey() {
    GcmSender.newKeyValues(null, "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNewKeyValues_nullValue() {
    GcmSender.newKeyValues("key", null);
  }

  @Test
  public void testNewBody() {
    StringBuilder body = GcmSender.newBody("name", "value");
    assertEquals("name=value", body.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNewBody_nullKey() {
    GcmSender.newBody(null, "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNewBody_nullValue() {
    GcmSender.newBody("key", null);
  }

  @Test
  public void testAddParameter() {
    StringBuilder body = new StringBuilder("P=NP");
    GcmSender.addParameter(body, "name", "value");
    assertEquals("P=NP&name=value", body.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddParameter_nullBody() {
    GcmSender.addParameter(null, "key", "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddParameter_nullKey() {
    StringBuilder body = new StringBuilder();
    GcmSender.addParameter(body, null, "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddParameter_nullValue() {
    StringBuilder body = new StringBuilder();
    GcmSender.addParameter(body, "key", null);
  }

  @Test
  public void testGetString_oneLine() throws Exception {
    String expected = "108";
    InputStream stream = new ByteArrayInputStream(expected.getBytes());
    String actual = GcmSender.getString(stream);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetString_stripsLastLine() throws Exception {
    InputStream stream = new ByteArrayInputStream("108\n".getBytes());
    String stripped = GcmSender.getString(stream);
    assertEquals("108", stripped);
  }

  @Test
  public void testGetString_multipleLines() throws Exception {
    String expected = "4\n8\n15\n\n16\n23\n42";
    InputStream stream = new ByteArrayInputStream(expected.getBytes());
    String actual = GcmSender.getString(stream);
    assertEquals(expected, actual);
  }

  public void testGetString_nullValue() throws Exception {
    assertEquals("", GcmSender.getString(null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPost_noUrl() throws Exception {
    sender.post(null, "whatever");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPost_noBody() throws Exception {
    sender.post(GcmConstants.GCM_SEND_ENDPOINT, null);
  }

  @Test
  public void testPost() throws Exception {
    String requestBody = "req";
    String responseBody = "resp";
    setResponseExpectations(200, responseBody);
    HttpURLConnection response =
        sender.post(GcmConstants.GCM_SEND_ENDPOINT, requestBody);
    assertEquals(requestBody, new String(outputStream.toByteArray()));
    verify(mockedConn).setRequestMethod("POST");
    verify(mockedConn).setFixedLengthStreamingMode(requestBody.length());
    verify(mockedConn).setRequestProperty("Content-Type",
        "application/x-www-form-urlencoded;charset=UTF-8");
    verify(mockedConn).setRequestProperty("Authorization", "key=" + authKey);
    assertEquals(200, response.getResponseCode());
  }

  @Test
  public void testPost_customType() throws Exception {
    String requestBody = "req";
    String responseBody = "resp";
    setResponseExpectations(200, responseBody);
    HttpURLConnection response =
        sender.post(GcmConstants.GCM_SEND_ENDPOINT, "stuff", requestBody);
    assertEquals(requestBody, new String(outputStream.toByteArray()));
    verify(mockedConn).setRequestMethod("POST");
    verify(mockedConn).setFixedLengthStreamingMode(requestBody.length());
    verify(mockedConn).setRequestProperty("Content-Type", "stuff");
    verify(mockedConn).setRequestProperty("Authorization", "key=" + authKey);
    assertEquals(200, response.getResponseCode());
  }

  /**
   * Sets the expectations of the HTTP connection.
   */
  private void setResponseExpectations(int statusCode, String response) 
      throws IOException {
    when(mockedConn.getResponseCode()).thenReturn(statusCode);
    InputStream inputStream = (response == null) ?
        null : new ByteArrayInputStream(response.getBytes());
    if (statusCode == 200) {
      when(mockedConn.getInputStream()).thenReturn(inputStream);
    } else {
      when(mockedConn.getErrorStream()).thenReturn(inputStream);
    }
    when(mockedConn.getOutputStream()).thenReturn(outputStream);
    doReturn(mockedConn).when(sender)
        .getConnection(GcmConstants.GCM_SEND_ENDPOINT);
  }

  private void doNotSleep() {
    doThrow(new AssertionError("Thou should not sleep!")).when(sender)
        .sleep(anyInt());
  }

  private void assertRequestBody() throws Exception {
    ArgumentCaptor<String> capturedBody = ArgumentCaptor.forClass(String.class);
    verify(sender).post(eq(GcmConstants.GCM_SEND_ENDPOINT), capturedBody.capture());
    // parse body
    String body = capturedBody.getValue();
    Map<String, String> params = new HashMap<String, String>();
    for (String param : body.split("&")) {
      String[] split = param.split("=");
      params.put(split[0], split[1]);
    }
    // check parameters
    assertEquals("wrong parameters size for " + body, 9, params.size());
    assertParameter(params, "registration_id", regId);
    assertParameter(params, "collapse_key", collapseKey);
    assertParameter(params, "delay_while_idle", delayWhileIdle ? "1" : "0");
    assertParameter(params, "dry_run", dryRun ? "1" : "0");
    assertParameter(params, "restricted_package_name", restrictedPackageName);
    assertParameter(params, "time_to_live", "" + ttl);
    assertParameter(params, "data.k1", "v1");
    assertParameter(params, "data.k2", "v2");
    assertParameter(params, "data.k3", "v3");
  }

  static void assertParameter(Map<String, String> params, String name,
      String expectedValue) {
    assertEquals("invalid value for request parameter parameter " + name,
        params.get(name), expectedValue);
  }

}