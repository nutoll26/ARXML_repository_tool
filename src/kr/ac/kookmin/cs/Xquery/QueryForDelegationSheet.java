package kr.ac.kookmin.cs.Xquery;

public class QueryForDelegationSheet {
	static String queryDelegationSheet(String ecuName){
		String query = 
				"xquery version \"3.1\";"
				+ "declare default element namespace \"http://autosar.org/schema/r4.0\";"
				+ "declare namespace xsi=\"http://www.w3.org/2001/XMLSchema-instance\";"
				+ "declare namespace schemaLocation=\"http://autosar.org/schema/r4.0 autosar_4-2-2.xsd\";"
				+ "declare namespace output = \"http://www.w3.org/2010/xslt-xquery-serialization\";"
				+ "declare namespace map = \"http://www.w3.org/2005/xpath-functions/map\";"
				
				+ "for $CSWC in doc(\"nutoll/AUTOSAR_MOD_AISpecificationExamples.arxml\")//AR-PACKAGE/ELEMENTS/COMPOSITION-SW-COMPONENT-TYPE\n"
				+ "where $CSWC/SHORT-NAME = \"" + ecuName + "\"\n"
				
				+ "let $CSWCShortName := $CSWC/SHORT-NAME/data()\n"
				
				+ "let $deligationPorts := $CSWC/PORTS\n"
				+ "let $deligationPortShortName := $deligationPorts/*/SHORT-NAME/data()\n"
				
				+ "let $deligationPortInterfaceShortName := for $_deligationPortInterfaceShortName in $deligationPorts\n"
				+ "where $_deligationPortInterfaceShortName/*/SHORT-NAME/data() = $deligationPortShortName\n"
				+ "return $_deligationPortInterfaceShortName/*/*[name()=\"PROVIDED-INTERFACE-TREF\" or name()=\"REQUIRED-INTERFACE-TREF\"]/data()\n"
				
				+ "let $deligationPortCategory_ := for $_deligationPortCategory in $deligationPorts\n"
				+ "where $_deligationPortCategory/*/SHORT-NAME/data() = $deligationPortShortName\n"
				+ "return $_deligationPortCategory/*/name()\n"
				
				+ "let $deligationPortInterfaceGategory := for $_deligationPortInterfaceGategory in $deligationPorts\n"
				+ "where $_deligationPortInterfaceGategory/*/SHORT-NAME/data() = $deligationPortShortName\n"
				+ "return $_deligationPortInterfaceGategory/*/*/@DEST\n"
				
				+ "let $connetors := $CSWC/CONNECTORS\n"
				
				+ "let $deligationConnectors := $connetors/DELEGATION-SW-CONNECTOR\n"
				+ "let $deligationPortCategory := $connetors/DELEGATION-SW-CONNECTOR/*/@DEST\n"
				+ "let $deligationSWCAndPort := $deligationConnectors/INNER-PORT-IREF/*/*[name()=\"TARGET-P-PORT-REF\" or name() = \"TARGET-R-PORT-REF\"]\n"
				+ "let $tokenDeligationSWCAndPort := for $i in (1 to count($deligationSWCAndPort))\n"
				+ "    return fn:tokenize(string($deligationSWCAndPort[$i]), '/')\n"
				+ "let $deligationSWC := for $i in (1 to count($tokenDeligationSWCAndPort))\n"
				+ "    return if($i mod 2 = 1)\n"
				+ "        then $tokenDeligationSWCAndPort[$i]\n"
				+ "        else ()\n"
				+ "let $deligationPort := for $i in (1 to count($tokenDeligationSWCAndPort))\n"
				+ "    return if($i mod 2 = 0)\n"
				+ "        then $tokenDeligationSWCAndPort[$i]\n"
				+ "        else ()\n"
				
				+ "let $map := map{}\n"
				+ "let $puts := (\n"
				+ "    for $j in (1 to count($deligationPortShortName))\n"
				+ "        let $arr := [\n"
				+ "            for $i in (1 to count($deligationPort))\n"
				+ "            where $deligationPort[$i] = $deligationPortShortName[$j]\n"
				+ "            return concat($deligationSWC[$i],\":\",$deligationPortCategory[$i])\n"
				+ "        ]\n"
				+ "    return map:put($map, string($deligationPortShortName[$j]), $arr)\n"
				+ ")\n"
				
				+ "let $SR_CS_PortInterface := doc(\"nutoll/AUTOSAR_MOD_AISpecificationExamples.arxml\")//AR-PACKAGE/ELEMENTS/*[name()=\"SENDER-RECEIVER-INTERFACE\" or name()=\"CLIENT-SERVER-INTERFACE\"]\n"
				
				+ "for $i in (1 to count($deligationPortShortName))\n"
				+ "    for $j in (1 to count($puts))\n"
				+ "    where $deligationPortShortName[$i] = map:keys($puts[$j])\n"
				+ "    let $temp := map:get($puts[$j], map:keys($puts[$j]))\n"
				+ "    let $item :=\n"
				+ "    <tag>{\n"
				+ "        for $k in (1 to count(array:get($temp[1],1)))\n"
				+ "        let $null := \"\"\n"
				+ "        return concat($null, array:get($temp[1],1)[$k])}\n"
				+ "    </tag>/data()\n"
				+ "    let $dataElementName := for $_dataElementName in $SR_CS_PortInterface\n"
				+ "                        where $_dataElementName/SHORT-NAME/data() = $deligationPortInterfaceShortName[$i]\n"
				+ "                        return $_dataElementName/DATA-ELEMENTS/VARIABLE-DATA-PROTOTYPE/SHORT-NAME/data()\n"
				+ "    let $distinctDataElementName := distinct-values($dataElementName)\n"
				+ "    let $dataElementNameList :=\n"
				+ "    <tag>{\n"
				+ "        for $k in (1 to count($distinctDataElementName))\n"
				+ "        let $null := \"\"\n"
				+ "        return concat($null, $distinctDataElementName[$k])}\n"
				+ "    </tag>/data()"
				+ "    let $dataTypeName := for $_dataTypeName in $SR_CS_PortInterface\n"
				+ "                        where $_dataTypeName/SHORT-NAME/data() = $deligationPortInterfaceShortName[$i]\n"
				+ "                        return $_dataTypeName/DATA-ELEMENTS/VARIABLE-DATA-PROTOTYPE/TYPE-TREF/data()\n"
				+ "    let $distinctDataTypeName := distinct-values($dataTypeName)\n"
				
				+ "    let $dataTypeCategory := for $_dataTypeCategory in $SR_CS_PortInterface\n"
				+ "                        where $_dataTypeCategory/SHORT-NAME/data() = $deligationPortInterfaceShortName[$i]\n"
				+ "                        return $_dataTypeCategory/DATA-ELEMENTS/VARIABLE-DATA-PROTOTYPE/TYPE-TREF/@DEST\n"
				+ "    let $distinctDataTypeCategory := distinct-values($dataTypeCategory)\n"
				
				+ "    return if(count($connetors) != 0)\n"
				+ "            then concat(\n"
				+ "                $deligationPortInterfaceShortName[$i], \", \",\n"
				+ "                $deligationPortInterfaceGategory[$i], \", \",\n"
				+ "                $dataElementNameList, \", \",\n"
				+ "                $distinctDataTypeName, \", \",\n"
				+ "                $distinctDataTypeCategory, \", \",\n"
				+ "                $deligationPortShortName[$i], \", \",\n"
				+ "                $item\n"
				+ "                )\n"
				+ "            else concat(\n"
				+ "                $deligationPortInterfaceShortName[$i], \", \",\n"
				+ "                $deligationPortInterfaceGategory[$i], \", \",\n"
				+ "                $dataElementNameList, \", \",\n"
				+ "                $distinctDataTypeName, \", \" ,\n"
				+ "                $distinctDataTypeCategory, \", \",\n"
				+ "                $deligationPortShortName[$i], \", \",\n"
				+ "                $deligationPortCategory_[$i]\n"
				+ "                )";
		
		return query;
	}
}
