package com.cwdarmm.service;

import com.cwdarmm.model.StrategyExport;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;


@Service
public class StrategyXmlBuilder {

    public Document build(StrategyExport exp) {
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .newDocument();
            var root = doc.createElement("ATMStrategy");
            doc.appendChild(root);
            root.setAttribute("name", exp.getName());
            root.setAttribute("symbol", exp.getMarket().getSymbol());
            root.setAttribute("contracts", String.valueOf(exp.getContracts()));
            root.setAttribute("stopTicks", String.valueOf(exp.getStopTicks()));
            root.setAttribute("targetTicks", String.valueOf(exp.getTargetTicks()));
            return doc;
        } catch (Exception e) {
            throw new IllegalStateException("XML build error", e);
        }
    }
}
