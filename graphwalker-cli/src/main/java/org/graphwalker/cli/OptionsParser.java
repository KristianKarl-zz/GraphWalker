package org.graphwalker.cli;


import org.graphwalker.core.StopCondition;
import org.graphwalker.core.condition.AlternativeCondition;
import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.VertexCoverage;
import org.graphwalker.core.generator.CombinedPathGenerator;
import org.graphwalker.core.generator.RandomPath;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.graphwalker.cli.Lexer.lex;

public class OptionsParser {

  public static CombinedPathGenerator parseGenerator(String str) {

    List<Lexer.Token> tokens = lex(str);
    for(Lexer.Token t : tokens) {
      System.out.println(t);
    }


    CombinedPathGenerator combinedGenerator = new CombinedPathGenerator();



    return combinedGenerator;
  }
}
