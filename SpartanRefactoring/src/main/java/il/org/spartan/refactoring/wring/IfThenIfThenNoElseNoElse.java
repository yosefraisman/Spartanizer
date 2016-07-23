package il.org.spartan.refactoring.wring;

import il.org.spartan.refactoring.preferences.*;
import il.org.spartan.refactoring.preferences.PluginPreferencesResources.WringGroup;
import il.org.spartan.refactoring.suggestions.*;
import il.org.spartan.refactoring.utils.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import static il.org.spartan.refactoring.utils.Funcs.*;

import static il.org.spartan.refactoring.utils.extract.*;

import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

/**
 * A {@link Wring} to convert <code>if (x) if (a) f();</code> into <code>if (x
 * && a) f();</code>
 *
 * @author Yossi Gil
 * @since 2015-09-01
 */
public final class IfThenIfThenNoElseNoElse extends Wring<IfStatement> implements Kind.ConsolidateStatements {
  @Override String description(final IfStatement s) {
    return "Merge conditionals of if(" + s.getExpression() + ") ... with its nested if";
  }
  @Override boolean scopeIncludes(final IfStatement s) {
    return make(s) != null;
  }
  @Override Suggestion make(final IfStatement s, final ExclusionManager exclude) {
    if (!Is.vacuousElse(s))
      return null;
    final IfStatement then = asIfStatement(extract.singleThen(s));
    if (then == null || !Is.vacuousElse(then))
      return null;
    if (exclude != null)
      exclude.exclude(then);
    return new Suggestion(description(s), s) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        collapse(Wrings.blockIfNeeded(s, r, g), r, g);
      }
    };
  }
  static void collapse(final IfStatement s, final ASTRewrite r, final TextEditGroup g) {
    final IfStatement then = asIfStatement(extract.singleThen(s));
    final InfixExpression e = Subject.pair(s.getExpression(), then.getExpression()).to(CONDITIONAL_AND);
    r.replace(s.getExpression(), e, g);
    r.replace(then, duplicate(then(then)), g);
  }
  @Override Suggestion make(final IfStatement s) {
    return make(s, null);
  }
  @Override public WringGroup kind() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override public void go(final ASTRewrite r, final TextEditGroup g) {
    // TODO Auto-generated method stub
  }
}