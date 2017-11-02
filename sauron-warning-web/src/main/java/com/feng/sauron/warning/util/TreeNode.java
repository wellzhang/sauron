/**
 * @author User
 * @time 下午7:56:02
 */
package com.feng.sauron.warning.util;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * @author wei.wang@fengjr.com 
 * @version 创建时间：2016年1月11日 下午7:56:02 
 * 
 */
/**
 * @author User
 * 
 */
public class TreeNode {

	private String id;
	private String pid;
	private String name;
	private Document document;

	private ArrayList<TreeNode> children = new ArrayList<TreeNode>();

	public TreeNode(String id, String pid, String name, Document document) {

		this.id = id;
		this.pid = pid;
		this.name = name;
		this.document = document;

	}

	public boolean addNode(TreeNode node) {

		if (!this.name.equals(node.name)) {
			return false;
		}

		if ("0".equals(node.pid) || node.pid.equals(this.id)) {
			this.children.add(node);
			return true;
		} else {
			for (TreeNode temNode : this.children) {
				boolean addNode = temNode.addNode(node);
				if (addNode) {
					return true;
				}
			}
			return false;
		}
	}

	public static void main(String[] args) {

		ArrayList<Document> documents = new ArrayList<Document>();

		Document document2 = new Document();
		document2.put("Traceid", "51f56d1e-83c1-455b-a4db-72d3565708c8");
		document2.put("Spanid", "0");
		documents.add(document2);

		Document document4 = new Document();
		document4.put("Traceid", "51f56d1e-83c1-455b-a4db-72d3565708c8");
		document4.put("Spanid", "0.1.1");
		documents.add(document4);

		Document document5 = new Document();
		document5.put("Traceid", "51f56d1e-83c1-455b-a4db-72d3565708c8");
		document5.put("Spanid", "0.1");
		documents.add(document5);

		Document document3 = new Document();
		document3.put("Traceid", "51f56d1e-83c1-455b-a4db-72d3565708c8");
		document3.put("Spanid", "0.2");
		documents.add(document3);

		Document document7 = new Document();
		document7.put("Traceid", "51f56d1e-83c1-455b-a4db-72d3565708c8");
		document7.put("Spanid", "0.2.1");
		documents.add(document7);

		Document document8 = new Document();
		document8.put("Traceid", "51f56d1e-83c1-455b-a4db-72d3565708c8");
		document8.put("Spanid", "0.2.1.1");
		documents.add(document8);

		Document document6 = new Document();
		document6.put("Traceid", "51f56d1e-83c1-455b-a4db-72d3565708c8");
		document6.put("Spanid", "0.1.1.1");
		documents.add(document6);

		TreeNode dataToJson = dataToJson(documents);

		try {
			ObjectMapper om = new ObjectMapper();
			String writeValueAsString = om.writeValueAsString(dataToJson);
			System.out.println(writeValueAsString);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static TreeNode dataToJsonTest(List<Document> documents) {

		try {

			if (documents == null || documents.size() == 0) {
				return null;
			}

			 

 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public static TreeNode dataToJson(List<Document> documents) {

		try {

			if (documents == null || documents.size() == 0) {
				return null;
			}

			TreeNode rootNode = null;

			for (Document document : documents) {
				String spanid = document.get("Spanid").toString();

				if ("0".equals(spanid)) {
					String traceid = document.get("Traceid").toString();
					rootNode = new TreeNode(spanid, spanid, traceid, document);
					documents.remove(document);
					break;
				}
			}

			if (rootNode == null) {
				return null;
			}

			int size = documents.size();

			for (int i = 0; i < size; i++) {

				for (Document document : documents) {
					String spanid = document.get("Spanid").toString();
					String traceid = document.get("Traceid").toString();
					String pid = spanid.substring(0, spanid.lastIndexOf("."));
					boolean addNode = rootNode.addNode(new TreeNode(spanid, pid, traceid, document));
					if (addNode) {
						documents.remove(document);
						break;
					}
				}
			}

			return rootNode;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public ArrayList<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<TreeNode> children) {
		this.children = children;
	}

}
