package juzu.impl.plugin;

import juzu.impl.metadata.Descriptor;
import juzu.impl.utils.JSON;

/**
 * Base class for a plugin.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public abstract class Plugin {

  /** The plugin name. */
  private final String name;

  protected Plugin(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * Returns the plugin descriptor or null if the plugin does not provide any descriptor.
   *
   * @param loader the loader
   * @param config the plugin config
   * @return the descriptor
   * @throws Exception any exception
   */
  public Descriptor init(ClassLoader loader, JSON config) throws Exception {
    return Descriptor.EMPTY;
  }
}