/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.cake3.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author junichi11
 */
public class CakePHP3CommandXmlParser {

    private final List<Cake3CommandItem> commands;

    public CakePHP3CommandXmlParser(List<Cake3CommandItem> commands) {
        this.commands = commands;
    }

    public static void parse(File file, List<Cake3CommandItem> commands) throws SAXException {
        // XXX use XMLReader? : FileUtils.createXmlReader
        CakePHP3CommandXmlParser parser = new CakePHP3CommandXmlParser(commands);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(file);
            Element root = document.getDocumentElement();
            switch (root.getNodeName()) {
                case "shells": // NOI18N
                    parser.parseCommandList(root);
                    break;
                case "shell": // NOI18N
                    parser.parseCommand(root);
                    break;
                default:
                    break;
            }
        } catch (IOException | ParserConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected void parseCommandList(Element root) {
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attr = node.getAttributes();
            commands.add(new Cake3CommandItem(
                    attr.getNamedItem("call_as").getNodeValue(), attr.getNamedItem("provider").getNodeValue(), attr.getNamedItem("name").getNodeValue())); // NOI18N
        }
    }

    protected void parseCommand(Element root) {
        String command = root.getElementsByTagName("command").item(0).getTextContent(); // NOI18N
        String description = root.getElementsByTagName("description").item(0).getTextContent(); // NOI18N
        Cake3CommandItem item = new Cake3CommandItem(command, description, command);
        NodeList subcommands = root.getElementsByTagName("subcommands"); // NOI18N
        for (int i = 0; i < subcommands.getLength(); i++) {
            Node node = subcommands.item(i);
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                NamedNodeMap attr = child.getAttributes();
                item.addSubcommand(new Cake3CommandItem(
                        attr.getNamedItem("name").getTextContent(), // NOI18N
                        attr.getNamedItem("help").getTextContent(), // NOI18N
                        attr.getNamedItem("name").getTextContent())); // NOI18N
            }
        }
        commands.add(item);
    }
}
