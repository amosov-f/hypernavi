package amosov;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.util.MoreIOUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by amosov-f on 28.12.16
 */
public enum PisteMapsParser {
  ;

  private static final DocumentBuilder DOCUMENT_BUILDER;
  static {
    try {
      DOCUMENT_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
    final Document document = getOrDownload("https://skimap.org/SkiAreas/index.xml");
    final Node root = document.getDocumentElement();
    NodeList skiAreas = root.getChildNodes();
    final List<Integer> ids = new ArrayList<>();
    for (int i = 0; i < skiAreas.getLength(); i++) {
      final Node skiArea = skiAreas.item(i);
      if (skiArea.getNodeType() != Node.TEXT_NODE) {
        ids.add(Integer.valueOf(skiArea.getAttributes().getNamedItem("id").getNodeValue()));
      }
    }
    System.out.println(ids.size());
    for (int i : ids) {
      final Document d1 = getOrDownload("https://skimap.org/SkiAreas/view/" + i + ".xml");
      final Node georeferencing = d1.getElementsByTagName("georeferencing").item(0);
      if (georeferencing != null) {
        final Node latNode = georeferencing.getAttributes().getNamedItem("lat");
        if (latNode != null) {
          final double lat = Double.parseDouble(latNode.getNodeValue());
          final double lon = Double.parseDouble(georeferencing.getAttributes().getNamedItem("lng").getNodeValue());
//          System.out.println(i + " -> " + lat + " " + lon);
          final NodeList skiMaps = d1.getElementsByTagName("skiMaps").item(0).getChildNodes();
          final List<Pair<String, Integer>> xxx = new ArrayList<>();
          for (int j = 0; j < skiMaps.getLength(); j++) {
            final Node pizza = skiMaps.item(j);
            if (pizza.getNodeType() != Node.TEXT_NODE) {
              final int skiMapId = Integer.parseInt(pizza.getAttributes().getNamedItem("id").getNodeValue());
              final Document in2 = getOrDownload("https://skimap.org/SkiMaps/view/" + skiMapId + ".xml");
              if (in2 != null) {
                final NamedNodeMap m = in2.getElementsByTagName("unprocessed").item(0).getAttributes();
                final String renderUrl = Optional.ofNullable(in2.getElementsByTagName("render").item(0))
                    .map(r -> r.getAttributes().getNamedItem("url").getNodeValue())
                    .orElse(null);
                final String imgUrl = m.getNamedItem("url").getNodeValue();
                if (renderUrl != null) {
                  System.out.println(renderUrl + " " + imgUrl);
                }
                final String sizeString = m.getNamedItem("size").getNodeValue();
                if (!sizeString.isEmpty()) {
                  final int size = Integer.parseInt(sizeString);
//                  System.out.println(imgUrl + " " + size);
                  xxx.add(Pair.of(imgUrl, size));
                }
              }
            }
          }
          if (!xxx.isEmpty()) {
            System.out.println(lat + " " + lon);
            xxx.sort(Comparator.comparing(Pair::getValue));
            xxx.forEach(x -> System.out.println(x.getLeft() + " " + x.getRight()));
            System.out.println("\n");
          }
        }
      }
    }
  }

  @Nullable
  private static Document getOrDownload(@NotNull final String url) throws IOException {
    final File f = new File("skimap.org", new URIBuilder(url).build().getPath());
    if (f.exists()) {
//      System.out.println("getting from cache: https://" + f);
      try {
        return DOCUMENT_BUILDER.parse(f);
      } catch (SAXException ignored) {
        return null;
      }
    }
    final byte[] data = MoreIOUtils.read(url);
    FileUtils.writeByteArrayToFile(f, data);
    try {
      return DOCUMENT_BUILDER.parse(new ByteArrayInputStream(data));
    } catch (SAXException ignored) {
      return null;
    }
  }
}
