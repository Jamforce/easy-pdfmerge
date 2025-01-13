package com.example;

import org.apache.commons.cli.*;

public class PdfMergerApp {

  public static void main(String[] args) {
    Options options = new Options();

    Option filesOption = new Option("f", "files", true, "List of PDF files to merge");
    filesOption.setRequired(true);
    filesOption.setArgs(Option.UNLIMITED_VALUES); // Support multiple values
    options.addOption(filesOption);

    Option outputOption = new Option("o", "output", true, "Output file path");
    outputOption.setRequired(true);
    options.addOption(outputOption);

    Option paginateOption = new Option("p", "pageNumberStamp", true, "Apply page number stamp");
    paginateOption.setRequired(false);
    options.addOption(paginateOption);

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();

    try {
      CommandLine cmd = parser.parse(options, args);

      String[] inputFiles = cmd.getOptionValues("files");
      String outputPath = cmd.getOptionValue("output");
      String pageNumberStamp = cmd.getOptionValue("pageNumberStamp");

      PdfMerger merger = new PdfMerger();
      merger.merge(inputFiles, outputPath, pageNumberStamp);

      System.out.println("PDFs merged successfully into: " + outputPath);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      formatter.printHelp("pdf-merger", options);
      System.exit(1);
    }
  }
}
