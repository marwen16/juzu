package juzu.impl.controller.descriptor;

import juzu.impl.controller.ControllerResolver;
import juzu.impl.metadata.Descriptor;
import juzu.impl.common.JSON;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class ControllersDescriptor extends Descriptor {

  /** . */
  private final Class<?> defaultController;

  /** . */
  private final List<ControllerDescriptor> controllers;

  /** . */
  private final List<MethodDescriptor> methods;

  /** . */
  private final ArrayList<juzu.impl.inject.BeanDescriptor> beans;

  /** . */
  private final Boolean escapeXML;

  /** . */
  private final List<RouteDescriptor> routes;

  /** . */
  private ControllerDescriptorResolver resolver;

  public ControllersDescriptor(ClassLoader loader, JSON config) throws Exception {
    List<ControllerDescriptor> controllers = new ArrayList<ControllerDescriptor>();
    List<MethodDescriptor> controllerMethods = new ArrayList<MethodDescriptor>();
    ArrayList<juzu.impl.inject.BeanDescriptor> beans = new ArrayList<juzu.impl.inject.BeanDescriptor>();

    // Load controllers
    for (String fqn : config.getList("controllers", String.class)) {
      Class<?> clazz = loader.loadClass(fqn);
      Field f = clazz.getField("DESCRIPTOR");
      ControllerDescriptor bean = (ControllerDescriptor)f.get(null);
      controllers.add(bean);
      controllerMethods.addAll(bean.getMethods());
      beans.add(new juzu.impl.inject.BeanDescriptor(bean.getType(), null, null, null));
    }

    //
    Boolean escapeXML = config.getBoolean("escapeXML");

    //
    Class<?> defaultController = null;
    String defaultControllerName = config.getString("default");
    if (defaultControllerName != null) {
      defaultController = loader.loadClass(defaultControllerName);
    }

    // Routes
    List<RouteDescriptor> routes = new ArrayList<RouteDescriptor>();
    List<? extends JSON> abc = config.getList("routes", JSON.class);
    if (abc != null) {
      for (JSON route : abc) {
        String id = route.getString("id");
        List<? extends String> parameters = route.getList("parameters", String.class);
        String path = route.getString("path");
        RouteDescriptor r = new RouteDescriptor(
            id,
            Collections.unmodifiableSet(new HashSet<String>(parameters)),
            path
        );
        routes.add(r);
      }
    }

    //
    this.escapeXML = escapeXML;
    this.defaultController = defaultController;
    this.controllers = controllers;
    this.methods = controllerMethods;
    this.beans = beans;
    this.routes = Collections.unmodifiableList(routes);
    this.resolver = new ControllerDescriptorResolver(this);
  }

  public Iterable<juzu.impl.inject.BeanDescriptor> getBeans() {
    return beans;
  }

  public ControllerResolver<MethodDescriptor> getResolver() {
    return resolver;
  }

  public Class<?> getDefault() {
    return defaultController;
  }

  public Boolean getEscapeXML() {
    return escapeXML;
  }

  public List<ControllerDescriptor> getControllers() {
    return controllers;
  }

  public List<MethodDescriptor> getMethods() {
    return methods;
  }

  public List<RouteDescriptor> getRoutes() {
    return routes;
  }

  public MethodDescriptor getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
    for (int i = 0;i < methods.size();i++) {
      MethodDescriptor cm = methods.get(i);
      Method m = cm.getMethod();
      if (type.equals(cm.getType()) && m.getName().equals(name)) {
        Class<?>[] a = m.getParameterTypes();
        if (a.length == parameterTypes.length) {
          for (int j = 0;j < parameterTypes.length;j++) {
            if (!a[j].equals(parameterTypes[j])) {
              continue;
            }
          }
          return cm;
        }
      }
    }
    return null;
  }

  public MethodDescriptor getMethodById(String methodId) {
    for (int i = 0;i < methods.size();i++) {
      MethodDescriptor cm = methods.get(i);
      if (cm.getId().equals(methodId)) {
        return cm;
      }
    }
    return null;
  }
}
