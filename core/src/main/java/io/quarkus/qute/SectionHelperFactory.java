package io.quarkus.qute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Factory to create a new {@link SectionHelper} based on the {@link SectionInitContextImpl}.
 */
public interface SectionHelperFactory<T extends SectionHelper> {

    String MAIN_BLOCK_NAME = "$main";

    /**
     * 
     * @return the list of default aliases
     */
    default List<String> getDefaultAliases() {
        return Collections.emptyList();
    }

    /**
     * 
     * @return the info about the expected parameters
     */
    default ParametersInfo getParameters() {
        return ParametersInfo.EMPTY;
    }

    /**
     * 
     * @param context
     * @return a new helper instance
     */
    T initialize(SectionInitContext context);

    /**
     * Initialize a section block.
     * 
     * @return a map of name to type infos
     */
    default Map<String, String> initializeBlock(Map<String, String> outerNameTypeInfos, BlockInfo block) {
        return Collections.emptyMap();
    }

    /**
     * 
     */
    interface BlockInfo {

        String getLabel();

        Map<String, String> getParameters();

        default String getParameter(String name) {
            return getParameters().get(name);
        }

        default boolean hasParameter(String name) {
            return getParameters().containsKey(name);
        }

        Expression addExpression(String param, String value);

    }

    /**
     * Section initialization context.
     */
    public interface SectionInitContext {

        default Map<String, String> getParameters() {
            return getBlocks().get(0).parameters;
        }

        default boolean hasParameter(String name) {
            return getParameters().containsKey(name);
        }

        default String getParameter(String name) {
            return getParameters().get(name);
        }

        Expression getExpression(String parameterName);

        Expression parseValue(String value);

        List<SectionBlock> getBlocks();

        Engine getEngine();

    }

    public static final class ParametersInfo implements Iterable<List<Parameter>> {

        public static Builder builder() {
            return new Builder();
        }

        public static final ParametersInfo EMPTY = builder().build();

        private final Map<String, List<Parameter>> parameters;

        private ParametersInfo(Map<String, List<Parameter>> parameters) {
            this.parameters = new HashMap<>(parameters);
        }

        public List<Parameter> get(String sectionPart) {
            return parameters.getOrDefault(sectionPart, Collections.emptyList());
        }

        @Override
        public Iterator<List<Parameter>> iterator() {
            return parameters.values().iterator();
        }

        public static class Builder {

            private final Map<String, List<Parameter>> parameters;

            Builder() {
                this.parameters = new HashMap<>();
            }

            public Builder addParameter(String name) {
                return addParameter(SectionHelperFactory.MAIN_BLOCK_NAME, name, null);
            }

            public Builder addParameter(String name, String defaultValue) {
                return addParameter(SectionHelperFactory.MAIN_BLOCK_NAME, name, defaultValue);
            }

            public Builder addParameter(Parameter param) {
                return addParameter(SectionHelperFactory.MAIN_BLOCK_NAME, param);
            }

            public Builder addParameter(String blockLabel, String name, String defaultValue) {
                return addParameter(blockLabel, new Parameter(name, defaultValue, false));
            }

            public Builder addParameter(String blockLabel, Parameter parameter) {
                parameters.computeIfAbsent(blockLabel, c -> new ArrayList<>()).add(parameter);
                return this;
            }

            public ParametersInfo build() {
                return new ParametersInfo(parameters);
            }
        }

    }

}
