package com.nksoft.entrance_examination.config.hints;

import liquibase.changelog.ChangeLogHistoryServiceFactory;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.Nullable;

public class LiquibaseHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        // Register core Liquibase classes
        hints.reflection().registerType(
                DatabaseChangeLog.class,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
        );
        hints.reflection().registerType(
                ChangeSet.class,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
        );
        hints.reflection().registerType(
                ChangeLogHistoryServiceFactory.class,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
        );

        // Register resource patterns for YAML and SQL
        hints.resources().registerPattern("db/changelog/db.changelog-master.yaml");
        hints.resources().registerPattern("db/changelog/changeset/.*\\.sql");
    }
}
