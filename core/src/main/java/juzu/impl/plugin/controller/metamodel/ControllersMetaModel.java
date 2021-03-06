/*
 * Copyright 2013 eXo Platform SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package juzu.impl.plugin.controller.metamodel;

import juzu.impl.plugin.controller.AmbiguousResolutionException;
import juzu.impl.common.Name;
import juzu.impl.plugin.application.metamodel.ApplicationMetaModel;
import juzu.impl.compiler.ElementHandle;
import juzu.impl.metamodel.Key;
import juzu.impl.metamodel.MetaModelObject;
import juzu.impl.common.JSON;

import java.util.Iterator;
import java.util.Set;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class ControllersMetaModel extends MetaModelObject implements Iterable<ControllerMetaModel> {

  /** . */
  public final static Key<ControllersMetaModel> KEY = Key.of(ControllersMetaModel.class);

  /** . */
  Name defaultController;

  /** . */
  Boolean escapeXML;

  /** . */
  private ApplicationMetaModel application;

  @Override
  public JSON toJSON() {
    JSON json = new JSON();
    json.map("values", getChildren(ControllerMetaModel.class));
    return json;
  }

  public ApplicationMetaModel getApplication() {
    return application;
  }

  public Iterator<ControllerMetaModel> iterator() {
    return getChildren(ControllerMetaModel.class).iterator();
  }

  public ControllerMetaModel get(ElementHandle.Class handle) {
    return getChild(Key.of(handle, ControllerMetaModel.class));
  }

  public void add(ControllerMetaModel controller) {
    addChild(Key.of(controller.handle, ControllerMetaModel.class), controller);
  }

  public void remove(ControllerMetaModel controller) {
    if (controller.controllers != this) {
      throw new IllegalArgumentException();
    }
    removeChild(Key.of(controller.handle, ControllerMetaModel.class));
  }

  public MethodMetaModel resolve(String typeName, String methodName, Set<String> parameterNames) throws AmbiguousResolutionException {
    try {
      ControllerMetaModelResolver resolver = new ControllerMetaModelResolver(this);
      return resolver.resolve(typeName, methodName, parameterNames);
    }
    catch (AmbiguousResolutionException e) {
      // RootMetaModel.log.log("Could not resolve ambiguous method " + methodName + " " + parameterNames);
      return null;
    }
  }

  @Override
  protected void postAttach(MetaModelObject parent) {
    if (parent instanceof ApplicationMetaModel) {
      application = (ApplicationMetaModel)parent;
    }
  }

  @Override
  protected void preDetach(MetaModelObject parent) {
    if (parent instanceof ApplicationMetaModel) {
      application = null;
    }
  }
}
