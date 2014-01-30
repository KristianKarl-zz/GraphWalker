package org.graphwalker.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(commandDescription = "Generate a test sequence offline. The sequence is printed to the standard output")
public class CommandOffline {

  @Parameter(description = "The list of files to commit")
  private List<String> files;

  @Parameter(names = "--amend", description = "Amend")
  private Boolean amend = false;

  @Parameter(names = "--author")
  private String author;
}
