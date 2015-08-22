package ru.hypernavi.server;

import org.jetbrains.annotations.NotNull;


import java.util.concurrent.atomic.AtomicBoolean;


import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;

/**
 * Created by amosov-f on 21.08.15.
 */
public final class HyperNaviServerRunner extends Runner {
    @NotNull
    private final Runner delegate;
    @NotNull
    private final AtomicBoolean registered = new AtomicBoolean();

    public HyperNaviServerRunner(@NotNull final Class<?> clazz) throws InitializationError {
        delegate = new JUnit4(clazz);
    }

    @NotNull
    @Override
    public Description getDescription() {
        return delegate.getDescription();
    }

    @Override
    public void run(@NotNull final RunNotifier runNotifier) {
        if (!registered.getAndSet(true)) {
            runNotifier.addListener(HyperNaviLaunchingListener.INSTANCE);
        }
        delegate.run(runNotifier);
    }

    private static final class HyperNaviLaunchingListener extends RunListener {
        private static final HyperNaviLaunchingListener INSTANCE = new HyperNaviLaunchingListener();

        @NotNull
        private final HyperNaviServerLauncher launcher = new HyperNaviServerLauncher();
        @NotNull
        private final AtomicBoolean launched = new AtomicBoolean();

        @Override
        public void testStarted(@NotNull final Description description) throws Exception {
            if (!launched.getAndSet(true)) {
                launcher.start();
            }
        }

        @Override
        public void testRunFinished(@NotNull final Result result) {
            if (launched.getAndSet(false)) {
                launcher.stop();
            }
        }
    }
}
