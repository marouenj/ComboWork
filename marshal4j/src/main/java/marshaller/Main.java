package marshaller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.fasterxml.jackson.databind.JsonNode;

public class Main {
	
	public static void main(String[] args) {
		CommandLine cmd = cli(args);
		
		JsonNode vars = JsonUtil.read("file://" + cmd.getOptionValue("vars"));
		if (!JsonUtil.varsIsValid(vars))
			System.exit(1);
		
		JsonNode vals = JsonUtil.read("file://" + cmd.getOptionValue("vals"));
		if (!JsonUtil.valsIsValid(vals))
			System.exit(1);
		
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(cmd.getOptionValue("out"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(fout);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Marshaller.parseTestcases(vars, vals, out);
		
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static CommandLine cli(String[] args) {
		Options options = new Options();
		
		options.addOption("vars", true, "path to vars file");
		options.addOption("vals", true, "path to vals file");
		options.addOption("out" , true, "path to persisted file");
		
//		CommandLineParser parser = new BasicParser();
		CommandLineParser parser = new PosixParser();
		
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.exit(1);
		}
		
		if (!cmd.hasOption("vars") || !cmd.hasOption("vals") || !cmd.hasOption("out")) {
			System.exit(1);
		}
		
		return cmd;
	}

}
