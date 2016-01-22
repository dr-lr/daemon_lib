package com.liveramp.warlock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.liveramp.warlock.Daemon;
import com.liveramp.warlock.DaemonNotifier;
import com.liveramp.warlock.JobletCallback;
import com.liveramp.warlock.JobletConfig;
import com.liveramp.warlock.JobletConfigProducer;
import com.liveramp.warlock.built_in.NoOpDaemonLock;
import com.liveramp.warlock.executors.JobletExecutor;
import com.liveramp.warlock.utils.DaemonException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class TestDaemon extends DaemonLibTestCase {
  JobletExecutor<JobletConfig> executor;
  Daemon<JobletConfig> daemon;
  private JobletConfig config;
  private JobletConfigProducer configProducer;

  @Before
  @SuppressWarnings("unchecked")
  public void setup() {
    this.executor = mock(JobletExecutor.class);
    this.config = mock(JobletConfig.class);
    this.configProducer = mock(JobletConfigProducer.class);
    this.daemon = new Daemon("identifier", executor, configProducer, new JobletCallback.None<>(),
        new NoOpDaemonLock(), mock(DaemonNotifier.class), new Daemon.Options());
  }

  @Test
  public void executeConfig() throws DaemonException {
    Mockito.when(executor.canExecuteAnother()).thenReturn(true);
    Mockito.when(configProducer.getNextConfig()).thenReturn(config);

    daemon.processNext();

    Mockito.verify(executor, times(1)).execute(config);
  }

  @Test
  public void executionUnavailable() throws DaemonException {
    Mockito.when(executor.canExecuteAnother()).thenReturn(false);
    Mockito.when(configProducer.getNextConfig()).thenReturn(config);

    daemon.processNext();

    Mockito.verify(executor, never()).execute(any(JobletConfig.class));
  }

  @Test
  public void noNextConfig() throws DaemonException {
    Mockito.when(executor.canExecuteAnother()).thenReturn(false);
    Mockito.when(configProducer.getNextConfig()).thenReturn(null);

    daemon.processNext();

    Mockito.verify(executor, never()).execute(any(JobletConfig.class));
  }
}