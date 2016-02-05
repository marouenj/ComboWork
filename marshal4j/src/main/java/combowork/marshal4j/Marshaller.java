package combowork.marshal4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import combowork.marshal4j.util.CliUtil;
import combowork.marshal4j.util.JsonUtil;
import combowork.marshal4j.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

public class Marshaller {

    private final static Logger LOGGER = LoggerFactory.getLogger(Marshaller.class);

    /**
     * Loop over the list of vars files.
     * For each iteration, loads vars file and vals file, opens output file.
     *
     * @param templates List of template paths
     * @param config    Config file
     */
    public static void forEachTemplate(List<String> templates, CliUtil.Config config) {
        if (templates == null) {
            throw new NullPointerException(LogMessages.TEMPLATE_LIST_NULL.getText());
        }

        for (String template : templates) {
            JsonNode vars = JsonUtil.read(config.getBaseDir() + "/" + template);
            if (!JsonUtil.varsIsValid(vars)) {
                LOGGER.error(LogMessages.VARS_NOT_VALID.getText(template));
                throw new RuntimeException(LogMessages.VARS_NOT_VALID.getText(template));
            }

            JsonNode vals = JsonUtil.read(config.getPathToCombine() + "/" + template);
            if (!JsonUtil.valsIsValid(vals)) {
                LOGGER.error(LogMessages.VALS_NOT_VALID.getText(template));
                throw new RuntimeException(LogMessages.VALS_NOT_VALID.getText(template));
            }

            FileOutputStream fout;
            try {
                fout = new FileOutputStream(config.getPathToMarshal4j() + "/" + template);
            } catch (FileNotFoundException e) {
                LOGGER.error(LogMessages.FILE_OPEN_UNABLE.getText(template));
                throw new RuntimeException(LogMessages.FILE_OPEN_UNABLE.getText(template));
            }

            ObjectOutput out;
            try {
                out = new ObjectOutputStream(fout);
            } catch (IOException e) {
                LOGGER.error(LogMessages.FILE_OPEN_UNABLE.getText(template));
                throw new RuntimeException(LogMessages.FILE_OPEN_UNABLE.getText(template));
            }

            forEachTestCase(vars, vals, out);
        }
    }

    /**
     * For the vars file, loop over the list of test cases.
     *
     * @param vars      Tree structure rooted at the list of vars
     * @param testCases Tree structure rooted at the list of test cases
     * @param out       file containing list of marshalled objects, one per testcase
     */
    private static void forEachTestCase(JsonNode vars, JsonNode testCases, ObjectOutput out) {
        for (JsonNode testCase : testCases) {
            if (testCase == null || testCase.get("Case") == null) { // TODO log the test case in a the final report
                continue;
            }

            forEachVar(vars, testCase.get("Case"), out);
        }
    }

    private static void forEachVar(JsonNode vars, JsonNode testcase, ObjectOutput out) {
        Iterator<JsonNode> testCaseItr = testcase.iterator();

        for (JsonNode var : vars) {
            if (var == null) { // TODO is this needed?
                throw new RuntimeException(LogMessages.VAR_NULL.getText());
            }

            JsonNode prefix = var.get("prefix");
            JsonNode suffix = var.get("suffix");

            if (prefix == null && suffix == null) { // primitive
                JsonNode val = testCaseItr.next();
                String type = JsonUtil.asText(var, "type", "");
                try {
                    switch (type) {
                        // objects
                        case "String":
                            out.writeObject(val.asText());
                            break;
                        case "Boolean":
                            out.writeObject(val.asBoolean());
                            break;
                        case "Character":
                            out.writeObject(val.asText().charAt(0));
                            break;
                        case "Byte":
                            out.writeObject((byte) val.asInt());
                            break;
                        case "Short":
                            out.writeObject((short) val.asInt());
                            break;
                        case "Integer":
                            out.writeObject(val.asInt());
                            break;
                        case "Long":
                            out.writeObject(val.asLong());
                            break;
                        case "Float":
                            out.writeObject((float) val.asDouble());
                            break;
                        case "Double":
                            out.writeObject(val.asDouble());
                            break;
                        // primitives
                        case "boolean":
                            out.writeBoolean(val.asBoolean());
                            break;
                        case "char":
                            out.writeChar(val.asText().charAt(0));
                            break;
                        case "byte":
                            out.writeByte((byte) val.asInt());
                            break;
                        case "short":
                            out.writeShort((short) val.asInt());
                            break;
                        case "int":
                            out.writeInt(val.asInt());
                            break;
                        case "long":
                            out.writeLong(val.asLong());
                            break;
                        case "float":
                            out.writeFloat((float) val.asDouble());
                            break;
                        case "double":
                            out.writeDouble(val.asDouble());
                            break;
                        default:
                            out.writeObject(val.asText());
                    }
                } catch (IOException e) {
                    LOGGER.error(LogMessages.FILE_WRITE_UNABLE.getText());
                    throw new RuntimeException(LogMessages.FILE_WRITE_UNABLE.getText());
                }
            } else if (prefix != null && suffix != null) { // complex
                Object obj = ReflectionUtil.instanceFrom(prefix.asText() + "." + suffix.asText());

                forEachValObject(var.get("vals"), testCaseItr, obj);

                try {
                    out.writeObject(obj);
                } catch (IOException e) {
                    LOGGER.error(LogMessages.FILE_WRITE_UNABLE.getText());
                    throw new RuntimeException(LogMessages.FILE_WRITE_UNABLE.getText());
                }
            } else { // TODO undefined. Handle as bug
                LOGGER.error(LogMessages.UNDEFINED_STATE_PREFIX_SUFFIX.getText());
                throw new RuntimeException(LogMessages.UNDEFINED_STATE_PREFIX_SUFFIX.getText());
            }
        }
    }

    private static void forEachValObject(JsonNode vals, Iterator<JsonNode> testCaseItr, Object obj) {
        for (JsonNode var : vals) {
            if (var.getNodeType() != JsonNodeType.OBJECT) { // TODO handle as bug. Structure should be validated beforehand
                continue;
            }

            String prefix = JsonUtil.asText(var, "prefix", null);
            String suffix = JsonUtil.asText(var, "suffix", null);
            String setter = JsonUtil.asText(var, "setter", null);
            String type = JsonUtil.asText(var, "type", "");

            if (setter == null) { // TODO handle as template is not valid
                throw new RuntimeException();
            }

            if (prefix == null && suffix == null) { // primitive
                JsonNode val = testCaseItr.next();

                Class<?>[] argsType = new Class<?>[1];
                Object[] args = new Object[1];

                switch (type) {
                    // objects
                    case "String":
                        argsType[0] = java.lang.String.class;
                        args[0] = val.asText();
                        break;
                    case "Boolean":
                        argsType[0] = java.lang.Boolean.class;
                        args[0] = val.asBoolean();
                        break;
                    case "Character":
                        argsType[0] = java.lang.Character.class;
                        args[0] = val.asText().charAt(0);
                        break;
                    case "Byte":
                        argsType[0] = java.lang.Byte.class;
                        args[0] = (byte) val.asInt();
                        break;
                    case "Short":
                        argsType[0] = java.lang.Short.class;
                        args[0] = (short) val.asInt();
                        break;
                    case "Integer":
                        argsType[0] = java.lang.Integer.class;
                        args[0] = val.asInt();
                        break;
                    case "Long":
                        argsType[0] = java.lang.Long.class;
                        args[0] = val.asLong();
                        break;
                    case "Float":
                        argsType[0] = java.lang.Float.class;
                        args[0] = (float) val.asDouble();
                        break;
                    case "Double":
                        argsType[0] = java.lang.Double.class;
                        args[0] = val.asDouble();
                        break;
                    // primitives
                    case "boolean":
                        argsType[0] = boolean.class;
                        args[0] = val.asBoolean();
                        break;
                    case "char":
                        argsType[0] = char.class;
                        args[0] = val.asText().charAt(0);
                        break;
                    case "byte":
                        argsType[0] = byte.class;
                        args[0] = (byte) val.asInt();
                        break;
                    case "short":
                        argsType[0] = short.class;
                        args[0] = (short) val.asInt();
                        break;
                    case "int":
                        argsType[0] = int.class;
                        args[0] = val.asInt();
                        break;
                    case "long":
                        argsType[0] = long.class;
                        args[0] = val.asLong();
                        break;
                    case "float":
                        argsType[0] = float.class;
                        args[0] = (float) val.asDouble();
                        break;
                    case "double":
                        argsType[0] = double.class;
                        args[0] = val.asDouble();
                        break;
                }

                ReflectionUtil.invokeMethod(obj, setter, argsType, args);
            } else if (prefix != null && suffix != null) { // complex
                String clazz = prefix + "." + suffix;

                Class<?>[] argsType = new Class<?>[1];
                argsType[0] = ReflectionUtil.classFrom(clazz);

                Object[] args = new Object[1];
                args[0] = ReflectionUtil.instanceFrom(clazz);

                ReflectionUtil.invokeMethod(obj, setter, argsType, args);

                forEachValObject(var.get("vals"), testCaseItr, args[0]);
            } else { // TODO undefined. Handle as bug
                LOGGER.error(LogMessages.UNDEFINED_STATE_PREFIX_SUFFIX.getText());
                throw new RuntimeException(LogMessages.UNDEFINED_STATE_PREFIX_SUFFIX.getText());
            }
        }
    }

    /**
     * Log messages for this class logger
     */
    private enum LogMessages {
        TEMPLATE_LIST_NULL("list of vars paths is null"),
        VARS_NOT_VALID("vars not valid, "),
        VALS_NOT_VALID("vals not valid, "),
        FILE_OPEN_UNABLE("unable to open file, "),
        FILE_WRITE_UNABLE("unable to write to file"),
        UNDEFINED_STATE_PREFIX_SUFFIX("undefined state: either 'prefix' or 'suffix' are null"),
        VAR_NULL("var element is null");

        private final String msg;

        LogMessages(String msg) {
            this.msg = msg;
        }

        public String getText() {
            return msg;
        }

        public String getText(String path) {
            return msg + path;
        }

    }
}
