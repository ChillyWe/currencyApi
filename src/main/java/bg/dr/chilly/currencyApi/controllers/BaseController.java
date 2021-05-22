package bg.dr.chilly.currencyApi.controllers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import bg.dr.chilly.currencyApi.Constants;
import bg.dr.chilly.currencyApi.exceptions.FixerException;
import bg.dr.chilly.currencyApi.io.URLReader;
import bg.dr.chilly.currencyApi.io.URLReaderImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public abstract class BaseController {

	protected final ObjectMapper objectMapper;
	protected final URLReader urlReader;

	@Autowired
	protected BaseController(ObjectMapper objectMapper, URLReaderImpl urlReader) {
		this.objectMapper = objectMapper;
		this.urlReader = urlReader;
	}

	// Method to extract json data from URL
	protected JsonNode readJSONfromURI(String uriString) {	
		JsonNode result = null;
		String jsonStringResult = Constants.EMPTY_STRING;
		
		try {
			URL url = new URL(uriString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(Constants.HTTP_GET_METHOD);
			conn.connect();
			int responsecode = conn.getResponseCode();

			if (responsecode != 200) {
				throw new FixerException(Constants.FIXER_EXCEPTION_MESSAGE + responsecode);
			} else {
				jsonStringResult = urlReader.read(url);
			}
			conn.disconnect();
			result = objectMapper.readTree(jsonStringResult);
		} catch (MalformedURLException me) {
			// TODO: 5/22/21 handle exception
			log.info(me.getMessage());
		} catch (IOException ioe) {
			// TODO: 5/22/21 handle exception
			log.info(ioe.getMessage());
		} catch (FixerException fe) {
			// TODO: 5/22/21 handle exception
			log.info(fe.getMessage());
		}
		
		return result;
	}
}