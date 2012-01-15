/*
 * #%L
 * GraphWalker GUI
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
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
package org.graphwalker.gui.components;

import org.graphwalker.gui.GraphWalkerController;
import org.graphwalker.gui.actions.ExitAction;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>ConsoleView class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class ConsoleView extends AbstractView {

    /**
     * <p>Constructor for ConsoleView.</p>
     *
     * @param controller a {@link org.graphwalker.gui.GraphWalkerController} object.
     */
    public ConsoleView(GraphWalkerController controller) {
        super(new JEditorPane(), ToolBarPlacement.WEST);
        addActionGroup(createActionGroup());
        captureOutput();
    }

    private JEditorPane getEditor() {
        return (JEditorPane)getComponent();
    }

    private List<Action> createActionGroup() {
        List<Action> actionGroup = new ArrayList<Action>();
        actionGroup.add(new ExitAction(null));
        return actionGroup;
    }

    private void captureOutput() {
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(final int b) throws IOException {
                updateEditor(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateEditor(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };
        System.setOut(new PrintStream(outputStream, true));
        System.setErr(new PrintStream(outputStream, true));
    }

    private void updateEditor(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Document document = getEditor().getDocument();
                try {
                    document.insertString(document.getLength(), text, null);
                } catch (BadLocationException e) {
                    //TODO: Handle exception
                }
                getEditor().setCaretPosition(document.getLength() - 1);
            }
        });
    }
}
