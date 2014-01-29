package org.graphwalker.cli;

import org.graphwalker.core.condition.*;
import org.graphwalker.core.generator.AStarPath;
import org.graphwalker.core.generator.RandomPath;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
  public static ArrayList<Token> lex(String input) {
    // The tokens to return
    ArrayList<Token> tokens = new ArrayList<Token>();

    // Lexer logic begins here
    StringBuffer tokenPatternsBuffer = new StringBuffer();
    for (TokenType tokenType : TokenType.values())
      tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
    Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));

    // Begin matching tokens
    Matcher matcher = tokenPatterns.matcher(input);
    while (matcher.find()) {
      if (matcher.group(TokenType.RANDOMGENERATOR.name()) != null) {
        tokens.add(new Token(TokenType.RANDOMGENERATOR, new RandomPath()));
        continue;

      } else if (matcher.group(TokenType.ASTARGENERATOR.name()) != null) {
        tokens.add(new Token(TokenType.ASTARGENERATOR, new AStarPath()));
        continue;

      } else if (matcher.group(TokenType.NEVER.name()) != null) {
        tokens.add(new Token(TokenType.NEVER, new Never()));
        continue;

      } else if (matcher.group(TokenType.EDGECOVERAGECONDITION.name()) != null) {
        tokens.add(new Token(TokenType.EDGECOVERAGECONDITION, new EdgeCoverage()));
        continue;

      } else if (matcher.group(TokenType.VERTEXCOVERAGECONDITION.name()) != null) {
        tokens.add(new Token(TokenType.VERTEXCOVERAGECONDITION, new VertexCoverage()));
        continue;

      } else if (matcher.group(TokenType.REACHEDEDGECONDITION.name()) != null) {
        tokens.add(new Token(TokenType.REACHEDEDGECONDITION, new ReachedEdge()));
        continue;

      } else if (matcher.group(TokenType.REACHEDVERTEXCONDITION.name()) != null) {
        tokens.add(new Token(TokenType.REACHEDVERTEXCONDITION, new ReachedVertex()));
        continue;

      } else if (matcher.group(TokenType.OR.name()) != null) {
        tokens.add(new Token(TokenType.OR, matcher.group(TokenType.OR.name())));
        continue;

      } else if (matcher.group(TokenType.AND.name()) != null) {
        tokens.add(new Token(TokenType.AND, matcher.group(TokenType.AND.name())));
        continue;

      } else if (matcher.group(TokenType.STRING.name()) != null) {
        tokens.add(new Token(TokenType.STRING, matcher.group(TokenType.STRING.name())));
        continue;

      } else if (matcher.group(TokenType.NUMBER.name()) != null) {
        tokens.add(new Token(TokenType.NUMBER, matcher.group(TokenType.NUMBER.name())));
        continue;

      } else if (matcher.group(TokenType.LPARENTHESES.name()) != null) {
        tokens.add(new Token(TokenType.LPARENTHESES, matcher.group(TokenType.LPARENTHESES.name())));
        continue;

      } else if (matcher.group(TokenType.RPARENTHESES.name()) != null) {
        tokens.add(new Token(TokenType.RPARENTHESES, matcher.group(TokenType.RPARENTHESES.name())));
        continue;

      } else if (matcher.group(TokenType.WHITESPACE.name()) != null)
        continue;
    }

    return tokens;
  }

  public static enum TokenType {
    // Token types cannot have underscores
    RANDOMGENERATOR("random"),
    ASTARGENERATOR("a_star"),
    EDGECOVERAGECONDITION("edge_coverage"),
    VERTEXCOVERAGECONDITION("vertex_coverage"),
    REACHEDEDGECONDITION("reached_edge"),
    REACHEDVERTEXCONDITION("reached_vertex"),
    NEVER("never"),
    OR(" or "),
    AND(" and "),
    LPARENTHESES("[(]{1}"),
    RPARENTHESES("[)]{1}"),
    STRING("\"\\p{ASCII}+?\""),
    NUMBER("-?[0-9]+"),
    WHITESPACE("[\\s]+");

    public final String pattern;

    private TokenType(String pattern) {
      this.pattern = pattern;
    }
  }

  public static class Token {
    public TokenType type;
    public Object data;

    public Token(TokenType type, Object data) {
      this.type = type;
      this.data = data;
    }

    @Override
    public String toString() {
      return String.format("(%s %s)", type.name(), data.toString());
    }
  }
}