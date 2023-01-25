package com.sample.route;

import org.apache.camel.builder.RouteBuilder;
import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

public class SnmpTrapV2cProducerRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:foo?repeatCount=1")
                .process(exchange -> {
                    PDU pdu = new PDU();
                    pdu.add(new VariableBinding(
                            new OID("1.2.3.4.5.1.1"),
                            new OctetString("127.0.0.1")
                    ));
                    pdu.add(new VariableBinding(
                            new OID("1.2.3.4.5.1.2"),
                            new OctetString("program")
                    ));
                    pdu.add(new VariableBinding(
                            new OID("1.2.3.4.5.1.3"),
                            new OctetString("error")
                    ));
                    pdu.add(new VariableBinding(
                            new OID("1.2.3.4.5.1.4"),
                            new OctetString("Program has an error")
                    ));
                    exchange.getIn().setBody(pdu, PDU.class);
                })
                .log("Body: ${body}")
                .to("snmp:192.168.0.15:162?protocol=udp&type=TRAP&snmpVersion=1")
                ;

    }
}
