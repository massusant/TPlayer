package com.heavenly.ticket.util;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

public class JavaUtils {
	public static String classpath = JavaUtils.class.getResource("/").getFile();
	
	/**
	 * 正则匹配
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static String[] match(String s, String pattern) {
		Matcher m = Pattern.compile(pattern).matcher(s);

		while (m.find()) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
			}
			return ss;
		}
		return null;
	}

	/**
	 * 正则匹配
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static List<String[]> matchAll(String s, String pattern) {
		Matcher m = Pattern.compile(pattern).matcher(s);
		List<String[]> result = new ArrayList<String[]>();

		while (m.find()) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
			}
			result.add(ss);
		}
		return result;
	}

	/**
	 * 正则匹配，指定开始位置
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static String[] firstMatch(String s, String pattern, int startIndex) {
		Matcher m = Pattern.compile(pattern).matcher(s);

		if (m.find(startIndex)) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
			}
			return ss;
		}
		return null;
	}

	/**
	 * 正则匹配
	 * 
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static boolean isMatch(String s, String pattern) {
		return s.matches(pattern);
	}

	public static boolean isAllMatch(String s, String pattern) {
		Matcher m = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(s);
		while (m.find()) {
			return true;
		}
		return false;
	}

	public static String XmlToString(Node node) {
		try {
			//org.apache.xml.serializer.TreeWalker;
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString().replaceAll("<\\?.*\\?>", "");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取结点的Text值
	 * @param node
	 * @return
	 */
	public static String getTextContent(Node node){
		if(node == null) return null;
		String textContent = node.getTextContent();
		if(textContent == null) return textContent;
		return textContent.trim();
	}
	
	/**
	 * 获取结点的属性值
	 * @param node
	 * @param attrName
	 * @return
	 */
	public  static String getNodeValue(Node node, String attrName){
		if(node == null || node.getAttributes()==null) return null;
		Node attrNode = node.getAttributes().getNamedItem(attrName);
		if(attrNode == null || attrNode.getNodeValue() == null) return null;
		return attrNode.getNodeValue().trim();
	}

	/**
	 * 获取结点的属性值
	 * @param node
	 * @param attrName
	 * @return
	 */
	public  static Node getNodeAttr(Node node, String attrName){
		if(node == null || node.getAttributes()==null) return null;
		Node attrNode = node.getAttributes().getNamedItem(attrName);
		return attrNode;
	}
	/**
	 * 获取结点的Tag (XML)
	 * @param node
	 * @return
	 */
	public static String getTagContent(Node node){
		return JavaUtils.XmlToString(node);
	}
	
	public static String toPostParam(Map<String,String> map) throws UnsupportedEncodingException{
		StringBuilder sb = new StringBuilder();
		for(Iterator<Entry<String, String>> it = map.entrySet().iterator(); it.hasNext();){
			Entry<String, String> entry = it.next();
			String value = entry.getValue() == null? "":entry.getValue().trim();
			sb.append(entry.getKey().trim()+"="+value+"&");
		}
		return sb.substring(0, sb.length()-1);
	}
}