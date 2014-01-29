package org.graphwalker.cli;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.graphwalker.cli.Lexer.lex;

public class LexerTest {

  @Test
  public void simple1() {

    List<Lexer.Token> tokens = lex("random(never)");
    for(Lexer.Token t : tokens) {
      System.out.println(t);
    }

  }

  @Test
  public void simple2() {

    List<Lexer.Token> tokens = lex("random(edge_coverage(100) and never)");
    for(Lexer.Token t : tokens) {
      System.out.println(t);
    }

  }

  @Test
  public void simple3() {

    List<Lexer.Token> tokens = lex("random(edge_coverage(100) and never), a_star(reached_vertex(\"v_SomeName\") or edge_coverage(90))");
    for(Lexer.Token t : tokens) {
      System.out.println(t);
    }

  }
}