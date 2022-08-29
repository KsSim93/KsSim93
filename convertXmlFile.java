public void convertXmlFile(String xmlFile) {
        try {
            File file = new File(xmlFile);

            DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
            Document doc = docBuild.parse(file);
            doc.getDocumentElement().normalize();

            System.out.println("Root element : " + doc.getDocumentElement().getNodeName());

            NodeList nodeList = doc.getElementsByTagName("UserStoreManager");

            System.out.println("nodeList.getLength() ::: " + nodeList.getLength());

            nodeList = nodeList.item(0).getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element)node;

                    Map<String, Object> map = new HashMap<>();

                    if(!element.getAttribute("name").equals("")) {
                        map.put(element.getAttribute("name"), element.getTextContent());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
