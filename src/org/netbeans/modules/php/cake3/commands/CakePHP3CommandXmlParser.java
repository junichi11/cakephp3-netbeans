/*
 * Copyright 2019 junichi11.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
