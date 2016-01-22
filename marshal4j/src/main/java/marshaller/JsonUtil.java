package marshaller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class JsonUtil {

	/*
	 * unmarshalls a json file into a tree-based data structure
	 */
	public static JsonNode read(String url) {
		// pattern-match the url
		String json = null;
		try {
			json = IOUtils.toString(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
        // create an ObjectMapper instance.
        ObjectMapper mapper = new ObjectMapper();
        
        // use the ObjectMapper to read the json string and create a tree
        JsonNode _cases = null;
		try {
			_cases = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return _cases;
	}
	
	
	
	
	
	/*
	 * validates the vars file, in order to keep to logic clean and less cluttered with conditional statements
	 */
	public static boolean varsIsValid(JsonNode vars) {
		if (vars.getNodeType() != JsonNodeType.ARRAY) // root should start with an array of objects
			return false;
		
		Iterator<JsonNode> itrVars = vars.iterator();
        while (itrVars.hasNext()) {
        	JsonNode var = itrVars.next();
        	if (var.getNodeType() != JsonNodeType.OBJECT) // it should be an object encompassing, among other things, the var of the vals
        		return false;
        }
		
		return true;
	}
	
	
	
	
	public static boolean valsIsValid(JsonNode vals) {
		return true;
	}
	
	
	
	
	
	/*
	 * abstract utilities
	 */
	
	/*
	 * utility to reduce the boilerplate of simply getting the key as string
	 */
	public static String getJsonNodeAsText(JsonNode node, String key) {
		String valAsText = null;
		if (node.get(key) != null)
			valAsText = node.get(key).asText();
		
		return valAsText;
	}
}
