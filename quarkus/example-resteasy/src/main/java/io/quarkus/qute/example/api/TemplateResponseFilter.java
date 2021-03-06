package io.quarkus.qute.example.api;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.interception.jaxrs.SuspendableContainerResponseContext;

@Provider
public class TemplateResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		if(responseContext.getEntityClass() == TemplateInstance.class) {
			SuspendableContainerResponseContext ctx = (SuspendableContainerResponseContext) responseContext;
			ctx.suspend();
			TemplateInstance template = (TemplateInstance) responseContext.getEntity();
			try {
				template.render(requestContext.getRequest())
				.handle((resp, err) -> {
				    if(err == null) {
				        // make sure we avoid setting a null media type because that causes
				        // an NPE further down
				        if(resp.getMediaType() != null)
				            ctx.setEntity(resp.getEntity(), null, resp.getMediaType());
				        else
				            ctx.setEntity(resp.getEntity());
				        ctx.setStatus(resp.getStatus());
				        ctx.resume();
				    } else {
				        ctx.resume(err);
				    }
				    return null;
				});
			}catch(Throwable t) {
				ctx.resume(t);
			}
		}
	}
}
