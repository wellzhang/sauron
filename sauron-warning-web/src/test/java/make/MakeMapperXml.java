/**
 * 
 */
package make;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mybatis.generator.api.ShellRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author wangwei18
 * 
 *         2014年9月25日 下午4:18:19
 */
public class MakeMapperXml {

	/**
	 * @param args
	 * @throws Exception
	 * @throws
	 */

	private final static String m2 = ".m2";
	private final static String next_line = "\r";
	private final static String main = "main";
	private final static String location = "location";
	private final static String configfile = "-configfile";
	private final static String classPathEntry = "classPathEntry";
	private final static String xmlPath = "src/test/java/make/generatorConfig.xml";
	private final static String generatorConfiguration = "<generatorConfiguration>";

	public static void main(String[] args) throws Exception {

		System.out.println("start...");
		long l = System.currentTimeMillis();
		File file = new File(xmlPath);
		String filesContent = getFileContent(file);

		try {

			System.out.println("file init ...");
//			makeFile(file, getLocalJarPath(), filesContent);

			if (file.exists()) {
				Method method = ShellRunner.class.getMethod(main, String[].class);
				method.invoke(new ShellRunner(), new Object[] { new String[] { configfile, xmlPath } });
			} else {
				System.out.println("no exists...");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeFile(filesContent, file);
			System.out.println("time: " + (System.currentTimeMillis() - l) + " ms");
		}

	}

	public static String getLocalJarPath() {

		URL location = ShellRunner.class.getProtectionDomain().getCodeSource().getLocation();
		String path = location.getPath();
		return path.substring(1, path.indexOf(m2)).replace("/", "\\");
	}

	@SuppressWarnings("resource")
	public static String getFileContent(File file) throws Exception {

		FileReader fileReader = new FileReader(file);
		BufferedReader br = new BufferedReader(fileReader);

		StringBuilder sb = new StringBuilder();

		String readLine = null;
		while ((readLine = br.readLine()) != null) {
			sb.append(readLine).append(next_line);
		}
		return sb.toString();
	}

	public static void writeFile(String content, File file) throws Exception {
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(content);
		fileWriter.flush();
		fileWriter.close();
	}

	public static void makeFile(File file, String jarPath, String filesContent) throws Exception {

		filesContent = filesContent.substring(0, filesContent.indexOf(generatorConfiguration));

		DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory2.newDocumentBuilder();
		Document doc = builder.parse(file);

		NodeList classPathEntryNode = doc.getElementsByTagName(classPathEntry);
		Element item = (Element) classPathEntryNode.item(0);
		String attribute = item.getAttribute(location);

		if (attribute.startsWith(m2)) {
			jarPath += attribute;
		} else {
			return;
		}

		item.setAttribute(location, jarPath);
		DOMSource source = new DOMSource(doc);
		StringWriter sw = new StringWriter();

		StreamResult result = new StreamResult(sw);
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = tff.newTransformer();
		tf.transform(source, result);

		String string = sw.toString();
		string = filesContent + next_line + string.substring(string.indexOf(generatorConfiguration), string.length());
		writeFile(string, file);
	}
}
