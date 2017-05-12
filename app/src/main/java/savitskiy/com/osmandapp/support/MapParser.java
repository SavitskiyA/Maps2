package savitskiy.com.osmandapp.support;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import savitskiy.com.osmandapp.models.Continent;
import savitskiy.com.osmandapp.models.Country;
import savitskiy.com.osmandapp.models.Map;
import savitskiy.com.osmandapp.models.Region;
import savitskiy.com.osmandapp.models.State;
import savitskiy.com.osmandapp.models.SubRegion;

public class MapParser {
    private SAXParserFactory saxParserFactory;
    private SAXParser saxParser;
    private List<Map> maps = new ArrayList<>();
    private MyHandler handler;

    public MapParser() {
        MyHandler myHandler = new MyHandler();
        this.handler=myHandler;
    }

    public List<Map> getContinents(InputStream is)throws IOException,SAXException,ParserConfigurationException

    {
        return defineMapFileName(parse(is, handler));
    }

    private List<Map> parse(InputStream is, MyHandler handler)
            throws ParserConfigurationException, SAXException, IOException {
        saxParserFactory = SAXParserFactory.newInstance();
        saxParser = saxParserFactory.newSAXParser();
        saxParser.parse(is, handler);
        return maps;
    }

    public class MyHandler extends DefaultHandler {
        private Stack<Map> stack = new Stack<>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (qName.equalsIgnoreCase("region"))
                stack.push(getObjectFromXML(stack, attributes));
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (qName.equalsIgnoreCase("region")) {
                stack.pop();
            }
        }

        private Map getObjectFromXML(Stack<Map> stack, Attributes attributes) throws SAXException {
            String name = attributes.getValue("name");
            String map = attributes.getValue("map");
            String type = attributes.getValue("type");
            String download_suffix = attributes.getValue("download_suffix");
            String download_prefix = attributes.getValue("download_prefix");
            String inner_download_suffix = attributes.getValue("inner_download_suffix");
            String inner_download_prefix = attributes.getValue("inner_download_prefix");
            Map mapRegion = null;
            Map parent = null;
            int level = stack.size();
            switch (level) {
                case 0:
                    parent = new Continent(null, null, null, null, null, null, null, null);
                    mapRegion = new Continent(name, type, map, download_suffix, download_prefix, inner_download_suffix,
                            inner_download_prefix, parent);

                    maps.add(mapRegion);
                    return mapRegion;
                case 1:
                    parent = (Map) stack.peek();
                    mapRegion = new Country(name, type, map, download_suffix, download_prefix, inner_download_suffix,
                            inner_download_prefix, parent);
                    parent.getChildRegions().add(mapRegion);
                    return mapRegion;
                case 2:
                    parent = (Map) stack.peek();
                    mapRegion = new Region(name, type, map, download_suffix, download_prefix, inner_download_suffix,
                            inner_download_prefix, (Map) stack.peek());
                    parent.getChildRegions().add(mapRegion);
                    return mapRegion;
                case 3:
                    parent = (Map) stack.peek();
                    mapRegion = new SubRegion(name, type, map, download_suffix, download_prefix, inner_download_suffix,
                            inner_download_prefix, (Map) stack.peek());
                    parent.getChildRegions().add(mapRegion);
                    return mapRegion;
                case 4:
                    parent = (Map) stack.peek();
                    mapRegion = new State(name, type, map, download_suffix, download_prefix, inner_download_suffix,
                            inner_download_prefix, (Map) stack.peek());
                    parent.getChildRegions().add(mapRegion);
                    return mapRegion;
                default:
                    throw new SAXException("Check xml structure. There is unexpected level " + level);
            }
        }

    }

    private List<Map> defineMapFileName(List<Map> maps) {
        setFileName(maps);
        return maps;
    }

    private void setFileName(List<Map> maps) {
        for (int i = 0; i < maps.size(); i++) {
            if (maps.get(i).getChildRegions().size() != 0) {
                setFileName(maps.get(i).getChildRegions());
            }
            if (maps.get(i).getType() == null) {
                if (maps.get(i).getMap() == null || maps.get(i).getMap().equalsIgnoreCase("yes")) {
                    maps.get(i).setFileName(constructMapFileName(maps.get(i)));
                }
            }

        }

    }

    private String constructMapFileName(Map map) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPreffix(map)).append(map.getName()).append(getSuffix(map))
                .append("_2.obf.zip");
        return up(stringBuilder.toString());
    }

    private String getPreffix(Map map) {
        if (map.getDownload_prefix() == null) {
            return getParentPreffix(map.getParent());
        } else {
            return map.getDownload_prefix();
        }
    }

    private String getParentPreffix(Map map) {
        if (map.getDownload_prefix() != null && !(map.getDownload_prefix().equalsIgnoreCase(""))) {
            return map.getDownload_prefix() + "_";
        } else if (map.getInner_download_prefix() != null) {
            return map.getInner_download_prefix() + "_";
        } else {
            if (map.getParent() != null) {
                return getParentPreffix(map.getParent());
            } else {
                return "";
            }
        }

    }

    private String getSuffix(Map map) {
        if (map.getDownload_suffix() == null) {
            return getParentSuffix(map.getParent());
        } else {
            return map.getDownload_suffix();
        }
    }

    private String getParentSuffix(Map map) {
        if (map.getDownload_suffix() != null && !(map.getDownload_suffix().equalsIgnoreCase(""))) {
            return "_" + map.getDownload_suffix();
        } else if (map.getInner_download_suffix() != null) {
            return "_" + map.getInner_download_suffix();
        } else {
            if (map.getParent() != null) {
                return getParentSuffix(map.getParent());
            } else {
                return "";
            }
        }
    }

    public static String up(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        String str1 = s.substring(0, 1).toUpperCase();
        String str2 = s.substring(1);
        stringBuilder.append(str1).append(str2);
        return stringBuilder.toString();
    }
}
