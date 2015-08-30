package ru.hypernavi.server;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by amosov-f on 30.08.15.
 */
public final class HandleErrorsInLog implements TestRule {
    @NotNull
    private final FailOnErrorAppender failOnErrorAppender = new FailOnErrorAppender();

    @NotNull
    @Override
    public Statement apply(@NotNull final Statement statement, @NotNull final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                failOnErrorAppender.inject();
                try {
                    statement.evaluate();
                    failOnErrorAppender.assertNoErrors();
                } finally {
                    failOnErrorAppender.eject();
                }
            }
        };
    }

    private static final class FailOnErrorAppender extends AppenderSkeleton {
        private volatile long mainThreadId = -1L;

        @NotNull
        private final AtomicReference<ErrorInLogException> assertionError = new AtomicReference<>();

        void inject() {
            mainThreadId = Thread.currentThread().getId();
            Logger.getRootLogger().addAppender(this);
            assertionError.set(null);
        }

        void eject() {
            mainThreadId = -1L;
            Logger.getRootLogger().removeAppender(this);
            assertionError.set(null);
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(@NotNull final LoggingEvent event) {
            if (!event.getLevel().isGreaterOrEqual(Level.ERROR)) {
                return;
            }

            final ErrorInLogException error = new ErrorInLogException(String.valueOf(event.getMessage()));
            Optional.ofNullable(event.getThrowableInformation()).ifPresent(info -> error.initCause(info.getThrowable()));

            if (Thread.currentThread().getId() == mainThreadId) {
                throw error;
            }
            assertionError.compareAndSet(null, error);
        }

        void assertNoErrors() throws AssertionError {
            final AssertionError error = assertionError.getAndSet(null);
            if (error != null) {
                throw error;
            }
        }

        @Override
        public void close() {
        }
    }

    private static final class ErrorInLogException extends AssertionError {
        ErrorInLogException(@NotNull final Object detailMessage) {
            super(detailMessage);
        }
    }
}
