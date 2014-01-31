/*
 * #%L
 * GraphWalker Command Line Interface
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.graphwalker.cli;


import org.graphwalker.core.PathGenerator;
import org.graphwalker.core.StopCondition;
import org.graphwalker.core.condition.AlternativeCondition;
import org.graphwalker.core.condition.BaseStopCondition;
import org.graphwalker.core.condition.CombinationalCondition;
import org.graphwalker.core.generator.BasePathGenerator;
import org.graphwalker.core.generator.CombinedPathGenerator;

import java.util.List;

import static org.graphwalker.cli.Lexer.lex;

public class GeneratorParser {

  private static int readStopConditions(BasePathGenerator generator, List<Lexer.Token> tokens, int i) {

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

    // Check if whether we have single, alternative or combinatorial stop condition
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
    BaseStopCondition currentConditon = null;
    for (int j = i - numberOfTokens; j < tokens.size(); j++) {
      if (tokens.get(j).data instanceof StopCondition) {
        currentConditon = (BaseStopCondition) tokens.get(j).data;
        if (stopCondition instanceof AlternativeCondition) {
          ((AlternativeCondition) stopCondition).add(currentConditon);
        } else if (stopCondition instanceof CombinationalCondition) {
          ((CombinationalCondition) stopCondition).add(currentConditon);
        } else {
          currentConditon = stopCondition = (BaseStopCondition) tokens.get(j).data;
        }
        continue;
      }
      if (tokens.get(j).type == Lexer.TokenType.NUMBER) {
        currentConditon.setValue((String) tokens.get(j).data);
        continue;
      }
      if (tokens.get(j).type == Lexer.TokenType.STRING) {
        currentConditon.setValue((String) tokens.get(j).data);
        continue;
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

    return i;
  }

  public static PathGenerator parse(String str) {

    BasePathGenerator generator = null;
    List<Lexer.Token> tokens = lex(str);

    // Check if whether we have more than one generator
    int numOfGenerators = 0;
    for (Lexer.Token t : tokens) {
      if (t.data instanceof PathGenerator) {
        numOfGenerators++;
        if (numOfGenerators >= 2) {
          generator = new CombinedPathGenerator();
          break;
        }
      }
    }

    for (int i = 0; i < tokens.size(); i++) {
      // First we expect a generator
      if (tokens.get(i).data instanceof PathGenerator) {
        if (generator instanceof CombinedPathGenerator) {
          ((CombinedPathGenerator) generator).addPathGenerator((PathGenerator) tokens.get(i).data);
        } else {
          generator = (BasePathGenerator) tokens.get(i).data;
        }
        i = readStopConditions((BasePathGenerator) tokens.get(i).data, tokens, ++i);

      }
    }


    return generator;
  }
}
