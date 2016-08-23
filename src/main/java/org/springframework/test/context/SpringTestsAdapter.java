package org.springframework.test.context;

import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.support.DefaultBootstrapContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
 * @author <a href="mailto:Young.Gu@ryeast.com">Young Gu</a>
 */

public class SpringTestsAdapter {

	public static TestContextManager createTestContextManager(Class<?> testClass) {
		TestContextManager testContextManager = new TestContextManager(
				testClass);
		BootstrapContext bootstrapContext = new DefaultBootstrapContext(
				testClass, new NoCacheAwareContextLoaderDelegate());
		TestContextBootstrapper testContextBootstrapper = BootstrapUtils
				.resolveTestContextBootstrapper(bootstrapContext);
		ReflectionTestUtils.setField(testContextManager, "testContext",
				testContextBootstrapper.buildTestContext());
		((List) ReflectionTestUtils.getField(testContextManager,
				"testExecutionListeners")).clear();
		testContextManager
				.registerTestExecutionListeners(testContextBootstrapper
						.getTestExecutionListeners());

		return testContextManager;
	}

}

/**
 * Copy from DefaultCacheAwareContextLoaderDelegate of Spring, we don't need
 * cache.
 */
class NoCacheAwareContextLoaderDelegate
		implements
			CacheAwareContextLoaderDelegate {
	/**
	 * Load the {@code ApplicationContext} for the supplied merged context
	 * configuration.
	 * <p>
	 * Supports both the {@link SmartContextLoader} and {@link ContextLoader}
	 * SPIs.
	 * 
	 * @throws Exception
	 *             if an error occurs while loading the application context
	 */
	private ApplicationContext loadContextInternal(
			MergedContextConfiguration mergedContextConfiguration)
			throws Exception {

		ContextLoader contextLoader = mergedContextConfiguration
				.getContextLoader();
		Assert.notNull(
				contextLoader,
				"Cannot load an ApplicationContext with a NULL 'contextLoader'. "
						+ "Consider annotating your test class with @ContextConfiguration or @ContextHierarchy.");

		ApplicationContext applicationContext;

		if (contextLoader instanceof SmartContextLoader) {
			SmartContextLoader smartContextLoader = (SmartContextLoader) contextLoader;
			applicationContext = smartContextLoader
					.loadContext(mergedContextConfiguration);
		} else {
			String[] locations = mergedContextConfiguration.getLocations();
			Assert.notNull(
					locations,
					"Cannot load an ApplicationContext with a NULL 'locations' array. "
							+ "Consider annotating your test class with @ContextConfiguration or @ContextHierarchy.");
			applicationContext = contextLoader.loadContext(locations);
		}

		return applicationContext;
	}

	@Override
	public ApplicationContext loadContext(
			MergedContextConfiguration mergedContextConfiguration) {
		try {
			return loadContextInternal(mergedContextConfiguration);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void closeContext(
			MergedContextConfiguration mergedContextConfiguration,
			DirtiesContext.HierarchyMode hierarchyMode) {

	}

}
