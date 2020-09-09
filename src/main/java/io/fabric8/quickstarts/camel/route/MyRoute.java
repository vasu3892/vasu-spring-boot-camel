package io.fabric8.quickstarts.camel.route;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class MyRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		restConfiguration().component("servlet").bindingMode(RestBindingMode.off);

		// accept rest get call
		rest().get("/heavy").description("Heavy Memory-Consumption Route").to("direct:heavyRoute");

		onException(Exception.class).handled(true).setBody()
				.simple("FINAL MEMORY FROM ON-EXCEPTION ::: ${in.headers.finalMemory}");

		from("direct:heavyRoute").log("entered heavy route").process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				List<byte[]> list = new ArrayList<byte[]>();
				long memory = 0;
				Runtime rt = Runtime.getRuntime();

				while (true) {
					byte[] b = new byte[1048576];
					list.add(b);
					memory = rt.freeMemory() / (1024 * 1024);
					System.out.println("free memory: " + memory + "MB");
					exchange.getIn().setHeader("finalMemory", memory + "MB");

					if (memory < 10) {
						break;
					}
				}

				memory = rt.freeMemory() / (1024 * 1024);
				exchange.getIn().setBody("final free memory: " + memory + "MB");
			}
		});

	}

}
