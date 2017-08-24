package com.feng.sauron.utils;

import java.lang.management.ManagementFactory;

/**
 * @author wei.wang@fengjr.com
 * @version 创建时间：2016年11月2日 下午4:22:06
 * 
 */
public class SauronUtils {

	// private static String string = "org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(java.lang.String,java.lang.Object,org.apache.ibatis.session.RowBounds)";
	// private static String string1 = "org.apache.ibatis.session.defaults.DefaultSqlSession.selectList()";
	// private static String string2 = "o.a.i.s.d.D.selectList(String,Object,RowBounds)";
	// private static String string3 = "o.a.i.s.d.D.selectList()";
//	private static String string4 = "D.selectList()";
	private static String string5 = "selectList()";

	private static String pid = null;

	static {

		try {
			pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		} catch (Exception e) {
		}

	}

	public static void main(String[] args) {

		String shortMethodName = getShortMethodName(string5);

		System.out.println(shortMethodName);
	}

	public static String getLastMethod(String longMethodName) {

		try {
			StringBuilder sb = new StringBuilder();

			String[] split = longMethodName.split("\\(");

			if (split.length == 2) {

				String longMethod = split[0];

				String[] split2 = longMethod.split("\\.");

				sb.append(split2[split2.length - 1]);

				sb.append("(");

				if (longMethodName.endsWith("()")) {
					sb.append(")");
					return sb.toString();
				}

				String longParams = split[1];

				String[] split3 = longParams.split("\\,");

				for (int i = 0; i < split3.length; i++) {
					String params = split3[i];
					String substring = params.substring(params.lastIndexOf(".") + 1);
					sb.append(substring).append(",");
				}

				if (sb.toString().endsWith(",")) {
					sb.deleteCharAt(sb.length() - 1);
				}
				return sb.toString();
			}
		} catch (Exception e) {
		}
		return longMethodName;
	}

	public static String getShortMethodName(String longMethodName) {

		try {
			StringBuilder sb = new StringBuilder();

			String[] split = longMethodName.split("\\(");

			if (split.length == 2) {

				String longMethod = split[0];

				String[] split2 = longMethod.split("\\.");

				for (int i = 0; i < split2.length - 2; i++) {
					sb.append(split2[i].charAt(0)).append(".");
				}

				sb.append(split2[split2.length - 2]).append(".");
				sb.append(split2[split2.length - 1]);

				sb.append("(");

				if (longMethodName.endsWith("()")) {
					sb.append(")");
					return sb.toString();
				}

				String longParams = split[1];

				String[] split3 = longParams.split("\\,");

				for (int i = 0; i < split3.length; i++) {
					String params = split3[i];
					String substring = params.substring(params.lastIndexOf(".") + 1);
					sb.append(substring).append(",");
				}

				if (sb.toString().endsWith(",")) {
					sb.deleteCharAt(sb.length() - 1);
				}
				return sb.toString();
			}
		} catch (Exception e) {
		}
		return longMethodName;
	}

	public static String getPid() {

		return pid;
	}
}
