package combowork.marshal4j;

import combowork.marshal4j.util.CliUtil;
import combowork.marshal4j.util.FileUtil;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            CliUtil.Config config = CliUtil.parseCli(args);
            List<String> listOfVars = FileUtil.getListOfVars(config);
            Marshaller.forEachTemplate(listOfVars, config);
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    private static void handleExceptions(Exception e) {
    }
}
