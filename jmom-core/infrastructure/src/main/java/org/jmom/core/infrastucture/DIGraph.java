package org.jmom.core.infrastucture;


import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.JMomFluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

public class DIGraph {

    private Set<DIGraph> diGraphs;
    private ListMultimap<Class, Provider> objectMap = ArrayListMultimap.create();
    private LinkedHashSet<DIRequestBuilder> diRequestBuilders = Sets.newLinkedHashSet();
    private Set<DIRuleBuilder> diRules = Sets.newHashSet();
    private Set<DIRuleBuilder> destructionDiRules = Sets.newHashSet();
    private Set<Object> evaluatedObjectByDiRules = Sets.newHashSet();

    private DIGraph() {
    }

    public static DIGraph aDIGraph() {
        return new DIGraph();
    }

    public DIGraph basedOn(DIGraph... otherGraphs) {
        this.diGraphs = Sets.newLinkedHashSetWithExpectedSize(otherGraphs.length);
        for (DIGraph otherGraph : otherGraphs) {
            diGraphs.add(otherGraph);
            this.diRules.addAll(otherGraph.diRules);
            this.destructionDiRules.addAll(otherGraph.destructionDiRules);
        }
        resolveDiRequestBuilders(this);
        return this;
    }

    public void resolveDiRequests() {
        resolveDiRequestBuilders(this);
    }

    public DIGraph withRule(DIRuleBuilder diRuleBuilder) {
        diRules.add(diRuleBuilder);
        return this;
    }

    public DIGraph withDestructionRule(DIRuleBuilder diRuleBuilder) {
        destructionDiRules.add(diRuleBuilder);
        return this;
    }

    public DIGraph register(Object object) {
        register(object.getClass(), object);
        return this;
    }

    public DIGraph register(Class clazz, Object object) {
        registerNoResolving(clazz, new SingletonProvider(object, null, null), this);
        resolveDiRequestBuilders(this);
        return this;
    }

    private DIGraph registerNoResolving(Class clazz, Provider provider, DIGraph diGraphForRulesResolving) {
        Object resolvedBean = provider.get();
        System.out.println("Registering clazz " + resolvedBean.getClass().getSimpleName() + " as " + clazz.getSimpleName() + " in digraph " + this.toString());
        objectMap.put(clazz, provider);
        resolveRules(resolvedBean, diGraphForRulesResolving);
        return this;
    }


    public DIGraph register(DIRequestBuilder diRequestBuilder) {
        diRequestBuilders.add(diRequestBuilder);
        resolveDiRequestBuilders(this);
        return this;
    }

    public boolean hasBean(Class clazz) {
        if (hasBeanLocally(clazz)) {
            return true;
        }
        if (diGraphs != null) {
            for (DIGraph diGraph : diGraphs) {
                if (diGraph.hasBean(clazz)) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean hasBeanLocally(Class clazz) {
        return objectMap.containsKey(clazz);
    }

    public <T> List<T> getBeans(Class<T> clazz) {
        List<T> result = Lists.newArrayList();
        getMatchingBeansAndAddToResult(clazz, result);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Bean " + clazz.getName() + " was not found.");
        }
        return result;
    }

    private <T> void getMatchingBeansAndAddToResult(Class<T> clazz, List<T> result) {
        if (objectMap.containsKey(clazz)) {
            result.addAll((Collection<T>) from(objectMap.get(clazz))
                    .transform(provider -> provider.get())
                    .toList());
        }
        if (diGraphs != null) {
            for (DIGraph diGraph : diGraphs) {
                diGraph.getMatchingBeansAndAddToResult(clazz, result);
            }
        }
    }

    public <T> Set<T> getBeansAsSet(Class<T> clazz) {
        return Sets.newLinkedHashSet(getBeans(clazz));
    }

    public <T> T getBean(Class<T> clazz) {
        Set<T> beans = getBeansAsSet(clazz);
        if (beans.size() == 1) {
            return beans.iterator().next();
        }
        throw new IllegalArgumentException("More than 1 (" + beans.size() + ") bean was found for " + clazz.getName() + ".");
    }

    public Set<Class> getUnfullfilledDependencies() {
        Set<Class> result = Sets.newHashSet();
        getUnfullfilledDependenciesAndAddTo(result);
        return result;
    }

    private void getUnfullfilledDependenciesAndAddTo(Set<Class> result) {
        for (DIRequestBuilder diRequestBuilder : diRequestBuilders) {
            result.addAll(Sets.newHashSet(diRequestBuilder.dependsOnClasses));
        }
        if (diGraphs != null) {
            for (DIGraph diGraph : diGraphs) {
                diGraph.getUnfullfilledDependenciesAndAddTo(result);
            }
        }
    }

    public void destroy() {
        for (Class aClass : objectMap.keySet()) {
            List<Provider> providers = objectMap.get(aClass);
            for (Provider provider : providers) {
                Object bean = provider.get();
                evaluateDestructionRules(bean, this);
                provider.runOnPredestroy();
            }
        }
        diGraphs.clear();
        diRules.clear();
        destructionDiRules.clear();
    }

    public void inject(Object object, Class<?> exclusiveParent) {
        from(getFieldsUpTo(object.getClass(), exclusiveParent))
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .forEachItem(field -> setBeanOnField(field, object));

        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                try {
                    Object bean = getBean(field.getType());
                    field.setAccessible(true);
                    field.set(object, bean);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setBeanOnField(Field field, Object object) {
        try {
            Object bean = getBean(field.getType());
            field.setAccessible(true);
            field.set(object, bean);
        } catch (IllegalAccessException e) {
            throw propagate(e);
        }
    }

    private static List<Field> getFieldsUpTo(Class<?> startClass, Class<?> exclusiveParent) {
        List<Field> currentClassFields = newArrayList(startClass.getDeclaredFields());
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null && (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }

    private void resolveDiRequestBuilders(DIGraph diGraph) {
        int resolvedDiRequests = resolveDiRequestBuilders(diGraph, 0);
        if (resolvedDiRequests > 0) {
            resolveDiRequestBuilders(diGraph);
        }
    }

    private int resolveDiRequestBuilders(DIGraph diGraph, int resolvedDiRequests) {
        resolvedDiRequests = resolveDiRequestsOfNestedGraphs(diGraph, resolvedDiRequests);
        resolvedDiRequests = resolveDiRequestsOfThisGraph(diGraph, resolvedDiRequests);
        return resolvedDiRequests;
    }

    private int resolveDiRequestsOfNestedGraphs(DIGraph diGraph, int resolvedDiRequests) {
        if (diGraphs != null) {
            for (DIGraph graph : diGraphs) {
                resolvedDiRequests += graph.resolveDiRequestBuilders(diGraph, resolvedDiRequests);
            }
        }
        return resolvedDiRequests;
    }

    private int resolveDiRequestsOfThisGraph(DIGraph diGraph, int resolvedDiRequests) {
        resolvedDiRequests += from(getResolvableDiRequestBuilders(diGraph))
                .forEachItem(diRequestBuilder -> diRequestBuilder.resolveDIRequest(diGraph, this))
                .size();
        return resolvedDiRequests;
    }

    private List<DIRequestBuilder> getResolvableDiRequestBuilders(DIGraph diGraph) {
        List<DIRequestBuilder> resolvableDiRequests = from(diRequestBuilders)
                .filter(diRequestBuilder -> diRequestBuilder.arePreconditionAndDependenciesSatisified(diGraph))
                .toList();
        diRequestBuilders.removeAll(resolvableDiRequests);
        return resolvableDiRequests;
    }

    private void resolveRules(Object object, DIGraph diGraphForDependencyResolving) {
        if (!diGraphForDependencyResolving.evaluatedObjectByDiRules.contains(object)) {
            diGraphForDependencyResolving.evaluatedObjectByDiRules.add(object);
            from(diGraphForDependencyResolving.diRules)
                    .filter(Predicates.and(
                            diRule -> diRule.isApplicableFor(object),
                            diRule -> diRule.areDependenciesSatisified(diGraphForDependencyResolving)))
                    .forEachItem(diRule -> {
                        System.out.println("    Applying rule " + diRule.assignableFrom.getSimpleName() + " on object " + object.getClass().getSimpleName());
                        diRule.onPostConstruct.apply(object, diGraphForDependencyResolving);
                    });

        }
    }

    private void evaluateDestructionRules(Object object, DIGraph diGraph) {
        from(diGraph.destructionDiRules)
                .filter(Predicates.and(
                        destructionDiRule -> destructionDiRule.isApplicableFor(object),
                        destructionDiRule -> destructionDiRule.areDependenciesSatisified(diGraph)))
                .forEachItem(destructionDiRule -> {
                    System.out.println("    Applying destruction rule " + destructionDiRule.assignableFrom.getSimpleName() + " on object " + object.getClass().getSimpleName());
                    destructionDiRule.onPostConstruct.apply(object, diGraph);
                });
    }

    public static class DIRequestBuilder {

        private Class<?>[] registerAsClasses;
        private Class<?>[] dependsOnClasses;
        private Function<DIGraph, Object> function;
        private DIRequestFunction<Object, DIGraph> onPostConstruct;
        private DIRequestFunction<Object, DIGraph> onPreDestroy;
        private Predicate<DIGraph> precondition;
        private boolean isPrototypeBean;

        private DIRequestBuilder() {
        }

        public static DIRequestBuilder aDIRequest() {
            return new DIRequestBuilder();
        }

        public DIRequestBuilder registerAs(Class<?>... clazzes) {
            this.registerAsClasses = clazzes;
            return this;
        }

        public DIRequestBuilder precondition(Predicate<DIGraph> predicate) {
            this.precondition = predicate;
            return this;
        }

        public DIRequestBuilder dependsOn(Class<?>... clazzes) {
            this.dependsOnClasses = clazzes;
            return this;
        }

        public DIRequestBuilder create(Function<DIGraph, Object> function) {
            this.function = function;
            return this;
        }

        public DIRequestBuilder asPrototypeBean() {
            this.isPrototypeBean = true;
            return this;
        }

        public DIRequestBuilder onPostConstruct(DIRequestFunction<Object, DIGraph> function) {
            this.onPostConstruct = function;
            return this;
        }

        public DIRequestBuilder onPreDestroy(DIRequestFunction<Object, DIGraph> function) {
            this.onPreDestroy = function;
            return this;
        }

        private boolean arePreconditionAndDependenciesSatisified(DIGraph diGraph) {
            if (dependsOnClasses != null && dependsOnClasses.length > 0) {
                for (Class<?> dependsOnClass : dependsOnClasses) {
                    if (!diGraph.hasBean(dependsOnClass)) {
                        return false;
                    }
                }
            }
            if (precondition != null && !precondition.apply(diGraph)) {
                return false;
            }
            return true;
        }

        private void resolveDIRequest(DIGraph diGraphForDependencyResolving, DIGraph diGraphForRegistering) {
            Provider provider = null;
            if (isPrototypeBean) {
                provider = new PrototypeProvider(function, diGraphForDependencyResolving, onPreDestroy);
            } else {
                provider = new SingletonProvider(function.apply(diGraphForDependencyResolving), diGraphForDependencyResolving, onPreDestroy);
            }

            Object result = provider.get();
            if (onPostConstruct != null) {
                onPostConstruct.apply(result, diGraphForDependencyResolving);
            }
            if (registerAsClasses != null && registerAsClasses.length > 0) {
                for (Class<?> registerAsClass : registerAsClasses) {
                    diGraphForRegistering.registerNoResolving(registerAsClass, provider, diGraphForDependencyResolving);
                }
            }
            diGraphForRegistering.registerNoResolving(result.getClass(), provider, diGraphForDependencyResolving);
        }
    }

    public static class DIRuleBuilder {

        private Class<?>[] dependsOnClasses;
        private Class<?> assignableFrom;
        private DIRequestFunction<Object, DIGraph> onPostConstruct;

        private DIRuleBuilder() {
        }

        public static DIRuleBuilder aDIRule() {
            return new DIRuleBuilder();
        }

        public DIRuleBuilder dependsOn(Class<?>... clazzes) {
            this.dependsOnClasses = clazzes;
            return this;
        }

        public DIRuleBuilder objectAssignableFrom(Class<?> clazz) {
            this.assignableFrom = clazz;
            return this;
        }

        public DIRuleBuilder onPostConstruct(DIRequestFunction<Object, DIGraph> function) {
            this.onPostConstruct = function;
            return this;
        }

        private boolean isApplicableFor(Object object) {
            return assignableFrom.isAssignableFrom(object.getClass());
        }

        private boolean areDependenciesSatisified(DIGraph diGraph) {
            if (dependsOnClasses != null && dependsOnClasses.length > 0) {
                for (Class<?> dependsOnClass : dependsOnClasses) {
                    if (!diGraph.hasBean(dependsOnClass)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static interface DIRequestFunction<A, B> {
        void apply(A a, B b);
    }

    private static class SingletonProvider implements Provider {

        private final Object object;
        private final DIGraph diGraph;
        private final DIRequestFunction predestroyFunction;

        public SingletonProvider(Object object, DIGraph diGraph, DIRequestFunction predestroyFunction) {
            this.object = object;
            this.diGraph = diGraph;
            this.predestroyFunction = predestroyFunction;
        }

        @Override
        public Object get() {
            return object;
        }

        @Override
        public void runOnPredestroy() {
            if(predestroyFunction != null) {
                predestroyFunction.apply(object, diGraph);
            }
        }
    }

    private static class PrototypeProvider implements Provider {

        private final Function<DIGraph, Object> function;
        private final DIGraph diGraph;
        private DIRequestFunction predestroyFunction;

        public PrototypeProvider(Function<DIGraph, Object> function, DIGraph diGraph, DIRequestFunction predestroyFunction) {
            this.function = function;
            this.diGraph = diGraph;
            this.predestroyFunction = predestroyFunction;
        }

        @Override
        public Object get() {
            return function.apply(diGraph);
        }

        @Override
        public void runOnPredestroy() {
            if(predestroyFunction != null) {
                predestroyFunction.apply(function.apply(diGraph), diGraph);
            }
        }
    }

    private static interface Provider {

        Object get();

        void runOnPredestroy();
    }
}
