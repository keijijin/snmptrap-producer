package com.sample.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.snmp4j.PDUv1;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import java.util.Date;

public class SnmpTrapV1ProducerRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:foo?repeatCount=1")
                .process(exchange -> {
                        PDUv1 pduV1 = new PDUv1();
                        pduV1.setEnterprise(new OID("1.2.3.4.5"));
                        pduV1.setAgentAddress(new IpAddress("127.0.0.1"));
                        pduV1.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
                        pduV1.setSpecificTrap(1);
                        pduV1.setTimestamp(new Date().getTime() / 1000);
                        pduV1.add(new VariableBinding(
                                new OID("1.2.3.4.5.1.1"),
                                new OctetString("program")
                        ));
                        pduV1.add(new VariableBinding(
                                new OID("1.2.3.4.5.1.2"),
                                new OctetString("error")
                        ));
                        pduV1.add(new VariableBinding(
                                new OID("1.2.3.4.5.1.3"),
                                new OctetString("Error in program")
                        ));
                        exchange.getIn().setBody(pduV1, PDUv1.class);
                })
                .log("Body: ${body}")
                .to("snmp:192.168.0.15:162?protocol=udp&type=TRAP&snmpVersion=0")
        ;
    }
}
