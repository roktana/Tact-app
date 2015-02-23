package com.tactile.tact.utils.webservices;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Basic REST web services methods call and handling
 * @author Leyan Abreu Sacre
 *
 */
public class WebServiceRESTBasic {

	DefaultHttpClient httpClient;
	HttpContext localContext;
	HttpResponse response = null;
	HttpPost httpPost = null;
	HttpGet httpGet = null;
	HttpDelete httpDelete = null;
	String webServiceUrl;

	/**
	 * 
	 * @param pWebServiceURL - URL to web service call, NO include method
	 */
	public WebServiceRESTBasic(String pWebServiceURL){
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);       
		localContext = new BasicHttpContext();
		httpClient = (DefaultHttpClient) HttpUtils.getNewHttpClient();
		webServiceUrl = pWebServiceURL;

	}

	/**
	 * Call a GET web service that return a String response (XML, JSON etc.)
	 * @param pMethodName - Name of the web service method to complete the URL call
	 * @param pParams - Parameter to call web service, as a List<NameValuePair> with <Parameter Name, Parameter Value>
	 * @param pHeaders - Headers to add to the call, as a List<NameValuePair> with <Header Name, Header Value>
	 * @param pContentType - Content type to set in the call header
	 * @return String 
	 * @throws WebServiceException
	 */
	public String callGETString(String pMethodName, List<NameValuePair> pParams, List<NameValuePair> pHeaders, String pContentType) throws WebServiceException {
		HttpResponse getResponse = null;
		try {
			getResponse = this.webGet(pMethodName, pParams, pHeaders, pContentType);
		} catch (WebServiceException e) {
			throw e;
		}
		if (getResponse == null) {
			throw new WebServiceException(WebServiceException.EMPTY_RESPONSE_EXCEPTION_STATUS, 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_MESSAGE, "", 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_EXCEPTION_USER_MESSAGE);
		}
		String stringEntity = null;
		try { 
			stringEntity = EntityUtils.toString(getResponse.getEntity());
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_STATUS, 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_USER_MESSAGE);
		}
		return stringEntity;
	}

	/**
	 * Call a GET web service that return a InputStream from the content of the response
	 * @param pMethodName - Name of the web service method to complete the URL call
	 * @param pParams - Parameter to call web service, as a List<NameValuePair> with <Parameter Name, Parameter Value>
	 * @param pHeaders - Headers to add to the call, as a List<NameValuePair> with <Header Name, Header Value>
	 * @param pContentType - Content type to set in the call header
	 * @return InputStream 
	 * @throws WebServiceException
	 */
	public InputStream callGETInputStream(String pMethodName, List<NameValuePair> pParams, List<NameValuePair> pHeaders, String pContentType) throws WebServiceException {
		HttpResponse getResponse = null;
		try {
			getResponse = this.webGet(pMethodName, pParams, pHeaders, pContentType);
		} catch (WebServiceException e) {
			throw e;
		}
		if (getResponse == null) {
			throw new WebServiceException(WebServiceException.EMPTY_RESPONSE_EXCEPTION_STATUS, 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_MESSAGE, "", 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_EXCEPTION_USER_MESSAGE);
		}
		if (getResponse.getStatusLine().getStatusCode() != 200) {
			throw new WebServiceException(WebServiceException.BAD_RESPONSE_CODE_EXCEPTION_STATUS, 
					WebServiceException.BAD_RESPONSE_CODE_EXCEPTION_MESSAGE, String.valueOf(getResponse.getStatusLine().getStatusCode()), 
					WebServiceException.BAD_RESPONSE_CODE_EXCEPTION_USER_MESSAGE);
		}
		InputStream inputStreamEntity = null;
		try { 
			inputStreamEntity = getResponse.getEntity().getContent();
		} catch (IllegalStateException e) {
			throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_ACCESS_EXCEPTION_STATUS, 
					WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_ACCESS_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_ACCESS_EXCEPTION_USER_MESSAGE);
		} catch (IOException e) {
			throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_EXCEPTION_STATUS, 
					WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_EXCEPTION_USER_MESSAGE);
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_STATUS, 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_USER_MESSAGE);
		}
		return inputStreamEntity;
	}

    public static InputStream callGETInputStream(String pURL) throws WebServiceException {
        HttpResponse getResponse = null;
        try {
            getResponse = webGet(pURL);
        } catch (WebServiceException e) {
            throw e;
        }
        if (getResponse == null) {
            throw new WebServiceException(WebServiceException.EMPTY_RESPONSE_EXCEPTION_STATUS,
                    WebServiceException.EMPTY_RESPONSE_EXCEPTION_MESSAGE, "",
                    WebServiceException.EMPTY_RESPONSE_EXCEPTION_EXCEPTION_USER_MESSAGE);
        }
        if (getResponse.getStatusLine().getStatusCode() != 200) {
            throw new WebServiceException(WebServiceException.BAD_RESPONSE_CODE_EXCEPTION_STATUS,
                    WebServiceException.BAD_RESPONSE_CODE_EXCEPTION_MESSAGE, String.valueOf(getResponse.getStatusLine().getStatusCode()),
                    WebServiceException.BAD_RESPONSE_CODE_EXCEPTION_USER_MESSAGE);
        }
        InputStream inputStreamEntity = null;
        try {
            inputStreamEntity = getResponse.getEntity().getContent();
        } catch (IllegalStateException e) {
            throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_ACCESS_EXCEPTION_STATUS,
                    WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_ACCESS_EXCEPTION_MESSAGE,
                    e.getMessage(),
                    WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_ACCESS_EXCEPTION_USER_MESSAGE);
        } catch (IOException e) {
            throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_EXCEPTION_STATUS,
                    WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_EXCEPTION_MESSAGE,
                    e.getMessage(),
                    WebServiceException.DECODING_RESPONSE_ENTITY_CONTENT_EXCEPTION_USER_MESSAGE);
        } catch (Exception e) {
            throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_STATUS,
                    WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_MESSAGE,
                    e.getMessage(),
                    WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_USER_MESSAGE);
        }

        return inputStreamEntity;
    }

	/**
	 * Call a DELETE web service assuming that the response error/message came in the response body, 
	 * normally this methods returns string specific data to parse (text, XML, JSON, etc.)
	 * @param pMethodName - Name of the web service method to complete the URL call
	 * @param pParams - Parameter to call web service, as a List<NameValuePair> with <Parameter Name, Parameter Value>
	 * @param pHeaders - Headers to add to the call, as a List<NameValuePair> with <Header Name, Header Value>
	 * @return String
	 * @throws WebServiceException
	 */
	public String callDELETEString(String pMethodName, List<NameValuePair> pParams, List<NameValuePair> pHeaders) throws WebServiceException {
		HttpResponse deleteResponse = null;
		try {
			deleteResponse = this.webDelete(pMethodName, pParams, pHeaders);
		} catch (WebServiceException e) {
			throw e;
		}
		if (deleteResponse == null) {
			throw new WebServiceException(WebServiceException.EMPTY_RESPONSE_EXCEPTION_STATUS, 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_MESSAGE, "", 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_EXCEPTION_USER_MESSAGE);
		}
		String stringEntity = null;
		try { 
			stringEntity = EntityUtils.toString(deleteResponse.getEntity());
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_STATUS, 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_USER_MESSAGE);
		}
		return stringEntity;
	}

	/**
	 * Call a POST web service with JSON parameters that return a String from the response content
	 * @param pMethodName - Name of the web service method to complete the URL call
	 * @param pHeaders - Headers to add to the call, as a List<NameValuePair> with <Header Name, Header Value>
	 * @param pUserAgent - User agent to set in the call header
	 * @return String
	 * @throws WebServiceException
	 */
	public String callPOSTJSONString(String pMethodName, List<NameValuePair> pParams, List<NameValuePair> pHeaders, String pUserAgent) throws WebServiceException {
		HttpResponse postResponse = null;
		JSONObject jsonObject = new JSONObject();
		for (NameValuePair param : pParams){
			try {
				jsonObject.put(param.getName(), param.getValue());
			}
			catch (JSONException e) {
				throw new WebServiceException(WebServiceException.ENCODING_EXCEPTION_STATUS, 
						WebServiceException.ENCODING_EXCEPTION_MESSAGE, 
						e.getMessage(), 
						WebServiceException.ENCODING_EXCEPTION_USER_MESSAGE);
			}
		}
		try {
			postResponse = this.webPost(pMethodName, pHeaders, HttpUtils.CONTENT_TYPE_JSON_UTF8, pUserAgent, jsonObject.toString());
		} catch (WebServiceException e) {
			throw e;
		}
		if (postResponse == null) {
			throw new WebServiceException(WebServiceException.EMPTY_RESPONSE_EXCEPTION_STATUS, 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_MESSAGE, "", 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_EXCEPTION_USER_MESSAGE);
		}
		String stringEntity = null;
		try { 
			stringEntity = EntityUtils.toString(postResponse.getEntity());
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_STATUS, 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_USER_MESSAGE);
		}
		return stringEntity;
	}

	public String callPOSTJSONString(String pMethodName, JSONObject pParams, List<NameValuePair> pHeaders, String pUserAgent) throws WebServiceException {
		HttpResponse postResponse = null;

		try {
			postResponse = this.webPost(pMethodName, pHeaders, HttpUtils.CONTENT_TYPE_JSON_UTF8, pUserAgent, pParams.toString());
		} catch (WebServiceException e) {
			throw e;
		}
		if (postResponse == null) {
			throw new WebServiceException(WebServiceException.EMPTY_RESPONSE_EXCEPTION_STATUS, 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_MESSAGE, "", 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_EXCEPTION_USER_MESSAGE);
		}
		String stringEntity = null;
		try { 
			stringEntity = EntityUtils.toString(postResponse.getEntity());
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_STATUS, 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_USER_MESSAGE);
		}
		return stringEntity;
	}

	/**
	 * Call a POST web service with JSON parameters that return a String from the response content
	 * @param pMethodName - Name of the web service method to complete the URL call
	 * @param pHeaders - Headers to add to the call, as a List<NameValuePair> with <Header Name, Header Value>
	 * @param pUserAgent - User agent to set in the call header)
	 * @return String
	 * @throws WebServiceException
	 */
	public String callPOSTJSONString(String pMethodName, String pParams, List<NameValuePair> pHeaders, String pUserAgent) throws WebServiceException {
		HttpResponse postResponse = null;

		try {
			postResponse = this.webPost(pMethodName, pHeaders, HttpUtils.CONTENT_TYPE_JSON_UTF8, pUserAgent, pParams);
		} catch (WebServiceException e) {
			throw e;
		}
		if (postResponse == null) {
			throw new WebServiceException(WebServiceException.EMPTY_RESPONSE_EXCEPTION_STATUS, 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_MESSAGE, "", 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_EXCEPTION_USER_MESSAGE);
		}
		String stringEntity = null;
		try { 
			stringEntity = EntityUtils.toString(postResponse.getEntity());
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_STATUS, 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_USER_MESSAGE);
		}
		return stringEntity;
	}

	public String callPOSTInputStreamString(String pMethodName, InputStreamEntity pParams, List<NameValuePair> pHeaders, String pUserAgent) throws WebServiceException {
		HttpResponse postResponse = null;

		try {
			postResponse = this.webPost(pMethodName, pHeaders, pUserAgent, pParams);
		} catch (WebServiceException e) {
			throw e;
		}
		if (postResponse == null) {
			throw new WebServiceException(WebServiceException.EMPTY_RESPONSE_EXCEPTION_STATUS, 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_MESSAGE, "", 
					WebServiceException.EMPTY_RESPONSE_EXCEPTION_EXCEPTION_USER_MESSAGE);
		}
		String stringEntity = null;
		try { 
			stringEntity = EntityUtils.toString(postResponse.getEntity());
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_STATUS, 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.DECODING_RESPONSE_ENTITY_EXCEPTION_USER_MESSAGE);
		}
		return stringEntity;
	}

	/**
	 * Basic call to a REST web service by GET method
	 * @param pMethodName - Name of the web service method to complete the URL call
	 * @param pParams - Parameter to call web service, as a List<NameValuePair> with <Parameter Name, Parameter Value>
	 * @param pHeaders - Headers to add to the call, as a List<NameValuePair> with <Header Name, Header Value>
	 * @param pContentType - Content type to set in the call header
	 * @return HttpResponse object
	 * @throws WebServiceException
	 */
	private HttpResponse webGet(String pMethodName, List<NameValuePair> pParams, List<NameValuePair> pHeaders, String pContentType) throws WebServiceException {
		String getUrl = webServiceUrl + pMethodName;
		int i = 0;
		if (pParams != null && !pParams.isEmpty() && pParams.size() > 0) {
			for (NameValuePair param : pParams)
			{
				if(i == 0){
					getUrl += "?";
				}
				else{
					getUrl += "&";
				}

				try {
					getUrl += param.getName() + "=" + URLEncoder.encode(param.getValue(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new WebServiceException(WebServiceException.ENCODING_EXCEPTION_STATUS, 
							WebServiceException.ENCODING_EXCEPTION_MESSAGE, 
							e.getMessage(), 
							WebServiceException.ENCODING_EXCEPTION_USER_MESSAGE);
				}

				i++;
			}
		}

		httpGet = new HttpGet(getUrl);

		if (pHeaders != null && !pHeaders.isEmpty() && pHeaders.size() > 0) {
			for (NameValuePair header : pHeaders)
			{
				httpGet.addHeader(header.getName(), header.getValue());	 
			}
		}
		if (pContentType != null) {
			httpGet.setHeader("Content-Type", pContentType);
		} else {
			httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
		}

		try {
			response = httpClient.execute(httpGet);
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_STATUS, 
					WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_USER_MESSAGE);
		}

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new WebServiceException(response.getStatusLine().getStatusCode(), 
					response.getStatusLine().getReasonPhrase(), "", "");
		}

		return response;

	}

    public static HttpResponse webGet(String pUrl) throws WebServiceException {
        HttpGet httpGet = new HttpGet(pUrl);
        DefaultHttpClient httpClient = (DefaultHttpClient) HttpUtils.getNewHttpClient();
        HttpResponse response;

        try {
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
            throw new WebServiceException(WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_STATUS,
                    WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_MESSAGE,
                    e.getMessage(),
                    WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_USER_MESSAGE);
        }

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new WebServiceException(response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase(), "", "");
        }

        return response;

    }

	/**
	 * Basic call to a REST web service by DELETE method
	 * @param pMethodName - Name of the web service method to complete the URL call
	 * @param pParams - Parameter to call web service, as a List<NameValuePair> with <Parameter Name, Parameter Value>
	 * @param pHeaders - Headers to add to the call, as a List<NameValuePair> with <Header Name, Header Value>
	 * @return HttpResponse object
	 * @throws WebServiceException
	 */
	public HttpResponse webDelete(String pMethodName, List<NameValuePair> pParams, List<NameValuePair> pHeaders) throws WebServiceException {
		String deleteUrl = webServiceUrl + pMethodName;

		int i = 0;
		if (pParams != null && !pParams.isEmpty() && pParams.size() > 0) {
			for (NameValuePair param : pParams)
			{
				if(i == 0){
					deleteUrl += "?";
				}
				else{
					deleteUrl += "&";
				}

				try {
					deleteUrl += param.getName() + "=" + URLEncoder.encode(param.getValue(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new WebServiceException(WebServiceException.ENCODING_EXCEPTION_STATUS, 
							WebServiceException.ENCODING_EXCEPTION_MESSAGE, 
							e.getMessage(), 
							WebServiceException.ENCODING_EXCEPTION_USER_MESSAGE);
				}

				i++;
			}
		}

		httpDelete = new HttpDelete(deleteUrl);

		if (pHeaders != null && !pHeaders.isEmpty() && pHeaders.size() > 0) {
			for (NameValuePair header : pHeaders)
			{
				httpDelete.addHeader(header.getName(), header.getValue());	 
			}
		}

		try {
			response = httpClient.execute(httpDelete);
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_STATUS, 
					WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_USER_MESSAGE);
		}
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new WebServiceException(response.getStatusLine().getStatusCode(), 
					response.getStatusLine().getReasonPhrase(), "", "");
		}

		return response;
	}

	/**
	 * Basic call to a REST web service by POST method
	 * @param pMethodName - Name of the web service method to complete the URL call
	 * @param pHeaders - Headers to add to the call, as a List<NameValuePair> with <Header Name, Header Value>
	 * @param pContentType - Content type to set in the call header
	 * @param pUserAgent - User agent to set in the call header
	 * @param pData - Data to add to the POST body as parameters to the call (XML, JSON, etc)
	 * @return HttpResponse object
	 * @throws WebServiceException
	 */
	private HttpResponse webPost(String pMethodName, List<NameValuePair> pHeaders, String pContentType, String pUserAgent, String pData) throws WebServiceException {

		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

		httpPost = new HttpPost(webServiceUrl + pMethodName);
		response = null;

		StringEntity strEntity = null;        

		if (pUserAgent != null) {
			httpPost.setHeader("User-Agent", pUserAgent);
		}

		//httpPost.setHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		//for accept types view: http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html

		if (pContentType != null) {
			httpPost.setHeader("Content-Type", pContentType);
		} else {
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		}

		if (pHeaders != null && !pHeaders.isEmpty() && pHeaders.size() > 0) {
			for (NameValuePair header : pHeaders)
			{
				httpPost.addHeader(header.getName(), header.getValue());	 
			}
		}

		try {
			strEntity = new StringEntity(pData,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new WebServiceException(WebServiceException.ENCODING_EXCEPTION_STATUS, 
					WebServiceException.ENCODING_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.ENCODING_EXCEPTION_USER_MESSAGE);
		}

		httpPost.setEntity(strEntity);

		try {
			response = httpClient.execute(httpPost,localContext);
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_STATUS, 
					WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_USER_MESSAGE);
		}
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new WebServiceException(response.getStatusLine().getStatusCode(), 
					response.getStatusLine().getReasonPhrase(), "", "");
		}

		return response;
	}

	private HttpResponse webPost(String pMethodName, List<NameValuePair> pHeaders, String pUserAgent, InputStreamEntity pData) throws WebServiceException {

		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

		httpPost = new HttpPost(webServiceUrl + pMethodName);
		response = null;       

		if (pUserAgent != null) {
			httpPost.setHeader("User-Agent", pUserAgent);
		}

		httpPost.setHeader("Content-Type", "binary/octet-stream");

		if (pHeaders != null && !pHeaders.isEmpty() && pHeaders.size() > 0) {
			for (NameValuePair header : pHeaders)
			{
				httpPost.addHeader(header.getName(), header.getValue());	 
			}
		}

		if (pData != null) {
			pData.setContentType("binary/octet-stream");
			pData.setChunked(true);
		}

		httpPost.setEntity(pData);

		try {
			response = httpClient.execute(httpPost,localContext);
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_STATUS, 
					WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.HTTP_CLIENT_EXECUTION_EXCEPTION_USER_MESSAGE);
		}
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new WebServiceException(response.getStatusLine().getStatusCode(), 
					response.getStatusLine().getReasonPhrase(), "", "");
		}

		return response;
	}

	/**
	 * Get a Input stream from a HTTP connection
	 * @param pUrlString - URL to connect to
	 * @return InputStream
	 * @throws WebServiceException
	 */
	public InputStream getHttpStream(String pUrlString) throws WebServiceException {
		InputStream in = null;
		int response = -1;


		try{
			URL url = new URL(pUrlString);
			URLConnection conn = url.openConnection();

			if (!(conn instanceof HttpURLConnection))
				throw new WebServiceException(WebServiceException.NO_HTTP_CONNECTION_EXCEPTION_STATUS, 
						WebServiceException.NO_HTTP_CONNECTION_EXCEPTION_MESSAGE, "", 
						WebServiceException.NO_HTTP_CONNECTION_EXCEPTION_USER_MESSAGE);

			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect(); 

			response = httpConn.getResponseCode();                 

			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
			}
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.HTTP_CONNECTION_EXCEPTION_STATUS, 
					WebServiceException.HTTP_CONNECTION_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.HTTP_CONNECTION_EXCEPTION_USER_MESSAGE);
		}

		return in;
	}

	/**
	 * Clear the cookies from http client
	 */
	public void clearCookies() {
		httpClient.getCookieStore().clear();
	}

	/**
	 * Abort HTTP connection
	 * @throws WebServiceException
	 */
	public void abort() throws WebServiceException {
		try {
			if (httpClient != null) {
				httpPost.abort();
			}
		} catch (Exception e) {
			throw new WebServiceException(WebServiceException.ABORT_HTTP_CONNECTION_EXCEPTION_STATUS, 
					WebServiceException.ABORT_HTTP_CONNECTION_EXCEPTION_MESSAGE, 
					e.getMessage(), 
					WebServiceException.ABORT_HTTP_CONNECTION_EXCEPTION_USER_MESSAGE);
		}
	}

}

