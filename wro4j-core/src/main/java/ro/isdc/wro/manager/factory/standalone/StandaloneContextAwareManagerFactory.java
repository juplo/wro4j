/*
 * Copyright (C) 2010.
 * All rights reserved.
 */
package ro.isdc.wro.manager.factory.standalone;

import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;


/**
 * An implementation of {@link WroManagerFactory} aware about the run context.<br/>
 * TODO: find a way to not require {@link StandaloneContextAwareManagerFactory} for build time processing, but use any
 * instance of {@link WroManagerFactory}.
 *
 * @author Alex Objelean
 */
public interface StandaloneContextAwareManagerFactory
  extends WroManagerFactory {
  /**
   * Called by standalone process for initialization. It is responsibility of the implementor to take care of
   * {@link StandaloneContext} in order to initialize the internals.
   *
   * @param standaloneContext {@link StandaloneContext} holding properties associated with current context.
   */
  void initialize(StandaloneContext standaloneContext);
  /**
   * set the processor factory to be used by the {@link WroManagerFactory} created by this factory.
   * @param processorsFactory
   */
  void setProcessorsFactory(ProcessorsFactory processorsFactory);
}
