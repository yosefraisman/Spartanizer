package il.org.spartan.spartanizer.wring;

import static il.org.spartan.lisp.*;
import static il.org.spartan.spartanizer.ast.step.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.assemble.*;
import il.org.spartan.spartanizer.ast.*;
import il.org.spartan.spartanizer.wring.dispatch.*;
import il.org.spartan.spartanizer.wring.strategies.*;

/** Replaces, e.g., <code>Integer x=new Integer(2);</code> with
 * <code>Integer x=Integer.valueOf(2);</code>, more generally new of of any
 * boxed primitive types/{@link String} with recommended factory method
 * <code>valueOf()</code>
 * @author Ori Roth <code><ori.rothh [at] gmail.com></code>
 * @since 2016-04-06 */
public class ClassInstanceCreationValueTypes extends ReplaceCurrentNode<ClassInstanceCreation> implements Kind.SyntacticBaggage {
  @Override public ASTNode replacement(final ClassInstanceCreation c) {
    if (arguments(c).size() != 1)
      return null;
    final Type t = c.getType();
    if (!wizard.isValueType(t))
      return null;
    final MethodInvocation $ = subject.operand(make.newSimpleName(c, t + "")).toMethod("valueOf");
    arguments($).add(duplicate.of(first(arguments(c))));
    return $;
  }

  @Override protected String description(final ClassInstanceCreation ¢) {
    return "Use factory method " + ¢.getType() + ".valueOf() instead of new ";
  }
}