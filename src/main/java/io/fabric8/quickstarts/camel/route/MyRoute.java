package io.fabric8.quickstarts.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class MyRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		restConfiguration().component("servlet").bindingMode(RestBindingMode.off);

		// accept rest get call
		rest().get("/heavy").description("Heavy Memory-Consumption Route").to("direct:heavyRoute");

		from("direct:heavyRoute")
			.log("from heavy route")
			.setBody().simple("<root>from heavy route</root>")
			.process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					exchange.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_XML);
				}
			})
		;

	}

}
