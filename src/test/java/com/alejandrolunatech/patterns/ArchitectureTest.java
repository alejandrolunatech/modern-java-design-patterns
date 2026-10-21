package com.alejandrolunatech.patterns;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/** Each pattern stands alone, so it can be read, deleted for a kata, or copied on its own. */
@AnalyzeClasses(packages = "com.alejandrolunatech.patterns", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule gofPatternsAreIndependent = slices()
            .matching("com.alejandrolunatech.patterns.gof.(*)..")
            .should().notDependOnEachOther();

    @ArchTest
    static final ArchRule llmPatternsOnlyShareTheCore = slices()
            .matching("com.alejandrolunatech.patterns.llm.(*)..")
            .should().notDependOnEachOther()
            .ignoreDependency(alwaysTrue(), resideInAPackage("com.alejandrolunatech.patterns.llm.core.."));

    @ArchTest
    static final ArchRule gofAndLlmDoNotMix = noClasses()
            .that().resideInAPackage("..patterns.gof..")
            .should().dependOnClassesThat().resideInAPackage("..patterns.llm..");

    @ArchTest
    static final ArchRule theCoreDependsOnNoPattern = noClasses()
            .that().resideInAPackage("..patterns.llm.core..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..llm.adapter..", "..llm.routing..", "..llm.decorator..", "..llm.guardrails..", "..llm.resilience..");
}
