package combowork.marshal4j;

import com.fasterxml.jackson.databind.JsonNode;
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
     * @param varsPaths List of vars files.
     * @param config    Config file
     */
    public static void forEachFile(List<String> varsPaths, CliUtil.Config config) {
        if (varsPaths == null) {
            throw new NullPointerException(LogMessages.VARS_LIST_NULL.getText());
        }

        for (String varsPath : varsPaths) {
            JsonNode vars = JsonUtil.read(config.getBaseDir() + "/" + varsPath);
            if (!JsonUtil.varsIsValid(vars)) {
                LOGGER.error(LogMessages.VARS_NOT_VALID.getText(varsPath));
                throw new RuntimeException(LogMessages.VARS_NOT_VALID.getText(varsPath));
            }

            JsonNode vals = JsonUtil.read(config.getPathToCombine() + "/" + varsPath);
            if (!JsonUtil.valsIsValid(vals)) {
                LOGGER.error(LogMessages.VALS_NOT_VALID.getText(varsPath));
                throw new RuntimeException(LogMessages.VALS_NOT_VALID.getText(varsPath));
            }

            FileOutputStream fout;
            try {
                fout = new FileOutputStream(config.getPathToMarshal4j() + "/" + varsPath);
            } catch (FileNotFoundException e) {
                LOGGER.error(LogMessages.FILE_OPEN_UNABLE.getText(varsPath));
                throw new RuntimeException(LogMessages.FILE_OPEN_UNABLE.getText(varsPath));
            }

            ObjectOutput out;
            try {
                out = new ObjectOutputStream(fout);
            } catch (IOException e) {
                LOGGER.error(LogMessages.FILE_OPEN_UNABLE.getText(varsPath));
                throw new RuntimeException(LogMessages.FILE_OPEN_UNABLE.getText(varsPath));
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
                continue;
            }

            JsonNode prefix = var.get("prefix");
            JsonNode suffix = var.get("suffix");

            if (prefix == null && suffix == null) { // primitive
                JsonNode val = testCaseItr.next();
                JsonNode type = var.get("type");
                String typeAsText = "";
                if (type != null) {
                    typeAsText = type.asText();
                }
                try {
                    switch (typeAsText) {
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
                JsonNode varsRec = var.get("vals");
                Object obj = ReflectionUtil.instanceFrom(prefix.asText() + "." + suffix.asText());

//                parseVarsRec(varsRec, obj, itrVals);

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

    /**
     * Log messages for this class logger
     */
    private enum LogMessages {
        VARS_LIST_NULL("list of vars paths is null"),
        VARS_NOT_VALID("vars not valid, "),
        VALS_NOT_VALID("vals not valid, "),
        FILE_OPEN_UNABLE("unable to open file, "),
        FILE_WRITE_UNABLE("unable to write to file"),
        UNDEFINED_STATE_PREFIX_SUFFIX("undefined state: either 'prefix' or 'suffix' are null");

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
