package com.lagou.config;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {
    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");
        List<Element> selectList = rootElement.selectNodes("//select");
        addMappedStatementMap(selectList, namespace);
        List<Element> insertList = rootElement.selectNodes("//insert");
        addMappedStatementMap(insertList, namespace);
        List<Element> updateList = rootElement.selectNodes("//update");
        addMappedStatementMap(updateList, namespace);
        List<Element> deleteList = rootElement.selectNodes("//delete");
        addMappedStatementMap(deleteList, namespace);
    }

    public void addMappedStatementMap(List<Element> elementList, String namespace) {
        for (Element element : elementList) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String parameterType = element.attributeValue("parameterType");
            String sqlText = "";
            if (element.content().size() == 1) {
                sqlText = element.getTextTrim();
            } else {
                for (Object o : element.content()) {
                    if (o instanceof DefaultElement) {
                        DefaultElement o1 = (DefaultElement) o;
                        sqlText += "<" + o1.getName() + ">" + o1.getTextTrim() + "</" + o1.getName() + ">";
                    } else {
                        sqlText += ((DefaultText) o).getText().trim();
                    }
                }
            }
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sqlText);
            String key = namespace + "." + id;
            configuration.getMappedStatementMap().put(key, mappedStatement);
        }
    }
}
