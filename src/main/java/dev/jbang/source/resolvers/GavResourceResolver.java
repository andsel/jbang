package dev.jbang.source.resolvers;

import java.util.function.Function;

import javax.annotation.Nonnull;

import dev.jbang.dependencies.DependencyUtil;
import dev.jbang.dependencies.ModularClassPath;
import dev.jbang.source.ResourceRef;
import dev.jbang.source.ResourceResolver;

/**
 * A <code>ResourceResolver</code> that, when given a resource string which
 * looks like a Maven GAV, will try to resolve that dependency and return a
 * reference to the downloaded artifact JAR.
 */
public class GavResourceResolver implements ResourceResolver {
	private final Function<String, ModularClassPath> depResolver;

	public GavResourceResolver(Function<String, ModularClassPath> depResolver) {
		this.depResolver = depResolver;
	}

	@Nonnull
	@Override
	public String description() {
		return "Maven GAV";
	}

	@Override
	public ResourceRef resolve(String resource) {
		ResourceRef result = null;

		if (DependencyUtil.looksLikeAGav(resource)) {
			result = ResourceRef.forLazyFileResource(resource, ref -> {
				ModularClassPath mcp = depResolver.apply(resource);
				// We possibly get a whole bunch of artifacts, but we're only interested in
				// the one we asked for which we assume is always the first one in the list
				// (hopefully we're right).
				return mcp.getArtifacts().get(0).getFile();
			});
		}

		return result;
	}
}
