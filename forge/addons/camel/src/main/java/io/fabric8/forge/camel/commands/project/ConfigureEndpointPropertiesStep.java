/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.forge.camel.commands.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;

import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.camel.catalog.JSonSchemaHelper;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.roaster.model.util.Strings;

public class ConfigureEndpointPropertiesStep extends AbstractCamelProjectCommand implements UIWizardStep {

    @Inject
    private InputComponentFactory componentFactory;

    @Inject
    private DependencyInstaller dependencyInstaller;

    @Inject
    private DependencyResolver dependencyResolver;

    private List<UIInput> inputs = new ArrayList<>();

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        // lets create a field for each property on the component

        Map<Object, Object> attributeMap = builder.getUIContext().getAttributeMap();
        String camelComponentName = mandatoryAttributeValue(attributeMap, "componentName");
        CamelCatalog catalog = new DefaultCamelCatalog();
        String json = catalog.componentJSonSchema(camelComponentName);
        if (json == null) {
            throw new IllegalArgumentException("Could not find catalog entry for component name: " + camelComponentName);
        }

        List<Map<String, String>> data = JSonSchemaHelper.parseJsonSchema("properties", json, true);
        if (data != null) {
            Set<String> namesAdded = new HashSet<>();
            for (Map<String, String> propertyMap : data) {
                String name = propertyMap.get("name");
                String kind = propertyMap.get("kind");
                String type = propertyMap.get("type");
                String javaType = propertyMap.get("javaType");
                String deprecated = propertyMap.get("deprecated");
                String required = propertyMap.get("required");
                String defaultValue = propertyMap.get("defaultValue");
                String description = propertyMap.get("description");

                if (!Strings.isNullOrEmpty(name)) {
                    Class<Object> inputClazz = CamelCommands.loadValidInputTypes(javaType, type);
                    if (inputClazz != null) {
                        if (namesAdded.add(name)) {
                            UIInput<Object> input = componentFactory.createInput(name, inputClazz);
                            if (input != null) {
                                if (Objects.equals("true", required)) {
                                    input.setRequired(true);
                                }
                                input.setLabel(name);
                                // must use an empty description otherwise the UI prints null
                                input.setDescription(description != null ? description : "");
                                if (defaultValue != null) {
                                    input.setDefaultValue(defaultValue);
                                }
                                builder.add(input);
                                inputs.add(input);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public NavigationResult next(UINavigationContext context) throws Exception {
        return null;
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        Map<Object, Object> attributeMap = context.getUIContext().getAttributeMap();
        try {
            String camelComponentName = mandatoryAttributeValue(attributeMap, "componentName");
            String endpointInstanceName = mandatoryAttributeValue(attributeMap, "instanceName");
            String routeBuilder = mandatoryAttributeValue(attributeMap, "routeBuilder");
            String kind = mandatoryAttributeValue(attributeMap, "kind");

            Project project = getSelectedProject(context);
            JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);

            // does the project already have camel?
            Dependency core = CamelProjectHelper.findCamelCoreDependency(project);
            if (core == null) {
                return Results.fail("The project does not include camel-core");
            }

            // just build the uri and print to concole
            Map<String, String> options = new HashMap<String, String>();
            for (UIInput input : inputs) {
                String key = input.getName();
                // only use the value if a value was set
                if (input.hasValue()) {
                    String value = input.getValue().toString();
                    if (value != null) {
                        options.put(key, value);
                    }
                }
            }

            CamelCatalog catalog = new DefaultCamelCatalog();
            String uri = catalog.asEndpointUri(camelComponentName, options);
            if (uri != null) {
                return Results.success("Endpoint uri: " + uri);
            }

            // TODO: find the route builder, and then add the endpoint to its source code somehow
            String fqn = routeBuilder;

            JavaResource existing = facet.getJavaResource(fqn);
            if (existing != null && existing.exists()) {
                return Results.fail("A class with name " + fqn + " already exists");
            }

            // TODO: generate uri to add


            return Results.success("Added endpoint " + endpointInstanceName + " to " + routeBuilder);
        } catch (IllegalArgumentException e) {
            return Results.fail(e.getMessage());
        }
    }

    /**
     * Returns the mandatory String value of the given name
     *
     * @throws IllegalArgumentException if the value is not available in the given attribute map
     */
    public static String mandatoryAttributeValue(Map<Object, Object> attributeMap, String name) {
        Object value = attributeMap.get(name);
        if (value != null) {
            String text = value.toString();
            if (!Strings.isBlank(text)) {
                return text;
            }
        }
        throw new IllegalArgumentException("The attribute value '" + name + "' did not get passed on from the previous wizard page");
    }
}