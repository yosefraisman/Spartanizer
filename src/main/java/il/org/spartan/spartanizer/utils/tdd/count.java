package il.org.spartan.spartanizer.utils.tdd;

import org.eclipse.jdt.core.dom.*;

/** @author Ori Marcovitch
 * @since Oct 28, 2016 */
public interface count {
  /** @author Ori Marcovitch
   * @param astNode
   * @since Oct 28, 2016 */
  static int expressions(final ASTNode ¢) {
    return ¢ == null ? 0 : ((¢ + "").split(" ").length + 1) / 2;
  }
}
