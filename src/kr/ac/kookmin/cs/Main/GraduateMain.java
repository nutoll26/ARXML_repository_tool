package kr.ac.kookmin.cs.Main;

import kr.ac.kookmin.cs.Xquery.NetworkingExist;

import org.xmldb.api.base.XMLDBException;

import kr.ac.kookmin.cs.GUI.WindowGUI;

public class GraduateMain {

	public static void main(String args[]) throws XMLDBException {
		WindowGUI awtControlDemo = new WindowGUI();
		awtControlDemo.showList();

		NetworkingExist exist = new NetworkingExist();
		exist.SendQueryToExistDB("SysPedProtnActvn");
	}
}