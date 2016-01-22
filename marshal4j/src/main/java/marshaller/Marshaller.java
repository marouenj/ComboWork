package marshaller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class Marshaller {
	
	//
	// recursive calls: workflow
	//
	// # # # # # # # # ##                       # # # # # # #                 # # # # # # # ##             # # # # # # # # ##
	// #                # loop over test cases  #           # loop over vars  #              #             #                #
	// # parseTestcases # ====================> # parseVars # ==============> # parseVarsRec # ==========> # parseVarsArray #
	// #                #                       #           #                 #              #             #                #
	// ## # # # # # # # #                       # # # # # # #                 #              # <========== #                #
	//                                                                        #              #             #                #
	//                                                                        #              #             ## # # # # # # # #
	//                                                                        #              #
	//                                                                        #              #
	//                                                                        #              #
	//                                                                        #              #
	//                                                                        #              #                                             # # # # # # # # # # # # ##
	//                                                                        #              #                                             #                        #
	//                                                                        #              # <========================================== # parseVarsObjectComplex #
	//                                                                        #              #                                             #                        #
	//                                                                        #              #             # # # # # # # # # #             #                        #
	//                                                                        #              #             #                 #             #                        #
	//                                                                        #              # ==========> # parseVarsObject # ==========> #                        #
	//                                                                        #              #             #                 #             #                        #
	//                                                                        ## # # # # # # #             #                 #             ## # # # # # # # # # # # #
	//                                                                                                     #                 #
	//                                                                                                     #                 #
	//                                                                                                     #                 #             # # # # # # # # # # # # # ##
	//                                                                                                     #                 #             #                          #
	//                                                                                                     #                 # ==========> # parseVarsObjectPrimitive # (end point for recursive process)
	//                                                                                                     #                 #             #                          #
	//                                                                                                     # # # # # # # # # #             ## # # # # # # # # # # # # #
	//
	
	
	
	
	
	/*
	 * loop over each test case
	 */
	public static void parseTestcases(JsonNode vars, JsonNode tests, ObjectOutputStream out)
	{
        Iterator<JsonNode> itrTests = tests.iterator();
        
        while (itrTests.hasNext()) {
        	JsonNode test = itrTests.next();
        	
        	JsonNode vals = test.get("Case");
        	if (vals != null) {
            	Iterator<JsonNode> itrVals = vals.iterator();
                parseVars(vars, itrVals, out);
//                System.out.println();
        	}
        }
    }

	public static void parseVars(JsonNode vars, Iterator<JsonNode> itrVals, ObjectOutputStream out)
	{
		Iterator<JsonNode> itrVars = vars.iterator();
		
        while (itrVars.hasNext()) {
        	JsonNode var = itrVars.next();
        	
			JsonNode prefix = var.get("prefix");
			JsonNode suffix = var.get("suffix");
			
			if (prefix != null && suffix != null) { // complex
				JsonNode varsRec = var.get("vals");
				Object obj = ReflectionUtil.instanceFrom(prefix.asText() + "." + suffix.asText());
				
				parseVarsRec(varsRec, obj, itrVals);
				
				try {
					out.writeObject(obj);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
//				System.out.println(obj.toString());
        	}
			else { // primitive:
				// String (char, java.lang.Character, java.util.String),
				// Boolean (boolean, Java.lang.Boolean),
				// Number (byte, java.lang.Byte, short, java.lang.Short, int, java.lang,Integer, long, java.lang.Long, float, java.lang.Float, double, java.lang.Double)
				JsonNode val = itrVals.next();
				try {
					switch (var.get("type").asText()) {
					case "char" :
						out.writeChar(val.asText().charAt(0));
						break;
					case "Character" :
						out.writeObject((java.lang.Character) val.asText().charAt(0));
						break;
					case "String" :
						out.writeObject(val.asText());
						break;
					case "boolean" :
						out.writeBoolean(val.asBoolean());
						break;
					case "Boolean" :
						out.writeObject((java.lang.Boolean) val.asBoolean());
						break;
					case "byte":
						out.writeByte((byte) val.asInt());
						break;
					case "Byte":
						out.writeObject((java.lang.Byte) ((byte) val.asInt()));
						break;
					case "short":
						out.writeShort((short) val.asInt());
						break;
					case "Short":
						out.writeObject((java.lang.Short) ((short) val.asInt()));
						break;
					case "int":
						out.writeInt(val.asInt());
						break;
					case "Integer":
						out.writeObject((java.lang.Integer) val.asInt());
						break;
					case "long":
						out.writeLong(val.asLong());
						break;
					case "Long":
						out.writeObject((java.lang.Long) val.asLong());
						break;
					case "float":
						out.writeFloat((float) val.asDouble());
						break;
					case "Float":
						out.writeObject((java.lang.Float) ((float) val.asDouble()));
						break;
					case "double":
						out.writeDouble(val.asDouble());
						break;
					case "Double":
						out.writeObject((java.lang.Double) val.asDouble());
						break;
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
				
//        		System.out.println("setting val " + val + " to var " + var.get("name"));
        	}
        }
	}
	
	public static void parseVarsRec(JsonNode curr, Object parent, Iterator<JsonNode> itrVals)
	{
		if (curr.getNodeType() == JsonNodeType.ARRAY) {
			parseVarsArray(curr, parent, itrVals);
		}
		else if (curr.getNodeType() == JsonNodeType.OBJECT) {
			parseVarsObject(curr, parent, itrVals);
		}
	}
	
	public static void parseVarsArray(JsonNode curr, Object parent, Iterator<JsonNode> itrVals)
	{
		Iterator<JsonNode> currElmts = curr.iterator();
        while (currElmts.hasNext()) {
        	JsonNode currSub = currElmts.next();
        	parseVarsRec(currSub, parent, itrVals);
        }
	}
	
	public static void parseVarsObject(JsonNode curr, Object parent, Iterator<JsonNode> itrVals) {
		String prefix = JsonUtil.getJsonNodeAsText(curr, "prefix");
		String suffix = JsonUtil.getJsonNodeAsText(curr, "suffix");
		String setter = JsonUtil.getJsonNodeAsText(curr, "setter");
		String type   = JsonUtil.getJsonNodeAsText(curr, "type");
		
		if (prefix != null && suffix != null) { // complex object
			parseVarsObjectComplex(curr, parent, itrVals, prefix, suffix, setter);
    	}
		else { // primitive object (int, boolean, string, ...)
			parseVarsObjectPrimitive(parent, itrVals, setter, type);
    	}
	}
	
	public static void parseVarsObjectComplex(JsonNode curr, Object parent, Iterator<JsonNode> itrVals, String prefix, String suffix, String setter) {
		String clazz = prefix + "." + suffix;
		
	    Class<?>[] argsType = new Class<?>[1];
	    argsType[0] = ReflectionUtil.classFrom(clazz);
	    
	    Object[] args = new Object[1];
	    args[0] = ReflectionUtil.instanceFrom(clazz);
	    
	    ReflectionUtil.invokeMethod(parent, setter, argsType, args);
		
		JsonNode vals = curr.get("vals");
		parseVarsRec(vals, args[0], itrVals);
	}

	public static void parseVarsObjectPrimitive(Object parent, Iterator<JsonNode> itrVals, String setter, String type) {
	    Class<?>[] argsType = new Class<?>[1];
	    Object[] args = new Object[1];
	    
	    JsonNode val = itrVals.next();
	    
		if (type == null) { // when no type is specified, it defaults to java.util.String
			argsType[0] = String.class;
			args[0] = val.asText();
		} else {
			switch (type) {
				case "char" :
		    		argsType[0] = char.class;
				    args[0] = val.asText().charAt(0);
					break;
				case "Character" :
		    		argsType[0] = java.lang.Character.class;
				    args[0] = (java.lang.Character) val.asText().charAt(0);
					break;
				case "String" :
		    		argsType[0] = java.lang.String.class;
				    args[0] = val.asText();
					break;
				case "boolean" :
		    		argsType[0] = boolean.class;
				    args[0] = val.asBoolean();
					break;
				case "Boolean" :
		    		argsType[0] = java.lang.Boolean.class;
				    args[0] = (java.lang.Boolean) val.asBoolean();
					break;
				case "byte":
		    		argsType[0] = byte.class;
				    args[0] = (byte) val.asInt();
					break;
				case "Byte":
		    		argsType[0] = java.lang.Byte.class;
				    args[0] = (java.lang.Byte) ((byte) val.asInt());
					break;
				case "short":
		    		argsType[0] = short.class;
				    args[0] = (short) val.asInt();
					break;
				case "Short":
		    		argsType[0] = java.lang.Short.class;
				    args[0] = (java.lang.Short) ((short) val.asInt());
					break;
				case "int":
		    		argsType[0] = int.class;
				    args[0] = val.asInt();
					break;
				case "Integer":
		    		argsType[0] = java.lang.Integer.class;
				    args[0] = (java.lang.Integer) val.asInt();
					break;
				case "long":
		    		argsType[0] = long.class;
				    args[0] = val.asLong();
					break;
				case "Long":
		    		argsType[0] = java.lang.Long.class;
				    args[0] = (java.lang.Long) val.asLong();
					break;
				case "float":
		    		argsType[0] = float.class;
				    args[0] = (float) val.asDouble();
					break;
				case "Float":
		    		argsType[0] = java.lang.Float.class;
				    args[0] = (java.lang.Float) ((float) val.asDouble());
					break;
				case "double":
		    		argsType[0] = double.class;
				    args[0] = val.asDouble();
					break;
				case "Double":
		    		argsType[0] = java.lang.Double.class;
				    args[0] = (java.lang.Double) val.asDouble();
					break;
			}
		}
	    
	    ReflectionUtil.invokeMethod(parent, setter, argsType, args);
	}
}
