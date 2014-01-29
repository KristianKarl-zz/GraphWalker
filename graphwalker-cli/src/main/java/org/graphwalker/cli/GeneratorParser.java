package org.graphwalker.cli;


import org.graphwalker.core.PathGenerator;
import org.graphwalker.core.StopCondition;
import org.graphwalker.core.condition.AlternativeCondition;
import org.graphwalker.core.condition.BaseStopCondition;
import org.graphwalker.core.condition.CombinationalCondition;
import org.graphwalker.core.generator.BasePathGenerator;

import java.util.List;

import static org.graphwalker.cli.Lexer.lex;

public class GeneratorParser {

  private static void readStopConditions(BasePathGenerator generator, List<Lexer.Token> tokens, int i) {

    // Check parentheses
    if (tokens.get(i).type != Lexer.TokenType.LPARENTHESES) {
      throw new IllegalArgumentException("Expected a left parentheses, not: " + tokens.get(i).data);
    }
    int numberOfTokens = 0;
    int parenthesesBalance = 0;
    for (; i < tokens.size(); i++) {
      if (tokens.get(i).type == Lexer.TokenType.LPARENTHESES) {
        parenthesesBalance++;
      } else if (tokens.get(i).type == Lexer.TokenType.RPARENTHESES) {
        parenthesesBalance--;
      }

      if (parenthesesBalance == 0) {
        break;
      }
      numberOfTokens++;
    }

    if (parenthesesBalance != 0) {
      throw new IllegalArgumentException("Problem with parentheses balance.");
    }

    BaseStopCondition stopCondition = null;

    List<Lexer.Token> generatorTokens = tokens.subList(i - numberOfTokens, i);
    for (int x = 0; x < generatorTokens.size(); x++) {
      if (tokens.get(x).type == Lexer.TokenType.OR) {
        stopCondition = new AlternativeCondition();
        break;
      } else if (tokens.get(x).type == Lexer.TokenType.AND) {
        stopCondition = new CombinationalCondition();
        break;
      }
    }

    parenthesesBalance = 0;
    for (int j = i - numberOfTokens; j < tokens.size(); j++) {
      if (tokens.get(j).data instanceof StopCondition) {
        if (stopCondition instanceof AlternativeCondition ) {
          ((AlternativeCondition)stopCondition).add((BaseStopCondition) tokens.get(j).data);
        } else if (stopCondition instanceof CombinationalCondition) {
          ((CombinationalCondition)stopCondition).add((BaseStopCondition) tokens.get(j).data);
        } else {
          stopCondition = (BaseStopCondition) tokens.get(j).data;
        }
      }
      if (tokens.get(j).type == Lexer.TokenType.NUMBER) {
        stopCondition.setValue((String) tokens.get(j).data);
      }
      if (tokens.get(j).type == Lexer.TokenType.STRING) {
        stopCondition.setValue((String) tokens.get(j).data);
      }

      if (tokens.get(j).type == Lexer.TokenType.LPARENTHESES) {
        parenthesesBalance++;
      } else if (tokens.get(j).type == Lexer.TokenType.RPARENTHESES) {
        parenthesesBalance--;
      }

      if (parenthesesBalance == 0) {
        break;
      }
    }
    generator.setStopCondition(stopCondition);
  }

  public static PathGenerator parse(String str) {

    BasePathGenerator generator = null;
    List<Lexer.Token> tokens = lex(str);

    for (int i = 0; i < tokens.size(); i++) {
      System.out.println(tokens.get(i).toString());

      // First we expect a generator
      if (tokens.get(i).data instanceof PathGenerator) {
        generator = (BasePathGenerator) tokens.get(i).data;
        readStopConditions(generator, tokens, ++i);
      }
    }


    return generator;
  }
}
