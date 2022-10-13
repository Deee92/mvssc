package se.kth.mvssc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * This class generates a specialized pom from the debloated pom
 */
public class PomManipulator {
    public String createSpecializedPomFromDebloatedPom(String debloatedPomPath,
                                                       String originalDependencyGroupId,
                                                       String originalDependencyArtifactId,
                                                       String specializedJarName) throws Exception {
        String debloatedAndSpecializedPom = debloatedPomPath.replace(".xml", "-spl.xml");

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File(debloatedPomPath));
        doc.getDocumentElement().normalize();

        // Replace original dependency with specialized dependency
        NodeList dependencies = doc.getDocumentElement().getElementsByTagName("dependency");
        for (int i = 0; i < dependencies.getLength(); i++) {
            Element dependencyNode = (Element) dependencies.item(i);
            Node groupIdNode = dependencyNode.getElementsByTagName("groupId").item(0);
            Node artifactIdNode = dependencyNode.getElementsByTagName("artifactId").item(0);
            if (groupIdNode.getTextContent().equals(originalDependencyGroupId) &
                    artifactIdNode.getTextContent().equals(originalDependencyArtifactId)) {
                System.out.println("Found original dependency in debloated pom");
                System.out.println("Replacing with specialized dependency");
                Node versionNode = dependencyNode.getElementsByTagName("version").item(0);
                groupIdNode.setTextContent(MagicStrings.deptrimSpecializedGroupId);
                artifactIdNode.setTextContent(specializedJarName);
                versionNode.setTextContent(MagicStrings.deptrimSpecializedVersion);
                saveUpdatedDomInANewPom(doc, debloatedAndSpecializedPom);
                break;
            }
        }
        return debloatedAndSpecializedPom;
    }

    private void saveUpdatedDomInANewPom(Document document, String debloatedSpecializedPom) throws Exception {
        DOMSource dom = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();

        StreamResult result = new StreamResult(new File(debloatedSpecializedPom));
        transformer.transform(dom, result);
    }
}
